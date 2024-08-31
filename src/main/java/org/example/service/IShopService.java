package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.Result;
import org.example.entity.Shop;

public interface IShopService extends IService<Shop> {
    Result queryByid(long id);

    Result update(Shop shop);

    Result queryShopByType(Integer typeId, Integer current, Double x, Double y);
}
