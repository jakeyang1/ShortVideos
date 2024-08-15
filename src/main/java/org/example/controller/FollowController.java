package org.example.controller;

import org.example.dto.Result;
import org.example.service.IFollowService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Resource
    private IFollowService followService;

         @PutMapping("/{id}/{isFollow}")
        public Result follow(@PathVariable("id") Long followUserId,   @PathVariable("isFollow") Boolean isFollow){

            return  followService.follow(followUserId, isFollow);

         }

     @GetMapping("/or/not/{id}")
    public Result isGetOrNotFollow(@PathVariable("id") Long followUserId){
             return followService.isFollow(followUserId);
    }

    @GetMapping("/common/{id}")
    public Result followCommons(@PathVariable("id") long id) {
             return followService.followCommons(id);

    }

}
