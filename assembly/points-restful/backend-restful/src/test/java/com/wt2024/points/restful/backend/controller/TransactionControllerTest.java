package com.wt2024.points.restful.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.TransType;
import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.common.sequence.Sequence;
import com.wt2024.points.core.api.domain.trans.*;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import com.wt2024.points.restful.backend.SpringBootContextTest;
import com.wt2024.points.restful.backend.constant.Constants;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/26 10:37
 * @project points3.0:com.wt2024.points.restful.backend.controller
 */
@AutoConfigureMockMvc
class TransactionControllerTest extends SpringBootContextTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void queryPointsTransList_points_trans_not_exists() throws Exception {
        PointsTransQueryListInput pointsTransQueryListInput = new PointsTransQueryListInput() {{
            setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
            setVoucher(new Voucher() {{
                setVoucherNo("844669109487534080");
                setVoucherType(VoucherType.CUST);
            }});
            setPointsTypeNo("844700918006939648");
        }};
        mockMvc.perform(
                        MockMvcRequestBuilders.post(RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.QUERY_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pointsTransQueryListInput))
                                .headers(createHttpHeaders(pointsTransQueryListInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult<PointsTransQueryListOutput> responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0000.getCode(), responseResult.getCode());
                    PointsTransQueryListOutput output = responseResult.getData();
                    Assertions.assertTrue(CollectionUtils.isEmpty(output.getPointsTransList()));
                });
    }

    @Test
    void queryPointsTransList_points_trans_exists() throws Exception {
        PointsTransQueryListInput pointsTransQueryListInput = new PointsTransQueryListInput() {{
            setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
            setVoucher(new Voucher() {{
                setVoucherNo("844703788571820032");
                setVoucherType(VoucherType.CUST);
            }});
            setPointsTypeNo("844700918006939648");
        }};
        mockMvc.perform(
                        MockMvcRequestBuilders.post(RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.QUERY_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pointsTransQueryListInput))
                                .headers(createHttpHeaders(pointsTransQueryListInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult<PointsTransQueryListOutput> responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0000.getCode(), responseResult.getCode());
                    PointsTransQueryListOutput output = responseResult.getData();
                    Assertions.assertFalse(CollectionUtils.isEmpty(output.getPointsTransList()));
                });
    }

    @Test
    void consumePoints_points_not_enough() throws Exception {
        PointsConsumeInput pointsConsumeInput = new PointsConsumeInput() {{
            setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
            setVoucher(new Voucher() {{
                setVoucherNo("844703788571820032");
                setVoucherType(VoucherType.CUST);
            }});
            setSysTransNo(String.valueOf(Sequence.getId()));
            setDescription("消费积分不足测试");
            setOperator(Constants.DEFAULT_OPERATOR_SYSTEM);
            setMerchantNo("merchant");
            setPoints(new Points() {{
                setPointsTypeNo("844700918006939649");
                setPointsAmount(new BigDecimal(10000000000L));

            }});
        }};

        mockMvc.perform(
                        MockMvcRequestBuilders.post(RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.CONSUME_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pointsConsumeInput))
                                .headers(createHttpHeaders(pointsConsumeInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult<PointsConsumeOutput> responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_2010.getCode(), responseResult.getCode());
                });
    }

    @Test
    void consumePoints_success() throws Exception {
        PointsConsumeInput pointsConsumeInput = new PointsConsumeInput() {{
            setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
            setVoucher(new Voucher() {{
                setVoucherNo("844703788571820032");
                setVoucherType(VoucherType.CUST);
            }});
            setSysTransNo(String.valueOf(Sequence.getId()));
            setDescription("消费积分测试");
            setOperator(Constants.DEFAULT_OPERATOR_SYSTEM);
            setMerchantNo("merchant");
            setPoints(new Points() {{
                setPointsTypeNo("844700918006939649");
                setPointsAmount(BigDecimal.ONE);

            }});
        }};

        mockMvc.perform(
                        MockMvcRequestBuilders.post(RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.CONSUME_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pointsConsumeInput))
                                .headers(createHttpHeaders(pointsConsumeInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult<PointsConsumeOutput> responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0000.getCode(), responseResult.getCode());
                    PointsConsumeOutput output = responseResult.getData();
                    assertNotNull(output.getPayTime());
                    assertNotNull(output.getPointsTransNo());
                    assertEquals(pointsConsumeInput.getSysTransNo(), output.getSysTransNo());
                });
    }

    @Test
    void accTransPoints_credit_success() throws Exception {
        PointsAccTransInput pointsAccTransInput = new PointsAccTransInput() {{
            setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
            setVoucher(new Voucher() {{
                setVoucherNo("844703788571820032");
                setVoucherType(VoucherType.CUST);
            }});
            setSysTransNo(String.valueOf(Sequence.getId()));
            setDescription("增加积分");
            setOperator(Constants.DEFAULT_OPERATOR_SYSTEM);
            setMerchantNo("merchant");
            setPoints(new Points() {{
                setPointsTypeNo("844700918006939649");
                setPointsAmount(new BigDecimal(10000000000L));
                setEndDate(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS).format(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault())));
            }});
            setTransType(TransType.GAIN_AWARD_POINT);
            setDebitOrCredit(TransType.GAIN_AWARD_POINT.getDebitOrCredit());
            setCostLine("1");
        }};

        mockMvc.perform(
                        MockMvcRequestBuilders.post(RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.ACC_TRANS_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pointsAccTransInput))
                                .headers(createHttpHeaders(pointsAccTransInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult<PointsAccTransOutput> responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0000.getCode(), responseResult.getCode());
                    PointsAccTransOutput output = responseResult.getData();
                    assertNotNull(output.getTransDate());
                    assertNotNull(output.getTransNo());
                    assertTrue(output.getPointsBalance().compareTo(pointsAccTransInput.getPoints().getPointsAmount()) >= 0);
                });
    }

    @Test
    void accTransPoints_debit_balance_not_enough() throws Exception {
        PointsAccTransInput pointsAccTransInput = new PointsAccTransInput() {{
            setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
            setVoucher(new Voucher() {{
                setVoucherNo("844700917985968128");
                setVoucherType(VoucherType.CUST);
            }});
            setSysTransNo(String.valueOf(Sequence.getId()));
            setDescription("消费积分");
            setOperator(Constants.DEFAULT_OPERATOR_SYSTEM);
            setMerchantNo("merchant");
            setPoints(new Points() {{
                setPointsTypeNo("844700918006939648");
                setPointsAmount(new BigDecimal(10000000000L));
            }});
            setTransType(TransType.CONSUME_POINT);
            setDebitOrCredit(TransType.CONSUME_POINT.getDebitOrCredit());
        }};

        mockMvc.perform(
                        MockMvcRequestBuilders.post(RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.ACC_TRANS_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pointsAccTransInput))
                                .headers(createHttpHeaders(pointsAccTransInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult<PointsAccTransOutput> responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_2010.getCode(), responseResult.getCode());
                });
    }

    @Test
    void accTransPoints_debit_success() throws Exception {
        PointsAccTransInput pointsAccTransInput = new PointsAccTransInput() {{
            setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
            setVoucher(new Voucher() {{
                setVoucherNo("844703788571820032");
                setVoucherType(VoucherType.CUST);
            }});
            setSysTransNo(String.valueOf(Sequence.getId()));
            setDescription("消费积分");
            setOperator(Constants.DEFAULT_OPERATOR_SYSTEM);
            setMerchantNo("merchant");
            setPoints(new Points() {{
                setPointsTypeNo("844700918006939649");
                setPointsAmount(new BigDecimal(1L));
            }});
            setTransType(TransType.CONSUME_POINT);
            setDebitOrCredit(TransType.CONSUME_POINT.getDebitOrCredit());
        }};

        mockMvc.perform(
                        MockMvcRequestBuilders.post(RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.ACC_TRANS_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pointsAccTransInput))
                                .headers(createHttpHeaders(pointsAccTransInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult<PointsAccTransOutput> responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0000.getCode(), responseResult.getCode());
                    PointsAccTransOutput output = responseResult.getData();
                    assertNotNull(output.getTransDate());
                    assertNotNull(output.getTransNo());
                    assertTrue(output.getPointsBalance().compareTo(pointsAccTransInput.getPoints().getPointsAmount()) >= 0);
                });
    }

    @Test
    void queryState_systransno_not_exists() throws Exception {
        PointsQueryStateInput pointsQueryStateInput = new PointsQueryStateInput();
        pointsQueryStateInput.setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
        pointsQueryStateInput.setSysTransNo("not_exists");

        mockMvc.perform(
                        MockMvcRequestBuilders.post(RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.QUERY_STATE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pointsQueryStateInput))
                                .headers(createHttpHeaders(pointsQueryStateInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult<PointsQueryStateOutput> responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_2008.getCode(), responseResult.getCode());
                });
    }

    @Test
    void queryState_success() throws Exception {
        PointsQueryStateInput pointsQueryStateInput = new PointsQueryStateInput();
        pointsQueryStateInput.setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
        pointsQueryStateInput.setSysTransNo("880527837759537152");

        mockMvc.perform(
                        MockMvcRequestBuilders.post(RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.QUERY_STATE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pointsQueryStateInput))
                                .headers(createHttpHeaders(pointsQueryStateInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult<PointsQueryStateOutput> responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_0000.getCode(), responseResult.getCode());
                    PointsQueryStateOutput output = responseResult.getData();
                    Assertions.assertNotNull(output.getTransDate());
                    Assertions.assertNotNull(output.getTransNo());
                    Assertions.assertNotNull(output.getTransStatus());
                });
    }

    @Test
    void reversePoints_with_month() throws Exception {
        PointsReverseInput pointsReverseInput = new PointsReverseInput();
        pointsReverseInput.setInstitutionNo(TestConstants.DEFAULT_INSTITUTION_NO);
        pointsReverseInput.setSysTransNo(Sequence.getId().toString());
        pointsReverseInput.setReverseSysTransNo("880527837759537152");
        pointsReverseInput.setOperator("撤销操作员");

        mockMvc.perform(
                        MockMvcRequestBuilders.post(RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.REVERSE_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(pointsReverseInput))
                                .headers(createHttpHeaders(pointsReverseInput)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    ResponseResult<PointsReverseOutput> responseResult = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertEquals(PointsCode.TRANS_2013.getCode(), responseResult.getCode());
                });
    }
}