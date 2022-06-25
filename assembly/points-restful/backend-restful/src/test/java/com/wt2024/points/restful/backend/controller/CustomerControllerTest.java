package com.wt2024.points.restful.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.Gender;
import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.core.api.domain.customer.CustomerCreateInput;
import com.wt2024.points.core.api.domain.customer.CustomerCreateOutput;
import com.wt2024.points.core.api.domain.customer.CustomerInfoInput;
import com.wt2024.points.core.api.domain.customer.CustomerInfoOutput;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import com.wt2024.points.restful.backend.SpringBootContextTest;
import com.wt2024.points.restful.backend.constant.RestPathConstants;
import com.wt2024.points.restful.backend.constant.TestConstants;
import com.wt2024.points.restful.backend.domain.ResponseResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.CollectionUtils;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/25 15:42
 * @project points3.0:com.wt2024.points.restful.backend.controller
 */
@AutoConfigureMockMvc
class CustomerControllerTest extends SpringBootContextTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void queryCustomerInfo() throws Exception {
        CustomerInfoInput customerInfoInput = new CustomerInfoInput() {{
            setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
            setVoucherNo("844703788571820032");
            setVoucherType(VoucherType.CUST);
        }};
        mockMvc.perform(
                        MockMvcRequestBuilders.post(RestPathConstants.CUSTOMER.PATH + RestPathConstants.CUSTOMER.QUERY_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerInfoInput))
                                .headers(createHttpHeaders(customerInfoInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult<CustomerInfoOutput> responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0000.getCode(), responseResult.getCode());
                    CustomerInfoOutput customerInfoOutput = responseResult.getData();
                    Assertions.assertNotNull(customerInfoOutput.getCustomerId());
                    Assertions.assertFalse(CollectionUtils.isEmpty(customerInfoOutput.getPointsAccountInfoList()));
                });
    }

    @Test
    void createCustomer() throws Exception {
        CustomerCreateInput customerCreateInput = new CustomerCreateInput(){{
            setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
            setVoucher(new Voucher(){{
                setVoucherNo("844703788571820032");
                setVoucherType(VoucherType.CUST);
            }});
            setPhoneNumber("18800000000");
            setGender(Gender.FEMALE);
            setCustomerName("测试");
            setEmail("999@666.com");
            setBirthdate("19900506");
            setAddress("地址");
        }};
        mockMvc.perform(
                        MockMvcRequestBuilders.post(RestPathConstants.CUSTOMER.PATH + RestPathConstants.CUSTOMER.CREATE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerCreateInput))
                                .headers(createHttpHeaders(customerCreateInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult<CustomerCreateOutput> responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0000.getCode(), responseResult.getCode());
                    CustomerCreateOutput output = responseResult.getData();
                    Assertions.assertNotNull(output.getCustomerId());
                });
    }
}