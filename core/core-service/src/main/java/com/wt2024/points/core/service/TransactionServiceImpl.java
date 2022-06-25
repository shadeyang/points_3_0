package com.wt2024.points.core.service;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.TransStatus;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.core.api.domain.account.PointsAccountExpireInput;
import com.wt2024.points.core.api.domain.trans.*;
import com.wt2024.points.core.api.service.TransactionService;
import com.wt2024.points.core.constant.Constants;
import com.wt2024.points.core.converter.ConverterConstants;
import com.wt2024.points.core.handler.trans.*;
import com.wt2024.points.core.handler.trans.vo.*;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import com.wt2024.points.repository.api.account.repository.PointsExpireRepository;
import com.wt2024.points.repository.api.account.repository.PointsTransRepository;
import com.wt2024.points.repository.api.cache.repository.CacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/25 16:47
 * @project points3.0:com.wt2024.points.core.service
 */
@Service
@Validated
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private PointsTransRepository pointsTransRepository;

    private PointsExpireRepository pointsExpireRepository;

    private ConsumeTransHandler consumeTransHandler;

    private AccountTransHandler accountTransHandler;

    private ReverseTransHandler reverseTransHandler;

    private ExpireTransHandler expireTransHandler;

    private BackTransHandler backTransHandler;

    private CacheRepository cacheRepository;

    @Override
    public PointsTransQueryListOutput queryPointsTransList(PointsTransQueryListInput pointsTransQueryListInput) {
        List<PointsTransDTO> pointsTransList = pointsTransRepository.queryPointsTransByPage(pointsTransQueryListInput.getCustomerInfo().getCustomerId(),
                pointsTransQueryListInput.getCustomerInfo().getInstitutionId(),
                pointsTransQueryListInput.getPointsTypeNo(),
                pointsTransQueryListInput.getFromId(),
                pointsTransQueryListInput.getFromDate(),
                pointsTransQueryListInput.getToDate(),
                pointsTransQueryListInput.getIndex(),
                pointsTransQueryListInput.getPageSize());
        return PointsTransQueryListOutput.builder().pointsTransList(pointsTransList.stream().map(ConverterConstants.POINTS_TRANS_CONVERTER::toPointsTrans).collect(Collectors.toList())).build();
    }

    @Override
    public PointsConsumeOutput consumePoints(PointsConsumeInput pointsConsumeInput) {
        TransHandlerFactory<ConsumeTransVo> consumeTransVoTransHandlerFactory = TransHandlerFactory.factory(consumeTransHandler);
        return consumeTransVoTransHandlerFactory.execute(new ConsumeTransVo(pointsConsumeInput)).getOutput();
    }

    @Override
    public PointsAccTransOutput accTransPoints(PointsAccTransInput pointsAccTransInput) {
        TransHandlerFactory<AccountTransVo> accountTransVoTransHandlerFactory = TransHandlerFactory.factory(accountTransHandler);
        return accountTransVoTransHandlerFactory.execute(new AccountTransVo(pointsAccTransInput)).getOutput();
    }

    @Override
    public PointsQueryStateOutput queryState(PointsQueryStateInput pointsQueryStateInput) {
        PointsTransDTO pointsTrans = pointsTransRepository.existsPointsBySysTransNo(pointsQueryStateInput.getSysTransNo());
        if (Objects.isNull(pointsTrans)) {
            throw new PointsException(PointsCode.TRANS_2008);
        }
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(TimeUnit.MILLISECONDS.toSeconds(pointsTrans.getTransTimestamp()), 0, Constants.ZONE_OFFSET);

        return PointsQueryStateOutput.builder()
                .transNo(pointsTrans.getTransNo())
                .transDate(localDateTime.format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS)))
                .transStatus(TransStatus.getEnum(pointsTrans.getTransStatus()))
                .build();
    }

    @Override
    public PointsReverseOutput reversePoints(PointsReverseInput pointsReverseInput) {
        TransHandlerFactory<ReverseTransVo> reverseTransVoTransHandlerFactory = TransHandlerFactory.factory(reverseTransHandler);
        return reverseTransVoTransHandlerFactory.execute(new ReverseTransVo(pointsReverseInput)).getOutput();
    }

    @Override
    public PointsBackOutput backPoints(PointsBackInput pointsBackInput) {
        TransHandlerFactory<BackTransVo> backTransVoTransHandlerFactory = TransHandlerFactory.factory(backTransHandler);
        return backTransVoTransHandlerFactory.execute(new BackTransVo(pointsBackInput)).getOutput();
    }

    @Override
    @Async("asyncTransExecutor")
    public void eventAsyncTrans(String customerId, String pointsTypeNo) {
        String accountLockKey = String.join(Constants.DELIMITER, Constants.REDIS_ASYNC_HANDLE_ACCOUNT_LOCK_KEY, customerId, pointsTypeNo);
        try {
            if (!cacheRepository.lock(accountLockKey, 10, TimeUnit.MINUTES)) {
                log.warn("异步处理帐户加锁失败 customerId = {}, pointsTypeNo = {}", customerId, pointsTypeNo);
                return;
            }
            Long startIndex = -1L;
            Integer pageSize = 100;
            List<PointsTransDTO> transTempList;
            while (pageSize > 0) {
                transTempList = pointsTransRepository.queryAllWantToAsync(customerId, pointsTypeNo, startIndex, pageSize);
                if (transTempList.isEmpty()) {
                    break;
                }
                for (PointsTransDTO transDTO : transTempList) {
                    String cacheKey = String.join(Constants.DELIMITER, Constants.REDIS_ASYNC_HANDLE_POINTS_TRANS_LOCK_KEY, String.valueOf(transDTO.getId()));
                    try {
                        if (!cacheRepository.lock(cacheKey, 10, TimeUnit.MINUTES)) {
                            log.info("异步处理加锁失败id = {}", transDTO.getId());
                            continue;
                        }
                        pointsTransRepository.processAsyncTrans(transDTO);
                    } catch (Exception e) {
                        log.info("异步处理异常，id = {}", transDTO.getId(), e);
                    } finally {
                        cacheRepository.unLock(cacheKey);
                    }
                    startIndex = transDTO.getId();
                }
                TimeUnit.MILLISECONDS.sleep(1L);
            }
        } catch (Exception e) {
            log.warn("异步处理异常", e);
        } finally {
            cacheRepository.unLock(accountLockKey);
        }
    }

    @Override
    public void eventDeleteTransTemp() {
        String deleteLockKey = Constants.REDIS_DELETE_POINTS_TRANS_TEMP_LOCK_KEY;
        if (!cacheRepository.lock(deleteLockKey, 10, TimeUnit.MINUTES)) {
            log.warn("删除中间临时数据加锁失败");
            return;
        }
        try {
            int deleteHasDone = pointsTransRepository.deletePointsTempHasDone();
            log.debug("删除" + deleteHasDone + "条处理数据");
        }catch (Exception e) {
            log.warn("删除中间临时数据异常", e);
        } finally {
            cacheRepository.unLock(deleteLockKey);
        }
    }

    @Override
    public List<PointsAccountExpireInput> queryPointsAccountExpireList(Long startId) {
        return pointsExpireRepository.queryCustomerByExpire(startId,0,1000).stream().map(ConverterConstants.POINTS_ACCOUNT_INFO_CONVERTER::toPointsAccountExpire).collect(Collectors.toList());
    }

    @Override
    public void eventPointsAccountExpire(PointsAccountExpireInput pointsAccountExpireInput) {
        TransHandlerFactory.factory(expireTransHandler).execute(new ExpireTransVo(pointsAccountExpireInput)).getOutput();
    }

    @Autowired
    public void setPointsTransRepository(PointsTransRepository pointsTransRepository) {
        this.pointsTransRepository = pointsTransRepository;
    }

    @Autowired
    public void setConsumeTransHandler(ConsumeTransHandler consumeTransHandler) {
        this.consumeTransHandler = consumeTransHandler;
    }

    @Autowired
    public void setAccountTransHandler(AccountTransHandler accountTransHandler) {
        this.accountTransHandler = accountTransHandler;
    }

    @Autowired
    public void setReverseTransHandler(ReverseTransHandler reverseTransHandler) {
        this.reverseTransHandler = reverseTransHandler;
    }

    @Autowired
    public void setCacheRepository(CacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    @Autowired
    public void setExpireTransHandler(ExpireTransHandler expireTransHandler) {
        this.expireTransHandler = expireTransHandler;
    }

    @Autowired
    public void setPointsExpireRepository(PointsExpireRepository pointsExpireRepository) {
        this.pointsExpireRepository = pointsExpireRepository;
    }

    @Autowired
    public void setBackTransHandler(BackTransHandler backTransHandler) {
        this.backTransHandler = backTransHandler;
    }
}
