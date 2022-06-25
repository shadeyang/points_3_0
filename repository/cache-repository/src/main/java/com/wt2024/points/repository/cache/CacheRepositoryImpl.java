package com.wt2024.points.repository.cache;

import com.wt2024.points.repository.api.cache.repository.CacheRepository;
import com.wt2024.points.repository.cache.local.CacheManager;
import com.wt2024.points.repository.cache.redis.RedisCache;
import com.wt2024.points.repository.cache.redis.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName CacheRepositoryImpl
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/28
 * @Version V1.0
 **/
@Slf4j
@Repository
public class CacheRepositoryImpl implements CacheRepository {

    @Value("${cache.type:}")
    private String cacheType;
    @Autowired(required = false)
    private RedisCache redisCache;
    @Autowired(required = false)
    private RedisLock redisLock;

    
    /**
     * 校验键是否存在
     *
     * @param key 键
     * @return 校验键是否存在
     */
    @Override
    public boolean exists(String key) {
        return Constants.CACHE_TYPE_REDIS.equals(cacheType) ? redisCache.exists(key) : (CacheManager.getData(key) != null);
    }

    /**
     * 获取键值
     *
     * @param key 键
     * @param <T> 缓存实体
     * @return 缓存实体
     */
    @Override
    public <T> T get(String key) {
        return Constants.CACHE_TYPE_REDIS.equals(cacheType)  ? redisCache.get(key) : CacheManager.getData(key);
    }

    /**
     * 获取键值
     *
     * @param prefix 前缀
     * @param key    键
     * @param <T>    缓存实体
     * @return 缓存实体
     */
    @Override
    public <T> T get(String prefix, String key) {
        return this.get(prefix + key);
    }
    
    /**
     * 移除缓存
     *
     * @param key 键
     */
    @Override
    public void remove(String key) {
        if (Constants.CACHE_TYPE_REDIS.equals(cacheType) ) {
            redisCache.remove(key);
        } else {
            CacheManager.clear(key);
        }
    }


    /**
     * 存储键值（默认缓存30天）
     * @param key   键
     * @param value 值
     */
    @Override
    public void set(String key, Object value) {
        this.set(key,value,Constants.DEFAULT_TIMEOUT_METRICS,TimeUnit.DAYS);
    }

    /**
     * 存储键值
     * @param key   键
     * @param value 值
     * @param milliseconds  缓存毫秒
     */
    @Override
    public void set(String key, Object value, long milliseconds) {
        this.set(key, value, milliseconds, TimeUnit.MILLISECONDS);
    }

    /**
     * 存储键值
     * @param key   键
     * @param value 值
     * @param timeout   缓存时间
     * @param unit  时间单位
     */
    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        if (Constants.CACHE_TYPE_REDIS.equals(cacheType) ) {
            redisCache.set(key,value,timeout,unit);
        }else {
            CacheManager.setData(key,value,(int)unit.toSeconds(timeout));
        }
    }

    /**
     * 缓存加锁
     * @param key
     * @param timeout   缓存时间
     * @param unit  时间单位
     * @return
     */
    @Override
    public boolean lock(String key, long timeout, TimeUnit unit){
        try {
            if (Constants.CACHE_TYPE_REDIS.equals(cacheType) ) {
                return redisLock.lock(key,unit.toMillis(timeout));
            }else {
                if(CacheManager.getData(key) == null){
                    CacheManager.setData(key,1,(int)unit.toSeconds(timeout));
                    return true;
                }
            }
        }catch (Exception e){
            log.error("缓存加锁失败",e);
        }
        return false;
    }

    @Override
    public boolean lock(String key, long timeout){
        return this.lock(key,timeout,TimeUnit.MILLISECONDS);
    }

    /**
     * 缓存解锁
     * @param key
     */
    @Override
    public void unLock(String key){
        if (Constants.CACHE_TYPE_REDIS.equals(cacheType) ) {
            redisLock.unLock(key);
        }else {
            CacheManager.clear(key);
        }
    }

    /**
     * 存储键值
     * @param prefix    前缀
     * @param key       键
     * @param value     值
     * @param seconds   缓存秒
     */
    @Override
    public void set(String prefix, String key, Object value, long seconds) {
        this.set(prefix + key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 存储键值（默认缓存30天）
     * @param prefix    前缀
     * @param key       键
     * @param value     值
     */
    @Override
    public void set(String prefix, String key, String value) {
        this.set(prefix + key, value);
    }

}
