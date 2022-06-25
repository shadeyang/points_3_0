package com.wt2024.points.common.enums;

import java.util.Arrays;

/**
 * @ClassName TransTypeno
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/8/10
 * @Version V1.0
 **/
public enum TransType {
    GAIN_ASSERT_POINT("0001", "获资产业务积分", DebitOrCredit.CREDIT),
    GAIN_CONSUME_POINT("0002", "获消费积分", DebitOrCredit.CREDIT),
    GAIN_AWARD_POINT("0003", "获奖励业务积分", DebitOrCredit.CREDIT),
    GAIN_INVEST_POINT("0004", "获投资业务积分", DebitOrCredit.CREDIT),
    TRANSFER_IN_POINT("0006", "转入积分", DebitOrCredit.CREDIT),
    ACTIVITY_POINT("0009", "活动积分", DebitOrCredit.CREDIT),
    GAIN_TEST_POINT("0010", "获测试积分", DebitOrCredit.CREDIT),
    RECHARGE_IN_POINT("0101", "充值转入", DebitOrCredit.CREDIT),
    CONSUME_POINT("1001", "消费", DebitOrCredit.DEBIT),
    TRANSFER_OUT_POINT("1002", "转让", DebitOrCredit.DEBIT),
    FEE_POINT("1003", "手续费", DebitOrCredit.DEBIT),
    DUE_POINT("1004", "过期冲销", DebitOrCredit.DEBIT),
    RECHARGE_OUT_POINT("1101", "充值转出", DebitOrCredit.DEBIT),
    ;


    private String code;
    private String desc;
    private DebitOrCredit debitOrCredit;

    TransType(String code, String desc, DebitOrCredit debitOrCredit) {
        this.code = code;
        this.desc = desc;
        this.debitOrCredit = debitOrCredit;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public DebitOrCredit getDebitOrCredit() {
        return debitOrCredit;
    }

    public void setDebitOrCredit(DebitOrCredit debitOrCredit) {
        this.debitOrCredit = debitOrCredit;
    }

    public static TransType getEnum(String code) {
        return Arrays.asList(TransType.values()).stream()
                .filter(transType -> transType.getCode().equals(code))
                .findFirst().orElse(null);
    }
}
