package org.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.Result;
import org.example.dto.UserDTO;
import org.example.entity.VoucherOrder;
import org.example.mapper.VoucherOrderMapper;
import org.example.service.ISeckillVoucherService;
import org.example.service.IVoucherOrderService;
import org.example.utils.Redis.RedisIdworker;
import org.example.utils.UserHolder;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



@Slf4j
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder>  implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService iSeckillVoucherService;

    @Resource
    private RedisIdworker redisIdworker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    private static final DefaultRedisScript<Long> SECKILl_SCRIPT;

    static {//redis set and Invoke the lua script
        SECKILl_SCRIPT = new DefaultRedisScript<>();
        SECKILl_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILl_SCRIPT.setResultType(Long.class);
    }


    private static ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();//Creating a thread pool

    @PostConstruct
    private void init() {
        SECKILL_ORDER_EXECUTOR.submit(new VoucherOrderHander());

    }



    private class VoucherOrderHander implements Runnable {//get stream message queue

        String queueName = "stream.orders";

        @Override
        public void run() {

            while (true) {

                try {
                    //Gets order information for the stream message queue
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(20)),
                            StreamOffset.create(queueName, ReadOffset.lastConsumed())
                    );

                    //Check whether the message is successfully obtained.
                    if(list == null || list.isEmpty()) {
                        //If this fails, there is no message and the next loop is entered
                        continue;
                    }

                    //Parse order information
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> values = record.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(values, new VoucherOrder(), true);

                    handleVoucherOrder(voucherOrder);
                    //confirm an order
                   stringRedisTemplate.opsForStream().acknowledge(queueName, "g1",record.getId());

                } catch (Exception e) {
                    log.error("处理订单异常", e);
                    handlePengingList();
                }
            }

        }

        private void handlePengingList() {

            while (true) {

                try {
                    //Gets order information for the pendinglist message queue
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1),
                            StreamOffset.create(queueName, ReadOffset.from("0"))
                    );

                    //Check whether the message is successfully obtained.
                    if(list == null || list.isEmpty()) {
                        //If this fails, there is no message and the stop
                        continue;
                    }

                    //Parse order information
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> values = record.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(values, new VoucherOrder(), true);

                    handleVoucherOrder(voucherOrder);

                    //confirm an order
                    stringRedisTemplate.opsForStream().acknowledge(queueName, "g1",record.getId());

                } catch (Exception e) {
                    log.error("处理订单异常", e);

                }
            }
        }
    }

//    private BlockingQueue<VoucherOrder> ordersTasks = new ArrayBlockingQueue<>(1024 * 1024);//create Blocking queue
//    private class VoucherOrderHander implements Runnable {
//        @Override
//        public void run() {
//
//            while (true) {
//
//                try {
//                    //Get order information
//                    VoucherOrder voucherOrder = ordersTasks.take();
//
//                    //creat order
//                    handleVoucherOrder(voucherOrder);
//                } catch (Exception e) {
//                    log.error("处理订单异常", e);
//                }
//            }
//
//        }
//    }


    private Result handleVoucherOrder(VoucherOrder voucherOrder) {

        Long userId = voucherOrder.getUserId();

        // create lock object
        RLock lock = redissonClient.getLock("lock:order:" + userId);

        //get lock
        boolean trylock = lock.tryLock();

        // Check whether the operation is successful
        if (!trylock) {
            log.error("不允许重复下单");
        }
        try {


            proxy.createVoucherOrder(voucherOrder);
        } finally {
            lock.unlock();// Release the lock


        }
        return null;
    }



    private IVoucherOrderService proxy;


    @Override  //(Implement the function with stream message queue)
    public Result seckillVoucher(Long voucherId) {

        //get user
        UserDTO userId = UserHolder.getUser();

        //get orderid
        long orderId = redisIdworker.nextId("order");

        //Execute the lua script
        Long result = stringRedisTemplate.execute(
                SECKILl_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), userId.toString(), String.valueOf(orderId)//Adds to the stream message queue

        );

        // Check whether the result is 0
        int r = result.intValue();
        if(r != 0){
            // not 0, Ineligibility to buy
            return Result.fail(r == 1 ? "库存不足" : "不能重复下单");
        }


        // Get proxy object
        proxy = (IVoucherOrderService) AopContext.currentProxy();

        // retrun orderid
        return Result.ok(orderId);
    }



