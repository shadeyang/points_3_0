package com.wt2024.points.core.handler.trans;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.*;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.common.sequence.Sequence;
import com.wt2024.points.core.api.domain.merchant.ClearingList;
import com.wt2024.points.core.api.domain.trans.PointsConsumeInput;
import com.wt2024.points.core.api.domain.trans.PointsConsumeOutput;
import com.wt2024.points.core.constant.Constants;
import com.wt2024.points.core.converter.ConverterConstants;
import com.wt2024.points.core.handler.trans.vo.ConsumeTransVo;
import com.wt2024.points.core.listener.event.AsyncTransAccountEvent;
import com.wt2024.points.core.listener.event.AsyncTransBusinessEvent;
import com.wt2024.points.repository.api.account.domain.PointsAccountInfoDTO;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import com.wt2024.points.repository.api.account.domain.PointsTypeDTO;
import com.wt2024.points.repository.api.account.repository.PointsAccountInfoRepository;
import com.wt2024.points.repository.api.account.repository.PointsTransRepository;
import com.wt2024.points.repository.api.account.repository.PointsTypeRepository;
import com.wt2024.points.repository.api.cache.repository.CacheRepository;
import com.wt2024.points.repository.api.merchant.domain.MerchantDTO;
import com.wt2024.points.repository.api.merchant.repository.MerchantRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 积分消费
 *
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/8/10 15:46
 * @Project points2.0:com.wt2024.points.backend.handler.trans
 */
@Component
public class ConsumeTransHandler extends AbstractTransHandler<ConsumeTransVo> {

    private PointsTransRepository pointsTransRepository;

    private PointsAccountInfoRepository pointsAccountInfoRepository;

    private PointsTypeRepository pointsTypeRepository;

    private MerchantRepository merchantRepository;

    public ConsumeTransHandler(@Autowired ApplicationContext applicationContext,
                               @Autowired CacheRepository cacheRepository,
                               @Autowired PointsTypeRepository pointsTypeRepository,
                               @Autowired PointsTransRepository pointsTransRepository,
                               @Autowired PointsAccountInfoRepository pointsAccountInfoRepository,
                               @Autowired MerchantRepository merchantRepository) {
        super(applicationContext, cacheRepository);

        this.pointsTypeRepository = pointsTypeRepository;
        this.pointsTransRepository = pointsTransRepository;
        this.pointsAccountInfoRepository = pointsAccountInfoRepository;
        this.merchantRepository = merchantRepository;
    }

    @Override
    protected void event(ConsumeTransVo vo) {
        PointsTransDTO pointsTrans = vo.getPointsTrans();
        applicationContext.publishEvent(new AsyncTransAccountEvent(this, pointsTrans.getCustomerId(), pointsTrans.getPointsTypeNo(), pointsTrans.getTransNo()));
    }

    @Override
    protected void failCallBack(ConsumeTransVo vo) throws PointsException {
        this.pointsAccount(vo);
        PointsTransDTO pointsTrans = vo.getPointsTrans();
        pointsTrans.setTransStatus(TransStatus.FAILED.getCode());
        pointsTrans.setDescription(pointsTrans.getDescription() + "【处理结果：" + vo.getMessage() + "】");
        pointsTransRepository.processTrans(pointsTrans, null);
    }

