package com.wt2024.points.dubbo.backend.task;

import com.wt2024.points.dubbo.backend.constant.LocalConstants;
import com.wt2024.points.dubbo.backend.domain.SysInfo;
import com.wt2024.points.dubbo.backend.service.SysInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
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
    public SysInfoService sysInfoService;

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
}
