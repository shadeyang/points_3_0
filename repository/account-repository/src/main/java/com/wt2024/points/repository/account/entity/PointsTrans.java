package com.wt2024.points.repository.account.entity;

import java.math.BigDecimal;
import java.util.Date;

public class PointsTrans {
    private Long id;

    private String transNo;

    private String customerId;

    private String pointsTypeNo;

    private String institutionId;

    private String transDate;

    private String transTime;

    private Long transTimestamp;

    private String transTypeNo;

    private String debitOrCredit;

    private Date endDate;

    private BigDecimal pointsAmount;

    private String reversedFlag;

    private String oldTransNo;

    private String transChannel;

    private String merchantNo;

    private String voucherTypeNo;

    private String voucherNo;

    private String transStatus;

    private String operator;

    private String sysTransNo;

    private Integer rulesId;

    private String costLine;

    private BigDecimal clearingAmt;

    private String description;

    public PointsTrans(Long id, String transNo, String customerId, String pointsTypeNo, String institutionId, String transDate, String transTime, Long transTimestamp, String transTypeNo, String debitOrCredit, Date endDate, BigDecimal pointsAmount, String reversedFlag, String oldTransNo, String transChannel, String merchantNo, String voucherTypeNo, String voucherNo, String transStatus, String operator, String sysTransNo, Integer rulesId, String costLine, BigDecimal clearingAmt) {
        this.id = id;
        this.transNo = transNo;
        this.customerId = customerId;
        this.pointsTypeNo = pointsTypeNo;
        this.institutionId = institutionId;
        this.transDate = transDate;
        this.transTime = transTime;
        this.transTimestamp = transTimestamp;
        this.transTypeNo = transTypeNo;
        this.debitOrCredit = debitOrCredit;
        this.endDate = endDate;
        this.pointsAmount = pointsAmount;
        this.reversedFlag = reversedFlag;
        this.oldTransNo = oldTransNo;
        this.transChannel = transChannel;
        this.merchantNo = merchantNo;
        this.voucherTypeNo = voucherTypeNo;
        this.voucherNo = voucherNo;
        this.transStatus = transStatus;
        this.operator = operator;
        this.sysTransNo = sysTransNo;
        this.rulesId = rulesId;
        this.costLine = costLine;
        this.clearingAmt = clearingAmt;
    }

    public PointsTrans(Long id, String transNo, String customerId, String pointsTypeNo, String institutionId, String transDate, String transTime, Long transTimestamp, String transTypeNo, String debitOrCredit, Date endDate, BigDecimal pointsAmount, String reversedFlag, String oldTransNo, String transChannel, String merchantNo, String voucherTypeNo, String voucherNo, String transStatus, String operator, String sysTransNo, Integer rulesId, String costLine, BigDecimal clearingAmt, String description) {
        this.id = id;
        this.transNo = transNo;
        this.customerId = customerId;
        this.pointsTypeNo = pointsTypeNo;
        this.institutionId = institutionId;
        this.transDate = transDate;
        this.transTime = transTime;
        this.transTimestamp = transTimestamp;
        this.transTypeNo = transTypeNo;
        this.debitOrCredit = debitOrCredit;
        this.endDate = endDate;
        this.pointsAmount = pointsAmount;
        this.reversedFlag = reversedFlag;
        this.oldTransNo = oldTransNo;
        this.transChannel = transChannel;
        this.merchantNo = merchantNo;
        this.voucherTypeNo = voucherTypeNo;
        this.voucherNo = voucherNo;
        this.transStatus = transStatus;
        this.operator = operator;
        this.sysTransNo = sysTransNo;
        this.rulesId = rulesId;
        this.costLine = costLine;
        this.clearingAmt = clearingAmt;
        this.description = description;
    }

    public PointsTrans() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPointsTypeNo() {
        return pointsTypeNo;
    }

    public void setPointsTypeNo(String pointsTypeNo) {
        this.pointsTypeNo = pointsTypeNo == null ? null : pointsTypeNo.trim();
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId == null ? null : institutionId.trim();
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

    public Long getTransTimestamp() {
        return transTimestamp;
    }

    public void setTransTimestamp(Long transTimestamp) {
        this.transTimestamp = transTimestamp;
    }

    public String getTransTypeNo() {
        return transTypeNo;
    }

    public void setTransTypeNo(String transTypeNo) {
        this.transTypeNo = transTypeNo == null ? null : transTypeNo.trim();
        ;
    }

    public String getDebitOrCredit() {
        return debitOrCredit;
    }

    public void setDebitOrCredit(String debitOrCredit) {
        this.debitOrCredit = debitOrCredit == null ? null : debitOrCredit.trim();
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

    public String getReversedFlag() {
        return reversedFlag;
    }

    public void setReversedFlag(String reversedFlag) {
        this.reversedFlag = reversedFlag == null ? null : reversedFlag.trim();
    }

    public String getOldTransNo() {
        return oldTransNo;
    }

    public void setOldTransNo(String oldTransNo) {
        this.oldTransNo = oldTransNo == null ? null : oldTransNo.trim();
    }

    public String getTransChannel() {
        return transChannel;
    }

    public void setTransChannel(String transChannel) {
        this.transChannel = transChannel == null ? null : transChannel.trim();
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo == null ? null : merchantNo.trim();
    }

    public String getVoucherTypeNo() {
        return voucherTypeNo;
    }

    public void setVoucherTypeNo(String voucherTypeNo) {
        this.voucherTypeNo = voucherTypeNo == null ? null : voucherTypeNo.trim();
    }

    public String getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo) {
        this.voucherNo = voucherNo == null ? null : voucherNo.trim();
    }

    public String getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(String transStatus) {
        this.transStatus = transStatus == null ? null : transStatus.trim();
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public String getSysTransNo() {
        return sysTransNo;
    }

    public void setSysTransNo(String sysTransNo) {
        this.sysTransNo = sysTransNo == null ? null : sysTransNo.trim();
    }

    public Integer getRulesId() {
        return rulesId;
    }

    public void setRulesId(Integer rulesId) {
        this.rulesId = rulesId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}