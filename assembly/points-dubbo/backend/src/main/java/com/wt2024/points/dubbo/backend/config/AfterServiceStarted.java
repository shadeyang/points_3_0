package com.wt2024.points.dubbo.backend.config;

import com.wt2024.base.entity.SysMachine;
import com.wt2024.base.service.ISysMachineService;
import com.wt2024.points.dubbo.backend.constant.Sequence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/9/5 22:54
 * @Project rightsplat:com.uxunchina.rightsplat.config
 */
@Component
@Slf4j
public class AfterServiceStarted implements ApplicationRunner {

    @Autowired
    ISysMachineService sysMachineService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        SysMachine machine = sysMachineService.queryAndSaveServerSysMachine();
        Sequence.workerId = machine.getId();
        log.info("Welcome!Service Startup Completed. WorkerId ===={} ", Sequence.workerId);

    }
}
