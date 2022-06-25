package com.wt2024.points.restful.backend.common.provider;

import org.apiguardian.api.API;
import org.junit.jupiter.params.converter.ConvertWith;

import java.lang.annotation.*;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "5.0")
@ConvertWith(FileArgumentConverter.class)
public @interface FileArgumentConversion {
    String resource() default "";
    String alias() default "";
}
