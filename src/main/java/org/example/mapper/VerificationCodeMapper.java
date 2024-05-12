package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.entity.VerificationCode;

@Mapper
public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {
    @Insert("INSERT INTO verificaton_code (phone, code) VALUES (#{phone}, #{code})")
    void saveVerificationCode(@Param("phone") String phone, @Param("code") String code);

    @Select("SELECT code FROM verificaton_code WHERE code = #{code}")
    String getVerificationCode(@Param("code") String code);

    @Delete("DELETE FROM verificaton_code WHERE code = #{code}")
    void delete(@Param("code") String code);

}
