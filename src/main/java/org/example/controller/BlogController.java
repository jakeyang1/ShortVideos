package org.example.controller;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.example.dto.Result;
import org.example.dto.UserDTO;
import org.example.entity.Blog;
import org.example.entity.User;
import org.example.service.IBlogService;
import org.example.service.IUserService;
import org.example.utils.SystemConstants;
import org.example.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Generated;
import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("blog")
public class BlogController {

    @Resource
    private IBlogService blogService;



    @PostMapping
    public Result saveBlog(@RequestBody Blog blog){


          return blogService.saveBlog(blog);


    }

   @PutMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id){

//        //change  gvie a like quantity  => (update tb_blog set liked = liked + 1 where  id = ？)
//        blogService.update().setSql("liked = liked + 1").eq("id", id).update();
//        return Result.ok();

       return  blogService.likeBlog(id);
   }


    @GetMapping("/likes/{id}")
    public Result queryBlogLikes(@PathVariable("id") Long id){

        return  blogService.queryBlogLikes(id);
    }
   @GetMapping("/of/me")
    public Result queryMyBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {

        UserDTO user = UserHolder.getUser();
        Page<Blog> page = blogService.query()
                .eq("user_id", user.getId()).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
                 List<Blog> records = page.getRecords();
                 return Result.ok(records);

   }

    @GetMapping("/hot")
    public Result queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return blogService.queryHotBlog(current);
    }

    @GetMapping("/{id}")
    public Result queryBlogById (@PathVariable("id") Long id) {
        return blogService.queryBlogById(id);
    }

    @GetMapping("/of/user")
    public Result queryByUserId(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam("id") Long id) {

        // query the blog list of the user
        Page<Blog> page = blogService.query().eq("user_id", id).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));

        //Gets the current page data
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }

    @GetMapping("/of/follow")
    public Result queryBlogofFollow(@RequestParam("lastId") Long max, @RequestParam(value = "offset", defaultValue = "0") Integer offset){

        return  blogService.queryBlogOfFollow(max,offset);

    }


   }