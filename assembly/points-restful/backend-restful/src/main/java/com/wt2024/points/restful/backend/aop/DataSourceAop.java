package com.wt2024.points.restful.backend.aop;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/7 15:29
 * @project points3.0:com.wt2024.points.restful.backend.aop
 */
@Aspect
@ConditionalOnProperty(
        prefix = DynamicDataSourceProperties.PREFIX,
        name = "test",
        havingValue = "false",
        matchIfMissing = true
)
@Component
@Order(0)
@Lazy(false)
@Slf4j
public class DataSourceAop {

    @Pointcut("execution(* com.wt2024.points.repository..*RepositoryImpl.*(..))")
    public void checkArgs() {
    }

    // 这里切到你的方法目录
    @Before("checkArgs()")
    public void process(JoinPoint joinPoint) throws NoSuchMethodException, SecurityException {
        String methodName = joinPoint.getSignature().getName();
        Class clazz = joinPoint.getTarget().getClass();
        if (clazz.isAnnotationPresent(DS.class)) {
            //获取类上注解
            return;
        }
        Class[] parameterTypes =
                ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
        Method method = clazz.getMethod(methodName, parameterTypes);
        if (method.isAnnotationPresent(DS.class)) {
            return;
        }
        String targetName = clazz.getName();
        DynamicDataSource dataSource = DynamicDataSource.chose(targetName);
        if (dataSource == null) {
            dataSource = DynamicDataSource.SYSTEM;
        }
        log.debug("{}当前执行的库：{}", targetName, dataSource.getSource());
        DynamicDataSourceContextHolder.push(dataSource.getSource());
    }

    @After("checkArgs()")
    public void afterAdvice() {
        DynamicDataSourceContextHolder.clear();
    }
}
