package com.wt2024.points.restful.backend.common.provider;

import org.apiguardian.api.API;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.*;

import static org.apiguardian.api.API.Status.STABLE;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@API(status = STABLE, since = "5.0")
@ArgumentsSource(FileArgumentProvider.class)
public @interface FileArgumentSources {

    String[] resources() default {};

    String encoding() default "UTF-8";

    String defaultContent() default "{}";
}
