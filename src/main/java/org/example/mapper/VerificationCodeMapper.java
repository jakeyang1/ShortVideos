package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.entity.VerificationCode;

public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {
    void saveVerificationCode(String phone, String code);


    String getVerificationCode(String code);
}
