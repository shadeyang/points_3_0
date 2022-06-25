package com.wt2024.points.repository.system.entity;

public class Institution {
    private String institutionId;

    private String institutionNo;

    private String institutionName;

    private String parentInstitutionId;

    private String topInstitutionId;

    private String description;

    public Institution(String institutionId, String institutionNo, String institutionName, String parentInstitutionId, String topInstitutionId, String description) {
        this.institutionId = institutionId;
        this.institutionNo = institutionNo;
        this.institutionName = institutionName;
        this.parentInstitutionId = parentInstitutionId;
        this.topInstitutionId = topInstitutionId;
        this.description = description;
    }

    public Institution() {
        super();
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId == null ? null : institutionId.trim();
    }

    public String getInstitutionNo() {
        return institutionNo;
    }

    public void setInstitutionNo(String institutionNo) {
        this.institutionNo = institutionNo == null ? null : institutionNo.trim();
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName == null ? null : institutionName.trim();
    }

    public String getParentInstitutionId() {
        return parentInstitutionId;
    }

    public void setParentInstitutionId(String parentInstitutionId) {
        this.parentInstitutionId = parentInstitutionId == null ? null : parentInstitutionId.trim();
    }

    public String getTopInstitutionId() {
        return topInstitutionId;
    }

    public void setTopInstitutionId(String topInstitutionId) {
        this.topInstitutionId = topInstitutionId == null ? null : topInstitutionId.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}