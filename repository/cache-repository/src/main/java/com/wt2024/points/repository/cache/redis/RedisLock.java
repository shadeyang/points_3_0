package com.wt2024.points.repository.cache.redis;

import com.wt2024.points.repository.cache.Constants;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisCommands;

import java.util.concurrent.TimeUnit;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2019-02-19 17:32
 * @Project uxun-framework-base:com.uxunchina.redis
 */
@Component
@ConditionalOnProperty(
        prefix = "cache",
        name = "type",
        havingValue = Constants.CACHE_TYPE_REDIS,
        matchIfMissing = true
)
@ConditionalOnClass(RedisTemplate.class)
public class RedisLock {

    private static final Logger logger = LoggerFactory.getLogger(RedisLock.class);

    @Autowired
    private RedisTemplate redisTemplate;
    private long defaultLockTimeMillis = 600000L;
    private long defaultTryLockIntervalMillis = 1000L;

    private static final Long RELEASE_SUCCESS = 1L;
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    /**
     * 当前设置 过期时间单位, EX = seconds; PX = milliseconds
     */
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    private static final String RELEASE_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    public String  clientId = "";

    public boolean lock(String key)
    {
        return lock(key, this.defaultLockTimeMillis);
    }

    public boolean lock(String key, long lockMillis)
    {
        return (boolean) redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
            Object nativeConnection = redisConnection.getNativeConnection();
            RedisSerializer<String> stringRedisSerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
            byte[] keyByte = stringRedisSerializer.serialize(key);
            byte[] valueByte = stringRedisSerializer.serialize(clientId);

            // lettuce连接包下 redis 单机模式
            if (nativeConnection instanceof RedisAsyncCommands) {
                RedisAsyncCommands connection = (RedisAsyncCommands) nativeConnection;
                RedisCommands commands = connection.getStatefulConnection().sync();
                String result = commands.set(keyByte, valueByte, SetArgs.Builder.nx().px(lockMillis));
                if (LOCK_SUCCESS.equals(result)) {
                    return true;
                }
            }
            // lettuce连接包下 redis 集群模式
            if (nativeConnection instanceof RedisAdvancedClusterAsyncCommands) {
                RedisAdvancedClusterAsyncCommands connection = (RedisAdvancedClusterAsyncCommands) nativeConnection;
                RedisAdvancedClusterCommands commands = connection.getStatefulConnection().sync();
                String result = commands.set(keyByte, valueByte, SetArgs.Builder.nx().px(lockMillis));
                if (LOCK_SUCCESS.equals(result)) {
                    return true;
                }
            }

            if (nativeConnection instanceof JedisCommands) {
                JedisCommands jedis = (JedisCommands) nativeConnection;
                String result = jedis.set(key, clientId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, lockMillis);
                if (LOCK_SUCCESS.equals(result)) {
                    return true;
                }
            }
            return false;
        });
    }

    public boolean tryLock(String key)
    {
        return tryLockWithLockTime(key, this.defaultLockTimeMillis, this.defaultTryLockIntervalMillis);
    }

    public boolean tryLock(String key, long intervalMillis)
    {
        return tryLockWithLockTime(key, this.defaultLockTimeMillis, intervalMillis);
    }

    public boolean tryLockWithLockTime(String key, long lockMillis)
    {
        return tryLockWithLockTime(key, lockMillis, this.defaultTryLockIntervalMillis);
    }

    public boolean tryLockWithLockTime(String key, long lockMillis, long intervalMillis)
    {
        Assert.isTrue(intervalMillis > 0L);
        Assert.isTrue(lockMillis > 0L);

        int tryTimes = 0;
        while (tryTimes < 3)
        {
            boolean succeed = lock(key, lockMillis);
            if (succeed) {
                return succeed;
            }
            tryTimes++;
            logger.debug("tryLock try:{},key:{}", Integer.valueOf(tryTimes), key);
            try
            {
                Thread.sleep(intervalMillis * tryTimes);
            }
            catch (InterruptedException e)
            {
                logger.warn("key:{} try lock fail", key, e);
            }
        }
        return false;
    }

    public boolean unLock(String key)
    {
        DefaultRedisScript<Integer> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(RELEASE_LOCK_SCRIPT);
        redisScript.setResultType(Integer.class);
        Object execute = redisTemplate.execute((RedisConnection connection) -> connection.eval(
                RELEASE_LOCK_SCRIPT.getBytes(),
                ReturnType.INTEGER,
                1,
                key.getBytes(),
                clientId.getBytes()));
        if (RELEASE_SUCCESS.equals(execute)) {
            return true;
        }
//        return false;
        return this.redisTemplate.expire(key, 0L, TimeUnit.MILLISECONDS);
    }



}
