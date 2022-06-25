package com.wt2024.points.common.enums;

import java.util.Arrays;

/**
 * @author shade.yang
 */

public enum VoucherType {

    CUST("cust", "积分客户号"),
    MOBILE("mobile", "手机号码"),
    EMAIL("email", "电子邮箱"),
    IDENTITY("identity", "身份证"),
    THIRDNO("thirdno", "第三方凭证编号"),
    ;

    private String type;
    private String desc;

    VoucherType(String type, String desc) {
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

    public static VoucherType getEnum(String type) {
        return Arrays.asList(VoucherType.values()).stream()
                .filter(voucherType -> voucherType.getType().equals(type))
                .findFirst().orElse(null);
    }
}