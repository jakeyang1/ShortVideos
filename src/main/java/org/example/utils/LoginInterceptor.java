package org.example.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import org.example.dto.UserDTO;
import org.example.entity.User;
import org.example.mapper.VerificationCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.example.utils.RedisConstants.LOGIN_USER_KEY;
import static org.example.utils.RedisConstants.LOGIN_USER_TTL;

public class LoginInterceptor implements HandlerInterceptor {





    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {//Start interceptor


        if (UserHolder.getUser() == null) {//判断是否需要拦截（THreadLocal是否有用户）
            response.setStatus(401);
            return false;//没有，需要拦截，设置状态吗
        }
        return true;//有用户，退出
    }

}