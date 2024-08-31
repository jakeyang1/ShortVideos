package org.example.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dto.Result;
import org.example.entity.Shop;
import org.example.mapper.ShoppMapper;
import org.example.service.IShopService;
import org.example.utils.CacheSolution.BloomFilterUtil;
import org.example.utils.CacheSolution.MultiLevelCacheClient;
import org.example.utils.SystemConstants;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.*;
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

    @Override
    public Result queryShopByType(Integer typeId, Integer current, Double x, Double y) {

        //Determine whether to query by coordinate
        if(x == null || y == null) {
            //No  need to coordinate query, database query
            Page<Shop> page = query()
                    .eq("type_id", typeId)
                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            //return data
            return  Result.ok(page.getRecords());
        }

        //Computed paging parameter
        int form = (current - 1) * SystemConstants.DEFAULT_PAGE_SIZE;
        int end = current * SystemConstants.DEFAULT_PAGE_SIZE;

        //Query redis, sort by distance, paginate, results
        String key = SHOP_GEO_KEY + typeId;
       GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo()
               .search(
                       key, GeoReference.fromCoordinate(x,y), new Distance(5000),
                RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end));


        //Resolution id
        if( results ==null) {
            return Result.ok(Collections.emptyList());
        }
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();

        ArrayList<Object> ids = new ArrayList<>(list.size());
        Map<String, Distance> distanceMap = new HashMap<>(list.size());

        list.stream().forEach(result ->{
           String shopIdStr = result.getContent().getName();
           ids.add(Long.valueOf(shopIdStr));
           Distance distance =result.getDistance();
           distanceMap.put(shopIdStr, distance);
       });

        //Query shop by id
        String idStr = StrUtil.join(",", ids);

        List<Shop> shops = query().in("id", ids).last("ORDER BY FIELD(id," + ids + ")").list();
        
        for(Shop shop : shops){
            shop.setDistance(distanceMap.get(shop.getId()).getValue());
            
        }

          return  null;
    }
}
