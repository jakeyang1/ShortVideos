package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dto.Result;
import org.example.dto.UserDTO;
import org.example.entity.Follow;
import org.example.entity.User;
import org.example.mapper.FollowMapper;
import org.example.service.IFollowService;
import org.example.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.KeyBoundCursor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.Key;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

    @Autowired
    private FollowMapper followMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private  UserServiceImpl userService;

    @Override
    public Result follow(Long followUserId, Boolean isFollow) {

        //0: get login user
        Long userId = UserHolder.getUser().getId();
        String key = "follows:" + userId;


        //1:Determine the logic of following and unfollowing
        if (isFollow) {
            //2:following, Insert new data
            Follow follow = new Follow();
            follow.setUserId(userId);
            follow.setFollowUserId(followUserId);
            boolean isSucces = save(follow);


            if(isSucces)  {
                //2.5:Put the id of the interested user into the redis set collection
                stringRedisTemplate.opsForSet().add(key, followUserId.toString());
            }

        } else {
            //3:unfollowing,delete old data
         boolean isSuccess =   followMapper.deleteFollow(userId, followUserId);
         if(isSuccess){
             //4:drop Focus on the id of the user for redis set
             stringRedisTemplate.opsForSet().remove(key,followUserId.toString());
         }

        }

        return  Result.ok();
    }

    @Override
    public Result isFollow(Long followUserId) {
        //0: get login user
        Long userId = UserHolder.getUser().getId();
        //1.query  Concern
        Long count = query().eq("user_id", userId).eq("Follow_user_id", followUserId).count();
        //2:Judge
        return Result.ok(count > 0);
    }

    @Override
    public Result followCommons(long id) {
        //1:get now user
        UserDTO userId = UserHolder.getUser();
        String key = "follows:" + userId.getId();

        //2:get intersection
        String key2 = "follows:" + id;
        Set<String> intersect = stringRedisTemplate.opsForSet().intersect(key, key2);
        if(intersect == null || intersect.isEmpty()) {
            //no intersection
            return Result.ok(Collections.emptyList());
        }

        //3:Resolution id set
        List<Long> ids = intersect.stream().map(Long::valueOf).collect(Collectors.toList());


        //4:quary user
        List<UserDTO> users = userService.listByIds(ids)
        .stream().map(user -> BeanUtil.copyProperties(user, UserDTO.class))
        .collect(Collectors.toList());
        return Result.ok(users);
    }
}