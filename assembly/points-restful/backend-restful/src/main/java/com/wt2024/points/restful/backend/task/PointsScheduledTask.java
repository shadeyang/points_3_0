package com.wt2024.points.restful.backend.task;

import com.wt2024.points.core.api.domain.account.PointsAccountExpireInput;
import com.wt2024.points.core.api.service.TransactionService;
import com.wt2024.points.core.constant.Constants;
import com.wt2024.points.repository.api.cache.repository.CacheRepository;
import com.wt2024.points.restful.backend.constant.LocalConstants;
import com.wt2024.points.restful.backend.domain.SysInfo;
import com.wt2024.points.restful.backend.service.SysInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName ScheduledTask
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/5/7
 * @Version V1.0
 **/
@Component
@EnableScheduling
@Slf4j
public class PointsScheduledTask {

    @Autowired
    private SysInfoService sysInfoService;

    @Autowired
    private CacheRepository cacheRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ThreadPoolTaskExecutor expireExecutor;

    @PostConstruct
    @Scheduled(cron = "0 0/10 * * * ?")
    public void syncSysInfo() {
        StopWatch sw = new StopWatch();
        sw.start();
        log.debug("开始同步商户接入验证数据");
        List<SysInfo> bankSysInfoDomainList = sysInfoService.queryAllSysInfo();
        Map<String, SysInfo> temp = bankSysInfoDomainList.stream().collect(Collectors.toMap(SysInfo::getInstitutionNo, sysInfoDomain -> sysInfoDomain));
        LocalConstants.sysInfoDomainHashMap = temp;
        sw.stop();
        log.info("同步接入数据结束，耗时{}ms", sw.getTotalTimeMillis());
    }

    @Scheduled(cron = "0 * * * * ?")
    public void expirePoints() {
        String expireKey = Constants.REDIS_POINTS_TRANS_ACCOUNT_EXPIRE_KEY;
        LocalDateTime stopDateTime = LocalDateTime.now().plusHours(1);
        if (!cacheRepository.lock(expireKey, 1, TimeUnit.HOURS)) {
            log.debug("过期积分处理加锁失败");
            return;
        }
        StopWatch sw = new StopWatch("过期积分处理");
        try {
            Long startId = -1L;
            while (stopDateTime.compareTo(LocalDateTime.now()) >= 0) {
                sw.start("开始读取 startId= " + startId + " 的过期数据");
                List<PointsAccountExpireInput> expireDTOList = transactionService.queryPointsAccountExpireList(startId);
                if (expireDTOList.isEmpty()) {
                    sw.stop();
                    break;
                }

                try {
                    final CountDownLatch latch = new CountDownLatch(expireDTOList.size());
                    for (PointsAccountExpireInput trans : expireDTOList) {
                        expireExecutor.execute(() -> {
                            try {
                                //设定默认值，通过valid验证，没有任何实际意义
                                trans.setInstitutionNo("0");
                                transactionService.eventPointsAccountExpire(trans);
                            } catch (Exception e) {
                                log.error("event expire method error ", e);
                            } finally {
                                latch.countDown();
                            }
                        });
                        if (startId < trans.getId()) {
                            startId = trans.getId();
                        }
                    }
                    latch.await();
                } catch (InterruptedException e) {
                    log.error("expire error ", e);
                    Thread.currentThread().interrupt();
                }
                sw.stop();
            }
            if (log.isDebugEnabled()) {
                log.debug(sw.prettyPrint());
            }
        } finally {
            cacheRepository.unLock(expireKey);
        }
        log.info("过期积分处理结束，耗时{}ms", sw.getTotalTimeMillis());
    }

}
