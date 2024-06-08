package org.example.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dto.Result;
import org.example.entity.SeckillVoucher;
import org.example.entity.Voucher;
import org.example.entity.VoucherOrder;
import org.example.mapper.VoucherOrderMapper;
import org.example.service.ISeckillVoucherService;
import org.example.service.IVoucherOrderService;
import org.example.service.IVoucherService;
import org.example.utils.Redis.RedisIdworker;
import org.example.utils.SimpleRedisLock;
import org.example.utils.UserHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder>  implements IVoucherOrderService {

   @Resource
   private ISeckillVoucherService iSeckillVoucherService;

   @Resource
   private RedisIdworker redisIdworker;

   private StringRedisTemplate stringRedisTemplate;

    @Override

    public Result seckillVoucher(Long voucherId) {

        SeckillVoucher voucherid = iSeckillVoucherService.getById(voucherId);


        //Second kill time logic 1
       if(voucherid.getBeginTime().isAfter(LocalDateTime.now())) {

           return Result.fail("秒杀未开始");

        }

       //Second kill time logic 2
       if(voucherid.getEndTime().isBefore(LocalDateTime.now())){
           return Result.fail("秒杀已结束");
       }



        //Inventory is less than 1 insufficient
       if (voucherid.getStock() < 1 ){

           return Result.fail("库存不足");
        }



        //Pessimistic lock
        Long userid = UserHolder.getUser().getId();
//        synchronized (userid.toString().intern()) {

        //创建锁对象
        SimpleRedisLock lock = new SimpleRedisLock("order:" + userid, stringRedisTemplate);
        //获取锁
        boolean trylock = lock.trylock(1200);
        //判断是否获取成功
        if(!trylock){
            return Result.fail("不允许重复下单");
        }try {

            //获取代理对象（事务）
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.createVoucherOrder(voucherId);
        }finally {
            lock.unlock();//释放锁
        }

//        }
    }


    @Transactional
    public   Result createVoucherOrder(Long voucherId) {
        //One person, one single
        Long userid = UserHolder.getUser().getId();


            Long count = query().eq("user_id", userid).eq("voucher_id", voucherId).count();

            if (count > 0) {
                return Result.fail("用户已经购买过一次");
            }


            //optimistic lock
            boolean success = iSeckillVoucherService.update().setSql("stock = stock -1")
                    .eq("voucher_id", voucherId).gt("stock", 0).update();


            if (!success) {
                return Result.fail("库存不足");
            }



            VoucherOrder voucherOrder = new VoucherOrder();//create order

            long orderId = redisIdworker.nextId("order");
            voucherOrder.setId(orderId);

            Long userId = UserHolder.getUser().getId();

            voucherOrder.setUserId(userId);

            voucherOrder.setVoucherId(voucherId);


            save(voucherOrder);


            return Result.ok(orderId);
        }

}
