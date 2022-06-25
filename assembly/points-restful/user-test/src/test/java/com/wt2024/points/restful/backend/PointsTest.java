package com.wt2024.points.restful.backend;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.*;
import com.wt2024.points.common.sequence.Sequence;
import com.wt2024.points.core.api.domain.account.*;
import com.wt2024.points.core.api.domain.customer.CustomerCreateInput;
import com.wt2024.points.core.api.domain.customer.CustomerCreateOutput;
import com.wt2024.points.core.api.domain.trans.*;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import com.wt2024.points.restful.backend.common.provider.FileArgumentConversion;
import com.wt2024.points.restful.backend.common.provider.FileArgumentSources;
import com.wt2024.points.restful.backend.constant.Constants;
import com.wt2024.points.restful.backend.constant.RestPathConstants;
import com.wt2024.points.restful.backend.domain.ResponseResult;
import com.wt2024.points.restful.backend.tools.generator.*;
import org.junit.jupiter.params.ParameterizedTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/9 17:08
 * @project points3.0:com.wt2024.points.restful.backend
 */
public class PointsTest extends BaseTest {

    @ParameterizedTest
    @FileArgumentSources
    void consumePoints_balance_not_enough(@FileArgumentConversion CustomerCreateInput customerCreateInput,
                                          @FileArgumentConversion AccountInfoQueryAllInput accountInfoQueryAllInput,
                                          @FileArgumentConversion PointsConsumeInput pointsConsumeInput,
                                          @FileArgumentConversion PointsQueryStateInput pointsQueryStateInput) throws Exception {
        setStep("第一步确认客户");
        String customerId = sureCustomer(customerCreateInput);
        assertNotNull(customerId);

        setStep("第二步查询客户积分");
        AccountInfoQueryAllOutput accountInfoQueryAllOutput = queryAccountInfo(customerCreateInput, accountInfoQueryAllInput);
        assertNotNull(accountInfoQueryAllOutput);

        setStep("第三步消费客户积分");
        pointsConsumeInput.setPoints(new Points() {{
            setPointsTypeNo(Constants.DEFAULT_POINTS_TYPE_NO);
            PointsAccountInfo pointsAccountInfo = accountInfoQueryAllOutput.getPointsAccountInfoList().stream().filter(accountInfo -> Constants.DEFAULT_POINTS_TYPE_NO.equals(accountInfo.getPointsTypeNo())).findFirst().orElse(null);
            setPointsAmount((pointsAccountInfo == null ? BigDecimal.ZERO : pointsAccountInfo.getPointsBalance()).add(BigDecimal.ONE));
        }});
        pointsConsumeInput.setInstitutionNo(customerCreateInput.getInstitutionNo());
        pointsConsumeInput.setVoucher(customerCreateInput.getVoucher());
        pointsConsumeInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsConsumeInput.setDescription("消费积分不足测试");
        pointsConsumeInput.setOperator(Constants.DEFAULT_OPERATOR_SYSTEM);
        pointsConsumeInput.setMerchantNo("merchant");

        ResponseResult<PointsConsumeOutput> consume_result = HttpPostWithJson.httpPostWithJson(pointsConsumeInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.CONSUME_PATH, PointsConsumeOutput.class);
        assertEquals(PointsCode.TRANS_2010.getCode(), consume_result.getCode());

        setStep("第四步查询消费记录");
        PointsQueryStateOutput pointsQueryStateOutput = queryState(pointsQueryStateInput, customerCreateInput.getInstitutionNo(), pointsConsumeInput.getSysTransNo());
        assertNotNull(pointsQueryStateOutput);
        assertNotNull(pointsQueryStateOutput.getTransNo());
        assertNotNull(pointsQueryStateOutput.getTransDate());
        assertEquals(TransStatus.FAILED, pointsQueryStateOutput.getTransStatus());
    }

    @ParameterizedTest
    @FileArgumentSources
    void consumePoints_success(@FileArgumentConversion CustomerCreateInput customerCreateInput,
                               @FileArgumentConversion AccountInfoQueryInput accountInfoQueryInput,
                               @FileArgumentConversion PointsAccTransInput pointsAccTransInput,
                               @FileArgumentConversion PointsConsumeInput pointsConsumeInput,
                               @FileArgumentConversion PointsQueryStateInput pointsQueryStateInput,
                               @FileArgumentConversion PointsTransQueryListInput pointsTransQueryListInput) throws Exception {
        setStep("第一步确认客户");
        String customerId = sureCustomer(customerCreateInput);
        assertNotNull(customerId);

        setStep("第二步查询客户指定积分");
        AccountInfoQueryOutput accountInfoQueryOutput = querySingleAccountInfo(customerCreateInput, accountInfoQueryInput);
        assertNotNull(accountInfoQueryOutput);

        setStep("第三步客户增加积分");
        PointsAccTransOutput pointsAccTransOutput = addPoints(pointsAccTransInput, Constants.DEFAULT_INSTITUTION_NO, customerCreateInput.getVoucher(), accountInfoQueryInput.getPointsTypeNo());
        assertNotNull(pointsAccTransOutput);
        assertNotNull(pointsAccTransOutput.getTransDate());
        assertNotNull(pointsAccTransOutput.getTransNo());
        assertEquals(0, pointsAccTransOutput.getPointsBalance().compareTo(pointsAccTransInput.getPoints().getPointsAmount().add(accountInfoQueryOutput.getPointsAccountInfo().getPointsBalance())));

        setStep("第四步查询客户指定积分");
        AccountInfoQueryOutput accountInfoQueryOutputAgain = querySingleAccountInfo(customerCreateInput, accountInfoQueryInput);
        assertNotNull(accountInfoQueryOutputAgain);
        assertEquals(0, pointsAccTransOutput.getPointsBalance().compareTo(accountInfoQueryOutputAgain.getPointsAccountInfo().getPointsBalance()));

        setStep("第五步消费客户积分");
        Date startDate = new Date();
        pointsConsumeInput.setPoints(new Points() {{
            setPointsTypeNo(accountInfoQueryInput.getPointsTypeNo());
            setPointsAmount(pointsAccTransOutput.getPointsBalance().subtract(BigDecimal.ONE));
        }});
        pointsConsumeInput.setInstitutionNo(customerCreateInput.getInstitutionNo());
        pointsConsumeInput.setVoucher(customerCreateInput.getVoucher());
        pointsConsumeInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsConsumeInput.setDescription("消费积分成功测试");
        pointsConsumeInput.setOperator(Constants.DEFAULT_OPERATOR_SYSTEM);
        pointsConsumeInput.setMerchantNo("merchant");

        ResponseResult<PointsConsumeOutput> consume_result = HttpPostWithJson.httpPostWithJson(pointsConsumeInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.CONSUME_PATH, PointsConsumeOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), consume_result.getCode());
        PointsConsumeOutput pointsConsumeOutput = consume_result.getData();
        assertNotNull(pointsConsumeOutput);
        assertNotNull(pointsConsumeOutput.getPayTime());
        assertNotNull(pointsConsumeOutput.getPointsTransNo());
        assertEquals(pointsConsumeInput.getSysTransNo(), pointsConsumeOutput.getSysTransNo());

