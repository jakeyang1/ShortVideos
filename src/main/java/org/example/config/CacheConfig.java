package org.example.config;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {//复制

    @Bean
    public Cache<String, Object> localCache() {
        // 创建一个基于 LRUCache 策略的本地缓存，最大容量为 1000
        return CacheUtil.newLRUCache(1000);
    }
}
