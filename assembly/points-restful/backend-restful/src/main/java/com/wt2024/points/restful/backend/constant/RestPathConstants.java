package com.wt2024.points.restful.backend.constant;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/5/26 16:26
 * @Project points2.0:com.wt2024.points.service.constant
 */
public class RestPathConstants {

    public final static class ACCOUNT {
        public final static String PATH = "/account";
        public final static String QUERY_ALL_PATH = "/query/all";
        public final static String QUERY_PATH = "/query";
        public static final String QUERY_DETAIL_PATH = "/query/details";
    }

    public final static class CUSTOMER {
        public final static String PATH = "/customer";
        public final static String QUERY_PATH = "/query";
        public final static String CREATE_PATH = "/create";
    }

    public final static class VOUCHER {
        public final static String PATH = "/voucher";
        public final static String ADD_PATH = "/add";
    }

    public final static class TRANS {
        public final static String PATH = "/trans";
        public final static String QUERY_PATH = "/query";
        public final static String CONSUME_PATH = "/consume";
        public final static String ACC_TRANS_PATH = "/acctrans";
        public final static String QUERY_STATE_PATH = "/state";
        public final static String REVERSE_PATH = "/reverse";
        public final static String BACK_PATH = "/back";
    }

    public final static class INDEX {
        public final static String PATH = "";
        public final static String INDEX_PATH = "/index";
    }

}
