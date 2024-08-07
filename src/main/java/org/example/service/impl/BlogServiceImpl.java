package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dto.Result;
import org.example.dto.ScrollResult;
import org.example.dto.UserDTO;
import org.example.entity.Blog;
import org.example.entity.Follow;
import org.example.entity.User;
import org.example.mapper.BlogMapper;
import org.example.service.IBlogService;
import org.example.service.IFollowService;
import org.example.service.IUserService;
import org.example.utils.SystemConstants;
import org.example.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.example.utils.Redis.RedisConstants.BLOG_LIKED_KEY;
import static org.example.utils.Redis.RedisConstants.FEED_KEY;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    @Resource
    private IUserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryHotBlog(Integer current) {
        //According to user query
        Page<Blog> page = query().orderByDesc("liked")
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));

        //Gets the current page data
        List<Blog> records = page.getRecords();

        //Query user
        records.forEach(blog -> {
            this.queryBlogUser(blog);
            this.isBlogLiked(blog);
        });
        return Result.ok(records);
    }


    @Override
    public Result queryBlogById(Long id) {
        //1:Query blog
        Blog blog = getById(id);
        if (blog == null) {
            return Result.fail("笔记不存在");

        }
        //2:Query blog of user
        queryBlogUser(blog);

        //3:Query whether the blog is liked
        isBlogLiked(blog);
        return Result.ok(blog);


    }

    private void isBlogLiked(Blog blog) {
        //1:Get log-in user
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            // user not login
            return;
        }
        Long userId = user.getId();
        //2:Determine if the logged-in user has already clicked the like button
        String key = "blog:liked:" + blog.getId();
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, userId.toString());
        blog.setIsLike(BooleanUtil.isTrue(isMember));

    }

    @Override//
    public Result likeBlog(Long id) {
        //1:Get log-in user
        Long userId = UserHolder.getUser().getId();

        //2:Determine if the logged-in user has already clicked the like button
        String key = BLOG_LIKED_KEY + id;
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, userId.toString());

        if (BooleanUtil.isFalse(isMember)) {

            //3:If you don't like it, you get permission to like it
            //3.1: like number + 1
            Boolean isSuccess = update().setSql("liked = liked + 1").eq("id", id).update();

            //3.2: save user to redis set assemble
            if (isSuccess == true) {
                stringRedisTemplate.opsForSet().add(key, userId.toString());

            }
        } else {


            //4:if you like it, unlike
            //4.1: database likes Number  - 1
            boolean isSuccess = update().setSql("liked = liked - 1").eq("id", id).update();

            //4.2:set assemble  remove user
            if (isSuccess) {
                stringRedisTemplate.opsForSet().remove(key, userId.toString());


            }
        }
       return Result.ok();
    }



    @Override
    public Result queryBlogLikes(Long id) {
        return null;
    }

    @Override
    public Result saveBlog(Blog blog) {
        return null;
    }

    @Override
    public Result queryBlogOfFollow(Long max, Integer offset) {
        return null;
    }

    private void queryBlogUser(Blog blog) {
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
    }

}
