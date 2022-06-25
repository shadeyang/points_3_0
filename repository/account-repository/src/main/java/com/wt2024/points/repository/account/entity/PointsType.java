package com.wt2024.points.repository.account.entity;

import java.math.BigDecimal;

public class PointsType {
    private String pointsTypeNo;

    private String pointsTypeName;

    private String institutionId;

    private BigDecimal rate;

    private String description;

    public PointsType(String pointsTypeNo, String pointsTypeName, String institutionId, BigDecimal rate, String description) {
        this.pointsTypeNo = pointsTypeNo;
        this.pointsTypeName = pointsTypeName;
        this.institutionId = institutionId;
        this.rate = rate;
        this.description = description;
    }

    public PointsType() {
        super();
    }

    public String getPointsTypeNo() {
        return pointsTypeNo;
    }

    public void setPointsTypeNo(String pointsTypeNo) {
        this.pointsTypeNo = pointsTypeNo == null ? null : pointsTypeNo.trim();
    }

    public String getPointsTypeName() {
        return pointsTypeName;
    }

    public void setPointsTypeName(String pointsTypeName) {
        this.pointsTypeName = pointsTypeName == null ? null : pointsTypeName.trim();
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId == null ? null : institutionId.trim();
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}