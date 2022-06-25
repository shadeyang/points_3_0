package com.wt2024.points.core.handler.trans;


import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.*;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.common.sequence.Sequence;
import com.wt2024.points.core.api.domain.merchant.ClearingList;
import com.wt2024.points.core.api.domain.trans.PointsAccTransInput;
import com.wt2024.points.core.api.domain.trans.PointsAccTransOutput;
import com.wt2024.points.core.constant.Constants;
import com.wt2024.points.core.converter.ConverterConstants;
import com.wt2024.points.core.handler.trans.vo.AccountTransVo;
import com.wt2024.points.core.listener.event.AsyncTransAccountEvent;
import com.wt2024.points.core.listener.event.AsyncTransBusinessEvent;
import com.wt2024.points.repository.api.account.domain.PointsAccountInfoDTO;
import com.wt2024.points.repository.api.account.domain.PointsCostDTO;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import com.wt2024.points.repository.api.account.domain.PointsTypeDTO;
import com.wt2024.points.repository.api.account.repository.PointsAccountInfoRepository;
import com.wt2024.points.repository.api.account.repository.PointsCostRepository;
import com.wt2024.points.repository.api.account.repository.PointsTransRepository;
import com.wt2024.points.repository.api.account.repository.PointsTypeRepository;
import com.wt2024.points.repository.api.cache.repository.CacheRepository;
import com.wt2024.points.repository.api.merchant.domain.MerchantDTO;
import com.wt2024.points.repository.api.merchant.repository.MerchantRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 积分交易 账务借贷
 *
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/8/24 14:44
 * @project points2.0:com.wt2024.points.backend.handler.trans
 */
@Component
public class AccountTransHandler extends AbstractTransHandler<AccountTransVo> {


    private PointsTypeRepository pointsTypeRepository;

    private PointsTransRepository pointsTransRepository;

    private PointsAccountInfoRepository pointsAccountInfoRepository;

    private MerchantRepository merchantRepository;

    private PointsCostRepository pointsCostRepository;

