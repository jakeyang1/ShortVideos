package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.SeckillVoucher;
import org.example.entity.Voucher;
import org.example.mapper.VoucherMapper;
import org.example.service.ISeckillVoucherService;
import org.example.service.IVoucherService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.transform.Result;
import java.util.List;

public class VoucherServicelmpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {
    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Override
    public Result queryVoucherOfShop(Long ShopId) {
        List<Voucher> vouchers = getBaseMapper().queryVoucherofShop(ShopId);

        return Result.ok(vouchers);
    }

    @Override
    @Transactional
    public void addSeckillVoucher(Voucher voucher){
        save(voucher);

        SeckillVoucher seckillVoucher = new SeckillVoucher();

        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndtime());
        seckillVoucherService.save(seckillVoucher);
    }
}
