package com.wt2024.points.common.enums;

import java.util.Arrays;

/**
 * @ClassName TransStatus
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/8/10
 * @Version V1.0
 **/
public enum TransStatus {
    SUCCESS("0"),
    FAILED("1"),
    ;


    private String code;

    TransStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static TransStatus getEnum(String code) {
        return Arrays.asList(TransStatus.values()).stream()
                .filter(transStatus -> transStatus.getCode().equals(code))
                .findFirst().orElse(null);
    }
}
