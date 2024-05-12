package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.Result;
import org.example.entity.VerificationCode;

public interface IVerificationCodeService extends IService<VerificationCode> {

    Result saveVerificationCode(String phone,String Code);

    Result delete(String code);
}
