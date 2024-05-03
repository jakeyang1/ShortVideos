package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.Shop;
import org.example.mapper.ShoppMapper;
import org.example.service.IShopService;
import org.springframework.stereotype.Service;


@Service
public class ShopServicelmpl extends ServiceImpl<ShoppMapper, Shop> implements IShopService {
}
