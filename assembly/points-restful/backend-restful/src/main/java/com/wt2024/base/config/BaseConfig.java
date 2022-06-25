package com.wt2024.base.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * @ClassName BaseInitConfig
 * @Description: TODO
 * @Author shade.yang
 * @Date 2020/6/12
 * @Version V1.0
 **/
@Component
public class BaseConfig {

    public static Logger logger = LoggerFactory.getLogger(BaseConfig.class);

    public static String machine ;

    public static String moduleName;

    @Bean
    protected static String getMachine() {
        try {
            machine = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            logger.warn("IP地址获取失败" + e.toString(),e);
        }
        return machine;
    }

    @Value("${spring.application.module}")
    public void setModuleName(String moduleName) {
        BaseConfig.moduleName = moduleName;
    }


}
