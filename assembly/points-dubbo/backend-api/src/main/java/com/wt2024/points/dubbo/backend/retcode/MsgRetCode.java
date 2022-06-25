package com.wt2024.points.dubbo.backend.retcode;

public enum MsgRetCode {
    //通讯层使用成功响应码
    COMM_0000("0000", "通讯成功"),
    COMM_0011("0011", "系统繁忙，请稍后再试"),
    COMM_0032("0032", "客户端IP[%s]地址不匹配"),
    COMM_0040("0040", "必要参数%s缺失"),
    COMM_0041("0041", "交易请求过期，请同步国际标准时间"),
    COMM_0042("0042", "机构%s未开放对应权限"),
    COMM_0043("0043", "机构%s未开放接口%s对应权限"),
    COMM_0044("0044", "机构%s未设置"),
    COMM_0045("0045", "签名验证不通过"),
    COMM_0046("0046", "机构%s缺少密钥配置"),
    COMM_9000("9000", "本地调用服务失败：%s"),

    //业务层响应码
    //0开头对应所有模块公共错误码
    TRANS_0000("0000", "交易成功"),
    TRANS_0001("0001", "参数为空"),
    TRANS_0002("0002", "当前系统%s，版本%s"),
    TRANS_0005("0005", "交易时异常，请稍后再试"),
    TRANS_0047("0047", "数据校验不通过%s"),

    TRANS_0050("0050", "第三方服务失败：【%s】%s"),
    ;

    private String retCode;
    private String retShow;

    private MsgRetCode(String retCode, String retShow) {
        this.retCode = retCode;
        this.retShow = retShow;
    }

    public String getRetCode() {
        return retCode;
    }

    public String getRetShow() {
        return retShow;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public void setRetShow(String retShow) {
        this.retShow = retShow;
    }

    @Override
    public String toString() {
        return "MsgRetCode{" +
                "retCode='" + retCode + '\'' +
                ", retShow='" + retShow + '\'' +
                '}';
    }
}
