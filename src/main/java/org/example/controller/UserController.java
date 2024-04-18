package org.example.controller;


import lombok.extern.slf4j.Slf4j;
import org.example.dto.Result;
import org.example.entity.UserInfo;
import org.example.service.IUserInfoService;
import org.example.service.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.print.DocFlavor;
import javax.servlet.http.HttpSession;

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

        return Result.fail("功能未完成");
    }

    @GetMapping("/logout")
    public  Result logout(){

        return Result.fail("功能未完成");
    }

    @GetMapping("/me")
    public Result me() {
        return Result.fail("功能未完成");

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


}
