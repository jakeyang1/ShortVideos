package org.example.controller;


import org.example.dto.Result;
import org.example.entity.ShopType;
import org.example.service.IShopTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/Shop-Type")
public class ShopTypeController {

    @Resource
    private IShopTypeService shopTypeService;

    @GetMapping("list")
    public Result queryTypeList(){
        List<ShopType> typeList = shopTypeService.query().orderByAsc("sort").list();

        return Result.ok(typeList);

    }


}
