package com.wt2024.points.restful.backend.constant;

import java.time.ZoneOffset;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2019-01-09 10:56
 * @Project openapi:com.wt2024.points.constant
 */
public class Constants extends com.wt2024.base.Constants{

    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(8);
    public static final String SWAGGER_SKIPPED_INSTITUTION_NO = "100001";
    public static final String DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_TIME_FORMATTER_HHMMSS = "HHmmss";

    public static final String CONSTANT_REST = "rest";

    public static final String FLAG_YES = "Y";
    public static final String FLAG_NO = "N";
    /**
     * 请求头时间戳
     **/
    public static final String CONSTANT_HEADER_TIMESTAMP = "timestamp";

    //超时设置
    public static final long CONSTANT_HEADER_TIMESTAMP_EXPIRE = 5 * 60 * 1000L;

    /**
     * 请求头银行编号
     **/
    public static final String CONSTANT_HEADER_INSTITUTION = "institution";

    /**
     * 请求头签名字段
     */
    public static final String CONSTANT_HEADER_SIGN = "sign";

    public static final String DELIMITER = "_";

    public static final String DEFAULT_OPERATOR_SYSTEM = "system";
    public static final String DEFAULT_SYSTEM_TRANS_CHANNEL = "001";

}
