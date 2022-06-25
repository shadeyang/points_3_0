package com.wt2024.points.core.api.validation.annotation;

import com.wt2024.points.core.api.validation.CustomerInfoValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/16 09:24
 * @project points3.0:com.wt2024.points.core.api.validation.annotation
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomerInfoValidator.class)
public @interface CustomerInfoValid {

    String message() default "customer info valid failure";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String voucherNo() default "voucherNo";

    String voucherType() default "voucherType";

    String institutionNo() default "institutionNo";

    String output() default "";
}
