package com.wt2024.points.repository.customer.entity;

public class Customer {
    private String customerId;

    private String customerName;

    private String gender;

    private String phone;

    private String email;

    private String address;

    private String birthdate;

    private String institutionId;

    private String operator;

    private String customerLvl;

    public Customer(String customerId, String customerName, String gender, String phone, String email, String address, String birthdate, String institutionId, String operator, String customerLvl) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.birthdate = birthdate;
        this.institutionId = institutionId;
        this.operator = operator;
        this.customerLvl = customerLvl;
    }

    public Customer() {
        super();
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId == null ? null : customerId.trim();
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName == null ? null : customerName.trim();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender == null ? null : gender.trim();
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

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate == null ? null : birthdate.trim();
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

    public String getCustomerLvl() {
        return customerLvl;
    }

    public void setCustomerLvl(String customerLvl) {
        this.customerLvl = customerLvl == null ? null : customerLvl.trim();
    }
}