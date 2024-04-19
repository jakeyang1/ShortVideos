package org.example.controller;


import org.example.dto.Result;
import org.example.entity.Voucher;
import org.example.service.IVoucherService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/voucher")
public class VoucherController {

    @Resource
    private IVoucherService voucherService;

    @PostMapping
    public Result addVoucher(@RequestBody Voucher voucher){

        voucherService.save(voucher);
        return Result.ok(voucher.getId());
    }


    @PostMapping("sekill")
    public Result addSeckill(@RequestBody Voucher voucher){

        voucherService.addSeckillVoucher(voucher);
        return Result.ok(voucher.getId());

    }


    @GetMapping("/list/{shopId}")
    public Result queryVoucherOfShop(@PathVariable("shopId") Long shopId){

    return voucherService.queryVoucherOfShop(shopId);

    }

}


