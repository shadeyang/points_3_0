package com.wt2024.points.core.handler.trans;


import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.DebitOrCredit;
import com.wt2024.points.common.enums.ReversedFlag;
import com.wt2024.points.common.enums.TransStatus;
import com.wt2024.points.common.enums.TransType;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.common.sequence.Sequence;
import com.wt2024.points.core.api.domain.merchant.ClearingList;
import com.wt2024.points.core.api.domain.trans.PointsReverseInput;
import com.wt2024.points.core.constant.Constants;
import com.wt2024.points.core.converter.ConverterConstants;
import com.wt2024.points.core.handler.trans.vo.ReverseTransVo;
import com.wt2024.points.core.listener.event.AsyncTransAccountEvent;
import com.wt2024.points.core.listener.event.AsyncTransBusinessEvent;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import com.wt2024.points.repository.api.account.domain.PointsTransDetailsDTO;
import com.wt2024.points.repository.api.account.repository.PointsTransRepository;
import com.wt2024.points.repository.api.cache.repository.CacheRepository;
import com.wt2024.points.repository.api.system.domain.InstitutionDTO;
import com.wt2024.points.repository.api.system.repository.InstitutionRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName ReverseTransHandler
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/8/26
 * @Version V1.0
 **/
@Component
public class ReverseTransHandler extends AbstractTransHandler<ReverseTransVo> {

    private PointsTransRepository pointsTransRepository;

    private InstitutionRepository institutionRepository;

    public ReverseTransHandler(@Autowired ApplicationContext applicationContext,
                               @Autowired CacheRepository cacheRepository,
                               @Autowired InstitutionRepository institutionRepository,
                               @Autowired PointsTransRepository pointsTransRepository) {
        super(applicationContext, cacheRepository);

        this.institutionRepository = institutionRepository;
        this.pointsTransRepository = pointsTransRepository;
    }

    @Override
    protected void event(ReverseTransVo vo) {
        PointsTransDTO pointsTrans = vo.getPointsTrans();
        applicationContext.publishEvent(new AsyncTransAccountEvent(this, pointsTrans.getCustomerId(), pointsTrans.getPointsTypeNo(), pointsTrans.getTransNo()));
    }

    @Override
    protected void failCallBack(ReverseTransVo vo) throws PointsException {
        this.pointsAccount(vo);
        PointsTransDTO pointsTrans = vo.getPointsTrans();
        pointsTrans.setTransStatus(TransStatus.FAILED.getCode());
        pointsTrans.setDescription(pointsTrans.getDescription() + "【处理结果：" + vo.getMessage() + "】");
        pointsTransRepository.processTrans(pointsTrans, null);
    }

