package org.example.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dto.Result;
import org.example.entity.ShopType;
import org.example.mapper.ShopTypeMapper;
import org.example.service.IShopTypeService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.example.utils.RedisConstants.CACHE_SHOPTYPE_KEY;

@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Override
    public Result queryBylist() {

        String cacheKey = CACHE_SHOPTYPE_KEY;



        String shoptypeJson = stringRedisTemplate.opsForValue().get(cacheKey);

        if(StrUtil.isNotBlank(shoptypeJson)){

            List<ShopType> shopTypeList  = JSONUtil.toList(JSONUtil.parseArray(shoptypeJson), ShopType.class);

            return Result.ok(shopTypeList);
        }


        List<ShopType> shopType = list();


        if(shopType == null){
            return Result.fail("店铺类型错误:401");

        }

        stringRedisTemplate.opsForValue().set(cacheKey, JSONUtil.toJsonStr(shopType));

        return Result.ok(shopType);
    }
}
