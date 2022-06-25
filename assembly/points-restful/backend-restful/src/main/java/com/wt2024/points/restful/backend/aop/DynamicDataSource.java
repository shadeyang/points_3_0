package com.wt2024.points.restful.backend.aop;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/7 15:34
 * @project points3.0:com.wt2024.points.restful.backend.aop
 */
public enum DynamicDataSource {
    MASTER("master", null),
    SLAVE("slave", null),
    ACCOUNT("account", Pattern.compile("^com.wt2024.points.repository.account")),
    CUSTOMER("customer", Pattern.compile("^com.wt2024.points.repository.customer")),
    SYSTEM("system", Pattern.compile("^com.wt2024.points.repository.system")),
    MERCHANT("merchant",Pattern.compile("^com.wt2024.points.repository.merchant")),
    ;

    private String source;
    private Pattern pattern;

    DynamicDataSource(String source, Pattern pattern) {
        this.source = source;
        this.pattern = pattern;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public static DynamicDataSource chose(String classPath) {
        return Arrays.stream(DynamicDataSource.values())
                .filter(dynamicDataSource -> !dynamicDataSource.equals(MASTER) && !dynamicDataSource.equals(SLAVE) && dynamicDataSource.getPattern().matcher(classPath).find())
                .findFirst().orElse(null);
    }
}
