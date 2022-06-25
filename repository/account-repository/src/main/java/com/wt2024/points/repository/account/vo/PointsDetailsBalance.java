package com.wt2024.points.repository.account.vo;

import java.math.BigDecimal;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/16 15:07
 * @project points3.0:com.wt2024.points.repository.account.vo
 */
public class PointsDetailsBalance {

    private String transNo;

    private String customerId;

    private String pointsTypeNo;

    private BigDecimal pointsAmount;

    public PointsDetailsBalance(String transNo, String customerId, String pointsTypeNo,  BigDecimal pointsAmount) {
        this.transNo = transNo;
        this.customerId = customerId;
        this.pointsTypeNo = pointsTypeNo;
        this.pointsAmount = pointsAmount;
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPointsTypeNo() {
        return pointsTypeNo;
    }

    public void setPointsTypeNo(String pointsTypeNo) {
        this.pointsTypeNo = pointsTypeNo;
    }

    public BigDecimal getPointsAmount() {
        return pointsAmount;
    }

    public void setPointsAmount(BigDecimal pointsAmount) {
        this.pointsAmount = pointsAmount;
    }
}
