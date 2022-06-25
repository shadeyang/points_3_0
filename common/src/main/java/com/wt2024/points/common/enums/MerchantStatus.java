package com.wt2024.points.common.enums;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/6/1 14:00
 * @project points3.0:com.wt2024.points.common.enums
 */
public enum MerchantStatus {
    
    AVAILABLE("0"),
    DISABLE("1");

    private String status;

    MerchantStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
