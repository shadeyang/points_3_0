package com.wt2024.points.repository.account.entity;

import java.math.BigDecimal;
import java.util.Date;

public class PointsTransDetails {
    private String transNo;

    private String customerId;

    private String sourceTransNo;

    private String pointsTypeNo;

    private Date endDate;

    private BigDecimal pointsAmount;

    private String merchantNo;

    private String costLine;

    private BigDecimal clearingAmt;

    public PointsTransDetails(String transNo, String customerId, String sourceTransNo, String pointsTypeNo, Date endDate, BigDecimal pointsAmount, String merchantNo, String costLine, BigDecimal clearingAmt) {
        this.transNo = transNo;
        this.customerId = customerId;
        this.sourceTransNo = sourceTransNo;
        this.pointsTypeNo = pointsTypeNo;
        this.endDate = endDate;
        this.pointsAmount = pointsAmount;
        this.merchantNo = merchantNo;
        this.costLine = costLine;
        this.clearingAmt = clearingAmt;
    }

    public PointsTransDetails() {
        super();
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo == null ? null : transNo.trim();
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId == null ? null : customerId.trim();
    }

    public String getSourceTransNo() {
        return sourceTransNo;
    }

    public void setSourceTransNo(String sourceTransNo) {
        this.sourceTransNo = sourceTransNo == null ? null : sourceTransNo.trim();
    }

    public String getPointsTypeNo() {
        return pointsTypeNo;
    }

    public void setPointsTypeNo(String pointsTypeNo) {
        this.pointsTypeNo = pointsTypeNo == null ? null : pointsTypeNo.trim();
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getPointsAmount() {
        return pointsAmount;
    }

    public void setPointsAmount(BigDecimal pointsAmount) {
        this.pointsAmount = pointsAmount;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo == null ? null : merchantNo.trim();
    }

    public String getCostLine() {
        return costLine;
    }

    public void setCostLine(String costLine) {
        this.costLine = costLine;
    }

    public BigDecimal getClearingAmt() {
        return clearingAmt;
    }

    public void setClearingAmt(BigDecimal clearingAmt) {
        this.clearingAmt = clearingAmt;
    }
}