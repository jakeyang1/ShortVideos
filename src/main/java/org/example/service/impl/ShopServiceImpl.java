package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dto.Result;
import org.example.entity.Shop;
import org.example.mapper.ShoppMapper;
import org.example.service.IShopService;
import org.example.utils.CacheSolution.BloomFilterUtil;
import org.example.utils.CacheSolution.MultiLevelCacheClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;

import static org.example.utils.Redis.RedisConstants.*;


@Service
public class ShopServiceImpl extends ServiceImpl<ShoppMapper, Shop> implements IShopService {


    @Resource
    private MultiLevelCacheClient multiLevelCacheClient;
    @Resource
    StringRedisTemplate stringRedisTemplate;


    @Override
    public Result queryByid(long id) {




        if(BloomFilterUtil.mightContain(id)) {
            return Result.fail("店铺信息不存在");
        }


        Shop shop = multiLevelCacheClient.queryWithMultiLevelCache(CACHE_SHOP_KEY, String.valueOf(id), Shop.class,
                this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);


        // 互斥锁解决缓存击穿
        // Shop shop = cacheClient
        //         .queryWithMutex(CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);

        // 逻辑过期解决缓存击穿
        // Shop shop = cacheClient
        //         .queryWithLogicalExpire(CACHE_SHOP_KEY, id, Shop.class, this::getById, 20L,


        if (shop == null) {
            return Result.fail("401,店铺信息不存在");
        }

        return Result.ok(shop);

    }


    private Shop getById(Long id) {
        return baseMapper.selectById(id);
    }






    @Override
    @Transactional
    public Result update(Shop shop) {
        Long updateid = shop.getId();

        if(updateid == null) {
            Result.fail("店铺id为空");
        }
        updateById(shop);



        multiLevelCacheClient.invalidate(CACHE_SHOP_KEY + updateid);


        return Result.ok();
    }
}
