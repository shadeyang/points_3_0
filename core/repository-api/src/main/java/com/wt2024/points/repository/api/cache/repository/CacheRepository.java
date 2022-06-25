package com.wt2024.points.repository.api.cache.repository;

import java.util.concurrent.TimeUnit;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/26 16:43
 * @project points3.0:com.wt2024.points.repository.api.cache.repository
 */
public interface CacheRepository {
    boolean exists(String key);

    <T> T get(String key);

    <T> T get(String prefix, String key);

    void remove(String key);

    void set(String key, Object value);

    void set(String key, Object value, long milliseconds);

    void set(String key, Object value, long timeout, TimeUnit unit);

    boolean lock(String key, long timeout, TimeUnit unit);

    boolean lock(String key, long timeout);

    void unLock(String key);

    void set(String prefix, String key, Object value, long seconds);

    void set(String prefix, String key, String value);
}
