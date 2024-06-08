package org.example.utils.Redis;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisIdworker {

    private static  final long COUNT_BITS = 32;
    private StringRedisTemplate stringRedisTemplate;



    public long nextId(String keyPrefix) {


        LocalDateTime now = LocalDateTime.now();
        long timestamp = now.toEpochSecond(ZoneOffset.UTC);

        String data = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));

        Long count = stringRedisTemplate.opsForValue().increment("icr" + keyPrefix + ":" + data);

        return timestamp << COUNT_BITS | count;
    }
    }


