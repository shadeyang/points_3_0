package com.wt2024.points.core.service;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.core.api.domain.valid.CustomerInfoValidResult;
import com.wt2024.points.repository.api.customer.domain.CustomerDTO;
import com.wt2024.points.repository.api.customer.repository.CustomerRepository;
import com.wt2024.points.repository.api.system.domain.InstitutionDTO;
import com.wt2024.points.repository.api.system.repository.InstitutionRepository;
import com.wt2024.points.repository.api.voucher.domain.VoucherDTO;
import com.wt2024.points.repository.api.voucher.repository.VoucherRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AggregationServiceImplTest {

    @InjectMocks
    AggregationServiceImpl aggregationService;
    @Mock
    CustomerRepository customerRepository;
    @Mock
    VoucherRepository voucherRepository;
    @Mock
    InstitutionRepository institutionRepository;

    @BeforeAll
    static void beforeAll() {
        MockitoAnnotations.openMocks(AggregationServiceImpl.class);
    }

    @Test
    void checkInputCustomerInfo_institution_not_exists() {
        String voucherNo = "voucher_no";
        String institutionNo = "institution_no";
        when(institutionRepository.queryTopInstitution(eq(institutionNo))).thenReturn(null);
        PointsException exception = assertThrows(PointsException.class, () -> aggregationService.checkInputCustomerInfo(VoucherType.CUST, voucherNo, institutionNo));
        assertEquals(PointsCode.TRANS_1101, exception.getPointsCode());
    }

    @Test
    void checkInputCustomerInfo_voucher_not_exists() {
        String voucherNo = "voucher_no";
        String institutionNo = "institution_no";
        when(institutionRepository.queryTopInstitution(eq(institutionNo))).thenReturn(new InstitutionDTO());
        when(voucherRepository.queryVoucher(eq(VoucherType.IDENTITY.getType()), eq(voucherNo))).thenReturn(null);
        PointsException exception = assertThrows(PointsException.class, () -> aggregationService.checkInputCustomerInfo(VoucherType.IDENTITY, voucherNo, institutionNo));
        assertEquals(PointsCode.TRANS_1003, exception.getPointsCode());
    }

    @Test
    void checkInputCustomerInfo_customer_not_exists() {
        String voucherNo = "voucher_no";
        String institutionNo = "institution_no";
        when(institutionRepository.queryTopInstitution(eq(institutionNo))).thenReturn(new InstitutionDTO());
        when(customerRepository.queryCustomerById(eq(voucherNo))).thenReturn(null);
        PointsException exception = assertThrows(PointsException.class, () -> aggregationService.checkInputCustomerInfo(VoucherType.CUST, voucherNo, institutionNo));
        assertEquals(PointsCode.TRANS_1001, exception.getPointsCode());
    }

    @Test
    void checkInputCustomerInfo_success() {
        String voucherNo = "voucher_no";
        VoucherType voucherType = VoucherType.IDENTITY;
        String institutionNo = "institution_no";
        String customerId = "customer_id";
        when(institutionRepository.queryTopInstitution(eq(institutionNo))).thenReturn(new InstitutionDTO());
        when(voucherRepository.queryVoucher(eq(voucherType.getType()), eq(voucherNo))).thenReturn(new VoucherDTO(){{
            setCustomerId(customerId);
            setVoucherNo(voucherNo);
            setVoucherTypeNo(voucherType.getType());
        }});
        when(customerRepository.queryCustomerById(eq(customerId))).thenReturn(new CustomerDTO(){{
            setCustomerId(customerId);
        }});
        CustomerInfoValidResult customerInfoValidResult = aggregationService.checkInputCustomerInfo(voucherType, voucherNo, institutionNo);
        assertNotNull(customerInfoValidResult);
        assertEquals(customerId,customerInfoValidResult.getCustomerId());
    }
}