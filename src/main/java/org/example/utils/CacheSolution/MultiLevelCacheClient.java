
package org.example.utils.CacheSolution;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.example.utils.Redis.RedisConstants.CACHE_NULL_TTL;

@Slf4j
@Component
public class MultiLevelCacheClient {//复制，后续梳理逻辑


    private final StringRedisTemplate stringRedisTemplate;
    private final Cache<String, Object> localCache;
    private final CacheClient cacheClient;



    public MultiLevelCacheClient(StringRedisTemplate stringRedisTemplate, CacheClient cacheClient) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.cacheClient = cacheClient;
        this.localCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES) // 设置本地缓存过期时间为10分钟
                .maximumSize(1000) // 设置本地缓存的最大容量为1000
                .build();
    }

    // 设置本地缓存和Redis缓存，同时为Redis缓存设置随机TTL
    public void setWithRandomTTL(String key, Object value, Long time, TimeUnit unit) {

        cacheClient.setWithRadomTTL(key,value,time, unit);
        // 设置本地缓存
        localCache.put(key, value);
    }




    // 多级缓存查询方法
    public <R, ID> R queryWithMultiLevelCache(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        String key = keyPrefix + id;

        // 1. 先查询本地缓存
        R result = (R) localCache.getIfPresent(key);
        if (result != null) {
            return result;
        }

        // 2. 本地缓存没有，再查询Redis
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(json)) {
            result = JSONUtil.toBean(json, type);
            // 存入本地缓存
            localCache.put(key, result);
            return result;
        }

        // 3. 如果Redis缓存有值，但值为"null"（表示数据库中不存在此记录），返回null
        if (json != null) {
            return null;
        }

        // 4. Redis缓存没有，再查询数据库
        result = dbFallback.apply(id);
        if (result == null) {
            // 数据库中不存在此记录，设置空值缓存防止缓存穿透
            stringRedisTemplate.opsForValue().set(key, "null", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }

        // 5. 数据库查询结果存入Redis和本地缓存，并设置随机TTL
        setWithRandomTTL(key, result, time, unit);
        return result;
    }

    // 失效缓存方法
    public void invalidate(String key) {
        localCache.invalidate(key);
        stringRedisTemplate.delete(key);
    }
}