    @Override
    protected void successCallBack(ReverseTransVo vo) throws PointsException {
        pointsTransRepository.processTrans(vo.getPointsTrans(), vo.getExpirePointsTrans());
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(TimeUnit.MILLISECONDS.toSeconds(vo.getPointsTrans().getTransTimestamp()), 0, Constants.ZONE_OFFSET);
        vo.getOutput().setTransDate(localDateTime.format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS)));
        vo.getOutput().setReverseTransNo(vo.getPointsTrans().getTransNo());
        vo.setRetCode(PointsCode.TRANS_0000);
    }

    @Override
    protected void initLock(ReverseTransVo vo) {
        this.lock(vo, String.join(Constants.DELIMITER, Constants.REDIS_POINTS_TRANS_REVERSED_AND_BACK_SYS_TRANS_NO_KEY, vo.getInput().getReverseSysTransNo()));
        this.lock(vo, String.join(Constants.DELIMITER, Constants.REDIS_POINTS_TRANS_SYS_TRANS_NO_KEY, vo.getInput().getSysTransNo()));
    }

    @Override
    protected boolean check(ReverseTransVo vo) {
        PointsReverseInput input = vo.getInput();

        InstitutionDTO institution = institutionRepository.queryTopInstitution(input.getInstitutionNo());
        if (Objects.isNull(institution)) {
            vo.setRetCode(PointsCode.TRANS_1101);
            return false;
        }

        //检查交易流水重复
        if (Objects.nonNull(pointsTransRepository.existsPointsBySysTransNo(input.getSysTransNo()))) {
            vo.setRetCode(PointsCode.TRANS_2003);
            failCalled(vo);
            return false;
        }

        List<PointsTransDTO> pointsTransList = pointsTransRepository.queryPointsBySysTransNoAndStatus(input.getReverseSysTransNo(), TransStatus.SUCCESS.getCode())
                .stream().filter(pointsTrans -> institution.getInstitutionId().equals(pointsTrans.getInstitutionId())).sorted(Comparator.comparing(PointsTransDTO::getId))
                .collect(Collectors.toList());

        if (pointsTransList.isEmpty()) {
            vo.setRetCode(PointsCode.TRANS_2008);
            return false;
        } else if (pointsTransList.size() > 1) {
            vo.setRetCode(PointsCode.TRANS_2016);
            vo.setOldPointsTrans(pointsTransList.get(0));
            failCalled(vo);
            return false;
        }

        PointsTransDTO oldPointsTrans = pointsTransList.get(0);
        this.lock(vo, String.join(Constants.DELIMITER, Constants.REDIS_POINTS_TRANS_ACCOUNT_KEY, oldPointsTrans.getCustomerId(), oldPointsTrans.getPointsTypeNo()));
        vo.setOldPointsTrans(oldPointsTrans);

        if (ReversedFlag.REVERSED.getCode().equals(oldPointsTrans.getReversedFlag())) {
            vo.setRetCode(PointsCode.TRANS_2012);
            failCalled(vo);
            return false;
        }

        if (TransType.DUE_POINT.getCode().equals(oldPointsTrans.getTransTypeNo())) {
            vo.setRetCode(PointsCode.TRANS_2018);
            failCalled(vo);
            return false;
        }

        //判断是否为当月冲正，跨月已归账，不支持冲正
        LocalDate transDate = Instant.ofEpochMilli(oldPointsTrans.getTransTimestamp()).atZone(ZoneOffset.systemDefault()).toLocalDate();
        LocalDate reverseTime = vo.getReverseDateTime().toLocalDate();
        vo.setSameDay(transDate.equals(reverseTime));
        if (reverseTime.getYear() != transDate.getYear() || reverseTime.getMonthValue() != transDate.getMonthValue()) {
            vo.setRetCode(PointsCode.TRANS_2013);
            failCalled(vo);
            return false;
        }

        //判断是否存在异步处理未完成账务
        boolean existsAccountingTrans = pointsTransRepository.existsAccountingTrans(oldPointsTrans.getCustomerId(), oldPointsTrans.getPointsTypeNo(), oldPointsTrans.getTransNo(), !vo.isSameDay());
        if (existsAccountingTrans) {
            vo.setRetCode(PointsCode.TRANS_2014);
            failCalled(vo);
            return false;
        }

        List<PointsTransDTO> oldTransNoPointsTransList = pointsTransRepository.queryPointsByOldTransNoAndStatus(oldPointsTrans.getTransNo(), TransStatus.SUCCESS.getCode())
                .stream().sorted(Comparator.comparing(PointsTransDTO::getId)).collect(Collectors.toList());

        if (oldTransNoPointsTransList.stream().anyMatch(pointsTrans -> !ReversedFlag.NOT_REVERSED.getCode().equals(pointsTrans.getReversedFlag()))) {
            vo.setRetCode(PointsCode.TRANS_2011);
            failCalled(vo);
            return false;
        }

        //冲正判断是否存在加分已被使用的情况
        List<PointsTransDetailsDTO> pointsTransDetailsList = pointsTransRepository.queryPointsTransDetailsByTransNo(oldPointsTrans.getTransNo());
        vo.setOldPointsTransDetailsList(pointsTransDetailsList);
        if (DebitOrCredit.CREDIT.getCode().equals(oldPointsTrans.getDebitOrCredit())) {
            List<PointsTransDetailsDTO> pointsTransDetails = pointsTransRepository.queryPointsTransDetailsBySourceTransNo(oldPointsTrans.getTransNo());
            if (!CollectionUtils.isEmpty(pointsTransDetails)) {
                vo.setRetCode(PointsCode.TRANS_2015);
                failCalled(vo);
                return false;
            }
        }

        return true;
    }

    @Override
    protected void pointsAccount(ReverseTransVo vo) {
        PointsReverseInput input = vo.getInput();
        PointsTransDTO oldPointsTrans = vo.getOldPointsTrans();
        PointsTransDTO pointsTrans = ConverterConstants.POINTS_TRANS_CONVERTER.clone(vo.getOldPointsTrans());

        pointsTrans.setTransNo(Sequence.getTransNo());
        pointsTrans.setTransDate(vo.getReverseDateTime().format(DateTimeFormatter.BASIC_ISO_DATE));
        pointsTrans.setTransTime(vo.getReverseDateTime().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_HHMMSS)));
        pointsTrans.setReversedFlag(ReversedFlag.REVERSED.getCode());
        pointsTrans.setTransStatus(TransStatus.SUCCESS.getCode());
        pointsTrans.setOperator(StringUtils.isBlank(input.getOperator()) ? Constants.DEFAULT_OPERATOR_SYSTEM : input.getOperator());
        pointsTrans.setOldTransNo(vo.getOldPointsTrans().getTransNo());
        pointsTrans.setDescription("原始发起交易流水" + oldPointsTrans.getSysTransNo() + "的冲正交易，冲回" + pointsTrans.getPointsAmount() + "分");
        pointsTrans.setSysTransNo(input.getSysTransNo());
        pointsTrans.setTransTimestamp(vo.getReverseDateTime().toInstant(Constants.ZONE_OFFSET).toEpochMilli());
        vo.setPointsTrans(pointsTrans);

        //隔日冲正过期积分情况考虑
        if (DebitOrCredit.DEBIT.getCode().equals(oldPointsTrans.getDebitOrCredit())) {
            BigDecimal expirePointsAmount = vo.getOldPointsTransDetailsList().stream().map(pointsTransDetails -> {
                LocalDateTime expireDateTime = LocalDateTime.ofInstant(pointsTransDetails.getEndDate().toInstant(), ZoneId.systemDefault());
                return expireDateTime.isBefore(vo.getReverseDateTime()) ? pointsTransDetails.getPointsAmount() : BigDecimal.ZERO;
            }).reduce(BigDecimal.ZERO, BigDecimal::add);

            if (expirePointsAmount.compareTo(BigDecimal.ZERO) != 0) {
                PointsTransDTO expirePointsTrans = ConverterConstants.POINTS_TRANS_CONVERTER.clone(pointsTrans);
                expirePointsTrans.setTransNo(Sequence.getTransNo());
                expirePointsTrans.setOldTransNo(pointsTrans.getTransNo());
                expirePointsTrans.setTransTypeNo(TransType.DUE_POINT.getCode());
                expirePointsTrans.setDebitOrCredit(DebitOrCredit.DEBIT.getCode());
                expirePointsTrans.setPointsAmount(expirePointsAmount.negate());
                expirePointsTrans.setReversedFlag(ReversedFlag.NOT_REVERSED.getCode());
                expirePointsTrans.setTransStatus(TransStatus.SUCCESS.getCode());
                expirePointsTrans.setClearingAmt(BigDecimal.ZERO);
                expirePointsTrans.setDescription("原始发起交易流水" + oldPointsTrans.getSysTransNo() + "的冲正交易，过期" + expirePointsTrans.getPointsAmount() + "分");

                vo.setExpirePointsTrans(expirePointsTrans);
            }
        }
    }

    @Override
    protected void businessAccount(ReverseTransVo vo) {
        PointsTransDTO pointsTrans = vo.getPointsTrans();
        if (TransStatus.SUCCESS.getCode().equals(pointsTrans.getTransStatus()) && Objects.nonNull(pointsTrans.getMerchantNo())) {
            ClearingList clearingList = ConverterConstants.MERCHANT_CONVERTER.toClearingList(pointsTrans);
            clearingList.setPointsAmount(DebitOrCredit.DEBIT.getCode().equals(pointsTrans.getDebitOrCredit()) ? clearingList.getPointsAmount() : clearingList.getPointsAmount().negate());
            clearingList.setClearingAmt(DebitOrCredit.DEBIT.getCode().equals(pointsTrans.getDebitOrCredit()) ? clearingList.getClearingAmt().negate() : clearingList.getClearingAmt());
            applicationContext.publishEvent(new AsyncTransBusinessEvent(this, clearingList));
        }
    }
}
