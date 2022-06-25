package com.wt2024.points.dubbo.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/9/19 16:02
 * @Project rightsplat:com.uxunchina.rightsplat.config
 */
@Component
@Slf4j
public class ApplicationConfigure implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("启动加载完成。。。");
    }
}
