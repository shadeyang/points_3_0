package com.wt2024.points.common.enums;

import java.util.Arrays;

/**
 * @ClassName ReversedFlag
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/8/10
 * @Version V1.0
 **/
public enum ReversedFlag {
    NOT_REVERSED("0"),
    REVERSED("1"),
    BACKED("2");


    private String code;

    ReversedFlag(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static ReversedFlag getEnum(String code) {
        return Arrays.asList(ReversedFlag.values()).stream()
                .filter(reversedFlag -> reversedFlag.getCode().equals(code))
                .findFirst().orElse(null);
    }
}
