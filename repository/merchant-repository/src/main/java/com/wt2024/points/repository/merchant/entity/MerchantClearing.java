package com.wt2024.points.repository.merchant.entity;

import java.math.BigDecimal;

public class MerchantClearing {
    private String merchantNo;

    private String transNo;

    private String transDate;

    private String transTime;

    private String oldTransNo;

    private String sysTransNo;

    private String pointsTypeNo;

    private BigDecimal pointsAmount;

    private String institutionId;

    private String reversedFlag;

    private BigDecimal clearingAmt;

    private String description;

    public MerchantClearing(String merchantNo, String transNo, String transDate, String transTime, String oldTransNo, String sysTransNo, String pointsTypeNo, BigDecimal pointsAmount, String institutionId, String reversedFlag, BigDecimal clearingAmt) {
        this.merchantNo = merchantNo;
        this.transNo = transNo;
        this.transDate = transDate;
        this.transTime = transTime;
        this.oldTransNo = oldTransNo;
        this.sysTransNo = sysTransNo;
        this.pointsTypeNo = pointsTypeNo;
        this.pointsAmount = pointsAmount;
        this.institutionId = institutionId;
        this.reversedFlag = reversedFlag;
        this.clearingAmt = clearingAmt;
    }

    public MerchantClearing(String merchantNo, String transNo, String transDate, String transTime, String oldTransNo, String sysTransNo, String pointsTypeNo, BigDecimal pointsAmount, String institutionId, String reversedFlag, BigDecimal clearingAmt, String description) {
        this.merchantNo = merchantNo;
        this.transNo = transNo;
        this.transDate = transDate;
        this.transTime = transTime;
        this.oldTransNo = oldTransNo;
        this.sysTransNo = sysTransNo;
        this.pointsTypeNo = pointsTypeNo;
        this.pointsAmount = pointsAmount;
        this.institutionId = institutionId;
        this.reversedFlag = reversedFlag;
        this.clearingAmt = clearingAmt;
        this.description = description;
    }

    public MerchantClearing() {
        super();
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo == null ? null : merchantNo.trim();
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo == null ? null : transNo.trim();
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate == null ? null : transDate.trim();
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime == null ? null : transTime.trim();
    }

    public String getOldTransNo() {
        return oldTransNo;
    }

    public void setOldTransNo(String oldTransNo) {
        this.oldTransNo = oldTransNo == null ? null : oldTransNo.trim();
    }

    public String getSysTransNo() {
        return sysTransNo;
    }

    public void setSysTransNo(String sysTransNo) {
        this.sysTransNo = sysTransNo == null ? null : sysTransNo.trim();
    }

    public String getPointsTypeNo() {
        return pointsTypeNo;
    }

    public void setPointsTypeNo(String pointsTypeNo) {
        this.pointsTypeNo = pointsTypeNo == null ? null : pointsTypeNo.trim();
    }

    public BigDecimal getPointsAmount() {
        return pointsAmount;
    }

    public void setPointsAmount(BigDecimal pointsAmount) {
        this.pointsAmount = pointsAmount;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId == null ? null : institutionId.trim();
    }

    public String getReversedFlag() {
        return reversedFlag;
    }

    public void setReversedFlag(String reversedFlag) {
        this.reversedFlag = reversedFlag == null ? null : reversedFlag.trim();
    }

    public BigDecimal getClearingAmt() {
        return clearingAmt;
    }

    public void setClearingAmt(BigDecimal clearingAmt) {
        this.clearingAmt = clearingAmt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}