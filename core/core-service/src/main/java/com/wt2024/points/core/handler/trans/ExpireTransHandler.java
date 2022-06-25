package com.wt2024.points.core.handler.trans;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.*;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.common.sequence.Sequence;
import com.wt2024.points.core.constant.Constants;
import com.wt2024.points.core.handler.trans.vo.ExpireTransVo;
import com.wt2024.points.core.listener.event.AsyncTransAccountEvent;
import com.wt2024.points.repository.api.account.domain.PointsDetailsBalanceDTO;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import com.wt2024.points.repository.api.account.repository.PointsExpireRepository;
import com.wt2024.points.repository.api.account.repository.PointsTransRepository;
import com.wt2024.points.repository.api.cache.repository.CacheRepository;
import com.wt2024.points.repository.api.customer.domain.CustomerDTO;
import com.wt2024.points.repository.api.customer.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/16 14:29
 * @project points3.0:com.wt2024.points.core.handler.trans
 */
@Component
@Slf4j
public class ExpireTransHandler extends AbstractTransHandler<ExpireTransVo> {

    private PointsExpireRepository pointsExpireRepository;

    private PointsTransRepository pointsTransRepository;

    private CustomerRepository customerRepository;

    private static final String ONLY_DELETE = "only_delete";

    private static final String EXPIRE_DELETE = "expire_delete";

