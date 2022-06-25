package com.wt2024.points.repository.merchant.entity;

public class Merchant {
    private String merchantNo;

    private String merchantName;

    private String contacts;

    private String phone;

    private String email;

    private String address;

    private String institutionId;

    private String operator;

    private String status;

    public Merchant(String merchantNo, String merchantName, String contacts, String phone, String email, String address, String institutionId, String operator, String status) {
        this.merchantNo = merchantNo;
        this.merchantName = merchantName;
        this.contacts = contacts;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.institutionId = institutionId;
        this.operator = operator;
        this.status = status;
    }

    public Merchant() {
        super();
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo == null ? null : merchantNo.trim();
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName == null ? null : merchantName.trim();
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts == null ? null : contacts.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId == null ? null : institutionId.trim();
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
}