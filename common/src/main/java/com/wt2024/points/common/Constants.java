package com.wt2024.points.common;

import java.time.ZoneOffset;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/11 17:11
 * @project points3.0:com.wt2024.points.common
 */
public class Constants {

    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(8);

    public static final String DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_TIME_FORMATTER_HHMMSS = "HHmmss";

    public static final String TIMEZONE = "GMT+8";

    public static final class PAGE {
        public static final int DEFAULT_INDEX = 1;
        public static final int DEFAULT_PAGE_SIZE = 10;
    }
}
