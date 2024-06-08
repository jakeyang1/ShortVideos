package org.example.utils;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

public class SimpleRedisLock implements ILock {

    public SimpleRedisLock(String name,StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;

    }

    private StringRedisTemplate stringRedisTemplate;
    private String name;



    private static final String KEY_PREFIX = "lock";

    @Override
    public boolean trylock(long timeoutSec) {//acquires lock

        //acquires thread identification
        long threadId = Thread.currentThread().getId();

        //acquires lock
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(KEY_PREFIX + name, threadId + "" , timeoutSec, TimeUnit.SECONDS);

        return Boolean.TRUE.equals(success);
    }

    @Override
    public void unlock() {//Release lock

        stringRedisTemplate.delete(KEY_PREFIX + name);
    }
}
