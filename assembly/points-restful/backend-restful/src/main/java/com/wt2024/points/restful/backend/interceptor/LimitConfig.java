package com.wt2024.points.restful.backend.interceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName LimitConfig
 * @Description: TODO
 * @Author shade.yang
 * @Date 2020/1/21
 * @Version V1.0
 **/
public class LimitConfig {

    /** 配置名 **/
    private String configName;
    /** 统计配置键值 **/
    private String configKey;
    /** 计数器上限 **/
    private int limitMax;
    /** 计数器时间 **/
    private long countTime;
    /** 额外配置 **/
    private Map<String,Integer> excludeConfig = new HashMap<String, Integer>();

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public int getLimitMax() {
        return limitMax;
    }

    public void setLimitMax(int limitMax) {
        this.limitMax = limitMax;
    }

    public long getCountTime() {
        return countTime;
    }

    public void setCountTime(long countTime) {
        this.countTime = countTime;
    }

    public Map<String, Integer> getExcludeConfig() {
        return excludeConfig;
    }

    public void setExcludeConfig(Map<String, Integer> excludeConfig) {
        this.excludeConfig = excludeConfig;
    }
}