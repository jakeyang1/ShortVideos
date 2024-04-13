package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.SeckillVoucher;
import org.example.entity.Voucher;
import org.example.entity.VoucherOrder;
import org.example.mapper.VoucherMapper;
import org.example.mapper.VoucherOrderMapper;
import org.example.service.ISeckillVoucherService;
import org.example.service.IVoucherOrderService;
import org.example.service.IVoucherService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.transform.Result;
import java.util.List;

@Service
public class VoucherOrderServicelmpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder>  implements IVoucherOrderService {



}
