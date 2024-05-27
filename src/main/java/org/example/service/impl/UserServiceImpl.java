package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;

import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.LoginFromDTO;
import org.example.dto.Result;
import org.example.dto.UserDTO;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.IUserService;
import org.example.utils.Redis.RegexUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.example.utils.Redis.RedisConstants.*;
import static org.example.utils.SystemConstants.USER_NICK_NAME_PREFIX;


@Slf4j//Simple Logging Facade for Java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @Resource
    private  StringRedisTemplate stringRedisTemplate;

    @Override//send code to user
    public Result sendCode(String phone, HttpSession session) {

       if(RegexUtils.isPhoneInvalid(phone)){//Determine whether the phone number is in the wrong format
           return  Result.fail("手机号格式错误");
       }


        String code = RandomUtil.randomNumbers(6);//Randomly generated code

       stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone,  code, LOGIN_CODE_TTL,TimeUnit.MINUTES);//Set key and value to the redis object




        /**
         * send code Module incomplete
         */
         log.debug("宝宝，你的验证码是:{}", code);
         return Result.ok();

    }

    @Override//Login verification
    public Result login(LoginFromDTO loginFrom, HttpSession session) {

        String phone = loginFrom.getPhone();//Extract phone


        if(RegexUtils.isPhoneInvalid(phone)){//Check whether the mobile phone number is empty or does not match the regular expression
            return Result.fail("手机号格式错误");

        }

        String code = loginFrom.getCode();//Extract code

        String cachecode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);//Extract the stored value and compare it


        if(cachecode == null || !code.equals(cachecode)){//Determine whether the verification code is empty or incorrect  注意

            return Result.fail("验证码错误");

        }

        //eq == equal
        User user = query().eq("phone", phone).one();//Query code in the database


        if(user == null) {//If the user is empty, an object is created
            user = CreateWithUser(phone);
        }

        String token = cn.hutool.core.lang.UUID.randomUUID().toString(true);//Generate a random token that serves as the key for storing user information


        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);//Simplified userDto display





        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(), CopyOptions.create()
                .setIgnoreNullValue(true)
                .setFieldValueEditor((fieldName, fieldValue) -> fieldValue != null ? fieldValue.toString() : null));



        String tokenKey = LOGIN_USER_KEY + token;

        stringRedisTemplate.opsForHash().putAll( tokenKey,userMap);//Set map  values and parameters

        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);//Set expiration time



       return Result.ok(token);
    }




    public User CreateWithUser(String phone){// creats user object

        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(5));
        save(user);
        return user;

    }


}
