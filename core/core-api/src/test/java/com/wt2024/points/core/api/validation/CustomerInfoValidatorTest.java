package com.wt2024.points.core.api.validation;

import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.core.api.domain.valid.CustomerInfoValidResult;
import com.wt2024.points.core.api.service.AggregationService;
import com.wt2024.points.core.api.validation.object.customerInfo.CustomerInfoValidRenameField;
import com.wt2024.points.core.api.validation.object.customerInfo.CustomerInfoValidSuccess;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/16 09:47
 * @project points3.0:com.wt2024.points.core.api.validation
 */
class CustomerInfoValidatorTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeAll
    static void beforeAll() {
        CustomerInfoValidator.aggregationService = mock(AggregationService.class);
    }

    @Test
    void customerInfoValidSuccess() {
        CustomerInfoValidSuccess obj = CustomerInfoValidSuccess.builder().voucherNo("voucherNo").voucherType(VoucherType.CUST).build();
        obj.setInstitutionNo("institutionNo");

        when(CustomerInfoValidator.aggregationService.checkInputCustomerInfo(eq(obj.getVoucherType()), eq(obj.getVoucherNo()), eq(obj.getInstitutionNo())))
                .thenReturn(new CustomerInfoValidResult() {{
                    setCustomer(new Customer());
                    setInstitution(new Institution());
                }});

        Set<ConstraintViolation<CustomerInfoValidSuccess>> result = validator.validate(obj);

        assertTrue(result.isEmpty());
        assertNotNull(obj.getObject());
    }

    @Test
    void customerInfoValidSuccess_when_result_exists() {
        CustomerInfoValidSuccess obj = CustomerInfoValidSuccess.builder().voucherNo("voucherNo_verify_never").voucherType(VoucherType.CUST).build();
        obj.setInstitutionNo("institutionNo_verify_never");
        obj.setObject(new CustomerInfoValidResult() {{
            setCustomer(new Customer(){{
                setCustomerId("customerId_verify_never");
            }});
            setInstitution(new Institution());
        }});

        Set<ConstraintViolation<CustomerInfoValidSuccess>> result = validator.validate(obj);
        assertTrue(result.isEmpty());
        verify(CustomerInfoValidator.aggregationService,never()).checkInputCustomerInfo(eq(obj.getVoucherType()), eq(obj.getVoucherNo()), eq(obj.getInstitutionNo()));

        assertNotNull(obj.getObject());
    }

    @Test
    void customerInfoValidVoucherNoIsEmpty() {
        CustomerInfoValidSuccess obj = CustomerInfoValidSuccess.builder().voucherType(VoucherType.CUST).build();
        obj.setInstitutionNo("institutionNo");

        when(CustomerInfoValidator.aggregationService.checkInputCustomerInfo(eq(obj.getVoucherType()), eq(obj.getVoucherNo()), eq(obj.getInstitutionNo())))
                .thenReturn(new CustomerInfoValidResult() {{
                    setCustomer(new Customer());
                    setInstitution(new Institution());
                }});

        Set<ConstraintViolation<CustomerInfoValidSuccess>> result = validator.validate(obj);

        result.forEach(System.out::println);
        assertFalse(result.isEmpty());
        assertEquals("field voucherNo not exists or value is empty", result.stream().findFirst().get().getMessage());
        assertNull(obj.getObject());
    }

    @Test
    void customerInfoValidVoucherTypeIsEmpty() {
        CustomerInfoValidSuccess obj = CustomerInfoValidSuccess.builder().voucherNo("voucherNo").build();
        obj.setInstitutionNo("institutionNo");

        when(CustomerInfoValidator.aggregationService.checkInputCustomerInfo(eq(obj.getVoucherType()), eq(obj.getVoucherNo()), eq(obj.getInstitutionNo())))
                .thenReturn(new CustomerInfoValidResult() {{
                    setCustomer(new Customer());
                    setInstitution(new Institution());
                }});

        Set<ConstraintViolation<CustomerInfoValidSuccess>> result = validator.validate(obj);

        result.forEach(System.out::println);
        assertFalse(result.isEmpty());
        assertEquals("field voucherType not exists or value is empty", result.stream().findFirst().get().getMessage());
        assertNull(obj.getObject());
    }

    @Test
    void customerInfoValidRenameField() {
        CustomerInfoValidRenameField obj = CustomerInfoValidRenameField.builder().voucherNo("voucherNo").type(VoucherType.CUST).build();
        obj.setInstitutionNo("institutionNo");

        when(CustomerInfoValidator.aggregationService.checkInputCustomerInfo(eq(obj.getType()), eq(obj.getVoucherNo()), eq(obj.getInstitutionNo())))
                .thenReturn(new CustomerInfoValidResult() {{
                    setCustomer(new Customer());
                    setInstitution(new Institution());
                }});

        Set<ConstraintViolation<CustomerInfoValidRenameField>> result = validator.validate(obj);

        assertTrue(result.isEmpty());
        assertNull(obj.getObject());
    }
}