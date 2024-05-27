package org.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.dto.Result;
import org.example.entity.Shop;
import org.example.service.IShopService;
import org.example.utils.SystemConstants;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("shop")
public class ShopController {

    @Resource
    public IShopService shopService;

    @GetMapping("/{id}")
    public Result queryShopById(@PathVariable("id") long id){

      return shopService.queryByid(id);

    }

    @PostMapping
    public Result saveShop(@RequestParam Shop shop){

        shopService.save(shop);

        return Result.ok(shop.getId());

    }

    @PutMapping
    public Result updataShop(@RequestParam Shop shop){



        return  shopService.update(shop);

    }

    @GetMapping("/of/Type")
    public Result queryShopByName(@RequestParam(value = "TypeId", required = false) Integer typeId,
                                  @RequestParam(value = "current",defaultValue = "1") Integer current
                                  ){

        Page<Shop> page = shopService.query().eq("type_id", typeId).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));

        return Result.ok(page.getRecords());

    }

    @GetMapping("/of/name")
    public Result queryShopByName(@RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "couuent",defaultValue = "1") Integer current
    ){

        Page<Shop> page = shopService.query().eq("name", name).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        return  Result.ok(page.getRecords());

    }


}
