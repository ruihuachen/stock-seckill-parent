package org.jeecg.modules.system.service;

import org.jeecg.modules.system.entity.StockGoods;
import org.jeecg.modules.system.entity.StockOrder;
import org.jeecg.modules.system.exception.RepeatKillException;
import org.jeecg.modules.system.exception.SeckillCloseException;
import org.jeecg.modules.system.exception.SeckillException;
import org.jeecg.modules.system.vo.Exposer;
import org.jeecg.modules.system.vo.SeckillExecution;

import java.util.List;

/**
 * 业务接口:站在"使用者"角度设计接口
 */
public interface ISeckillService {


    /**
     * 缓存预热：
     * 提前一个小时将即将达到开抢时间的票商品信息
     * 缓存在redis中
     */
    List<StockGoods> getAdvanceCachingToRedis();


    /**
     * 查询全部的秒杀票商品列表
     */
    List<StockGoods> getSeckillList();


    /**
     * 抢票开启则输出秒杀接口地址
     * 否则输出系统时间和开始抢票时间
     */
    Exposer exportSeckillUrl(String stockGoodId);


    /**
     * 执行秒杀操作
     */
    SeckillExecution executeSeckill(StockOrder stockOrder, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException;

    /**
     * 执行秒杀操作
     */
    SeckillExecution executeSeckill(String stockGoodId, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException;

    /**
     * 创建订单的参数
     * @param stockGoodId
     * @param buyerId
     * @return
     */
    StockOrder getSeckillOrder(String stockGoodId, String buyerId);


    /**
     * 创建新订单
     * @param stockGoodId
     * @param buyerId
     * @return
     */
    StockOrder createSeckillOrder(String stockGoodId, String buyerId);


    //--------------------------------------限时间抢票-------------------------------------------

    //清空订单表
    int deleteOrderDBBefore();

    //创建订单（存在超卖问题）
    int createWrongOrder(String stockGoodId) throws Exception;

    //数据库乐观锁更新库存，解决超卖问题
    int createOptimisticLockOrder(String stockGoodId) throws Exception;

    //数据库乐观锁更新库存，库存查询redis 减少db读压力
    int createOrderWithLimitByRedis(String stockGoodId) throws Exception;

    //数据库乐观锁更新库存，库存查询redis 减少db读压力
    SeckillExecution createOrderWithLimitByRedis2(String stockGoodId, String md5);

    //限流 + redis缓存库存信息 + kafkaTest 异步发送信息
    void createOrderWithLimitByRedisAndKafka(String stockGoodId) throws Exception;

    //kafka消费消息
    int consumerNewsToCreateOrderWithLimitByRedisAndKafka(StockGoods stock) throws Exception;



    int testOptimistic(StockGoods stockGoods);
}