    protected ExpireTransHandler(@Autowired ApplicationContext applicationContext,
                                 @Autowired CacheRepository cacheRepository,
                                 @Autowired PointsExpireRepository pointsExpireRepository,
                                 @Autowired PointsTransRepository pointsTransRepository,
                                 @Autowired CustomerRepository customerRepository) {
        super(applicationContext, cacheRepository);
        this.pointsExpireRepository = pointsExpireRepository;
        this.pointsTransRepository = pointsTransRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    protected void event(ExpireTransVo vo) {
        vo.getExpirePointsTransList().forEach(pointsTrans -> applicationContext.publishEvent(new AsyncTransAccountEvent(this, pointsTrans.getCustomerId(), pointsTrans.getPointsTypeNo(), pointsTrans.getTransNo())));
    }

    @Override
    protected void failCallBack(ExpireTransVo vo) throws PointsException {

    }

    @Override
    protected void successCallBack(ExpireTransVo vo) throws PointsException {
        if (!CollectionUtils.isEmpty(vo.getPointsExpireDelete())) {
            int result = pointsExpireRepository.deletePointsExpireWhenBalanceZero(vo.getCustomerId(), vo.getPointsTypeNo(), vo.getPointsExpireDelete().stream().map(PointsDetailsBalanceDTO::getTransNo).distinct().collect(Collectors.toList()));
            log.debug("清除余额为0的贷入流水{}条", result);
        }
        if (!CollectionUtils.isEmpty(vo.getPointsExpireInfo())) {
            vo.getExpirePointsTransList().forEach(pointsTrans -> {
                try {
                    pointsTransRepository.expireProcessTrans(pointsTrans);
                } catch (Exception e) {
                    log.error("交易流水{}的过期冲销处理异常", pointsTrans.getOldTransNo(), e);
                }
            });
            log.debug("需要处理的过期积分流水{}条", vo.getExpirePointsTransList().size());
        }
        vo.setRetCode(PointsCode.TRANS_0000);
    }

    @Override
    protected void initLock(ExpireTransVo vo) {
        this.lock(vo,String.join(Constants.DELIMITER, Constants.REDIS_POINTS_TRANS_ACCOUNT_KEY, vo.getInput().getCustomerId(), vo.getInput().getPointsTypeNo()));
    }

    @Override
    protected boolean check(ExpireTransVo vo) {
        String customerId = vo.getCustomerId();
        String pointsTypeNo = vo.getPointsTypeNo();
        //将用户机构作为默认交易机构
        CustomerDTO customerDTO = customerRepository.queryCustomerById(customerId);
        if (Objects.isNull(customerDTO)) {
            vo.setRetCode(PointsCode.TRANS_1001);
            return false;
        }
        vo.setInstitutionId(customerDTO.getInstitutionId());

        //判断是否存在异步处理未完成账务
        boolean existsAccountingTrans = pointsTransRepository.existsAccountingTrans(customerId, pointsTypeNo, null, true);
        if (existsAccountingTrans) {
            vo.setRetCode(PointsCode.TRANS_2014);
            return false;
        }

        //查找当前用户的过期流水
        List<String> transNoList = pointsExpireRepository.queryPointsExpireByCustomerIdAndPointsTypeNo(customerId, pointsTypeNo);
        if (transNoList.isEmpty()) {
            vo.setRetCode(PointsCode.TRANS_2017);
            return false;
        }
        vo.setTransNoList(transNoList);
        //查找流水使用余额情况
        Map<String, List<PointsDetailsBalanceDTO>> divideList = pointsTransRepository.queryPointsDetailsBalanceByTransNo(customerId, pointsTypeNo, transNoList).stream().collect(Collectors.groupingBy(balanceDTO -> BigDecimal.ZERO.compareTo(balanceDTO.getPointsAmount()) == 0 ? ONLY_DELETE : EXPIRE_DELETE));
        vo.setPointsExpireDelete(divideList.get(ONLY_DELETE));
        vo.setPointsExpireInfo(divideList.get(EXPIRE_DELETE));
        return true;
    }

    @Override
    protected void pointsAccount(ExpireTransVo vo) {
        LocalDateTime localDateTime = LocalDateTime.now();
        if (!CollectionUtils.isEmpty(vo.getPointsExpireInfo())) {
            vo.setExpirePointsTransList(vo.getPointsExpireInfo().stream().map(pointsDetailsBalanceDTO -> createExpirePointsTrans(vo, localDateTime, pointsDetailsBalanceDTO)).collect(Collectors.toList()));
        }
    }

    private PointsTransDTO createExpirePointsTrans(ExpireTransVo vo, LocalDateTime localDateTime, PointsDetailsBalanceDTO pointsDetailsBalanceDTO) {
        PointsTransDTO expirePointsTrans = new PointsTransDTO();
        expirePointsTrans.setTransNo(Sequence.getTransNo());
        expirePointsTrans.setOldTransNo(pointsDetailsBalanceDTO.getTransNo());
        expirePointsTrans.setCustomerId(vo.getCustomerId());
        expirePointsTrans.setPointsTypeNo(vo.getPointsTypeNo());
        expirePointsTrans.setInstitutionId(vo.getInstitutionId());
        expirePointsTrans.setTransDate(localDateTime.format(DateTimeFormatter.BASIC_ISO_DATE));
        expirePointsTrans.setTransTime(localDateTime.format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_HHMMSS)));
        expirePointsTrans.setTransTypeNo(TransType.DUE_POINT.getCode());
        expirePointsTrans.setDebitOrCredit(DebitOrCredit.DEBIT.getCode());
        expirePointsTrans.setPointsAmount(pointsDetailsBalanceDTO.getPointsAmount());
        expirePointsTrans.setReversedFlag(ReversedFlag.NOT_REVERSED.getCode());
        expirePointsTrans.setTransChannel(Constants.DEFAULT_SYSTEM_TRANS_CHANNEL);
        expirePointsTrans.setVoucherTypeNo(VoucherType.CUST.getType());
        expirePointsTrans.setVoucherNo(vo.getCustomerId());
        expirePointsTrans.setTransStatus(TransStatus.SUCCESS.getCode());
        expirePointsTrans.setOperator(Constants.DEFAULT_OPERATOR_SYSTEM);
        expirePointsTrans.setSysTransNo(String.valueOf(Sequence.getId()));

        expirePointsTrans.setClearingAmt(BigDecimal.ZERO);
        expirePointsTrans.setDescription("原贷入流水" + pointsDetailsBalanceDTO.getTransNo() + "剩余" + pointsDetailsBalanceDTO.getPointsAmount() + "积分的过期冲销");
        expirePointsTrans.setTransTimestamp(localDateTime.toInstant(Constants.ZONE_OFFSET).toEpochMilli());
        return expirePointsTrans;
    }

    @Override
    protected void businessAccount(ExpireTransVo vo) {

    }
}
