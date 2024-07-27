package org.example.utils;

import cn.hutool.core.lang.UUID;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class SimpleRedisLock implements ILock {

    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;

    }

    private StringRedisTemplate stringRedisTemplate;
    private String name;


    private static final String KEY_PREFIX = "lock";
    private static final String ID_PREFIX = UUID.randomUUID().toString(true) + "";

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    static {//redis set and Invoke the lua script
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }


    @Override
    public boolean trylock(long timeoutSec) {//acquires lock

        //acquires thread identification
        String threadId = ID_PREFIX + Thread.currentThread().getId();

        //acquires lock
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(KEY_PREFIX + name, threadId, timeoutSec, TimeUnit.SECONDS);

        return Boolean.TRUE.equals(success);
    }


    @Override
    public void unlock() {//two improvement : Invoke the lua script

           stringRedisTemplate.execute(UNLOCK_SCRIPT,
                   Collections.singletonList(KEY_PREFIX + name),
                   ID_PREFIX + Thread.currentThread().getId());

    }

//    @Override
//    public void unlock() {// one improvement  :Release lock
//        //Get thread identification
//        String threadId = ID_PREFIX + Thread.currentThread().getId();
//
//        //Gets the identifier in the lock
//         String id = stringRedisTemplate.opsForValue().get(KEY_PREFIX + name);
//        //Judge whether it is consistent
//        if(threadId.equals(id)){
//            //Release lock code
//            stringRedisTemplate.delete(KEY_PREFIX + name);
//       }
//     }


}
