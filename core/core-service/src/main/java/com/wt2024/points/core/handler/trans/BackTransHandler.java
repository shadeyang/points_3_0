package com.wt2024.points.core.handler.trans;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.DebitOrCredit;
import com.wt2024.points.common.enums.ReversedFlag;
import com.wt2024.points.common.enums.TransStatus;
import com.wt2024.points.common.enums.TransType;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.common.sequence.Sequence;
import com.wt2024.points.core.api.domain.merchant.ClearingList;
import com.wt2024.points.core.api.domain.trans.PointsBackInput;
import com.wt2024.points.core.constant.Constants;
import com.wt2024.points.core.converter.ConverterConstants;
import com.wt2024.points.core.handler.trans.vo.BackTransVo;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/5/18 16:42
 * @project points3.0:com.wt2024.points.core.handler.trans
 */
@Component
public class BackTransHandler extends AbstractTransHandler<BackTransVo> {

    private PointsTransRepository pointsTransRepository;

    private InstitutionRepository institutionRepository;

    protected BackTransHandler(@Autowired ApplicationContext applicationContext,
                               @Autowired CacheRepository cacheRepository,
                               @Autowired InstitutionRepository institutionRepository,
                               @Autowired PointsTransRepository pointsTransRepository) {
        super(applicationContext, cacheRepository);

        this.institutionRepository = institutionRepository;
        this.pointsTransRepository = pointsTransRepository;
    }

    @Override
    protected void event(BackTransVo vo) {
        PointsTransDTO pointsTrans = vo.getPointsTrans();
        applicationContext.publishEvent(new AsyncTransAccountEvent(this, pointsTrans.getCustomerId(), pointsTrans.getPointsTypeNo(), pointsTrans.getTransNo()));
    }

    @Override
    protected void failCallBack(BackTransVo vo) throws PointsException {
        this.pointsAccount(vo);
        PointsTransDTO pointsTrans = vo.getPointsTrans();
        pointsTrans.setTransStatus(TransStatus.FAILED.getCode());
        pointsTrans.setDescription(pointsTrans.getDescription() + "【处理结果：" + vo.getMessage() + "】");
        pointsTransRepository.processTrans(pointsTrans, null);
    }

