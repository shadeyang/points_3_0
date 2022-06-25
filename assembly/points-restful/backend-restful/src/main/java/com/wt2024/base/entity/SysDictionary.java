package com.wt2024.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("sys_dictionary")
public class SysDictionary extends BaseEntity {
    private String dictKey;

    private String dictLable;

    private String dictDesc;

    private String dictValue;

    private Integer dictIndex;

    private String dictStatus;

    private String dictModule;

    public String getDictKey() {
        return dictKey;
    }

    public void setDictKey(String dictKey) {
        this.dictKey = dictKey == null ? null : dictKey.trim();
    }

    public String getDictLable() {
        return dictLable;
    }

    public void setDictLable(String dictLable) {
        this.dictLable = dictLable == null ? null : dictLable.trim();
    }

    public String getDictDesc() {
        return dictDesc;
    }

    public void setDictDesc(String dictDesc) {
        this.dictDesc = dictDesc == null ? null : dictDesc.trim();
    }

    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue == null ? null : dictValue.trim();
    }

    public Integer getDictIndex() {
        return dictIndex;
    }

    public void setDictIndex(Integer dictIndex) {
        this.dictIndex = dictIndex;
    }

    public String getDictStatus() {
        return dictStatus;
    }

    public void setDictStatus(String dictStatus) {
        this.dictStatus = dictStatus == null ? null : dictStatus.trim();
    }

    public String getDictModule() {
        return dictModule;
    }

    public void setDictModule(String dictModule) {
        this.dictModule = dictModule == null ? null : dictModule.trim();
    }
}