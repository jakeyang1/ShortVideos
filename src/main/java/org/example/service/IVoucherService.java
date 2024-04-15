package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.Result;
import org.example.entity.Voucher;
import org.springframework.transaction.annotation.Transactional;



public interface IVoucherService extends IService<Voucher> {

    Result queryVoucherOfShop(Long ShopId);

    @Transactional
    void addSeckillVoucher(Voucher voucher);
}
