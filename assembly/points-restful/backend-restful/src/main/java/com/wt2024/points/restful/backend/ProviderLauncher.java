package com.wt2024.points.restful.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author shade.yang
 */
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.wt2024.points", "com.wt2024.base"})
@Slf4j
public class ProviderLauncher implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(ProviderLauncher.class, args);
    }

}
