package com.wt2024.points.common.enums;

import java.util.Arrays;

/**
 * @ClassName Gender
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/5/19
 * @Version V1.0
 **/
public enum Gender {
    MALE("0", "男"),
    FEMALE("1", "女"),
    UNKNOWN("2", "未知"),
    ;
    private String type;
    private String desc;

    private Gender(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static Gender getEnum(String type) {
        return Arrays.asList(Gender.values()).stream()
                .filter(gender -> gender.getType().equals(type))
                .findFirst().orElse(Gender.UNKNOWN);
    }
}
