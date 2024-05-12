package org.example.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dto.Result;
import org.example.entity.Shop;
import org.example.mapper.ShoppMapper;
import org.example.service.IShopService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static org.example.utils.RedisConstants.CACHE_SHOP_KEY;


@Service
public class ShopServicelmpl extends ServiceImpl<ShoppMapper, Shop> implements IShopService {


    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryByid(long id) {

        String key = CACHE_SHOP_KEY + id;
        String shopJson = stringRedisTemplate.opsForValue().get(key);

        if (StrUtil.isNotBlank(shopJson)) {

            Shop shopbean = JSONUtil.toBean(shopJson, Shop.class);

            return Result.ok(shopbean);

        }

            Shop shop = baseMapper.selectById(id);
            if(shop == null){
                return Result.fail("401,店铺类型不存在");

            }


         stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(shop));
        return Result.ok(shop);




    }
}
