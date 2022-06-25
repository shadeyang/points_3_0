package com.wt2024.points.core.api.validation;

import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.core.api.domain.InputBase;
import com.wt2024.points.core.api.domain.valid.CustomerInfoValidResult;
import com.wt2024.points.core.api.service.AggregationService;
import com.wt2024.points.core.api.validation.annotation.*;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/16 09:23
 * @project points3.0:com.wt2024.points.core.api.validation
 */
public class CustomerInfoValidator implements ConstraintValidator<CustomerInfoValid, Object> {

    public static final String REGEX_DOT = "\\.";
    private CustomerInfoValid customerInfoValid;

    public static AggregationService aggregationService;

    @Override
    public void initialize(CustomerInfoValid constraintAnnotation) {
        this.customerInfoValid = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        if (object instanceof InputBase) {
            String institutionNo = getInstitutionNo(object);
            String voucherNo = getVoucherNo(object);
            VoucherType voucherType = getVoucherType(object);

            if (validValueIsNull(institutionNo, customerInfoValid.institutionNo(), constraintValidatorContext)
                    || validValueIsNull(voucherNo, customerInfoValid.voucherNo(), constraintValidatorContext)
                    || validValueIsNull(voucherType, customerInfoValid.voucherType(), constraintValidatorContext)) {
                return false;
            }

            CustomerInfoValidResult result = this.getFieldValue(object, getOutputFieldName(object), CustomerInfoValidResult.class);
            if (Objects.isNull(result)) {
                result = aggregationService.checkInputCustomerInfo(voucherType, voucherNo, institutionNo);
                if (Objects.isNull(result)) {
                    return false;
                }
                this.setFieldValue(object, getOutputFieldName(object), result);
            }
            return true;
        } else {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("object must be extends " + InputBase.class.getName()).addConstraintViolation();
            return false;
        }
    }

    private String getOutputFieldName(Object object) {
        OutputField outputField = object.getClass().getAnnotation(OutputField.class);
        return Objects.nonNull(outputField) ? outputField.field() : customerInfoValid.output();
    }

    private VoucherType getVoucherType(Object object) {
        VoucherTypeField voucherTypeField = object.getClass().getAnnotation(VoucherTypeField.class);
        if (Objects.nonNull(voucherTypeField)) {
            return getFieldValue(object, voucherTypeField.field(), VoucherType.class);
        } else {
            return getFieldValue(object, customerInfoValid.voucherType(), VoucherType.class);
        }
    }

    private String getInstitutionNo(Object object) {
        InstitutionNoField institutionNoField = object.getClass().getAnnotation(InstitutionNoField.class);
        if (Objects.nonNull(institutionNoField)) {
            return getFieldValue(object, institutionNoField.field(), String.class);
        } else {
            return getFieldValue(object, customerInfoValid.institutionNo(), String.class);
        }
    }

    private String getVoucherNo(Object object) {
        VoucherNoField voucherNoField = object.getClass().getAnnotation(VoucherNoField.class);
        if (Objects.nonNull(voucherNoField)) {
            return getFieldValue(object, voucherNoField.field(), String.class);
        } else {
            return getFieldValue(object, customerInfoValid.voucherNo(), String.class);
        }
    }

    private boolean validValueIsNull(Object object, String fieldName, ConstraintValidatorContext context) {
        if (Objects.isNull(object)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("field " + fieldName + " not exists or value is empty").addPropertyNode(fieldName).addConstraintViolation();
            return true;
        }
        return false;
    }

    private <T> T getFieldValue(Object object, String fieldName, Class<T> clazz) {
        try {
            String[] name = fieldName.split(REGEX_DOT);
            for (int i = 0; i < name.length; i++) {
                object = getFiled(name[i], object.getClass()).get(object);
            }
            return (T) object;
        } catch (Exception e) {
            return null;
        }
    }

    private <T> void setFieldValue(Object object, String fieldName, T value) {
        if (StringUtils.isBlank(fieldName)) {
            return;
        }
        try {
            String[] name = fieldName.split(REGEX_DOT);
            for (int i = 0; i < name.length - 1; i++) {
                object = getFiled(name[i], object.getClass()).get(object);
            }
            Field field = getFiled(name[name.length - 1], object.getClass());
            field.set(object, value);
        } catch (Exception e) {

        }
    }

    private Field getFiled(String fieldName, Class clazz) {
        String error = null;
        Field field = null;
        while (clazz != null) {
            try {
                field = clazz.getDeclaredField(fieldName);
                error = null;
                break;
            } catch (Exception e) {
                clazz = clazz.getSuperclass();
                error = e.getMessage();
            }
        }
        if (error != null || field == null) {
            throw new RuntimeException("cannot get " + fieldName + " field: " + error);
        }
        field.setAccessible(true);
        return field;
    }
}
