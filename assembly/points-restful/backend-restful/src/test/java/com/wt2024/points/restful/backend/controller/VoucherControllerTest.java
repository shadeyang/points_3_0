package com.wt2024.points.restful.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.core.api.domain.voucher.AddVoucherInput;
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

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/25 16:27
 * @project points3.0:com.wt2024.points.restful.backend.controller
 */
@AutoConfigureMockMvc
class VoucherControllerTest extends SpringBootContextTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void addVoucher() throws Exception {
        AddVoucherInput addVoucherInput = new AddVoucherInput() {{
            setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
            setCustomerId("844697684374716416");
            setVoucher(new Voucher() {{
                setVoucherNo("123456");
                setVoucherType(VoucherType.THIRDNO);
                setVoucherOpenDate("20201010");
            }});
        }};
        mockMvc.perform(
                        MockMvcRequestBuilders.post(RestPathConstants.VOUCHER.PATH + RestPathConstants.VOUCHER.ADD_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(addVoucherInput))
                                .headers(createHttpHeaders(addVoucherInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0000.getCode(), responseResult.getCode());
                });
    }
}