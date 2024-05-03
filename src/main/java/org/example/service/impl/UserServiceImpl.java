package org.example.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.LoginFromDTO;
import org.example.dto.Result;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.mapper.VerificationCodeMapper;
import org.example.service.IUserService;
import org.example.utils.RegexUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import static org.example.utils.SystemConstants.USER_NICK_NAME_PREFIX;


@Slf4j//Simple Logging Facade for Java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
   private VerificationCodeMapper verificationCodeMapper;
    @Override
    public Result sendCode(String phone, HttpSession session) {

       if(RegexUtils.isPhoneInvalid(phone)){
           return  Result.fail("手机号格式错误");
       }

       //randomUtil create randomCode
        String code = RandomUtil.randomNumbers(6);

        session.setAttribute("code",code);

        verificationCodeMapper.saveVerificationCode(phone, code);

        /**
         * send code Module incomplete
         */
         log.debug("白痴，你的验证码是:{}", code);
         return Result.ok();

    }

    @Override
    public Result login(LoginFromDTO loginFrom, HttpSession session) {

        String phone = loginFrom.getPhone();


        if(RegexUtils.isPhoneInvalid(phone)){
            return Result.fail("手机号错误");

        }

       // Object cachecode = session.getAttribute("code");
        String code = loginFrom.getCode();

        String cacheCode = verificationCodeMapper.getVerificationCode(code);


        if(cacheCode.equals(null) || !code.equals(cacheCode)){

            return Result.ok("验证码错误");

        }

        //eq == equal
        User user = query().eq("phone", phone).one();


        if(user == null) {
            user = CreateWithUser(phone);
        }

        session.setAttribute("user", user);

       return Result.ok();
    }

    public User CreateWithUser(String phone){

        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(5));
        save(user);
        return user;

    }


}
