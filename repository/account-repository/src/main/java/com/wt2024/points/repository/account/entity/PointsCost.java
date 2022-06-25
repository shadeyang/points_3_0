package com.wt2024.points.repository.account.entity;

public class PointsCost {
    private String costLine;

    private String institutionId;

    private String costName;

    public PointsCost(String costLine, String institutionId, String costName) {
        this.costLine = costLine;
        this.institutionId = institutionId;
        this.costName = costName;
    }

    public PointsCost() {
        super();
    }

    public String getCostLine() {
        return costLine;
    }

    public void setCostLine(String costLine) {
        this.costLine = costLine;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId == null ? null : institutionId.trim();
    }

    public String getCostName() {
        return costName;
    }

    public void setCostName(String costName) {
        this.costName = costName == null ? null : costName.trim();
    }
}