package org.jeecg.modules.system.manager;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.entity.StockGoods;
import org.jeecg.modules.system.enums.RedisKeyEnum;
import org.jeecg.modules.system.util.RedisPool;
import org.jeecg.modules.system.util.RedisPoolUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

/**
 * create by Ernest on 2020/3/17.
 */
@Slf4j
public class StockByRedis {

    /**
     * redis 事务保证库存更新
     * 捕获异常后应该删除缓存
     * @param stockGood
     */
    public static void updateStockByRedis(StockGoods stockGood) {
        Jedis jedis = null;
        try{
            jedis = RedisPool.getJedisFromPool();

            //开始事务
            Transaction transaction = jedis.multi();
            //事务操作
            RedisPoolUtil.decr(RedisKeyEnum.STOCK_INVENTORY.getKeyInfo() + stockGood.getId());
            RedisPoolUtil.incr(RedisKeyEnum.STOCK_SALE.getKeyInfo() + stockGood.getId());
            RedisPoolUtil.incr(RedisKeyEnum.STOCK_VERSION.getKeyInfo() + stockGood.getId());

            //结束事务
            List<Object> list = transaction.exec();
        }catch (Exception e) {
            log.error("updateStock 获取 Jedis 实例失败：", e);
        }finally {
            RedisPool.closeRedisToPool(jedis);
        }
    }


    /**
     * 重置缓存
     */
    public static void initRedisBefore() {
        Jedis jedis = null;
        try{
            jedis = RedisPool.getJedisFromPool();

            Transaction transaction = jedis.multi();
            RedisPoolUtil.set(RedisKeyEnum.STOCK_INVENTORY.getKeyInfo() + 1, "50");
            RedisPoolUtil.set(RedisKeyEnum.STOCK_SALE.getKeyInfo() + 1, "0");
            RedisPoolUtil.set(RedisKeyEnum.STOCK_VERSION.getKeyInfo() + 1, "0");
            List<Object> list = transaction.exec();
        }catch (Exception e) {
            log.error("initRedis 获取 jedis 实例失败: ", e);
        }finally {
            RedisPool.closeRedisToPool(jedis);
        }
    }
}
