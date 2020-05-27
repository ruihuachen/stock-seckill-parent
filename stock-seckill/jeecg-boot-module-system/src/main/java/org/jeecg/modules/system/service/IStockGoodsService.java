package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.StockGoods;

import java.util.List;

/**
 * 票商品表
 */
public interface IStockGoodsService extends IService<StockGoods> {

    boolean deleteGood(String goodId);

    boolean deleteBatchGood(String[] goodIds);

    void addGood(StockGoods stockGoods);

    void updateGood(StockGoods stockGoods);

    //--------------------------------------限时间抢票-------------------------------------------

    // 根据id -> 获取剩余库存数
    int getStockCount(String id);

    // 根据id -> 获取剩余库存信息
    StockGoods getStockById(String id);

    // 根据id -> 更新库存信息
    int updateStockById(StockGoods stock);

    // 乐观锁更新库存，解决超卖问题
    int updateStockByOptimisticLock(StockGoods stock);

    // 初始化数据库
    int initDB();

    List<StockGoods> getStockGoods();
}
