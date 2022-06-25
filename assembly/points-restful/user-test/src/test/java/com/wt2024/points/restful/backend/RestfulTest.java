package com.wt2024.points.restful.backend;

import com.wt2024.points.common.enums.TransType;
import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.common.sequence.Sequence;
import com.wt2024.points.core.api.domain.trans.*;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import com.wt2024.points.restful.backend.constant.Constants;
import com.wt2024.points.restful.backend.constant.RestPathConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName RestfulTest
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/5/16
 * @Version V1.0
 **/
public class RestfulTest {

    public static void main(String[] args) throws Exception {
//        add("CUST975515180546924544",BigDecimal.TEN);
//        consume("CUST975515180546924544",new BigDecimal(11));
        reverse("975515214394949632");
    }


    static void add(String customerId, BigDecimal points) throws Exception {
        Voucher voucher = new Voucher(){{
            setVoucherNo(customerId);
            setVoucherType(VoucherType.CUST);
        }};
        PointsAccTransInput pointsAccTransInput = new PointsAccTransInput();
        pointsAccTransInput.setInstitutionNo(Constants.DEFAULT_INSTITUTION_NO);
        pointsAccTransInput.setVoucher(voucher);
        pointsAccTransInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsAccTransInput.setDescription("增加积分");
        pointsAccTransInput.setOperator(Constants.DEFAULT_OPERATOR_SYSTEM);
        pointsAccTransInput.setMerchantNo("merchant");
        pointsAccTransInput.setPoints(new Points() {{
            setPointsTypeNo(Constants.DEFAULT_POINTS_TYPE_NO);
            setPointsAmount(points);
            setEndDate(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS).format(LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault())));
        }});
        pointsAccTransInput.setTransType(TransType.GAIN_AWARD_POINT);
        pointsAccTransInput.setDebitOrCredit(TransType.GAIN_AWARD_POINT.getDebitOrCredit());
        pointsAccTransInput.setCostLine("1");

        HttpPostWithJson.httpPostWithJson(pointsAccTransInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.ACC_TRANS_PATH, PointsAccTransOutput.class);
    }


    static void consume(String customerId, BigDecimal points) throws Exception {
        PointsConsumeInput pointsConsumeInput = new PointsConsumeInput();
        pointsConsumeInput.setPoints(new Points() {{
            setPointsTypeNo(Constants.DEFAULT_POINTS_TYPE_NO);
            setCustomerId(customerId);
            setPointsAmount(points);
        }});
        pointsConsumeInput.setInstitutionNo(Constants.DEFAULT_INSTITUTION_NO);
        pointsConsumeInput.setVoucher(new Voucher(){{
            setVoucherNo(customerId);
            setVoucherType(VoucherType.CUST);
        }});
        pointsConsumeInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsConsumeInput.setDescription("消费积分");
        pointsConsumeInput.setOperator(Constants.DEFAULT_OPERATOR_SYSTEM);
        pointsConsumeInput.setMerchantNo("merchant");

        HttpPostWithJson.httpPostWithJson(pointsConsumeInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.CONSUME_PATH, PointsConsumeOutput.class);
    }

    static void reverse(String sysTransNo) throws Exception {
        PointsReverseInput pointsReverseInput = new PointsReverseInput();
        pointsReverseInput.setOperator("reverse");
        pointsReverseInput.setInstitutionNo(Constants.DEFAULT_INSTITUTION_NO);
        pointsReverseInput.setSysTransNo(String.valueOf(Sequence.getId()));
        pointsReverseInput.setReverseSysTransNo(sysTransNo);

        HttpPostWithJson.httpPostWithJson(pointsReverseInput, RestPathConstants.TRANS.PATH + RestPathConstants.TRANS.REVERSE_PATH, PointsReverseOutput.class);
    }
}
