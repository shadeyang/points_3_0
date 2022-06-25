package com.wt2024.points.dubbo.backend;

import com.wt2024.points.dubbo.backend.zookeeper.EmbeddedZooKeeper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.wt2024.points", "com.wt2024.base"})
@Slf4j
public class ProviderLauncher implements WebMvcConfigurer {

    private static boolean startEmbeddedZooKeeper;

    private static EmbeddedZooKeeper zooKeeper;

    public static void main(String[] args) {
        startEmbeddedZooKeeper = Boolean.valueOf(System.getProperty("embedded.zookeeper.enable"));
        if (startEmbeddedZooKeeper) {
            log.info("开启内置zookeeper");
            zooKeeper = new EmbeddedZooKeeper(2181, false);
            zooKeeper.start();
        } else {
            log.warn("使用外置zookeeper");
        }
        SpringApplication.run(ProviderLauncher.class, args);
    }

}
