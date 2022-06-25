package com.wt2024.points.repository.cache.redis;

import com.wt2024.points.repository.cache.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Shade.Yang on 2017/7/19.
 */
@Component
@ConditionalOnProperty(
        prefix = "cache",
        name = "type",
        havingValue = Constants.CACHE_TYPE_REDIS,
        matchIfMissing = true
)
@ConditionalOnClass(RedisTemplate.class)
public class RedisCache {

    public static final long DEFAULT_TIMEOUT_METRICS = 30L;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 存储redis键值
     * @param prefix    前缀
     * @param key       键
     * @param value     值
     * @param seconds   缓存秒
     */
    public void set(String prefix, String key, Object value, long seconds) {
        this.set(prefix + key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 存储redis键值（默认缓存30天）
     * @param key   redis键
     * @param value redis值
     */
    public void set(String key, Object value) {
        this.redisTemplate.opsForValue().set(key, value, DEFAULT_TIMEOUT_METRICS, TimeUnit.DAYS);
    }

    /**
     * 存储redis键值（默认缓存30天）
     * @param prefix    redis前缀
     * @param key       redis键
     * @param value     redis值
     */
    public void set(String prefix, String key, String value) {
        this.set(prefix + key, value);
    }

    /**
     * 存储redis键值
     * @param key   redis键
     * @param value redis值
     * @param milliseconds  缓存毫秒
     */
    public void set(String key, Object value, long milliseconds) {
        this.set(key, value, milliseconds, TimeUnit.MILLISECONDS);
    }

    /**
     * 存储redis键值
     * @param key   redis键
     * @param value redis值
     * @param timeout   缓存时间
     * @param unit  时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        this.redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取redis键值
     * @param prefix    redis前缀
     * @param key   redis键
     * @param <T>   redis缓存实体
     * @return redis缓存实体
     */
    public <T> T get(String prefix, String key) {
        return this.get(prefix + key);
    }

    /**
     * 获取redis键值
     * @param key   redis键
     * @param <T>   redis缓存实体
     * @return redis缓存实体
     */
    public <T> T get(String key) {
        return (T) this.redisTemplate.opsForValue().get(key);
    }

    /**
     * 查询redis键
     * @param pattern 匹配参数
     * @return redis键
     */
    public Set<String> keys(String pattern) {
        return this.redisTemplate.keys(pattern);
    }

    /**
     * 校验redis键是否存在
     * @param key   redis键
     * @return 校验redis键是否存在
     */
    public boolean exists(String key) {
        return this.redisTemplate.hasKey(key).booleanValue();
    }

    /**
     * 移除redis缓存
     * @param key redis键
     */
    public void remove(String key) {
        this.redisTemplate.delete(key);
    }

    /**
     * 批量移除redis缓存
     * @param keys  redis键
     */
    public void remove(Collection keys) {
        this.redisTemplate.delete(keys);
    }

    /**
     * redis计数器
     * @param key   redis键
     * @param delta 增量步长
     * @return redis计数器
     */
    public Long increment(String key, long delta) {
        return this.redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 带有时效的redis计数器，不续期
     * @param key   redis键
     * @param delta 增量步长
     * @param timeout   缓存时间
     * @param unit   时间单位
     * @return redis计数器
     */
    public Long incrementAndSetExpireFirstTime(String key, long delta, long timeout, TimeUnit unit) {
        Long count = this.redisTemplate.opsForValue().increment(key, delta);
        if (count.longValue() == 1L) {
            this.redisTemplate.expire(key, timeout, unit);
        }

        return count;
    }

    /**
     * 带有时效的redis计数器，每次调用续期
     * @param key   redis键
     * @param delta 增量步长
     * @param timeout   缓存续期时间
     * @param unit   时间单位
     * @return redis计数器
     */
    public Long incrementAndSetExpireEveryTime(String key, long delta, long timeout, TimeUnit unit) {
        Long count = this.redisTemplate.opsForValue().increment(key, delta);
        this.redisTemplate.expire(key, timeout, unit);
        return count;
    }

    /**
     * 初始化redis计数器，每次调用续期
     * @param key   redis键
     * @return redis计数器
     */
    public String getStringWithoutDeserialize(String key) {
        return this.redisTemplate.opsForValue().get(key, 0L, -1L);
    }
}
