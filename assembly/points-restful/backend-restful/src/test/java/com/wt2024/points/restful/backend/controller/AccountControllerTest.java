package com.wt2024.points.restful.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.core.api.domain.account.*;
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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/31 10:44
 * @project points3.0:com.wt2024.points.restful.backend.controller
 */
@AutoConfigureMockMvc
class AccountControllerTest extends SpringBootContextTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void queryAllAccountInfo_points_accounts_not_exists() throws Exception {
        AccountInfoQueryAllInput accountInfoQueryAllInput = new AccountInfoQueryAllInput(){{
           setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
           setVoucherNo("2101011990d3d7053X");
           setVoucherType(VoucherType.IDENTITY);
        }};
        mockMvc.perform(
                MockMvcRequestBuilders.post(RestPathConstants.ACCOUNT.PATH + RestPathConstants.ACCOUNT.QUERY_ALL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountInfoQueryAllInput))
                        .headers(createHttpHeaders(accountInfoQueryAllInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult<AccountInfoQueryAllOutput> responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0000.getCode(),responseResult.getCode());
                    Assertions.assertTrue(CollectionUtils.isEmpty(responseResult.getData().getPointsAccountInfoList()));
                });
    }

    @Test
    void queryAllAccountInfo_points_accounts_exists() throws Exception {
        AccountInfoQueryAllInput accountInfoQueryAllInput = new AccountInfoQueryAllInput(){{
            setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
            setVoucherNo("844703788571820032");
            setVoucherType(VoucherType.CUST);
        }};
        mockMvc.perform(
                        MockMvcRequestBuilders.post(RestPathConstants.ACCOUNT.PATH + RestPathConstants.ACCOUNT.QUERY_ALL_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(accountInfoQueryAllInput))
                                .headers(createHttpHeaders(accountInfoQueryAllInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult<AccountInfoQueryAllOutput> responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0000.getCode(),responseResult.getCode());
                    Assertions.assertTrue(responseResult.getData().getPointsAccountInfoList().size()==2);
                });
    }

    @Test
    void queryAccountInfoByPointsType_points_accounts_not_exists() throws Exception {
        AccountInfoQueryInput accountInfoQueryInput = new AccountInfoQueryInput(){{
            setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
            setVoucherNo("1101011990d3d7053X");
            setVoucherType(VoucherType.IDENTITY);
            setPointsTypeNo("844700918006939648");
        }};
        mockMvc.perform(
                        MockMvcRequestBuilders.post(RestPathConstants.ACCOUNT.PATH + RestPathConstants.ACCOUNT.QUERY_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(accountInfoQueryInput))
                                .headers(createHttpHeaders(accountInfoQueryInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult<AccountInfoQueryOutput> responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0000.getCode(),responseResult.getCode());
                });
    }

    @Test
    void queryAccountDetails_points_accounts_exists() throws Exception {
        AccountDetailsQueryInput accountDetailsQueryInput = new AccountDetailsQueryInput(){{
            setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
            setVoucherType(VoucherType.CUST);
            setVoucherNo("844703788571820032");
            setPointsTypeNo("844700918006939648");
        }};

        mockMvc.perform(
                        MockMvcRequestBuilders.post(RestPathConstants.ACCOUNT.PATH + RestPathConstants.ACCOUNT.QUERY_DETAIL_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(accountDetailsQueryInput))
                                .headers(createHttpHeaders(accountDetailsQueryInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult<AccountDetailsQueryOutput> responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0000.getCode(),responseResult.getCode());
                    Assertions.assertNotNull(responseResult.getData().getPointsAccountDetailsList());
                    Assertions.assertNotNull(responseResult.getData().getPointsTransList());
                    Assertions.assertEquals(new BigDecimal("100.00"), responseResult.getData().getPointsAccountDetailsList().get(0).getPointsAmount().add(responseResult.getData().getPointsTransList().get(0).getPoints().getPointsAmount()));
                });

    }


}