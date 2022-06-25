package com.wt2024.points.restful.backend.interceptor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.restful.backend.SpringBootContextTest;
import com.wt2024.points.restful.backend.constant.Constants;
import com.wt2024.points.restful.backend.constant.TestConstants;
import com.wt2024.points.restful.backend.controller.example.domain.TestData;
import com.wt2024.points.restful.backend.controller.example.domain.TestDataValid;
import com.wt2024.points.restful.backend.domain.Index;
import com.wt2024.points.restful.backend.domain.ResponseResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
 * @date 2022/1/1 14:57
 * @project points3.0:com.wt2024.points.restful.backend.interceptor
 */
@AutoConfigureMockMvc
class RestControllerInterceptorTest extends SpringBootContextTest {

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
    }

    @Test
    void test_white_path() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/index")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseResult<Index>>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0002.getCode(), responseResult.getCode());
                });
    }

    @Test
    void test_not_exists_inst() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/test/index")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseResult<Index>>() {
                    });
                    Assertions.assertEquals(PointsCode.COMM_0040.getCode(), responseResult.getCode());
                });
    }

    @Test
    void test_not_setting_inst() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/test/index")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(Constants.CONSTANT_HEADER_INSTITUTION,"not_exists_inst"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseResult<Index>>() {
                    });
                    Assertions.assertEquals(PointsCode.COMM_0044.getCode(), responseResult.getCode());
                });
    }

    @Test
    void test_setting_rsa_success() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/test/index")
                                .contentType(MediaType.APPLICATION_JSON)
                                .headers(createHttpHeaders(null)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseResult<Index>>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0000.getCode(), responseResult.getCode());
                });
    }

    @Test
    void test_request_param() throws Exception {
        TestData testDataDomain = new TestData();
        testDataDomain.setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/test/data/index")
                                .contentType(MediaType.APPLICATION_JSON)
                                .headers(createHttpHeaders(testDataDomain))
                                .content(objectMapper.writeValueAsString(testDataDomain)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseResult<Index>>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0000.getCode(), responseResult.getCode());
                });
    }

    @Test
    void test_request_param_with_valid() throws Exception {
        TestDataValid testDataDomain = new TestDataValid();
        testDataDomain.setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/test/data/valid")
                                .contentType(MediaType.APPLICATION_JSON)
                                .headers(createHttpHeaders(testDataDomain))
                                .content(objectMapper.writeValueAsString(testDataDomain)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseResult<Index>>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0047.getCode(), responseResult.getCode());
                });

    }

    @Test
    void test_setting_ip_success() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/test/index")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(Constants.CONSTANT_HEADER_INSTITUTION,"100001")
                                .header(Constants.CONSTANT_HEADER_TIMESTAMP,System.currentTimeMillis()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseResult<Index>>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0000.getCode(), responseResult.getCode());
                });
    }

    @Test
    void test_setting_ip_forbid() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/test/index")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.CONSTANT_HEADER_INSTITUTION,"100002")
                        .header(Constants.CONSTANT_HEADER_TIMESTAMP,System.currentTimeMillis()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseResult<Index>>() {
                    });
                    Assertions.assertEquals(PointsCode.COMM_0032.getCode(), responseResult.getCode());
                });
    }

    @Test
    void test_setting_path_forbid() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/test/index")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constants.CONSTANT_HEADER_INSTITUTION,"100003")
                        .header(Constants.CONSTANT_HEADER_TIMESTAMP,System.currentTimeMillis()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseResult<Index>>() {
                    });
                    Assertions.assertEquals(PointsCode.COMM_0043.getCode(), responseResult.getCode());
                });
    }

    @Test
    void test_request_throw_exception() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/test/exception")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(createHttpHeaders(null)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseResult<Index>>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0005.getCode(), responseResult.getCode());
                });
    }

    @Test
    void test_request_throw_points_exception() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/test/points/exception")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(createHttpHeaders(null)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseResult<Index>>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_1003.getCode(), responseResult.getCode());
                });
    }
}