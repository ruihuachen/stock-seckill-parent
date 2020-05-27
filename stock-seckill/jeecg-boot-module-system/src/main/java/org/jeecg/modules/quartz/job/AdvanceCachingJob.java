package org.jeecg.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.StockGoods;
import org.jeecg.modules.system.enums.RedisKeyEnum;
import org.jeecg.modules.system.service.ISeckillService;
import org.jeecg.modules.system.util.RedisPoolUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 定时任务
 * 使redis提前一个小时缓存需要开抢的票商品信息
 * 票商品详情页-暴露url缓存了对象 id：object
 * 先在redis中减库存：已缓存库存 已出售量 版本号
 * stockGood_inventory_(id)
 * stockGood_sale_(sale)
 * stockGood_version_(version)
 */
@Slf4j
public class AdvanceCachingJob implements Job {

    @Autowired
    private ISeckillService seckillService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<StockGoods> list = seckillService.getAdvanceCachingToRedis();
        list.forEach(item -> {
            //一个小时后若是同一个对象那么新缓存会覆盖旧缓存，新的则增加
            redisUtil.set(item.getId(), item, 60 * 60);
            RedisPoolUtil.set(RedisKeyEnum.STOCK_INVENTORY.getKeyInfo() + item.getId(), String.valueOf(item.getInventory()));
            RedisPoolUtil.set(RedisKeyEnum.STOCK_SALE.getKeyInfo() + item.getId(), String.valueOf(item.getSale()));
            RedisPoolUtil.set(RedisKeyEnum.STOCK_VERSION.getKeyInfo() + item.getId(), String.valueOf(item.getVersion()));
        });
        log.info(String.format(" redis提前热缓存 定时任务 AdvanceCachingJob !  时间:" + DateUtils.getTimestamp()));
    }
}