    @Override
    protected void successCallBack(BackTransVo vo) throws PointsException {
        pointsTransRepository.processTrans(vo.getPointsTrans(), vo.getExpirePointsTrans());
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(TimeUnit.MILLISECONDS.toSeconds(vo.getPointsTrans().getTransTimestamp()), 0, Constants.ZONE_OFFSET);
        vo.getOutput().setTransDate(localDateTime.format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS)));
        vo.getOutput().setBackTransNo(vo.getPointsTrans().getTransNo());
        vo.setRetCode(PointsCode.TRANS_0000);
    }

    @Override
    protected void initLock(BackTransVo vo) {
        this.lock(vo, String.join(Constants.DELIMITER, Constants.REDIS_POINTS_TRANS_REVERSED_AND_BACK_SYS_TRANS_NO_KEY, vo.getInput().getBackSysTransNo()));
        this.lock(vo, String.join(Constants.DELIMITER, Constants.REDIS_POINTS_TRANS_SYS_TRANS_NO_KEY, vo.getInput().getSysTransNo()));
    }

    @Override
    protected boolean check(BackTransVo vo) {
        PointsBackInput input = vo.getInput();

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

        List<PointsTransDTO> pointsTransList = pointsTransRepository.queryPointsBySysTransNoAndStatus(input.getBackSysTransNo(), TransStatus.SUCCESS.getCode())
                .stream().filter(pointsTrans -> institution.getInstitutionId().equals(pointsTrans.getInstitutionId())).sorted(Comparator.comparing(PointsTransDTO::getId))
                .collect(Collectors.toList());

        if (pointsTransList.isEmpty()) {
            vo.setRetCode(PointsCode.TRANS_2008);
            return false;
        }

        PointsTransDTO oldPointsTrans = pointsTransList.get(0);
        this.lock(vo, String.join(Constants.DELIMITER, Constants.REDIS_POINTS_TRANS_ACCOUNT_KEY, oldPointsTrans.getCustomerId(), oldPointsTrans.getPointsTypeNo()));

        vo.setOldPointsTrans(oldPointsTrans);

        if (pointsTransList.size() > 1) {
            vo.setRetCode(PointsCode.TRANS_2016);
            failCalled(vo);
            return false;
        }

        //检查第一笔是否为消费积分
        if (!DebitOrCredit.DEBIT.getCode().equalsIgnoreCase(oldPointsTrans.getDebitOrCredit()) || !ReversedFlag.NOT_REVERSED.getCode().equals(oldPointsTrans.getReversedFlag())) {
            vo.setRetCode(PointsCode.TRANS_2019);
            failCalled(vo);
            return false;
        }

        //检查第一笔是否为过期冲销
        if (TransType.DUE_POINT.getCode().equals(vo.getOldPointsTrans().getTransTypeNo())) {
            vo.setRetCode(PointsCode.TRANS_2018);
            failCalled(vo);
            return false;
        }

        //判断是否为当月冲正，跨月已归账，不支持退货
        LocalDate transDate = Instant.ofEpochMilli(oldPointsTrans.getTransTimestamp()).atZone(ZoneOffset.systemDefault()).toLocalDate();
        LocalDate reverseTime = vo.getBackDateTime().toLocalDate();
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

        BigDecimal pointAmount = oldPointsTrans.getPointsAmount();
        BigDecimal clearingAmt = oldPointsTrans.getClearingAmt();
        //其他交易流水是否为正常退货流水
        List<PointsTransDTO> backPointsTransList = new ArrayList<>();
        for (int index = 1; index < oldTransNoPointsTransList.size(); index++) {
            PointsTransDTO backPointsTrans = oldTransNoPointsTransList.get(index);
            if (oldPointsTrans.getTransNo().equalsIgnoreCase(backPointsTrans.getTransNo())) {
                continue;
            }
            if (ReversedFlag.REVERSED.getCode().equals(backPointsTrans.getReversedFlag())) {
                vo.setRetCode(PointsCode.TRANS_2011);
                failCalled(vo);
                return false;
            } else if (ReversedFlag.BACKED.getCode().equals(backPointsTrans.getReversedFlag())) {
                pointAmount = pointAmount.subtract(backPointsTrans.getPointsAmount());
                clearingAmt = clearingAmt.add(backPointsTrans.getClearingAmt());
                backPointsTransList.add(backPointsTrans);
            } else {
                if (TransType.DUE_POINT.getCode().equalsIgnoreCase(backPointsTrans.getTransTypeNo())) {
                    continue;
                }
                vo.setRetCode(PointsCode.TRANS_2021);
                failCalled(vo);
                return false;
            }
        }
        vo.setRemainPointAmount(pointAmount);
        vo.setRemainClearingAmt(clearingAmt);
        vo.setBackPointsTransList(backPointsTransList);

        if (pointAmount.compareTo(input.getPointsAmount()) < 0) {
            vo.setRetCode(PointsCode.TRANS_2022);
            failCalled(vo);
            return false;
        }

        return true;
    }

    @Override
    protected void pointsAccount(BackTransVo vo) {
        PointsBackInput input = vo.getInput();
        PointsTransDTO oldPointsTrans = vo.getOldPointsTrans();
        PointsTransDTO pointsTrans = ConverterConstants.POINTS_TRANS_CONVERTER.clone(vo.getOldPointsTrans());

        pointsTrans.setTransNo(Sequence.getTransNo());
        pointsTrans.setTransDate(vo.getBackDateTime().format(DateTimeFormatter.BASIC_ISO_DATE));
        pointsTrans.setTransTime(vo.getBackDateTime().format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_HHMMSS)));
        pointsTrans.setReversedFlag(ReversedFlag.BACKED.getCode());
        pointsTrans.setTransStatus(TransStatus.SUCCESS.getCode());
        pointsTrans.setDebitOrCredit(DebitOrCredit.CREDIT.getCode());
        pointsTrans.setOperator(StringUtils.isBlank(input.getOperator()) ? Constants.DEFAULT_OPERATOR_SYSTEM : input.getOperator());
        pointsTrans.setOldTransNo(vo.getOldPointsTrans().getTransNo());
        pointsTrans.setDescription("原始发起交易流水" + oldPointsTrans.getSysTransNo() + "的退货交易，退回" + formatToNumber(input.getPointsAmount()) + "分");
        pointsTrans.setSysTransNo(input.getSysTransNo());
        pointsTrans.setTransTimestamp(vo.getBackDateTime().toInstant(Constants.ZONE_OFFSET).toEpochMilli());
        pointsTrans.setPointsAmount(input.getPointsAmount());
        if (vo.getRemainPointAmount().compareTo(input.getPointsAmount()) == 0) {
            pointsTrans.setClearingAmt(vo.getRemainClearingAmt().negate());
        } else {
            BigDecimal backClearingAmt = vo.getRemainClearingAmt().multiply(input.getPointsAmount()).divide(vo.getRemainPointAmount(), 2, RoundingMode.DOWN);
            pointsTrans.setClearingAmt(backClearingAmt.negate());
        }
        vo.setPointsTrans(pointsTrans);

        List<String> transNoList = new ArrayList<>();
        transNoList.add(oldPointsTrans.getTransNo());
        vo.getBackPointsTransList().stream().forEach(pointsTransDTO -> transNoList.add(pointsTransDTO.getTransNo()));
        //获取退货交易的剩余可退货明细
        List<PointsTransDetailsDTO> pointsTransDetailsList = pointsTransRepository.queryBackPointsTransDetailsByTransNo(transNoList);

        //退货过期积分情况考虑
        BigDecimal expirePointsAmount = pointsTransDetailsList.stream().map(pointsTransDetails -> {
            LocalDateTime expireDateTime = LocalDateTime.ofInstant(pointsTransDetails.getEndDate().toInstant(), ZoneId.systemDefault());
            return expireDateTime.isBefore(vo.getBackDateTime()) ? pointsTransDetails.getPointsAmount() : BigDecimal.ZERO;
        }).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (expirePointsAmount.compareTo(BigDecimal.ZERO) != 0) {
            PointsTransDTO expirePointsTrans = ConverterConstants.POINTS_TRANS_CONVERTER.clone(pointsTrans);
            expirePointsTrans.setTransNo(Sequence.getTransNo());
            expirePointsTrans.setOldTransNo(pointsTrans.getTransNo());
            expirePointsTrans.setTransTypeNo(TransType.DUE_POINT.getCode());
            expirePointsTrans.setDebitOrCredit(DebitOrCredit.DEBIT.getCode());
            expirePointsTrans.setPointsAmount(expirePointsAmount.add(input.getPointsAmount()).compareTo(BigDecimal.ZERO) > 0 ? expirePointsAmount.negate() : input.getPointsAmount());
            expirePointsTrans.setReversedFlag(ReversedFlag.NOT_REVERSED.getCode());
            expirePointsTrans.setTransStatus(TransStatus.SUCCESS.getCode());
            expirePointsTrans.setClearingAmt(BigDecimal.ZERO);
            expirePointsTrans.setDescription("原始发起交易流水" + oldPointsTrans.getSysTransNo() + "的退货交易，过期" + formatToNumber(expirePointsTrans.getPointsAmount()) + "分");

            vo.setExpirePointsTrans(expirePointsTrans);
        }
    }

    /**
     * @desc 1.0~1之间的BigDecimal小数，格式化后失去前面的0,则前面直接加上0。
     * 2.传入的参数等于0，则直接返回字符串"0.00"
     * 3.大于1的小数，直接格式化返回字符串
     * @param original 传入的小数
     * @return
     */
    public static String formatToNumber(BigDecimal original) {
        DecimalFormat df = new DecimalFormat("#.00");
        if(original.compareTo(BigDecimal.ZERO)==0) {
            return "0.00";
        }else if(original.compareTo(BigDecimal.ZERO)>0&&original.compareTo(new BigDecimal(1))<0){
            return "0"+df.format(original).toString();
        }else if(original.compareTo(BigDecimal.ZERO)<0&&original.compareTo(new BigDecimal(-1))>0){
            df = new DecimalFormat("0.00");
            return df.format(original);
        }else {
            return df.format(original).toString();
        }
    }

    @Override
    protected void businessAccount(BackTransVo vo) {
        PointsTransDTO pointsTrans = vo.getPointsTrans();
        if (TransStatus.SUCCESS.getCode().equals(pointsTrans.getTransStatus()) && Objects.nonNull(pointsTrans.getMerchantNo())) {
            ClearingList clearingList = ConverterConstants.MERCHANT_CONVERTER.toClearingList(pointsTrans);
            applicationContext.publishEvent(new AsyncTransBusinessEvent(this, clearingList));
        }

    }
}
