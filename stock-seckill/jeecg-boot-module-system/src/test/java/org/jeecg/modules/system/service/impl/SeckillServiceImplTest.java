package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.FillRuleUtil;
import org.jeecg.modules.system.entity.OrderCustomer;
import org.jeecg.modules.system.entity.StockGoods;
import org.jeecg.modules.system.entity.StockOrder;
import org.jeecg.modules.system.entity.StockOrderGood;
import org.jeecg.modules.system.enums.SeckillStateEnum;
import org.jeecg.modules.system.exception.RepeatKillException;
import org.jeecg.modules.system.exception.SeckillCloseException;
import org.jeecg.modules.system.exception.SeckillException;
import org.jeecg.modules.system.mapper.*;
import org.jeecg.modules.system.vo.SeckillExecution;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SeckillServiceImplTest {

    @Autowired
    private StockGoodsMapper stockGoodsMapper;

    @Autowired
    private StockOrderGoodMapper stockOrderGoodMapper;

    @Autowired
    private StockOrderMapper stockOrderMapper;

    @Autowired
    private OrderCustomerMapper orderCustomerMapper;

    @Autowired
    private SysUserMapper userMapper;


    @Test
    public void testOptimistic() {
        StockGoods stockGoods = new StockGoods();
        stockGoods.setId("1253527237458767873");
        System.out.println(stockGoodsMapper.updateByOptimisticLock(stockGoods));
    }

    //todo success
    @Test
    public void executeSeckill() {
        String stockGoodId = "1253527237458767873";
        String currentUserId = ((LoginUser) SecurityUtils.getSubject().getPrincipal()).getId();
        //String currentUserId = "1249992232845484034";

        try {

            //记录购买行为-完成对三张表的操作
            //订单-票商品表
            StockOrderGood stockOrderGood = new StockOrderGood();
            StockGoods stockGood = stockGoodsMapper.getStockGoodById(stockGoodId);

            System.out.println();
            System.out.println(stockGood.toString());

            //订单表
            Double price = stockGood.getPrice();
            String orderId = (String) FillRuleUtil.executeRule("order_code_num", null);
            StockOrder order = new StockOrder(orderId, new Date(), price);
            order.setUserId(currentUserId);
            order.setGoodsId(stockGoodId);

            System.out.println("order " + order.toString());

            int insertCount = stockOrderMapper.insert(order);

            if (insertCount <= 0) {
                //重复秒杀
                log.warn("重复秒杀");
                throw new RepeatKillException("seckill repeated");
            } else {
                stockOrderGood.setProId(stockGood.getProId());
                stockOrderGood.setTitle(stockGood.getTitle());
                stockOrderGood.setDescription(stockGood.getDescription());
                stockOrderGood.setPrice(stockGood.getPrice());
                stockOrderGood.setTotalMoney(stockGood.getPrice());
                stockOrderGood.setOrderMainId(orderId);

                System.out.println("stockOrderGood " + stockOrderGood.toString());

                stockOrderGoodMapper.insert(stockOrderGood);

                //给之前订单-票商品更新orderMainId
//            stockOrderGoodMapper.update(new StockOrderGood().setOrderMainId(orderId),
//                    new LambdaQueryWrapper<StockOrderGood>().eq(StockOrderGood::getTitle, stockGood.getTitle()));

                //订单-消费者表
                System.out.println(".....currentUserId " + currentUserId);
                OrderCustomer orderCustomer = userMapper.selectMessageForOrderCustomer(currentUserId);
                orderCustomer.setOrderMainId(orderId);

                System.out.println("orderCustomer " + orderCustomer);

                orderCustomerMapper.insert(orderCustomer);

                //乐观锁减少库存
                int updateCount = stockGoodsMapper.updateByOptimisticLock(stockGood);
                if (updateCount <= 0) {
                    //没有更新到记录,秒杀结束
                    log.warn("没有更新数据库记录,说明抢票结束");
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //抢票成功
                    new SeckillExecution(stockGoodId, SeckillStateEnum.SUCCESS, order);
                }
            }

        } catch (SeckillCloseException e1) {
            //直接抛出
            throw e1;
        } catch (RepeatKillException e2) {
            //直接抛出
            throw e2;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new SeckillException("system inner error:" + e.getMessage());
        }
    }


}