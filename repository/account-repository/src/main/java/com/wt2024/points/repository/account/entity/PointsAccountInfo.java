package com.wt2024.points.repository.account.entity;

import java.math.BigDecimal;

public class PointsAccountInfo {
    private String customerId;

    private String pointsTypeNo;

    private BigDecimal pointsBalance;

    private BigDecimal freezingPoints;

    private BigDecimal inTransitPoints;

    private String pointsAccountStatus;

    public PointsAccountInfo(String customerId, String pointsTypeNo, BigDecimal pointsBalance, BigDecimal freezingPoints, BigDecimal inTransitPoints, String pointsAccountStatus) {
        this.customerId = customerId;
        this.pointsTypeNo = pointsTypeNo;
        this.pointsBalance = pointsBalance;
        this.freezingPoints = freezingPoints;
        this.inTransitPoints = inTransitPoints;
        this.pointsAccountStatus = pointsAccountStatus;
    }

    public PointsAccountInfo() {
        super();
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId == null ? null : customerId.trim();
    }

    public String getPointsTypeNo() {
        return pointsTypeNo;
    }

    public void setPointsTypeNo(String pointsTypeNo) {
        this.pointsTypeNo = pointsTypeNo == null ? null : pointsTypeNo.trim();
    }

    public BigDecimal getPointsBalance() {
        return pointsBalance;
    }

    public void setPointsBalance(BigDecimal pointsBalance) {
        this.pointsBalance = pointsBalance;
    }

    public BigDecimal getFreezingPoints() {
        return freezingPoints;
    }

    public void setFreezingPoints(BigDecimal freezingPoints) {
        this.freezingPoints = freezingPoints;
    }

    public BigDecimal getInTransitPoints() {
        return inTransitPoints;
    }

    public void setInTransitPoints(BigDecimal inTransitPoints) {
        this.inTransitPoints = inTransitPoints;
    }

    public String getPointsAccountStatus() {
        return pointsAccountStatus;
    }

    public void setPointsAccountStatus(String pointsAccountStatus) {
        this.pointsAccountStatus = pointsAccountStatus == null ? null : pointsAccountStatus.trim();
    }
}