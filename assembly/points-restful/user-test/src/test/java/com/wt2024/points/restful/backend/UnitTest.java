package com.wt2024.points.restful.backend;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.Gender;
import com.wt2024.points.common.enums.TransType;
import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.common.sequence.Sequence;
import com.wt2024.points.core.api.domain.customer.CustomerCreateInput;
import com.wt2024.points.core.api.domain.customer.CustomerCreateOutput;
import com.wt2024.points.core.api.domain.trans.*;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import com.wt2024.points.restful.backend.constant.Constants;
import com.wt2024.points.restful.backend.constant.RestPathConstants;
import com.wt2024.points.restful.backend.domain.ResponseResult;
import com.wt2024.points.restful.backend.tools.generator.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/5/27 15:11
 * @project points3.0:com.wt2024.points.restful.backend
 */
public class UnitTest {
    public static void main(String[] args) throws Exception {
//        CustomerCreateOutput customerCreateOutput = create();
//        String pointsType = "844703788584402944";
//        add(customerCreateOutput.getCustomerId(),pointsType,BigDecimal.TEN,1L);
//        add(customerCreateOutput.getCustomerId(),pointsType,BigDecimal.TEN,5L);
//        PointsConsumeOutput pointsConsumeOutput = consume(customerCreateOutput.getCustomerId(),pointsType,new BigDecimal(13));
        String sysTransNo = "983788931310944256";
        PointsBackOutput pointsBackOutput = back(sysTransNo,new BigDecimal(3));
//        PointsReverseOutput pointsReverseOutput = reverse(sysTransNo);
    }

    private static CustomerCreateOutput create() throws Exception {
        CustomerCreateInput customerCreateInput = new CustomerCreateInput();
        customerCreateInput.setInstitutionNo(Constants.DEFAULT_INSTITUTION_NO);
        customerCreateInput.setCustomerName(ChineseNameGenerator.getInstance().generate());
        customerCreateInput.setAddress(ChineseAddressGenerator.getInstance().generate());
        customerCreateInput.setEmail(EmailAddressGenerator.getInstance().generate());
        customerCreateInput.setPhoneNumber(ChineseMobileNumberGenerator.getInstance().generate());
        customerCreateInput.setVoucher(new Voucher(){{
            setVoucherType(VoucherType.IDENTITY);
            setVoucherNo(ChineseIDCardNumberGenerator.getInstance().generate());
        }});
        customerCreateInput.setBirthdate(customerCreateInput.getVoucher().getVoucherNo().substring(6,14));
        customerCreateInput.setGender(Gender.FEMALE);

        ResponseResult<CustomerCreateOutput> create_result = HttpPostWithJson.httpPostWithJson(customerCreateInput, RestPathConstants.CUSTOMER.PATH + RestPathConstants.CUSTOMER.CREATE_PATH, CustomerCreateOutput.class);
        return create_result.getData();
    }


    private static PointsAccTransOutput add(String customerId, String pointsTypeNo, BigDecimal amount, Long minutes) throws Exception {
        PointsAccTransInput pointsAccTransInput = new PointsAccTransInput();
        pointsAccTransInput.setInstitutionNo(Constants.DEFAULT_INSTITUTION_NO);
        pointsAccTransInput.setVoucher(new Voucher(){{
            setVoucherType(VoucherType.CUST);
            setVoucherNo(customerId);
        }});
        pointsAccTransInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsAccTransInput.setDescription("增加积分");
        pointsAccTransInput.setOperator(Constants.DEFAULT_OPERATOR_SYSTEM);
        pointsAccTransInput.setMerchantNo("merchant");
        pointsAccTransInput.setPoints(new Points() {{
            setPointsTypeNo(pointsTypeNo);
            setPointsAmount(amount);
            setEndDate(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS).format(LocalDateTime.now().plusMinutes(minutes).atZone(ZoneId.systemDefault())));
        }});
        pointsAccTransInput.setTransType(TransType.GAIN_AWARD_POINT);
        pointsAccTransInput.setDebitOrCredit(TransType.GAIN_AWARD_POINT.getDebitOrCredit());
        pointsAccTransInput.setCostLine("1");

        ResponseResult<PointsAccTransOutput> acc_trans_result = HttpPostWithJson.httpPostWithJson(pointsAccTransInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.ACC_TRANS_PATH, PointsAccTransOutput.class);
        return acc_trans_result.getData();
    }

    private static PointsConsumeOutput consume(String customerId, String pointsTypeNo, BigDecimal amount) throws Exception {
        PointsConsumeInput pointsConsumeInput = new PointsConsumeInput();
        pointsConsumeInput.setPoints(new Points() {{
            setPointsTypeNo(pointsTypeNo);
            setPointsAmount(amount);
        }});

        pointsConsumeInput.setInstitutionNo(Constants.DEFAULT_INSTITUTION_NO);
        pointsConsumeInput.setVoucher(new Voucher(){{
            setVoucherType(VoucherType.CUST);
            setVoucherNo(customerId);
        }});
        pointsConsumeInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsConsumeInput.setDescription("消费积分成功测试");
        pointsConsumeInput.setOperator(Constants.DEFAULT_OPERATOR_SYSTEM);
        pointsConsumeInput.setMerchantNo("merchant");

        ResponseResult<PointsConsumeOutput> consume_result = HttpPostWithJson.httpPostWithJson(pointsConsumeInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.CONSUME_PATH, PointsConsumeOutput.class);
        return consume_result.getData();
    }

    private static PointsBackOutput back(String sysTransNo, BigDecimal amount) throws Exception {
        PointsBackInput pointsBackInput = new PointsBackInput();
        pointsBackInput.setBackSysTransNo(sysTransNo);
        pointsBackInput.setOperator("back_operator");
        pointsBackInput.setPointsAmount(amount);
        pointsBackInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsBackInput.setInstitutionNo(Constants.DEFAULT_INSTITUTION_NO);
        ResponseResult<PointsBackOutput> back_result_again_more = HttpPostWithJson.httpPostWithJson(pointsBackInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.BACK_PATH, PointsBackOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), back_result_again_more.getCode());
        return back_result_again_more.getData();
    }

    private static PointsReverseOutput reverse(String sysTransNo) throws Exception {
        PointsReverseInput pointsReverseInput = new PointsReverseInput();
        pointsReverseInput.setInstitutionNo(Constants.DEFAULT_INSTITUTION_NO);
        pointsReverseInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsReverseInput.setReverseSysTransNo(sysTransNo);
        pointsReverseInput.setOperator("撤销交易");
        ResponseResult<PointsReverseOutput> reverse_result = HttpPostWithJson.httpPostWithJson(pointsReverseInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.REVERSE_PATH, PointsReverseOutput.class);
        return reverse_result.getData();
    }
}
