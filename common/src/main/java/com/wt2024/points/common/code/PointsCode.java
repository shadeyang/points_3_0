package com.wt2024.points.common.code;

import java.util.Arrays;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/15 17:03
 * @project points3.0:com.wt2024.points.common
 */
public enum PointsCode {

    //通讯层使用成功响应码
    COMM_0000("C000", "通讯成功", PointsCode.COMM_TYPE),
    COMM_0011("C011", "系统繁忙，请稍后再试", PointsCode.COMM_TYPE),
    COMM_0032("C032", "客户端IP[%s]地址不匹配", PointsCode.COMM_TYPE),
    COMM_0040("C040", "必要参数%s缺失", PointsCode.COMM_TYPE),
    COMM_0041("C041", "交易请求过期，请同步国际标准时间", PointsCode.COMM_TYPE),
    COMM_0042("C042", "机构%s未开放对应权限", PointsCode.COMM_TYPE),
    COMM_0043("C043", "机构%s未开放接口%s对应权限", PointsCode.COMM_TYPE),
    COMM_0044("C044", "机构%s未设置", PointsCode.COMM_TYPE),
    COMM_0045("C045", "签名验证不通过", PointsCode.COMM_TYPE),
    COMM_0046("C046", "机构%s缺少密钥配置", PointsCode.COMM_TYPE),
    COMM_9000("C900", "本地调用服务失败：%s", PointsCode.COMM_TYPE),

    //业务层响应码
    //0开头对应所有模块公共错误码
    TRANS_0000("0000", "交易成功", PointsCode.TRANS_TYPE),
    TRANS_0001("0001", "参数为空", PointsCode.TRANS_TYPE),
    TRANS_0002("0002", "当前系统%s，版本%s", PointsCode.TRANS_TYPE),
    TRANS_0005("0005", "交易时异常，请稍后再试", PointsCode.TRANS_TYPE),
    TRANS_0047("0047", "数据校验不通过 %s", PointsCode.TRANS_TYPE),

    TRANS_0050("0050", "第三方服务失败：【%s】%s", PointsCode.TRANS_TYPE),

    //客户
    TRANS_1001("1001", "客户编号不存在", PointsCode.TRANS_TYPE),
    TRANS_1002("1002", "客户凭证已存在", PointsCode.TRANS_TYPE),
    TRANS_1003("1003", "凭证信息不存在", PointsCode.TRANS_TYPE),
    TRANS_1004("1004", "订单不存在", PointsCode.TRANS_TYPE),
    //机构
    TRANS_1101("1101", "机构信息不存在", PointsCode.TRANS_TYPE),
    TRANS_1102("1102", "成本核算条线不正确", PointsCode.TRANS_TYPE),
    TRANS_1103("1103", "机构号已存在【%s】，不可重复添加", PointsCode.TRANS_TYPE),
    TRANS_1104("1104", "机构号关键信息不可修改", PointsCode.TRANS_TYPE),
    TRANS_1105("1105", "机构号【%s】已存在子机构", PointsCode.TRANS_TYPE),
    //积分
    TRANS_2001("2001", "日期格式应为yyyyMMdd", PointsCode.TRANS_TYPE),
    TRANS_2002("2002", "借贷标记错误", PointsCode.TRANS_TYPE),
    TRANS_2003("2003", "发起方交易流水已存在", PointsCode.TRANS_TYPE),
    TRANS_2004("2004", "积分商户号%s不存在/已停用", PointsCode.TRANS_TYPE),
    TRANS_2005("2005", "积分类型%s不存在", PointsCode.TRANS_TYPE),
    TRANS_2006("2006", "日期%s已过期", PointsCode.TRANS_TYPE),
    TRANS_2007("2007", "发起方交易流水正在进行交易，请稍后查询结果", PointsCode.TRANS_TYPE),
    TRANS_2008("2008", "发起方交易流水不存在", PointsCode.TRANS_TYPE),
    TRANS_2009("2009", "当前账户交易繁忙，请稍后再试", PointsCode.TRANS_TYPE),
    TRANS_2010("2010", "可用积分余额不足", PointsCode.TRANS_TYPE),
    TRANS_2011("2011", "此交易流水已冲正/退货", PointsCode.TRANS_TYPE),
    TRANS_2012("2012", "原始交易为冲正流水", PointsCode.TRANS_TYPE),
    TRANS_2013("2013", "跨月交易不可冲正/退货", PointsCode.TRANS_TYPE),
    TRANS_2014("2014", "存在记账中交易，请稍后再重新发起交易", PointsCode.TRANS_TYPE),
    TRANS_2015("2015", "原始交易贷入积分已被使用，不可冲正", PointsCode.TRANS_TYPE),
    TRANS_2016("2016", "原始交易存在多笔流水", PointsCode.TRANS_TYPE),
    TRANS_2017("2017", "此积分交易流水不存在", PointsCode.TRANS_TYPE),
    TRANS_2018("2018", "过期冲销交易不可冲正/退货", PointsCode.TRANS_TYPE),
    TRANS_2019("2019", "原始交易非普通借出交易，不可退货", PointsCode.TRANS_TYPE),
    TRANS_2020("2020", "原始交易已被冲正，不可退货", PointsCode.TRANS_TYPE),
    TRANS_2021("2021", "原始交易存在异常，不可退货", PointsCode.TRANS_TYPE),
    TRANS_2022("2022", "可用退货积分不足", PointsCode.TRANS_TYPE),
    TRANS_2023("2023", "当前账户被锁定，不可进行积分相关交易", PointsCode.TRANS_TYPE),

    TRANS_2101("2101", "异步处理账务异常%s", PointsCode.TRANS_TYPE),
    TRANS_2102("2102", "异步处理清算异常%s", PointsCode.TRANS_TYPE),


    TRANS_3000("3000", "传入文件异常", PointsCode.TRANS_TYPE),
    TRANS_3001("3001", "查询文件不存在", PointsCode.TRANS_TYPE),


    ;

    public final static String COMM_TYPE = "COMM";
    public final static String TRANS_TYPE = "TRANS";

    private String code;
    private String show;
    private String type;

    private PointsCode(String code, String show, String type) {
        this.code = code;
        this.show = show;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public String getShow() {
        return show;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "PointsCode{" +
                "code='" + code + '\'' +
                ", show='" + show + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public static PointsCode matches(String code) {
        return Arrays.stream(PointsCode.values()).filter(pointsCode -> pointsCode.getCode().equals(code)).findFirst().orElseGet(null);
    }
}
