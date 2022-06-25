package com.wt2024.points.core.service;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.core.api.domain.voucher.AddVoucherInput;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import com.wt2024.points.repository.api.customer.domain.CustomerDTO;
import com.wt2024.points.repository.api.customer.repository.CustomerRepository;
import com.wt2024.points.repository.api.voucher.domain.VoucherDTO;
import com.wt2024.points.repository.api.voucher.repository.VoucherRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/25 14:50
 * @project points3.0:com.wt2024.points.core.service
 */
@ExtendWith(MockitoExtension.class)
class VoucherServiceImplTest {

    @InjectMocks
    VoucherServiceImpl voucherService;
    @Mock
    VoucherRepository voucherRepository;
    @Mock
    CustomerRepository customerRepository;

    @BeforeAll
    static void beforeAll() {
        MockitoAnnotations.openMocks(VoucherServiceImpl.class);
    }

    @Test
    void addVoucher_customer_not_exists() {
        AddVoucherInput addVoucherInput = new AddVoucherInput() {{
            setVoucher(new Voucher() {{
                setVoucherNo("voucher_no");
                setVoucherType(VoucherType.IDENTITY);
                setVoucherOpenDate("20000101");
            }});
            setInstitutionNo("institution_no");
            setCustomerId("customer_id");
        }};
        when(customerRepository.queryCustomerById(eq(addVoucherInput.getCustomerId()))).thenReturn(null);

        PointsException exception = assertThrows(PointsException.class, () -> voucherService.addVoucher(addVoucherInput));
        assertEquals(PointsCode.TRANS_1001, exception.getPointsCode());
    }

    @Test
    void addVoucher_voucher_exists() {
        AddVoucherInput addVoucherInput = new AddVoucherInput() {{
            setVoucher(new Voucher() {{
                setVoucherNo("voucher_no");
                setVoucherType(VoucherType.IDENTITY);
                setVoucherOpenDate("20000101");
            }});
            setInstitutionNo("institution_no");
            setCustomerId("customer_id");
        }};
        CustomerDTO customerDTO = new CustomerDTO() {{
            setCustomerId(addVoucherInput.getCustomerId());
        }};

        when(customerRepository.queryCustomerById(eq(addVoucherInput.getCustomerId()))).thenReturn(customerDTO);
        when(voucherRepository.queryVoucher(eq(addVoucherInput.getVoucher().getVoucherType().getType()), eq(addVoucherInput.getVoucher().getVoucherNo()))).thenReturn(new VoucherDTO());
        PointsException exception = assertThrows(PointsException.class, () -> voucherService.addVoucher(addVoucherInput));
        assertEquals(PointsCode.TRANS_1002, exception.getPointsCode());
    }

    @Test
    void addVoucher_success() {
        AddVoucherInput addVoucherInput = new AddVoucherInput() {{
            setVoucher(new Voucher() {{
                setVoucherNo("voucher_no");
                setVoucherType(VoucherType.IDENTITY);
                setVoucherOpenDate("20000101");
            }});
            setInstitutionNo("institution_no");
            setCustomerId("customer_id");
        }};
        CustomerDTO customerDTO = new CustomerDTO() {{
            setCustomerId(addVoucherInput.getCustomerId());
        }};

        when(customerRepository.queryCustomerById(eq(addVoucherInput.getCustomerId()))).thenReturn(customerDTO);
        when(voucherRepository.queryVoucher(eq(addVoucherInput.getVoucher().getVoucherType().getType()), eq(addVoucherInput.getVoucher().getVoucherNo()))).thenReturn(null);
        ArgumentCaptor<VoucherDTO> voucherDTOArgumentCaptor = ArgumentCaptor.forClass(VoucherDTO.class);
        when(voucherRepository.addVoucher(voucherDTOArgumentCaptor.capture())).thenReturn(1);

        assertDoesNotThrow(() -> voucherService.addVoucher(addVoucherInput));
        VoucherDTO voucherDTO = voucherDTOArgumentCaptor.getValue();
        assertAll(
                () -> assertEquals(addVoucherInput.getVoucher().getVoucherNo(), voucherDTO.getVoucherNo()),
                () -> assertEquals(addVoucherInput.getVoucher().getVoucherType().getType(), voucherDTO.getVoucherTypeNo()),
                () -> assertEquals(addVoucherInput.getVoucher().getVoucherOpenDate(), voucherDTO.getVoucherOpenDate()),
                () -> assertEquals(addVoucherInput.getCustomerId(), voucherDTO.getCustomerId())
        );
    }
}