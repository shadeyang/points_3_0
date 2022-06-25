package com.wt2024.points.core.service;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.Gender;
import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.core.api.domain.customer.CustomerCreateInput;
import com.wt2024.points.core.api.domain.customer.CustomerCreateOutput;
import com.wt2024.points.core.api.domain.customer.CustomerInfoInput;
import com.wt2024.points.core.api.domain.customer.CustomerInfoOutput;
import com.wt2024.points.core.api.domain.valid.CustomerInfoValidResult;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import com.wt2024.points.repository.api.account.domain.PointsAccountInfoDTO;
import com.wt2024.points.repository.api.account.repository.PointsAccountInfoRepository;
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

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/25 10:58
 * @project points3.0:com.wt2024.points.core.service
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @InjectMocks
    CustomerServiceImpl customerService;
    @Mock
    PointsAccountInfoRepository pointsAccountInfoRepository;
    @Mock
    InstitutionRepository institutionRepository;
    @Mock
    VoucherRepository voucherRepository;
    @Mock
    CustomerRepository customerRepository;

    @BeforeAll
    static void beforeAll() {
        MockitoAnnotations.openMocks(CustomerServiceImpl.class);
    }

    @Test
    void queryCustomerInfo() {
        CustomerInfoInput customerInfoInput = new CustomerInfoInput() {{
            setPointsTypeNo("points_type_no");
            setInstitutionNo("institution_no");
            setCustomerInfo(new CustomerInfoValidResult(
                    new CustomerInfoValidResult.Institution() {{
                        setInstitutionId("institution_id");
                    }},
                    new CustomerInfoValidResult.Customer() {{
                        setCustomerId("customer_id");
                    }}));
        }};

        when(pointsAccountInfoRepository.queryPointsAccountInfo(
                eq(customerInfoInput.getCustomerInfo().getCustomerId()),
                eq(customerInfoInput.getPointsTypeNo()),
                eq(customerInfoInput.getCustomerInfo().getInstitutionId())))
                .thenReturn(new ArrayList<>() {{
                    add(new PointsAccountInfoDTO() {{
                        setCustomerId(customerInfoInput.getCustomerInfo().getCustomerId());
                    }});
                }});
        CustomerInfoOutput customerInfoOutput = customerService.queryCustomerInfo(customerInfoInput);
        assertNotNull(customerInfoOutput);
        assertEquals(customerInfoInput.getCustomerInfo().getCustomerId(), customerInfoOutput.getCustomerId());
    }

    @Test
    void createCustomer_institution_not_exists() {
        CustomerCreateInput customerCreateInput = new CustomerCreateInput() {{
            setInstitutionNo("institution_no");
            setAddress("address");
            setCustomerName("customers_name");
            setBirthdate("19991231");
            setEmail("email");
            setGender(Gender.FEMALE);
            setPhoneNumber("13512345678");
            setVoucher(new Voucher() {{
                setVoucherNo("voucher_no");
                setVoucherOpenDate("20200101");
                setVoucherType(VoucherType.IDENTITY);
            }});
        }};
        when(institutionRepository.queryTopInstitution(eq(customerCreateInput.getInstitutionNo()))).thenReturn(null);
        PointsException exception = assertThrows(PointsException.class, () -> customerService.createCustomer(customerCreateInput));
        assertEquals(PointsCode.TRANS_1101, exception.getPointsCode());
    }

    @Test
    void createCustomer_voucher_exists() {
        CustomerCreateInput customerCreateInput = new CustomerCreateInput() {{
            setInstitutionNo("institution_no");
            setAddress("address");
            setCustomerName("customers_name");
            setBirthdate("19991231");
            setEmail("email");
            setGender(Gender.FEMALE);
            setPhoneNumber("13512345678");
            setVoucher(new Voucher() {{
                setVoucherNo("voucher_no");
                setVoucherOpenDate("20200101");
                setVoucherType(VoucherType.IDENTITY);
            }});
        }};

        InstitutionDTO institutionDTO = new InstitutionDTO() {{
            setInstitutionId("inst_id");
            setInstitutionNo(customerCreateInput.getInstitutionNo());
            setInstitutionName("inst_name");
            setDescription("desc");
            setParentInstitutionId("parent_inst_id");
            setTopInstitutionId("top_inst_id");
        }};

        VoucherDTO voucherDTO = new VoucherDTO() {{
            setCustomerId("customer_id");
            setVoucherNo(customerCreateInput.getVoucher().getVoucherNo());
            setVoucherOpenDate(customerCreateInput.getVoucher().getVoucherOpenDate());
            setVoucherTypeNo(customerCreateInput.getVoucher().getVoucherType().getType());
        }};

        when(institutionRepository.queryTopInstitution(eq(customerCreateInput.getInstitutionNo()))).thenReturn(institutionDTO);
        when(voucherRepository.queryVoucher(eq(customerCreateInput.getVoucher().getVoucherType().getType()), eq(customerCreateInput.getVoucher().getVoucherNo()))).thenReturn(voucherDTO);

        CustomerCreateOutput customerCreateOutput = customerService.createCustomer(customerCreateInput);
        assertEquals(voucherDTO.getCustomerId(), customerCreateOutput.getCustomerId());
    }

    @Test
    void createCustomer() {
        CustomerCreateInput customerCreateInput = new CustomerCreateInput() {{
            setInstitutionNo("institution_no");
            setAddress("address");
            setCustomerName("customers_name");
            setBirthdate("19991231");
            setEmail("email");
            setGender(Gender.FEMALE);
            setPhoneNumber("13512345678");
            setVoucher(new Voucher() {{
                setVoucherNo("voucher_no");
                setVoucherOpenDate("20200101");
                setVoucherType(VoucherType.IDENTITY);
            }});
        }};

        InstitutionDTO institutionDTO = new InstitutionDTO() {{
            setInstitutionId("inst_id");
            setInstitutionNo(customerCreateInput.getInstitutionNo());
            setInstitutionName("inst_name");
            setDescription("desc");
            setParentInstitutionId("parent_inst_id");
            setTopInstitutionId("top_inst_id");
        }};

        CustomerDTO customerDTO = new CustomerDTO() {{
            setCustomerId("customer_id");
        }};

        when(institutionRepository.queryTopInstitution(eq(customerCreateInput.getInstitutionNo()))).thenReturn(institutionDTO);
        when(voucherRepository.queryVoucher(eq(customerCreateInput.getVoucher().getVoucherType().getType()), eq(customerCreateInput.getVoucher().getVoucherNo()))).thenReturn(null);
        when(customerRepository.createCustomer(any(CustomerDTO.class), any(VoucherDTO.class))).thenReturn(customerDTO);

        CustomerCreateOutput customerCreateOutput = customerService.createCustomer(customerCreateInput);
        assertEquals(customerDTO.getCustomerId(), customerCreateOutput.getCustomerId());
    }
}