//package org.jeecg.modules.system.manager;
//
//
//import org.jeecg.modules.system.entity.StockGoods;
//import org.jeecg.modules.system.enums.RedisKeyEnum;
//import org.jeecg.modules.system.service.IStockGoodsService;
//import org.jeecg.modules.system.util.RedisPoolUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
///**
// * create by Ernest on 2020/3/24.
// */
//@Component
//public class RedisPreheatRunner implements ApplicationRunner {
//
//    @Autowired
//    private IStockGoodsService stockService;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        //查询热点商品信息
//        StockGoods stock = stockService.getStockById("1253527237458767873");
//
//        //删除旧缓存
//        RedisPoolUtil.del(RedisKeyEnum.STOCK_INVENTORY.getKeyInfo() + stock.getInventory());
//        RedisPoolUtil.del(RedisKeyEnum.STOCK_SALE.getKeyInfo() + stock.getSale());
//        RedisPoolUtil.del(RedisKeyEnum.STOCK_VERSION.getKeyInfo() + stock.getVersion());
//
//        //缓存预热
//        String sid = stock.getId();
//        RedisPoolUtil.set(RedisKeyEnum.STOCK_INVENTORY.getKeyInfo() + sid, String.valueOf(stock.getInventory()));
//        RedisPoolUtil.set(RedisKeyEnum.STOCK_SALE.getKeyInfo() + sid, String.valueOf(stock.getSale()));
//        RedisPoolUtil.set(RedisKeyEnum.STOCK_VERSION.getKeyInfo() + sid, String.valueOf(stock.getVersion()));
//
//    }
//}
