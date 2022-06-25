package com.wt2024.points.common.enums;

import java.util.Arrays;

/**
 * @ClassName TransFlag
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/8/10
 * @Version V1.0
 **/
public enum TransFlag {
    WAITING("0"),
    FINISHED("1"),
    PROCESS("2"),
    ;

    private String code;

    TransFlag(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static TransFlag getEnum(String code) {
        return Arrays.asList(TransFlag.values()).stream()
                .filter(transFlag -> transFlag.getCode().equals(code))
                .findFirst().orElse(null);
    }
}
