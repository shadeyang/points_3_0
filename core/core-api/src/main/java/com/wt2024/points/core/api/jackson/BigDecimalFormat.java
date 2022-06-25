package com.wt2024.points.core.api.jackson;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/6/20 13:49
 * @project points3.0:com.wt2024.points.core.api.jackson
 */
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = BigDecimalSerializer.class)
@JsonDeserialize(using = BigDecimalDeSerializer.class)
public @interface BigDecimalFormat {
    String value() default "#####0.00";
}