    @Override
    protected void successCallBack(ConsumeTransVo vo) throws PointsException {
        PointsTransDTO pointsTrans = vo.getPointsTrans();
        PointsConsumeOutput output = vo.getOutput();
        pointsTransRepository.processTrans(pointsTrans, null);
        output.setSysTransNo(pointsTrans.getSysTransNo());
        var localDateTime = LocalDateTime.ofEpochSecond(TimeUnit.MILLISECONDS.toSeconds(pointsTrans.getTransTimestamp()), 0, Constants.ZONE_OFFSET);
        output.setPayTime(localDateTime.format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS)));
        output.setPointsTransNo(pointsTrans.getTransNo());
        vo.setRetCode(PointsCode.TRANS_0000);
    }

    @Override
    protected boolean check(ConsumeTransVo vo) {
        PointsConsumeInput input = vo.getInput();

        //检查积分类型
        PointsTypeDTO pointsType = pointsTypeRepository.queryPointsTypeByInst(input.getPoints().getPointsTypeNo(), vo.getInstitution().getInstitutionId());
        if (Objects.isNull(pointsType)) {
            vo.setRetCode(PointsCode.TRANS_2005, input.getPoints().getPointsTypeNo());
            return false;
        }
        vo.setPointsType(pointsType);

        MerchantDTO merchant = merchantRepository.queryMerchantByMerchantNo(input.getMerchantNo(), input.getCustomerInfo().getInstitutionId());
        if (Objects.isNull(merchant) || !MerchantStatus.AVAILABLE.getStatus().equalsIgnoreCase(merchant.getStatus())) {
            vo.setRetCode(PointsCode.TRANS_2004, input.getMerchantNo());
            return false;
        }

        //检查交易流水重复
        PointsTransDTO pointsTrans = pointsTransRepository.existsPointsBySysTransNo(input.getSysTransNo());
        if (Objects.nonNull(pointsTrans)) {
            vo.setRetCode(PointsCode.TRANS_2003);
            failCalled(vo);
            return false;
        }
        //检查可用余额
        PointsAccountInfoDTO pointsAccountInfo = pointsAccountInfoRepository.queryPointsAccountInfoByType(vo.getCustomer().getCustomerId(), pointsType.getPointsTypeNo());
        if (!AccountStatus.AVAILABLE.getStatus().equalsIgnoreCase(pointsAccountInfo.getPointsAccountStatus())) {
            vo.setRetCode(PointsCode.TRANS_2023);
            failCalled(vo);
            return false;
        }

        if (pointsAccountInfo.getPointsBalance().subtract(pointsAccountInfo.getFreezingPoints()).compareTo(input.getPoints().getPointsAmount()) < 0) {
            vo.setRetCode(PointsCode.TRANS_2010);
            failCalled(vo);
            return false;
        }
        return true;
    }

    @Override
    protected void initLock(ConsumeTransVo vo) {
        this.lock(vo, String.join(Constants.DELIMITER, Constants.REDIS_POINTS_TRANS_ACCOUNT_KEY, vo.getCustomer().getCustomerId(), vo.getInput().getPoints().getPointsTypeNo()));
        this.lock(vo, String.join(Constants.DELIMITER, Constants.REDIS_POINTS_TRANS_SYS_TRANS_NO_KEY, vo.getInput().getSysTransNo()));
    }

    @Override
    protected void pointsAccount(ConsumeTransVo vo) {
        LocalDateTime localDateTime = LocalDateTime.now();
        PointsConsumeInput input = vo.getInput();
        PointsTransDTO pointsTrans = new PointsTransDTO();
        pointsTrans.setTransNo(Sequence.getTransNo());
        pointsTrans.setOldTransNo(pointsTrans.getTransNo());
        pointsTrans.setCustomerId(vo.getCustomer().getCustomerId());
        pointsTrans.setPointsTypeNo(input.getPoints().getPointsTypeNo());
        pointsTrans.setInstitutionId(vo.getInstitution().getInstitutionId());
        pointsTrans.setTransDate(localDateTime.format(DateTimeFormatter.BASIC_ISO_DATE));
        pointsTrans.setTransTime(localDateTime.format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_HHMMSS)));
        pointsTrans.setTransTypeNo(TransType.CONSUME_POINT.getCode());
        pointsTrans.setDebitOrCredit(DebitOrCredit.DEBIT.getCode());
        pointsTrans.setPointsAmount(input.getPoints().getPointsAmount());
        pointsTrans.setReversedFlag(ReversedFlag.NOT_REVERSED.getCode());
        pointsTrans.setTransChannel(Constants.DEFAULT_SYSTEM_TRANS_CHANNEL);
        pointsTrans.setMerchantNo(input.getMerchantNo());
        pointsTrans.setVoucherTypeNo(input.getVoucher().getVoucherType().getType());
        pointsTrans.setVoucherNo(input.getVoucher().getVoucherNo());
        pointsTrans.setTransStatus(TransStatus.SUCCESS.getCode());
        pointsTrans.setOperator(StringUtils.isBlank(input.getOperator()) ? Constants.DEFAULT_OPERATOR_SYSTEM : input.getOperator());
        pointsTrans.setSysTransNo(input.getSysTransNo());

        pointsTrans.setClearingAmt(input.getPoints().getPointsAmount().divide(vo.getPointsType().getRate(), 2, RoundingMode.HALF_EVEN));
        pointsTrans.setDescription(StringUtils.isEmpty(input.getDescription()) ? TransType.CONSUME_POINT.getDesc() : input.getDescription());
        pointsTrans.setTransTimestamp(localDateTime.toInstant(Constants.ZONE_OFFSET).toEpochMilli());

        vo.setPointsTrans(pointsTrans);
    }

    @Override
    protected void businessAccount(ConsumeTransVo vo) {
        PointsTransDTO pointsTrans = vo.getPointsTrans();
        if (TransStatus.SUCCESS.getCode().equals(pointsTrans.getTransStatus()) && Objects.nonNull(pointsTrans.getMerchantNo())) {
            ClearingList clearingList = ConverterConstants.MERCHANT_CONVERTER.toClearingList(pointsTrans);
            clearingList.setPointsAmount(clearingList.getPointsAmount().negate());
            applicationContext.publishEvent(new AsyncTransBusinessEvent(this, clearingList));
        }
    }

}
