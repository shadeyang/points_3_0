package com.wt2024.points.core.config;

import com.wt2024.points.core.api.service.AggregationService;
import com.wt2024.points.core.api.validation.CustomerInfoValidator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/16 15:23
 * @project points3.0:com.wt2024.points.core.config
 */
@Configuration
public class ValidConfig implements ApplicationContextAware, InitializingBean {

    private static ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (ValidConfig.applicationContext == null) {
            throw new RuntimeException("ApplicationContext is null! Please check!");
        }
        CustomerInfoValidator.aggregationService = applicationContext.getBean(AggregationService.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ValidConfig.applicationContext = applicationContext;
    }
}
