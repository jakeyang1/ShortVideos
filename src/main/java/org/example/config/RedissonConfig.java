package org.example.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {

        //set redisson
        Config config = new Config();

      //  config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("12345678"); error code
        config.useSingleServer().setAddress("redis://192.168.23.128:6379").setPassword("12345678");


        //create redissonClient object

        return Redisson.create(config);

    }

}