    private static final SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS);

    public AccountTransHandler(@Autowired ApplicationContext applicationContext,
                               @Autowired CacheRepository cacheRepository,
                               @Autowired PointsTypeRepository pointsTypeRepository,
                               @Autowired PointsTransRepository pointsTransRepository,
                               @Autowired PointsAccountInfoRepository pointsAccountInfoRepository,
                               @Autowired MerchantRepository merchantRepository,
                               @Autowired PointsCostRepository pointsCostRepository) {
        super(applicationContext, cacheRepository);
        this.pointsTypeRepository = pointsTypeRepository;
        this.pointsTransRepository = pointsTransRepository;
        this.pointsAccountInfoRepository = pointsAccountInfoRepository;
        this.merchantRepository = merchantRepository;
        this.pointsCostRepository = pointsCostRepository;
    }

    @Override
    protected void event(AccountTransVo vo) {
        PointsTransDTO pointsTrans = vo.getPointsTrans();
        applicationContext.publishEvent(new AsyncTransAccountEvent(this, pointsTrans.getCustomerId(), pointsTrans.getPointsTypeNo(), pointsTrans.getTransNo()));
    }

    @Override
    protected void failCallBack(AccountTransVo vo) throws PointsException {
        this.pointsAccount(vo);
        PointsTransDTO pointsTrans = vo.getPointsTrans();
        pointsTrans.setTransStatus(TransStatus.FAILED.getCode());
        pointsTrans.setDescription(pointsTrans.getDescription() + "【处理结果：" + vo.getMessage() + "】");
        pointsTransRepository.processTrans(pointsTrans, null);
    }

    @Override
    protected void successCallBack(AccountTransVo vo) throws PointsException {
        PointsTransDTO pointsTrans = vo.getPointsTrans();
        PointsAccTransOutput output = vo.getOutput();
        pointsTransRepository.processTrans(pointsTrans, null);
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(TimeUnit.MILLISECONDS.toSeconds(pointsTrans.getTransTimestamp()), 0, Constants.ZONE_OFFSET);
        output.setTransDate(localDateTime.format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS)));
        output.setTransNo(pointsTrans.getTransNo());
        PointsAccountInfoDTO pointsAccountInfo = pointsAccountInfoRepository.queryPointsAccountInfoByType(vo.getCustomer().getCustomerId(), vo.getPointsType().getPointsTypeNo());
        output.setPointsBalance(pointsAccountInfo.getPointsBalance());
        vo.setRetCode(PointsCode.TRANS_0000);
    }

    @Override
    protected boolean check(AccountTransVo vo) {
        PointsAccTransInput input = vo.getInput();
        //检查积分类型
        PointsTypeDTO pointsType = pointsTypeRepository.queryPointsTypeByInst(input.getPoints().getPointsTypeNo(), vo.getInstitution().getInstitutionId());
        if (Objects.isNull(pointsType)) {
            vo.setRetCode(PointsCode.TRANS_2005, input.getPoints().getPointsTypeNo());
            return false;
        }
        vo.setPointsType(pointsType);

        //检查交易流水重复
        PointsTransDTO pointsTrans = pointsTransRepository.existsPointsBySysTransNo(input.getSysTransNo());
        if (pointsTrans != null) {
            vo.setRetCode(PointsCode.TRANS_2003);
            failCalled(vo);
            return false;
        }

        PointsAccountInfoDTO pointsAccountInfo = pointsAccountInfoRepository.queryPointsAccountInfoByType(vo.getCustomer().getCustomerId(), pointsType.getPointsTypeNo());
        if (!AccountStatus.AVAILABLE.getStatus().equalsIgnoreCase(pointsAccountInfo.getPointsAccountStatus())) {
            vo.setRetCode(PointsCode.TRANS_2023);
            failCalled(vo);
            return false;
        }

        if (DebitOrCredit.DEBIT.equals(input.getDebitOrCredit())) {
            //借出交易检查可用余额
            if (pointsAccountInfo.getPointsBalance().subtract(pointsAccountInfo.getFreezingPoints()).compareTo(input.getPoints().getPointsAmount()) < 0) {
                vo.setRetCode(PointsCode.TRANS_2010);
                failCalled(vo);
                return false;
            }
            if (Objects.isNull(input.getMerchantNo())) {
                vo.setRetCode(PointsCode.TRANS_0047, "merchantNo");
                return false;
            }

            MerchantDTO merchant = merchantRepository.queryMerchantByMerchantNo(input.getMerchantNo(), input.getCustomerInfo().getInstitutionId());
            if (Objects.isNull(merchant) || !MerchantStatus.AVAILABLE.getStatus().equalsIgnoreCase(merchant.getStatus())) {
                vo.setRetCode(PointsCode.TRANS_2004, input.getMerchantNo());
                return false;
            }
        } else if (DebitOrCredit.CREDIT.equals(input.getDebitOrCredit())) {
            //贷入交易必须要有成本数据
            if (StringUtils.isEmpty(input.getCostLine())) {
                vo.setRetCode(PointsCode.TRANS_0047, "costLine");
                return false;
            } else {
                //校验成本机构
                PointsCostDTO pointsCostDTO = pointsCostRepository.queryPointsCostById(input.getCostLine(), vo.getInstitution().getInstitutionId());
                if (Objects.isNull(pointsCostDTO)) {
                    vo.setRetCode(PointsCode.TRANS_1102);
                    return false;
                }
            }
            //贷入交易必须要过期时间判定
            if (StringUtils.isEmpty(input.getPoints().getEndDate())) {
                vo.setRetCode(PointsCode.TRANS_0047, "endDate");
                return false;
            } else {
                try {
                    Date endDate = formatter.parse(input.getPoints().getEndDate());
                    if (endDate.compareTo(new Date()) < 1) {
                        vo.setRetCode(PointsCode.TRANS_0047, "endDate 小于当前时间");
                        return false;
                    }
                    vo.setEndDate(endDate);
                } catch (ParseException e) {
                    vo.setRetCode(PointsCode.TRANS_0047, "endDate[" + input.getPoints().getEndDate() + "] 格式应该为：" + Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void initLock(AccountTransVo vo) {
        this.lock(vo, String.join(Constants.DELIMITER, Constants.REDIS_POINTS_TRANS_ACCOUNT_KEY, vo.getCustomer().getCustomerId(), vo.getInput().getPoints().getPointsTypeNo()));
        if (StringUtils.isNotBlank(vo.getInput().getSysTransNo())) {
            this.lock(vo, String.join(Constants.DELIMITER, Constants.REDIS_POINTS_TRANS_SYS_TRANS_NO_KEY, vo.getInput().getSysTransNo()));
        }
    }

    @Override
    protected void pointsAccount(AccountTransVo vo) {
        LocalDateTime localDateTime = LocalDateTime.now();
        PointsAccTransInput input = vo.getInput();

        PointsTransDTO pointsTrans = new PointsTransDTO();
        pointsTrans.setTransNo(Sequence.getTransNo());
        pointsTrans.setOldTransNo(pointsTrans.getTransNo());
        pointsTrans.setCustomerId(vo.getCustomer().getCustomerId());
        pointsTrans.setPointsTypeNo(input.getPoints().getPointsTypeNo());
        pointsTrans.setInstitutionId(vo.getInstitution().getInstitutionId());
        pointsTrans.setTransDate(localDateTime.format(DateTimeFormatter.BASIC_ISO_DATE));
        pointsTrans.setTransTime(localDateTime.format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_HHMMSS)));
        pointsTrans.setTransTypeNo(input.getTransType().getCode());
        pointsTrans.setDebitOrCredit(input.getDebitOrCredit().getCode());
        pointsTrans.setPointsAmount(input.getPoints().getPointsAmount());
        pointsTrans.setReversedFlag(ReversedFlag.NOT_REVERSED.getCode());
        pointsTrans.setTransChannel(Constants.DEFAULT_SYSTEM_TRANS_CHANNEL);
        pointsTrans.setVoucherTypeNo(input.getVoucher().getVoucherType().getType());
        pointsTrans.setVoucherNo(input.getVoucher().getVoucherNo());
        pointsTrans.setTransStatus(TransStatus.SUCCESS.getCode());
        pointsTrans.setOperator(StringUtils.isBlank(input.getOperator()) ? Constants.DEFAULT_OPERATOR_SYSTEM : input.getOperator());
        pointsTrans.setSysTransNo(input.getSysTransNo());

        pointsTrans.setClearingAmt(BigDecimal.ZERO);
        pointsTrans.setDescription(StringUtils.isEmpty(input.getDescription()) ? input.getTransType().getDesc() : input.getDescription());
        pointsTrans.setTransTimestamp(localDateTime.toInstant(Constants.ZONE_OFFSET).toEpochMilli());

        if (DebitOrCredit.DEBIT.equals(input.getDebitOrCredit())) {
            pointsTrans.setMerchantNo(input.getMerchantNo());
        } else if (DebitOrCredit.CREDIT.equals(input.getDebitOrCredit())) {
            pointsTrans.setEndDate(vo.getEndDate());
            pointsTrans.setCostLine(input.getCostLine());
        }
        vo.setPointsTrans(pointsTrans);
    }

    @Override
    protected void businessAccount(AccountTransVo vo) {
        PointsTransDTO pointsTrans = vo.getPointsTrans();
        if (TransStatus.SUCCESS.getCode().equals(pointsTrans.getTransStatus()) && Objects.nonNull(pointsTrans.getMerchantNo())) {
            ClearingList clearingList = ConverterConstants.MERCHANT_CONVERTER.toClearingList(pointsTrans);
            clearingList.setPointsAmount(DebitOrCredit.CREDIT.getCode().equals(pointsTrans.getDebitOrCredit()) ? clearingList.getPointsAmount() : clearingList.getPointsAmount().negate());
            applicationContext.publishEvent(new AsyncTransBusinessEvent(this, clearingList));
        }
    }

}
