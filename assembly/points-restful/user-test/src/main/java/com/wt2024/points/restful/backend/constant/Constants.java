package com.wt2024.points.restful.backend.constant;

import java.time.ZoneOffset;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/7 17:22
 * @project points3.0:com.wt2024.points.restful.backend
 */
public class Constants {

    public static final String SERVER_PRIVATE_KEY_FILE = "keys/serverprikey.dat";
    public static final String CLIENT_PUBLIC_KEY_FILE = "keys/clientpubkey.dat";
    public static final String SERVER_THREE_DES_KEY = "546728969452145142781789";

    public static final String DEFAULT_INSTITUTION_NO = "100000";
    public static final String DEFAULT_POINTS_TYPE_NO = "844700918006939648";

    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(8);

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

    public static final String REDIS_POINTS_TRANS_ACCOUNT_KEY = "redis_points_trans_account_key_";

    public static final String REDIS_POINTS_TRANS_SYSTRANSNO_KEY = "redis_points_trans_systransno_key_";

    public static final class PAGE {
        public static final int DEFUALT_INDEX = 1;
        public static final int DEFUALT_PAGESIZE = 10;
    }

    public static final String DEFAULT_OPERATOR_SYSTEM = "system";
    public static final String DEFAULT_SYSTEM_TRANS_CHANNEL = "001";
}
