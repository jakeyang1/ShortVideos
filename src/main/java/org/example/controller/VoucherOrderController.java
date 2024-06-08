package org.example.controller;


import org.example.dto.Result;
import org.example.service.IVoucherOrderService;
import org.example.service.IVoucherService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {


    @Resource
    private IVoucherOrderService iVoucherOrderService;


    @PostMapping("seckill/{id}")
    public Result seckillVoucher(@PathVariable("id") Long voucherId){

        return  iVoucherOrderService.seckillVoucher(voucherId);

    }
}
