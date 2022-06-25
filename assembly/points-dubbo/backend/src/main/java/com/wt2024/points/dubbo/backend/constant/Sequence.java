package com.wt2024.points.dubbo.backend.constant;

import com.wt2024.points.dubbo.backend.utils.SnowflakeIdWorker;

/**
 * @ClassName Sequence
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/5/4
 * @Version V1.0
 **/
public class Sequence {

    /**
     * 工作机器ID(0~31)
     */
    public static long workerId;

    /**
     * 数据中心ID(0~31)
     */
    public static long datacenterId = 0;

    public static SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 获取唯一ID
     */
    public static Long getId() {
        if (snowflakeIdWorker == null) {
            snowflakeIdWorker = new SnowflakeIdWorker(workerId, datacenterId);
        }
        return snowflakeIdWorker.nextId();
    }

    public static String getCustomersId() {
        return "CUST" + getId();
    }

    public static String getTransNo() {
        return "TRANS" + getId();
    }
}
