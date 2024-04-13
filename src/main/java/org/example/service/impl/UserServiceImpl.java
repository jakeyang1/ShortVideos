package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.IUserService;

public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
}
