package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.example.entity.Follow;

public interface FollowMapper extends BaseMapper<Follow> {

    @Delete("DELETE FROM tb_follow WHERE user_id = #{userId} AND follow_user_id = #{followUserId}")
    boolean deleteFollow(@Param("userId") Long userId, @Param("followUserId") Long followUserId);
}
