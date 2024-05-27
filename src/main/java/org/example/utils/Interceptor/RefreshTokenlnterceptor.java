package org.example.utils.Interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import org.example.dto.UserDTO;
import org.example.utils.Redis.RedisConstants;
import org.example.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RefreshTokenlnterceptor implements HandlerInterceptor {

     private StringRedisTemplate stringRedisTemplate;//dependency injection


    public RefreshTokenlnterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {//Start interceptor


        String token = request.getHeader("authorization");//Accept front-end requests to obtain user information

        if (StrUtil.isBlank(token)) {//Check whether it is empty
           return true;
        }


        String Key = RedisConstants.LOGIN_USER_KEY + token;//Simplify understanding

        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(Key);//Extract user information from redis


        if(userMap.isEmpty()){//Check whether user is empty
            return  true;

        }

        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(),false);//Convert a map object to a userDTO

        UserHolder.saveUser(userDTO);//Save UserDTO to ThreadLocal


      stringRedisTemplate.expire(Key, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);//Refreshing the token Validity Period
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {//end interceptor



        UserHolder.removeUser();
    }
}
