package com.wt2024.points.common.enums;

/**
 * @ClassName AccountEnum
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/5/14
 * @Version V1.0
 **/
public enum AccountStatus {
    AVAILABLE("0"),
    DISABLE("1");

    private String status;

    AccountStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
