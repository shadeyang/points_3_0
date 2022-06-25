package com.wt2024.points.core.api.validation.annotation;

import java.lang.annotation.*;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/26 15:09
 * @project points3.0:com.wt2024.points.core.api.validation.annotation
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface OutputField {
    String field();
}