        setStep("第六步查询客户指定积分");
        AccountInfoQueryOutput accountInfoQueryOutputAgainMore = querySingleAccountInfo(customerCreateInput, accountInfoQueryInput);
        assertNotNull(accountInfoQueryOutputAgainMore);
        assertEquals(0, pointsAccTransOutput.getPointsBalance().compareTo(accountInfoQueryOutputAgainMore.getPointsAccountInfo().getPointsBalance().add(pointsConsumeInput.getPoints().getPointsAmount())));

        setStep("第七步查询交易状态");
        PointsQueryStateOutput pointsQueryStateOutput = queryState(pointsQueryStateInput, customerCreateInput.getInstitutionNo(), pointsConsumeInput.getSysTransNo());
        assertNotNull(pointsQueryStateOutput);
        assertNotNull(pointsQueryStateOutput.getTransNo());
        assertNotNull(pointsQueryStateOutput.getTransDate());
        assertEquals(TransStatus.SUCCESS, pointsQueryStateOutput.getTransStatus());

        setStep("第八步查询交易流水");
        pointsTransQueryListInput.setInstitutionNo(customerCreateInput.getInstitutionNo());
        pointsTransQueryListInput.setVoucher(customerCreateInput.getVoucher());
        pointsTransQueryListInput.setPointsTypeNo(accountInfoQueryInput.getPointsTypeNo());
        pointsTransQueryListInput.setFromDate(startDate);
        ResponseResult<PointsTransQueryListOutput> query_list_result = HttpPostWithJson.httpPostWithJson(pointsTransQueryListInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.QUERY_PATH, PointsTransQueryListOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), query_list_result.getCode());
        PointsTrans trans = query_list_result.getData().getPointsTransList().stream().filter(pointsTrans -> pointsConsumeOutput.getPointsTransNo().equals(pointsTrans.getTransNo())).findFirst().orElse(null);
        assertNotNull(trans);

        Trans ts = trans.getTrans();
        assertNotNull(ts);
        assertEquals(pointsConsumeInput.getSysTransNo(), ts.getSysTransNo());
        assertEquals(pointsConsumeOutput.getPointsTransNo(), ts.getTransNo());
        assertEquals(TransStatus.SUCCESS, ts.getTransStatus());
        assertEquals(DebitOrCredit.DEBIT, ts.getTransType().getDebitOrCredit());

        Points points = trans.getPoints();
        assertNotNull(points);
        assertEquals(pointsConsumeInput.getPoints().getPointsTypeNo(), points.getPointsTypeNo());
        assertEquals(pointsConsumeInput.getPoints().getPointsAmount(), points.getPointsAmount());

        Voucher voucher = trans.getVoucher();
        assertNotNull(voucher);
        assertEquals(pointsConsumeInput.getVoucher().getVoucherNo(), voucher.getVoucherNo());
        assertEquals(pointsConsumeInput.getVoucher().getVoucherType(), voucher.getVoucherType());

        assertEquals(DebitOrCredit.DEBIT, trans.getDebitOrCredit());
        assertEquals(ReversedFlag.NOT_REVERSED, trans.getReversedFlag());
        assertEquals(pointsConsumeInput.getMerchantNo(), trans.getMerchantNo());
        assertEquals(pointsConsumeInput.getOperator(), trans.getOperator());
        assertNotNull(trans.getDescription());
    }

    @ParameterizedTest
    @FileArgumentSources
    void accTransPoints_credit_success(@FileArgumentConversion CustomerCreateInput customerCreateInput,
                                       @FileArgumentConversion AccountInfoQueryInput accountInfoQueryInput,
                                       @FileArgumentConversion PointsAccTransInput pointsAccTransInput,
                                       @FileArgumentConversion PointsQueryStateInput pointsQueryStateInput,
                                       @FileArgumentConversion AccountDetailsQueryInput accountDetailsQueryInput) throws Exception {
        setStep("第一步确认客户");
        String customerId = sureCustomer(customerCreateInput);
        assertNotNull(customerId);

        setStep("第二步查询客户指定积分");
        AccountInfoQueryOutput accountInfoQueryOutput = querySingleAccountInfo(customerCreateInput, accountInfoQueryInput);
        assertNotNull(accountInfoQueryOutput);

        setStep("第三步客户增加积分");
        PointsAccTransOutput pointsAccTransOutput = addPoints(pointsAccTransInput, Constants.DEFAULT_INSTITUTION_NO, customerCreateInput.getVoucher(), accountInfoQueryInput.getPointsTypeNo());
        assertNotNull(pointsAccTransOutput);
        assertNotNull(pointsAccTransOutput.getTransDate());
        assertNotNull(pointsAccTransOutput.getTransNo());
        BigDecimal amountResult = pointsAccTransInput.getPoints().getPointsAmount().add(accountInfoQueryOutput.getPointsAccountInfo().getPointsBalance());
        assertEquals(0, pointsAccTransOutput.getPointsBalance().compareTo(amountResult));

        setStep("第四步查询交易状态");
        PointsQueryStateOutput pointsQueryStateOutput = queryState(pointsQueryStateInput, customerCreateInput.getInstitutionNo(), pointsAccTransInput.getSysTransNo());
        assertNotNull(pointsQueryStateOutput);
        assertNotNull(pointsQueryStateOutput.getTransNo());
        assertNotNull(pointsQueryStateOutput.getTransDate());
        assertEquals(TransStatus.SUCCESS, pointsQueryStateOutput.getTransStatus());

        setStep("第五步查询客户指定积分");
        AccountInfoQueryOutput accountInfoQueryOutputAgain = querySingleAccountInfo(customerCreateInput, accountInfoQueryInput);
        assertNotNull(accountInfoQueryOutputAgain);
        assertEquals(0, pointsAccTransOutput.getPointsBalance().compareTo(amountResult));

        setStep("第六步客户再次增加积分");
        PointsAccTransOutput pointsAccTransOutputAgain = addPoints(pointsAccTransInput, Constants.DEFAULT_INSTITUTION_NO, customerCreateInput.getVoucher(), accountInfoQueryInput.getPointsTypeNo());
        assertNotNull(pointsAccTransOutputAgain);
        assertNotNull(pointsAccTransOutputAgain.getTransDate());
        assertNotNull(pointsAccTransOutputAgain.getTransNo());
        BigDecimal amountResultAgain = pointsAccTransInput.getPoints().getPointsAmount().add(pointsAccTransInput.getPoints().getPointsAmount()).add(accountInfoQueryOutput.getPointsAccountInfo().getPointsBalance());
        assertEquals(0, pointsAccTransOutputAgain.getPointsBalance().compareTo(amountResultAgain));

        setStep("第七步查询交易状态");
        PointsQueryStateOutput pointsQueryStateOutputAgain = queryState(pointsQueryStateInput, customerCreateInput.getInstitutionNo(), pointsAccTransInput.getSysTransNo());
        assertNotNull(pointsQueryStateOutputAgain);
        assertNotNull(pointsQueryStateOutputAgain.getTransNo());
        assertNotNull(pointsQueryStateOutputAgain.getTransDate());
        assertEquals(TransStatus.SUCCESS, pointsQueryStateOutputAgain.getTransStatus());

        setStep("第八步查询客户指定积分");
        AccountInfoQueryOutput accountInfoQueryOutputAgainMore = querySingleAccountInfo(customerCreateInput, accountInfoQueryInput);
        assertNotNull(accountInfoQueryOutputAgainMore);
        assertEquals(0, pointsAccTransOutputAgain.getPointsBalance().compareTo(amountResultAgain));

        setStep("第九步查询客户指定积分明细");
        accountDetailsQueryInput.setInstitutionNo(customerCreateInput.getInstitutionNo());
        accountDetailsQueryInput.setPointsTypeNo(accountInfoQueryInput.getPointsTypeNo());
        accountDetailsQueryInput.setVoucherNo(customerCreateInput.getVoucher().getVoucherNo());
        accountDetailsQueryInput.setVoucherType(customerCreateInput.getVoucher().getVoucherType());
        ResponseResult<AccountDetailsQueryOutput> query_account_details_result = HttpPostWithJson.httpPostWithJson(accountDetailsQueryInput, RestPathConstants.ACCOUNT.PATH + RestPathConstants.ACCOUNT.QUERY_DETAIL_PATH, AccountDetailsQueryOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), query_account_details_result.getCode());
        AccountDetailsQueryOutput accountDetailsQueryOutput = query_account_details_result.getData();
        assertNotNull(accountDetailsQueryOutput);
    }

    @ParameterizedTest
    @FileArgumentSources
    void accTransPoints_debit_success(@FileArgumentConversion CustomerCreateInput customerCreateInput,
                                      @FileArgumentConversion PointsAccTransInput pointsAccTransInput,
                                      @FileArgumentConversion PointsQueryStateInput pointsQueryStateInput) throws Exception {

        setStep("第一步确认客户");
        String customerId = sureCustomer(customerCreateInput);
        assertNotNull(customerId);

        setStep("第二步客户增加积分");
        PointsAccTransOutput pointsAccTransOutput = addPoints(pointsAccTransInput, Constants.DEFAULT_INSTITUTION_NO, customerCreateInput.getVoucher(), Constants.DEFAULT_POINTS_TYPE_NO);
        assertNotNull(pointsAccTransOutput);
        assertNotNull(pointsAccTransOutput.getTransDate());
        assertNotNull(pointsAccTransOutput.getTransNo());
        BigDecimal amountResult = pointsAccTransInput.getPoints().getPointsAmount();
        assertTrue(pointsAccTransOutput.getPointsBalance().compareTo(amountResult) <= 0);

        setStep("第三步客户扣减积分");
        PointsAccTransOutput pointsAccTransDebitOutput = reducePoints(pointsAccTransInput, Constants.DEFAULT_INSTITUTION_NO, customerCreateInput.getVoucher(), pointsAccTransInput.getPoints().getPointsTypeNo());
        assertNotNull(pointsAccTransDebitOutput);
        assertNotNull(pointsAccTransDebitOutput.getTransDate());
        assertNotNull(pointsAccTransDebitOutput.getTransNo());
        BigDecimal amountDebitResult = pointsAccTransOutput.getPointsBalance().subtract(pointsAccTransInput.getPoints().getPointsAmount());
        assertEquals(0, pointsAccTransDebitOutput.getPointsBalance().compareTo(amountDebitResult));

        setStep("第四步查询交易状态");
        PointsQueryStateOutput pointsQueryStateOutput = queryState(pointsQueryStateInput, customerCreateInput.getInstitutionNo(), pointsAccTransInput.getSysTransNo());
        assertNotNull(pointsQueryStateOutput);
        assertNotNull(pointsQueryStateOutput.getTransNo());
        assertNotNull(pointsQueryStateOutput.getTransDate());
        assertEquals(TransStatus.SUCCESS, pointsQueryStateOutput.getTransStatus());

    }

    @ParameterizedTest
    @FileArgumentSources
    void reversePoints_accTransPoints_credit_success(@FileArgumentConversion CustomerCreateInput customerCreateInput,
                                                     @FileArgumentConversion AccountInfoQueryInput accountInfoQueryInput,
                                                     @FileArgumentConversion PointsAccTransInput pointsAccTransInput,
                                                     @FileArgumentConversion PointsQueryStateInput pointsQueryStateInput,
                                                     @FileArgumentConversion PointsReverseInput pointsReverseInput) throws Exception {
        setStep("第一步确认客户");
        String customerId = sureCustomer(customerCreateInput);
        assertNotNull(customerId);

        setStep("第二步查询客户指定积分");
        AccountInfoQueryOutput accountInfoQueryOutput = querySingleAccountInfo(customerCreateInput, accountInfoQueryInput);
        assertNotNull(accountInfoQueryOutput);

        setStep("第三步客户增加积分");
        PointsAccTransOutput pointsAccTransOutput = addPoints(pointsAccTransInput, customerCreateInput.getInstitutionNo(), customerCreateInput.getVoucher(), accountInfoQueryInput.getPointsTypeNo());
        assertNotNull(pointsAccTransOutput);
        assertNotNull(pointsAccTransOutput.getTransDate());
        assertNotNull(pointsAccTransOutput.getTransNo());
        BigDecimal amountResult = pointsAccTransInput.getPoints().getPointsAmount().add(accountInfoQueryOutput.getPointsAccountInfo().getPointsBalance());
        assertEquals(0, pointsAccTransOutput.getPointsBalance().compareTo(amountResult));

        setStep("第四步查询交易状态");
        PointsQueryStateOutput pointsQueryStateOutput = queryState(pointsQueryStateInput, customerCreateInput.getInstitutionNo(), pointsAccTransInput.getSysTransNo());
        assertNotNull(pointsQueryStateOutput);
        assertNotNull(pointsQueryStateOutput.getTransNo());
        assertNotNull(pointsQueryStateOutput.getTransDate());
        assertEquals(TransStatus.SUCCESS, pointsQueryStateOutput.getTransStatus());

        setStep("第五步撤销增加积分");
        ResponseResult<PointsReverseOutput> points_reverse_result = reverseTrans(customerCreateInput.getInstitutionNo(), pointsAccTransInput.getSysTransNo(), pointsReverseInput);
        assertEquals(PointsCode.TRANS_0000.getCode(), points_reverse_result.getCode());
        PointsReverseOutput reverseOutput = points_reverse_result.getData();
        assertNotNull(reverseOutput);
        assertNotNull(reverseOutput.getReverseTransNo());
        assertNotNull(reverseOutput.getTransDate());

        setStep("第六步查询交易状态");
        PointsQueryStateOutput pointsQueryStateOutputAgain = queryState(pointsQueryStateInput, customerCreateInput.getInstitutionNo(), pointsReverseInput.getSysTransNo());
        assertNotNull(pointsQueryStateOutputAgain);
        assertNotNull(pointsQueryStateOutputAgain.getTransNo());
        assertNotNull(pointsQueryStateOutputAgain.getTransDate());
        assertEquals(TransStatus.SUCCESS, pointsQueryStateOutputAgain.getTransStatus());

        TimeUnit.SECONDS.sleep(5);
        setStep("第七步再次撤销增加积分");
        ResponseResult<PointsReverseOutput> points_reverse_result_again = reverseTrans(customerCreateInput.getInstitutionNo(), pointsAccTransInput.getSysTransNo(), pointsReverseInput);
        assertEquals(PointsCode.TRANS_2011.getCode(), points_reverse_result_again.getCode());

        setStep("第八步查询二次撤销交易状态");
        PointsQueryStateOutput pointsQueryStateOutputAgainMore = queryState(pointsQueryStateInput, customerCreateInput.getInstitutionNo(), pointsReverseInput.getSysTransNo());
        assertNotNull(pointsQueryStateOutputAgainMore);
        assertNotNull(pointsQueryStateOutputAgainMore.getTransNo());
        assertNotNull(pointsQueryStateOutputAgainMore.getTransDate());
        assertEquals(TransStatus.FAILED, pointsQueryStateOutputAgainMore.getTransStatus());

        setStep("第九步查询客户指定积分");
        AccountInfoQueryOutput accountInfoQueryOutputAgain = querySingleAccountInfo(customerCreateInput, accountInfoQueryInput);
        assertNotNull(accountInfoQueryOutputAgain);
        assertEquals(0, accountInfoQueryOutput.getPointsAccountInfo().getPointsBalance().compareTo(accountInfoQueryOutputAgain.getPointsAccountInfo().getPointsBalance()));
    }

    @ParameterizedTest
    @FileArgumentSources
    void reversePoints_accTransPoints_debit_success(@FileArgumentConversion CustomerCreateInput customerCreateInput,
                                                    @FileArgumentConversion AccountInfoQueryInput accountInfoQueryInput,
                                                    @FileArgumentConversion PointsAccTransInput pointsAccTransInput,
                                                    @FileArgumentConversion PointsQueryStateInput pointsQueryStateInput,
                                                    @FileArgumentConversion PointsReverseInput pointsReverseInput) throws Exception {

        setStep("第一步确认客户");
        String customerId = sureCustomer(customerCreateInput);
        assertNotNull(customerId);

        setStep("第二步客户增加积分");
        PointsAccTransOutput pointsAccTransOutput = addPoints(pointsAccTransInput, Constants.DEFAULT_INSTITUTION_NO, customerCreateInput.getVoucher(), Constants.DEFAULT_POINTS_TYPE_NO);
        assertNotNull(pointsAccTransOutput);
        assertNotNull(pointsAccTransOutput.getTransDate());
        assertNotNull(pointsAccTransOutput.getTransNo());
        BigDecimal amountResult = pointsAccTransInput.getPoints().getPointsAmount();
        assertTrue(pointsAccTransOutput.getPointsBalance().compareTo(amountResult) <= 0);

        setStep("第三步查询客户指定积分");
        AccountInfoQueryOutput accountInfoQueryOutput = querySingleAccountInfo(customerCreateInput, accountInfoQueryInput);
        assertNotNull(accountInfoQueryOutput);

        setStep("第四步客户扣减积分");
        PointsAccTransOutput pointsAccTransDebitOutput = reducePoints(pointsAccTransInput, Constants.DEFAULT_INSTITUTION_NO, customerCreateInput.getVoucher(), pointsAccTransInput.getPoints().getPointsTypeNo());
        assertNotNull(pointsAccTransDebitOutput);
        assertNotNull(pointsAccTransDebitOutput.getTransDate());
        assertNotNull(pointsAccTransDebitOutput.getTransNo());
        BigDecimal amountDebitResult = pointsAccTransOutput.getPointsBalance().subtract(pointsAccTransInput.getPoints().getPointsAmount());
        assertEquals(0, pointsAccTransDebitOutput.getPointsBalance().compareTo(amountDebitResult));

        setStep("第五步查询交易状态");
        PointsQueryStateOutput pointsQueryStateOutput = queryState(pointsQueryStateInput, customerCreateInput.getInstitutionNo(), pointsAccTransInput.getSysTransNo());
        assertNotNull(pointsQueryStateOutput);
        assertNotNull(pointsQueryStateOutput.getTransNo());
        assertNotNull(pointsQueryStateOutput.getTransDate());
        assertEquals(TransStatus.SUCCESS, pointsQueryStateOutput.getTransStatus());

        setStep("第六步撤销扣减积分");
        ResponseResult<PointsReverseOutput> points_reverse_result = reverseTrans(customerCreateInput.getInstitutionNo(), pointsAccTransInput.getSysTransNo(), pointsReverseInput);
        assertEquals(PointsCode.TRANS_0000.getCode(), points_reverse_result.getCode());
        PointsReverseOutput reverseOutput = points_reverse_result.getData();
        assertNotNull(reverseOutput);
        assertNotNull(reverseOutput.getReverseTransNo());
        assertNotNull(reverseOutput.getTransDate());

        setStep("第七步查询交易状态");
        PointsQueryStateOutput pointsQueryStateOutputAgain = queryState(pointsQueryStateInput, customerCreateInput.getInstitutionNo(), pointsReverseInput.getSysTransNo());
        assertNotNull(pointsQueryStateOutputAgain);
        assertNotNull(pointsQueryStateOutputAgain.getTransNo());
        assertNotNull(pointsQueryStateOutputAgain.getTransDate());
        assertEquals(TransStatus.SUCCESS, pointsQueryStateOutputAgain.getTransStatus());

        setStep("第八步再次撤销扣减积分");
        ResponseResult<PointsReverseOutput> points_reverse_result_again = reverseTrans(customerCreateInput.getInstitutionNo(), pointsAccTransInput.getSysTransNo(), pointsReverseInput);
        assertEquals(PointsCode.TRANS_2011.getCode(), points_reverse_result_again.getCode());

        setStep("第九步查询客户指定积分");
        AccountInfoQueryOutput accountInfoQueryOutputAgain = querySingleAccountInfo(customerCreateInput, accountInfoQueryInput);
        assertNotNull(accountInfoQueryOutputAgain);
        assertEquals(accountInfoQueryOutput.getPointsAccountInfo().getPointsBalance(), accountInfoQueryOutputAgain.getPointsAccountInfo().getPointsBalance());
    }

    @ParameterizedTest
    @FileArgumentSources
    void reversePoints_accTransPoints_credit_has_been_used_success(@FileArgumentConversion CustomerCreateInput customerCreateInput,
                                                                   @FileArgumentConversion AccountInfoQueryInput accountInfoQueryInput,
                                                                   @FileArgumentConversion PointsAccTransInput pointsAccTransInput,
                                                                   @FileArgumentConversion PointsQueryStateInput pointsQueryStateInput,
                                                                   @FileArgumentConversion PointsReverseInput pointsReverseInput) throws Exception {

        setStep("第一步确认客户");
        String customerId = sureCustomer(customerCreateInput);
        assertNotNull(customerId);

        setStep("第二步客户增加积分");
        PointsAccTransOutput pointsAccTransOutput = addPoints(pointsAccTransInput, Constants.DEFAULT_INSTITUTION_NO, customerCreateInput.getVoucher(), Constants.DEFAULT_POINTS_TYPE_NO);
        String addPointsSysTransNo = pointsAccTransInput.getSysTransNo();
        assertNotNull(pointsAccTransOutput);
        assertNotNull(pointsAccTransOutput.getTransDate());
        assertNotNull(pointsAccTransOutput.getTransNo());
        BigDecimal amountResult = pointsAccTransInput.getPoints().getPointsAmount();
        assertTrue(pointsAccTransOutput.getPointsBalance().compareTo(amountResult) <= 0);

        setStep("第三步查询客户指定积分");
        AccountInfoQueryOutput accountInfoQueryOutput = querySingleAccountInfo(customerCreateInput, accountInfoQueryInput);
        assertNotNull(accountInfoQueryOutput);

        setStep("第四步客户扣减积分");
        PointsAccTransOutput pointsAccTransDebitOutput = reducePoints(pointsAccTransInput, Constants.DEFAULT_INSTITUTION_NO, customerCreateInput.getVoucher(), pointsAccTransInput.getPoints().getPointsTypeNo());
        assertNotNull(pointsAccTransDebitOutput);
        assertNotNull(pointsAccTransDebitOutput.getTransDate());
        assertNotNull(pointsAccTransDebitOutput.getTransNo());
        BigDecimal amountDebitResult = pointsAccTransOutput.getPointsBalance().subtract(pointsAccTransInput.getPoints().getPointsAmount());
        assertEquals(0, pointsAccTransDebitOutput.getPointsBalance().compareTo(amountDebitResult));

        setStep("第五步撤销增加积分");
        ResponseResult<PointsReverseOutput> points_reverse_result = reverseTrans(customerCreateInput.getInstitutionNo(), addPointsSysTransNo, pointsReverseInput);
        assertEquals(PointsCode.TRANS_2015.getCode(), points_reverse_result.getCode());

        setStep("第六步查询交易状态");
        PointsQueryStateOutput pointsQueryStateOutputAgain = queryState(pointsQueryStateInput, customerCreateInput.getInstitutionNo(), pointsReverseInput.getSysTransNo());
        assertNotNull(pointsQueryStateOutputAgain);
        assertNotNull(pointsQueryStateOutputAgain.getTransNo());
        assertNotNull(pointsQueryStateOutputAgain.getTransDate());
        assertEquals(TransStatus.FAILED, pointsQueryStateOutputAgain.getTransStatus());

    }

    @ParameterizedTest
    @FileArgumentSources
    void reversePoints_accTransPoints_debit_multiple_success(@FileArgumentConversion CustomerCreateInput customerCreateInput,
                                                             @FileArgumentConversion AccountInfoQueryInput accountInfoQueryInput,
                                                             @FileArgumentConversion PointsAccTransInput pointsAccTransInput,
                                                             @FileArgumentConversion PointsQueryStateInput pointsQueryStateInput,
                                                             @FileArgumentConversion PointsReverseInput pointsReverseInput,
                                                             @FileArgumentConversion AccountDetailsQueryInput accountDetailsQueryInput) throws Exception {
        setStep("第一步确认客户");
        String customerId = sureCustomer(customerCreateInput);
        assertNotNull(customerId);

        setStep("第二步查询客户指定积分");
        AccountInfoQueryOutput accountInfoQueryOutput = querySingleAccountInfo(customerCreateInput, accountInfoQueryInput);
        assertNotNull(accountInfoQueryOutput);

        setStep("第三步客户增加积分");
        PointsAccTransOutput pointsAccTransOutput = addPoints(pointsAccTransInput, customerCreateInput.getInstitutionNo(), customerCreateInput.getVoucher(), accountInfoQueryInput.getPointsTypeNo());
        assertNotNull(pointsAccTransOutput);
        assertNotNull(pointsAccTransOutput.getTransDate());
        assertNotNull(pointsAccTransOutput.getTransNo());
        BigDecimal amountResult = pointsAccTransInput.getPoints().getPointsAmount().add(accountInfoQueryOutput.getPointsAccountInfo().getPointsBalance());
        assertEquals(0, pointsAccTransOutput.getPointsBalance().compareTo(amountResult));

        TimeUnit.SECONDS.sleep(1);
        setStep("第四步客户再次增加积分");
        PointsAccTransOutput pointsAccTransOutputAgain = addPoints(pointsAccTransInput, customerCreateInput.getInstitutionNo(), customerCreateInput.getVoucher(), accountInfoQueryInput.getPointsTypeNo());
        assertNotNull(pointsAccTransOutputAgain);
        assertNotNull(pointsAccTransOutputAgain.getTransDate());
        assertNotNull(pointsAccTransOutputAgain.getTransNo());
        BigDecimal amountResultAgain = pointsAccTransInput.getPoints().getPointsAmount().add(amountResult);
        assertEquals(0, pointsAccTransOutputAgain.getPointsBalance().compareTo(amountResultAgain));


        setStep("第五步客户扣减积分");
        pointsAccTransInput.setInstitutionNo(customerCreateInput.getInstitutionNo());
        pointsAccTransInput.setVoucher(customerCreateInput.getVoucher());
        pointsAccTransInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsAccTransInput.setDescription("扣减积分");
        pointsAccTransInput.setOperator(Constants.DEFAULT_OPERATOR_SYSTEM);
        pointsAccTransInput.setMerchantNo("merchant");
        pointsAccTransInput.setPoints(new Points() {{
            setPointsTypeNo(pointsAccTransInput.getPoints().getPointsTypeNo());
            setPointsAmount(new BigDecimal("13"));
        }});
        pointsAccTransInput.setTransType(TransType.CONSUME_POINT);
        pointsAccTransInput.setDebitOrCredit(pointsAccTransInput.getTransType().getDebitOrCredit());

        ResponseResult<PointsAccTransOutput> acc_trans_result = HttpPostWithJson.httpPostWithJson(pointsAccTransInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.ACC_TRANS_PATH, PointsAccTransOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), acc_trans_result.getCode());
        PointsAccTransOutput pointsAccTransDebitOutput = acc_trans_result.getData();
        assertNotNull(pointsAccTransDebitOutput);
        assertNotNull(pointsAccTransDebitOutput.getTransDate());
        assertNotNull(pointsAccTransDebitOutput.getTransNo());
        BigDecimal amountDebitResult = pointsAccTransOutputAgain.getPointsBalance().subtract(pointsAccTransInput.getPoints().getPointsAmount());
        assertEquals(0, pointsAccTransDebitOutput.getPointsBalance().compareTo(amountDebitResult));

        setStep("第六步查询交易状态");
        PointsQueryStateOutput pointsQueryStateOutput = queryState(pointsQueryStateInput, customerCreateInput.getInstitutionNo(), pointsAccTransInput.getSysTransNo());
        assertNotNull(pointsQueryStateOutput);
        assertNotNull(pointsQueryStateOutput.getTransNo());
        assertNotNull(pointsQueryStateOutput.getTransDate());
        assertEquals(TransStatus.SUCCESS, pointsQueryStateOutput.getTransStatus());

        setStep("第七步撤销消费积分");
        ResponseResult<PointsReverseOutput> points_reverse_result = reverseTrans(customerCreateInput.getInstitutionNo(), pointsAccTransInput.getSysTransNo(), pointsReverseInput);
        assertEquals(PointsCode.TRANS_0000.getCode(), points_reverse_result.getCode());
        PointsReverseOutput reverseOutput = points_reverse_result.getData();
        assertNotNull(reverseOutput);
        assertNotNull(reverseOutput.getReverseTransNo());
        assertNotNull(reverseOutput.getTransDate());

        setStep("第八步查询交易状态");
        PointsQueryStateOutput pointsQueryStateOutputAgain = queryState(pointsQueryStateInput, customerCreateInput.getInstitutionNo(), pointsReverseInput.getSysTransNo());
        assertNotNull(pointsQueryStateOutputAgain);
        assertNotNull(pointsQueryStateOutputAgain.getTransNo());
        assertNotNull(pointsQueryStateOutputAgain.getTransDate());
        assertEquals(TransStatus.SUCCESS, pointsQueryStateOutputAgain.getTransStatus());

        setStep("第九步查询客户指定积分");
        AccountInfoQueryOutput accountInfoQueryOutputAgain = querySingleAccountInfo(customerCreateInput, accountInfoQueryInput);
        assertNotNull(accountInfoQueryOutputAgain);
        assertEquals(pointsAccTransOutputAgain.getPointsBalance(), accountInfoQueryOutputAgain.getPointsAccountInfo().getPointsBalance());

        setStep("第十步查询客户指定积分明细");
        accountDetailsQueryInput.setInstitutionNo(customerCreateInput.getInstitutionNo());
        accountDetailsQueryInput.setVoucherNo(customerCreateInput.getVoucher().getVoucherNo());
        accountDetailsQueryInput.setVoucherType(customerCreateInput.getVoucher().getVoucherType());
        accountDetailsQueryInput.setPointsTypeNo(Constants.DEFAULT_POINTS_TYPE_NO);

        ResponseResult<AccountDetailsQueryOutput> query_account_details_result = HttpPostWithJson.httpPostWithJson(accountDetailsQueryInput, RestPathConstants.ACCOUNT.PATH + RestPathConstants.ACCOUNT.QUERY_DETAIL_PATH, AccountDetailsQueryOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), query_account_details_result.getCode());
    }

    @ParameterizedTest
    @FileArgumentSources
    void backPoints_success(@FileArgumentConversion CustomerCreateInput customerCreateInput,
                            @FileArgumentConversion AccountInfoQueryInput accountInfoQueryInput,
                            @FileArgumentConversion PointsAccTransInput pointsAccTransInput,
                            @FileArgumentConversion PointsConsumeInput pointsConsumeInput,
                            @FileArgumentConversion PointsQueryStateInput pointsQueryStateInput,
                            @FileArgumentConversion PointsTransQueryListInput pointsTransQueryListInput,
                            @FileArgumentConversion PointsBackInput pointsBackInput) throws Exception {

        setStep("第一步确认客户");
        String customerId = sureCustomer(customerCreateInput);
        assertNotNull(customerId);

        setStep("第二步查询客户指定积分");
        AccountInfoQueryOutput accountInfoQueryOutput = querySingleAccountInfo(customerCreateInput, accountInfoQueryInput);
        assertNotNull(accountInfoQueryOutput);

        setStep("第三步客户增加积分");
        PointsAccTransOutput pointsAccTransOutput = addPoints(pointsAccTransInput, Constants.DEFAULT_INSTITUTION_NO, customerCreateInput.getVoucher(), accountInfoQueryInput.getPointsTypeNo());
        assertNotNull(pointsAccTransOutput);
        assertNotNull(pointsAccTransOutput.getTransDate());
        assertNotNull(pointsAccTransOutput.getTransNo());
        BigDecimal amountResult = pointsAccTransInput.getPoints().getPointsAmount().add(accountInfoQueryOutput.getPointsAccountInfo().getPointsBalance());
        assertEquals(0, pointsAccTransOutput.getPointsBalance().compareTo(pointsAccTransInput.getPoints().getPointsAmount().add(accountInfoQueryOutput.getPointsAccountInfo().getPointsBalance())));

        setStep("第四步客户二次增加积分");
        PointsAccTransOutput pointsAccTransOutputAgain = addPoints(pointsAccTransInput, customerCreateInput.getInstitutionNo(), customerCreateInput.getVoucher(), accountInfoQueryInput.getPointsTypeNo());
        assertNotNull(pointsAccTransOutputAgain);
        assertNotNull(pointsAccTransOutputAgain.getTransDate());
        assertNotNull(pointsAccTransOutputAgain.getTransNo());
        BigDecimal amountResultAgain = pointsAccTransInput.getPoints().getPointsAmount().add(amountResult);
        assertEquals(0, pointsAccTransOutputAgain.getPointsBalance().compareTo(amountResultAgain));

        setStep("第五步消费客户积分");
        Date startDate = new Date();
        pointsConsumeInput.setPoints(new Points() {{
            setPointsTypeNo(accountInfoQueryInput.getPointsTypeNo());
            setPointsAmount(new BigDecimal(15));
        }});

        pointsConsumeInput.setInstitutionNo(customerCreateInput.getInstitutionNo());
        pointsConsumeInput.setVoucher(customerCreateInput.getVoucher());
        pointsConsumeInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsConsumeInput.setDescription("消费积分成功测试");
        pointsConsumeInput.setOperator(Constants.DEFAULT_OPERATOR_SYSTEM);
        pointsConsumeInput.setMerchantNo("merchant");

        ResponseResult<PointsConsumeOutput> consume_result = HttpPostWithJson.httpPostWithJson(pointsConsumeInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.CONSUME_PATH, PointsConsumeOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), consume_result.getCode());
        PointsConsumeOutput pointsConsumeOutput = consume_result.getData();
        assertNotNull(pointsConsumeOutput);
        assertNotNull(pointsConsumeOutput.getPayTime());
        assertNotNull(pointsConsumeOutput.getPointsTransNo());
        assertEquals(pointsConsumeInput.getSysTransNo(), pointsConsumeOutput.getSysTransNo());

        setStep("第六步查询客户指定积分");
        AccountInfoQueryOutput accountInfoQueryOutputAgainMore = querySingleAccountInfo(customerCreateInput, accountInfoQueryInput);
        assertNotNull(accountInfoQueryOutputAgainMore);
        assertEquals(0, pointsAccTransOutputAgain.getPointsBalance().compareTo(accountInfoQueryOutputAgainMore.getPointsAccountInfo().getPointsBalance().add(pointsConsumeInput.getPoints().getPointsAmount())));

        setStep("第七步客户退货积分");
        pointsBackInput.setBackSysTransNo(pointsConsumeInput.getSysTransNo());
        pointsBackInput.setOperator("back_operator");
        pointsBackInput.setPointsAmount(new BigDecimal(6));
        pointsBackInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsBackInput.setInstitutionNo(customerCreateInput.getInstitutionNo());
        ResponseResult<PointsBackOutput> back_result = HttpPostWithJson.httpPostWithJson(pointsBackInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.BACK_PATH, PointsBackOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), back_result.getCode());
        PointsBackOutput pointsBackOutput = back_result.getData();
        assertNotNull(pointsBackOutput);
        assertNotNull(pointsBackOutput.getTransDate());
        assertNotNull(pointsBackOutput.getBackTransNo());

        setStep("第八步客户再退货积分");
        pointsBackInput.setBackSysTransNo(pointsConsumeInput.getSysTransNo());
        pointsBackInput.setOperator("back_operator");
        pointsBackInput.setPointsAmount(new BigDecimal(7));
        pointsBackInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsBackInput.setInstitutionNo(customerCreateInput.getInstitutionNo());
        ResponseResult<PointsBackOutput> back_result_again = HttpPostWithJson.httpPostWithJson(pointsBackInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.BACK_PATH, PointsBackOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), back_result_again.getCode());
        PointsBackOutput pointsBackOutputAgain = back_result_again.getData();
        assertNotNull(pointsBackOutputAgain);
        assertNotNull(pointsBackOutputAgain.getTransDate());
        assertNotNull(pointsBackOutputAgain.getBackTransNo());

        setStep("第九步客户再退货积分不足");
        pointsBackInput.setBackSysTransNo(pointsConsumeInput.getSysTransNo());
        pointsBackInput.setOperator("back_operator");
        pointsBackInput.setPointsAmount(new BigDecimal(3));
        pointsBackInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsBackInput.setInstitutionNo(customerCreateInput.getInstitutionNo());
        ResponseResult<PointsBackOutput> back_result_fail = HttpPostWithJson.httpPostWithJson(pointsBackInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.BACK_PATH, PointsBackOutput.class);
        assertEquals(PointsCode.TRANS_2022.getCode(), back_result_fail.getCode());

        setStep("第十步客户再退货积分");
        pointsBackInput.setBackSysTransNo(pointsConsumeInput.getSysTransNo());
        pointsBackInput.setOperator("back_operator");
        pointsBackInput.setPointsAmount(new BigDecimal(2));
        pointsBackInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsBackInput.setInstitutionNo(customerCreateInput.getInstitutionNo());
        ResponseResult<PointsBackOutput> back_result_again_more = HttpPostWithJson.httpPostWithJson(pointsBackInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.BACK_PATH, PointsBackOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), back_result_again_more.getCode());
        PointsBackOutput pointsBackOutputAgainMore = back_result_again_more.getData();
        assertNotNull(pointsBackOutputAgainMore);
        assertNotNull(pointsBackOutputAgainMore.getTransDate());
        assertNotNull(pointsBackOutputAgainMore.getBackTransNo());
    }

    private ResponseResult<PointsReverseOutput> reverseTrans(String institutionNo, String sysTransNo, PointsReverseInput pointsReverseInput) throws Exception {
        pointsReverseInput.setInstitutionNo(institutionNo);
        pointsReverseInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsReverseInput.setReverseSysTransNo(sysTransNo);
        pointsReverseInput.setOperator("撤销交易");
        return HttpPostWithJson.httpPostWithJson(pointsReverseInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.REVERSE_PATH, PointsReverseOutput.class);
    }

    private PointsAccTransOutput reducePoints(PointsAccTransInput pointsAccTransInput, String institutionNo, Voucher voucher, String pointsTypeNo) throws Exception {
        pointsAccTransInput.setInstitutionNo(institutionNo);
        pointsAccTransInput.setVoucher(voucher);
        pointsAccTransInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsAccTransInput.setDescription("扣减积分");
        pointsAccTransInput.setOperator(Constants.DEFAULT_OPERATOR_SYSTEM);
        pointsAccTransInput.setMerchantNo("merchant");
        pointsAccTransInput.setPoints(new Points() {{
            setPointsTypeNo(pointsTypeNo);
            setPointsAmount(BigDecimal.ONE);
        }});
        pointsAccTransInput.setTransType(TransType.CONSUME_POINT);
        pointsAccTransInput.setDebitOrCredit(pointsAccTransInput.getTransType().getDebitOrCredit());

        ResponseResult<PointsAccTransOutput> acc_trans_result = HttpPostWithJson.httpPostWithJson(pointsAccTransInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.ACC_TRANS_PATH, PointsAccTransOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), acc_trans_result.getCode());
        return acc_trans_result.getData();
    }

    private PointsQueryStateOutput queryState(PointsQueryStateInput pointsQueryStateInput, String institutionNo, String sysTransNo) throws Exception {
        pointsQueryStateInput.setInstitutionNo(institutionNo);
        pointsQueryStateInput.setSysTransNo(sysTransNo);
        ResponseResult<PointsQueryStateOutput> query_status_result = HttpPostWithJson.httpPostWithJson(pointsQueryStateInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.QUERY_STATE_PATH, PointsQueryStateOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), query_status_result.getCode());
        return query_status_result.getData();
    }

    private PointsAccTransOutput addPoints(PointsAccTransInput pointsAccTransInput, String institutionNo, Voucher voucher, String pointsTypeNo) throws Exception {
        pointsAccTransInput.setInstitutionNo(institutionNo);
        pointsAccTransInput.setVoucher(voucher);
        pointsAccTransInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsAccTransInput.setDescription("增加积分");
        pointsAccTransInput.setOperator(Constants.DEFAULT_OPERATOR_SYSTEM);
        pointsAccTransInput.setMerchantNo("merchant");
        pointsAccTransInput.setPoints(new Points() {{
            setPointsTypeNo(pointsTypeNo);
            setPointsAmount(BigDecimal.TEN);
            setEndDate(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS).format(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault())));
        }});
        pointsAccTransInput.setTransType(TransType.GAIN_AWARD_POINT);
        pointsAccTransInput.setDebitOrCredit(TransType.GAIN_AWARD_POINT.getDebitOrCredit());
        pointsAccTransInput.setCostLine("1");

        ResponseResult<PointsAccTransOutput> acc_trans_result = HttpPostWithJson.httpPostWithJson(pointsAccTransInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.ACC_TRANS_PATH, PointsAccTransOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), acc_trans_result.getCode());
        return acc_trans_result.getData();
    }

    private AccountInfoQueryOutput querySingleAccountInfo(CustomerCreateInput customerCreateInput, AccountInfoQueryInput accountInfoQueryInput) throws Exception {
        accountInfoQueryInput.setInstitutionNo(customerCreateInput.getInstitutionNo());
        accountInfoQueryInput.setVoucherNo(customerCreateInput.getVoucher().getVoucherNo());
        accountInfoQueryInput.setVoucherType(customerCreateInput.getVoucher().getVoucherType());
        accountInfoQueryInput.setPointsTypeNo(Constants.DEFAULT_POINTS_TYPE_NO);

        ResponseResult<AccountInfoQueryOutput> query_account_result = HttpPostWithJson.httpPostWithJson(accountInfoQueryInput, RestPathConstants.ACCOUNT.PATH + RestPathConstants.ACCOUNT.QUERY_PATH, AccountInfoQueryOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), query_account_result.getCode());
        return query_account_result.getData();
    }

    private String sureCustomer(CustomerCreateInput customerCreateInput) throws Exception {
        customerCreateInput.setInstitutionNo(Constants.DEFAULT_INSTITUTION_NO);
        customerCreateInput.setCustomerName(ChineseNameGenerator.getInstance().generate());
        customerCreateInput.setAddress(ChineseAddressGenerator.getInstance().generate());
        customerCreateInput.setEmail(EmailAddressGenerator.getInstance().generate());
        customerCreateInput.setPhoneNumber(ChineseMobileNumberGenerator.getInstance().generate());
        customerCreateInput.setVoucher(new Voucher() {{
            setVoucherType(VoucherType.IDENTITY);
            setVoucherNo(ChineseIDCardNumberGenerator.getInstance().generate());
        }});
        customerCreateInput.setBirthdate(customerCreateInput.getVoucher().getVoucherNo().substring(6, 14));
        customerCreateInput.setGender(Gender.FEMALE);

        ResponseResult<CustomerCreateOutput> create_result = HttpPostWithJson.httpPostWithJson(customerCreateInput, RestPathConstants.CUSTOMER.PATH + RestPathConstants.CUSTOMER.CREATE_PATH, CustomerCreateOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), create_result.getCode());
        assertNotNull(create_result.getData());
        return create_result.getData().getCustomerId();
    }


    private AccountInfoQueryAllOutput queryAccountInfo(CustomerCreateInput customerCreateInput, AccountInfoQueryAllInput accountInfoQueryAllInput) throws Exception {
        accountInfoQueryAllInput.setInstitutionNo(customerCreateInput.getInstitutionNo());
        accountInfoQueryAllInput.setVoucherNo(customerCreateInput.getVoucher().getVoucherNo());
        accountInfoQueryAllInput.setVoucherType(customerCreateInput.getVoucher().getVoucherType());

        ResponseResult<AccountInfoQueryAllOutput> query_account_result = HttpPostWithJson.httpPostWithJson(accountInfoQueryAllInput, RestPathConstants.ACCOUNT.PATH + RestPathConstants.ACCOUNT.QUERY_ALL_PATH, AccountInfoQueryAllOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), query_account_result.getCode());
        return query_account_result.getData();
    }


}
