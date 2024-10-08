package org.example.controller;


import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.LoginFromDTO;
import org.example.dto.Result;
import org.example.dto.UserDTO;
import org.example.entity.User;
import org.example.entity.UserInfo;
import org.example.service.IUserInfoService;
import org.example.service.IUserService;
import org.example.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.print.DocFlavor;
import javax.servlet.http.HttpSession;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IUserInfoService userInfoService;


    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session){


        return userService.sendCode(phone,session);

    }

    @PostMapping("login")
    public Result login(@RequestBody LoginFromDTO loginFrom, HttpSession session){

    return userService.login(loginFrom, session);
    }

    @GetMapping("/logout")
    public  Result logout(){

        return Result.fail("功能未完成");
    }

    @GetMapping("/me")
    public Result me() {
        UserDTO user = UserHolder.getUser();
        return Result.ok(user);

    }

    @GetMapping("/info/{id}")
    public  Result info(@PathVariable("id") Long userId){
        UserInfo info = userInfoService.getById(userId);

        if(info == null) {
            return Result.ok();
        }

        info.setCreateTime(null);
        info.setUpdateTime(null);

        return  Result.ok(info);
    }

    @GetMapping("/{id}")
    public Result queryUserById(@PathVariable("id") Long userId){
        //1,query User
        User user = userService.getById(userId);

        if(user == null) {
            return Result.ok("用户不存在");
        }

        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);

        //return
        return Result.ok(userDTO);
    }

    @PostMapping("/{sign}")
    public Result sign(){
        return userService.sign();
    }


    @PostMapping("/{sign/count}")
    public Result signCount(){
        return userService.signCount();
    }

}
