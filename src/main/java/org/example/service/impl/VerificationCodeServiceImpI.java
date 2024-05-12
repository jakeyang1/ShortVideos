package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dto.Result;
import org.example.entity.VerificationCode;
import org.example.mapper.VerificationCodeMapper;
import org.example.service.IVerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeServiceImpI extends ServiceImpl<VerificationCodeMapper, VerificationCode> implements IVerificationCodeService {


    @Autowired
    private VerificationCodeMapper verificationCodeMapper;

    @Override
    public Result saveVerificationCode(String phone, String code) {
        verificationCodeMapper.saveVerificationCode(phone, code);
        return Result.ok();
    }

    @Override
    public Result delete(String code) {
        verificationCodeMapper.delete(code);
        return Result.ok();
    }
}

