package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.Blog;
import org.example.mapper.BlogMapper;
import org.example.service.IBlogService;

public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {
}
