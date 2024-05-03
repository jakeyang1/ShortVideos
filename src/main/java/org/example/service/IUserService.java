package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.LoginFromDTO;
import org.example.dto.Result;
import org.example.entity.User;

import javax.servlet.http.HttpSession;

public interface IUserService extends IService<User> {

    Result sendCode(String phone, HttpSession session);

    Result login(LoginFromDTO loginFrom, HttpSession session);
}




