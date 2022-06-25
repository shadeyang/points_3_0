package com.wt2024.points.repository.customer.entity;

public class Voucher {
    private String customerId;

    private String voucherTypeNo;

    private String voucherNo;

    private String voucherOpenDate;

    public Voucher(String customerId, String voucherTypeNo, String voucherNo, String voucherOpenDate) {
        this.customerId = customerId;
        this.voucherTypeNo = voucherTypeNo;
        this.voucherNo = voucherNo;
        this.voucherOpenDate = voucherOpenDate;
    }

    public Voucher() {
        super();
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId == null ? null : customerId.trim();
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

    public String getVoucherOpenDate() {
        return voucherOpenDate;
    }

    public void setVoucherOpenDate(String voucherOpenDate) {
        this.voucherOpenDate = voucherOpenDate == null ? null : voucherOpenDate.trim();
    }
}