//    @Override  //(用lua实现功能)
//    public Result seckillVoucher(Long voucherId) {
//
//        UserDTO userId = UserHolder.getUser();
//
//        //Execute the lua script
//        Long result = stringRedisTemplate.execute(
//                SECKILl_SCRIPT,
//                Collections.emptyList(),
//                voucherId.toString(),
//                userId.toString()
//
//        );
//
//        // Check whether the result is 0
//        int r = result.intValue();
//        if(r != 0){
//            // not 0, Ineligibility to buy
//            return Result.fail(r == 1 ? "库存不足" : "不能重复下单");
//        }
//
//        //Be eligible to buy,Save to blocking queue
//        VoucherOrder voucherOrder = new VoucherOrder();//create order
//
//        long orderId = redisIdworker.nextId("order");
//
//        voucherOrder.setId(orderId);
//
//        voucherOrder.setUserId(userId.getId());
//
//        voucherOrder.setVoucherId(voucherId);
//
//
//        ordersTasks.add(voucherOrder);//Add order information to the blocking queue
//
//        // Get proxy object
//      proxy = (IVoucherOrderService) AopContext.currentProxy();
//
//        // retrun orderid
//            return Result.ok(orderId);
//    }



//  // @Override  （原java实现代码）
//    public Result seckillVoucher(Long voucherId) {
//
//        SeckillVoucher voucherid = iSeckillVoucherService.getById(voucherId);
//
//
//        //Second kill time logic 1
//       if(voucherid.getBeginTime().isAfter(LocalDateTime.now())) {
//
//           return Result.fail("秒杀未开始");
//
//        }
//
//       //Second kill time logic 2
//       if(voucherid.getEndTime().isBefore(LocalDateTime.now())){
//           return Result.fail("秒杀已结束");
//       }
//
//
//
//        //Inventory is less than 1 insufficient
//       if (voucherid.getStock() < 1 ){
//
//           return Result.fail("库存不足");
//        }
//
//
//
//        //Pessimistic lock
//        Long userid = UserHolder.getUser().getId();
////        synchronized (userid.toString().intern()) {
//
//        //create lock object
//       //Scrap code{ SimpleRedisLock lock = new SimpleRedisLock("order:" + userid, stringRedisTemplate);}
//        RLock lock = redissonClient.getLock("lock:order:" + userid);
//
//        //get lock
//        boolean trylock = lock.tryLock();
//
//        // Check whether the operation is successful
//        if(!trylock){
//            return Result.fail("不允许重复下单");
//        }try {
//
//            // Get proxy object (transaction)
//            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
//            return proxy.createVoucherOrder(voucherId);
//        }finally {
//            lock.unlock();// Release the lock
//
//
//        }
//
////        }
//    }


    @Transactional
    public void createVoucherOrder(VoucherOrder voucherOrder) {
        //One person, one single
       // Long userid = UserHolder.getUser().getId();
        Long userid = voucherOrder.getUserId();



        Long count = query().eq("user_id", userid).eq("voucher_id", voucherOrder).count();

            if (count > 0) {
              //  return Result.fail("用户已经购买过一次");
                log.error("用户已购买一次");
                return;
            }


            //optimistic lock
            boolean success = iSeckillVoucherService.update().setSql("stock = stock -1")
                    .eq("voucher_id", voucherOrder).gt("stock", 0).update();


            if (!success) {
               // return Result.fail("库存不足");
                log.error("库存不足");
                return;
            }



//            VoucherOrder voucherOrder = new VoucherOrder();//create order
//
//            long orderId = redisIdworker.nextId("order");
//            voucherOrder.setId(orderId);
//
//            Long userId = UserHolder.getUser().getId();
//
//            voucherOrder.setUserId(userId);
//
//            voucherOrder.setVoucherId(voucherOrder);


            save(voucherOrder);

//
//            return Result.ok(orderId);
      }

}
