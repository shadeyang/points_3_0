package com.wt2024.points.repository.account.entity;

import java.util.Date;

public class PointsTransExpire {
    private Long id;

    private String customerId;

    private String pointsTypeNo;

    private String transNo;

    private Date endDate;

    public PointsTransExpire(Long id, String customerId, String pointsTypeNo, String transNo, Date endDate) {
        this.id = id;
        this.customerId = customerId;
        this.pointsTypeNo = pointsTypeNo;
        this.transNo = transNo;
        this.endDate = endDate;
    }

    public PointsTransExpire() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo == null ? null : transNo.trim();
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}