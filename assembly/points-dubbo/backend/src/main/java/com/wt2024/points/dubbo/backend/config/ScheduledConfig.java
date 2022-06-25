package com.wt2024.points.dubbo.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newScheduledThreadPool;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2019-01-09 12:22
 * @Project openapi:com.uxunchina.openapi.config
 */
@Configuration
public class ScheduledConfig implements SchedulingConfigurer {


    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(setTaskExecutors());
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService setTaskExecutors() {
        return newScheduledThreadPool(3); // 3个线程来处理。
    }
}
