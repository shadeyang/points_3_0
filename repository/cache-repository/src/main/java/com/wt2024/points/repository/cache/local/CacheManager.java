package com.wt2024.points.repository.cache.local;

import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/6/21 14:18
 * @Project uxun-framework:com.uxunchina.framework.utils
 */
@Slf4j
public class CacheManager {

    private static Map<String, CacheData> CACHE_DATA = new ConcurrentHashMap<String, CacheData>();

    private static Map<String, Long> KEYS_DATA = new ConcurrentHashMap<String, Long>();

    public static boolean isClear = false;

    /**
     * 清理缓存极限 后期再实现
     **/
    public static long clearLimit = 10000;

    public static long clearLimitInit = 10000;

    public static <T> T getData(String key, Load<T> load, int expire) {
        T data = getData(key);
        if (data == null && load != null) {
            data = load.load();
            if (data != null) {
                setData(key, data, expire);
            }
        }
        return data;
    }

    public static <T> T getDataMillis(String key, Load<T> load, long expire) {
        T data = getData(key);
        if (data == null && load != null) {
            data = load.load();
            if (data != null) {
                setDataMillis(key, data, expire);
            }
        }
        return data;
    }

    public static <T> T getDataMillis(String key, T data, long expire) {
        T datatemp = getData(key);
        if (datatemp == null && data != null) {
            datatemp = data;
        }
        setDataMillis(key, datatemp, expire);
        return datatemp;
    }

    public static <T> T getData(String key) {
        cleareExpiredThread();
        CacheData<T> data = CACHE_DATA.get(key);
        if (data != null && (data.getExpire() <= 0 || data.getSaveTime() >= System.currentTimeMillis())) {
            return data.getData();
        } else if (data != null) {
            clear(key);
        }
        return null;
    }

    /**
     * 缓存对象秒时长
     *
     * @param key
     * @param data
     * @param expire
     * @param <T>
     */
    public static <T> void setData(String key, T data, int expire) {
        cleareExpiredThread();
        CACHE_DATA.put(key, new CacheData(data, expire * 1000));
        KEYS_DATA.put(key, expire <= 0 ? 0 : (System.currentTimeMillis() + (expire * 1000)));
    }

    /**
     * 缓存对象毫秒时长
     *
     * @param key
     * @param data
     * @param expire
     * @param <T>
     */
    public static <T> void setDataMillis(String key, T data, long expire) {
        cleareExpiredThread();
        CACHE_DATA.put(key, new CacheData(data, expire));
        KEYS_DATA.put(key, expire <= 0 ? 0 : (System.currentTimeMillis() + expire));
    }

    public static void clear(String key) {
        CACHE_DATA.remove(key);
        KEYS_DATA.remove(key);
    }

    public static void clearAll() {
        CACHE_DATA.clear();
        KEYS_DATA.clear();
    }

    public interface Load<T> {
        T load();
    }

    private static class CacheData<T> {
        CacheData(T t, long expire) {
            this.data = t;
            this.expire = expire <= 0 ? 0 : expire;
            this.saveTime = System.currentTimeMillis() + this.expire;
        }

        private T data;
        private long saveTime; // 存活时间
        private long expire;   // 过期时间 小于等于0标识永久存活

        public T getData() {
            return data;
        }

        public long getExpire() {
            return expire;
        }

        public long getSaveTime() {
            return saveTime;
        }
    }

    private static void cleareExpiredThread() {
        clearLimit--;
        try {
            if (isClear || clearLimit > 0) {
                return;
            }
            isClear = true;
            new Thread(new Runnable() {
                private Long timestamp = System.currentTimeMillis();

                @Override
                public void run() {
                    Map<String, Long> temp = KEYS_DATA;
                    Iterator it = temp.entrySet().iterator();
                    long foreverCount = 0;
                    while (it.hasNext()) {
                        Map.Entry<String, Long> entry = (Map.Entry<String, Long>) it.next();
                        if (entry.getValue() > 0 && entry.getValue() <= timestamp) {
                            log.debug("清除过期缓存{}", entry.getKey());
                            clear(entry.getKey());
                        } else if (entry.getValue() <= 0) {
                            foreverCount++;
                            if (log.isDebugEnabled()) {
                                log.debug("常驻内存缓存{}", entry.getKey());
                            }
                        }
                    }
                    if (CACHE_DATA.size() > (clearLimitInit * 0.8)) {
                        log.error("当前缓存数据太多，注意开发代码使用严谨性，size={}，常驻内存缓存数{}", CACHE_DATA.size(), foreverCount);
                    }
                    log.debug("当前缓存数据size={},keysize={}", CACHE_DATA.size(), KEYS_DATA.size());
                    isClear = false;
                }
            }).start();
            clearLimit = clearLimitInit;
        } catch (Exception e) {
            log.error("清除缓存异常", e);
            isClear = false;
        } finally {

        }
    }

}
