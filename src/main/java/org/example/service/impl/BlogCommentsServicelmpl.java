package org.example.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.BlogComments;
import org.example.mapper.BlogCommentMapper;
import org.example.service.IBlogCommentsService;
import org.springframework.stereotype.Service;

@Service
public class BlogCommentsServicelmpl extends ServiceImpl<BlogCommentMapper, BlogComments> implements IBlogCommentsService {
}
