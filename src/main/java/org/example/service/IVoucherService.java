package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.Voucher;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.Result;

public interface IVoucherService extends IService<Voucher> {

    Result queryVoucherOfShop(Long ShopId);

    @Transactional
    void addSeckillVoucher(Voucher voucher);
}
