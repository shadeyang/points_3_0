package com.wt2024.base.entity;

public class SysParameter extends BaseEntity {
    private String paramKey;

    private String paramDesc;

    private String paramValue1;

    private String paramValue2;

    private String paramValue3;

    private String paramStatus;

    private String paramModule;

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey == null ? null : paramKey.trim();
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc == null ? null : paramDesc.trim();
    }

    public String getParamValue1() {
        return paramValue1;
    }

    public void setParamValue1(String paramValue1) {
        this.paramValue1 = paramValue1 == null ? null : paramValue1.trim();
    }

    public String getParamValue2() {
        return paramValue2;
    }

    public void setParamValue2(String paramValue2) {
        this.paramValue2 = paramValue2 == null ? null : paramValue2.trim();
    }

    public String getParamValue3() {
        return paramValue3;
    }

    public void setParamValue3(String paramValue3) {
        this.paramValue3 = paramValue3 == null ? null : paramValue3.trim();
    }

    public String getParamStatus() {
        return paramStatus;
    }

    public void setParamStatus(String paramStatus) {
        this.paramStatus = paramStatus == null ? null : paramStatus.trim();
    }

    public String getParamModule() {
        return paramModule;
    }

    public void setParamModule(String paramModule) {
        this.paramModule = paramModule == null ? null : paramModule.trim();
    }
}