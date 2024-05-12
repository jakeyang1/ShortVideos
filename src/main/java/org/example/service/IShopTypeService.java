package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.Result;
import org.example.entity.ShopType;

public interface IShopTypeService extends IService<ShopType> {
    Result queryBylist();
}
