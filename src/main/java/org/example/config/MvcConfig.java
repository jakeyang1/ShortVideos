package org.example.config;

import org.example.utils.LoginInterceptor;
import org.example.utils.RefreshTokenlnterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;//dependency injection



    @Override
    public void addInterceptors(InterceptorRegistry registry){

        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        "/user/code",
                        "/user/login",
                        "/blog/hot",
                        "/shop/**",
                        "/shop-type/**",
                        "/upload/**",
                        "/voucher/**"
                ).order(1);

        registry.addInterceptor(new RefreshTokenlnterceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);


    }


}
