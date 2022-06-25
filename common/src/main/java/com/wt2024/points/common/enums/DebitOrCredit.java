package com.wt2024.points.common.enums;

import java.util.Arrays;

/**
 * @ClassName DebitOrCredit
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/8/10
 * @Version V1.0
 **/
public enum DebitOrCredit {

    DEBIT("D"),
    CREDIT("C"),
    ;
    private String code;

    DebitOrCredit(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static DebitOrCredit getEnum(String code) {
        return Arrays.asList(DebitOrCredit.values()).stream()
                .filter(debitOrCredit -> debitOrCredit.getCode().equals(code))
                .findFirst().orElse(null);
    }
}
