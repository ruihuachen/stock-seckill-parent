package org.jeecg.modules.system.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.mapper.GoodTypeMapper;
import org.jeecg.modules.system.entity.StockGoods;
import org.jeecg.modules.system.mapper.StockGoodsMapper;
import org.jeecg.modules.system.mapper.SysUserGoodMapper;
import org.jeecg.modules.system.service.IStockGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

/**
 * 商品表
 */
@Service
@Slf4j
public class StockGoodsServiceImpl extends ServiceImpl<StockGoodsMapper, StockGoods> implements IStockGoodsService {

    @Autowired
    private StockGoodsMapper stockGoodsMapper;

    @Autowired
    private SysUserGoodMapper userGoodMapper;


    @Override
    @Transactional
    public boolean deleteGood(String goodId) {
        //1.删除票商品和用户关系
        userGoodMapper.deleteGoodUserRelation(goodId);
        //2.删除票商品
        this.removeById(goodId);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteBatchGood(String[] goodIds) {
        //1.删除票商品和用户关系
        userGoodMapper.deleteBathGoodUserRelation(goodIds);
        //2.删除票商品
        removeByIds(Arrays.asList(goodIds));
        return true;
    }



    //--------------------------------------限时间抢票-------------------------------------------

    @Override
    public int getStockCount(String id) {
        StockGoods stock = stockGoodsMapper.selectByPrimaryKey(id);
        return stock.getInventory();
    }

    @Override
    public StockGoods getStockById(String id) {
        return stockGoodsMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateStockById(StockGoods stock) {
        return stockGoodsMapper.updateByPrimaryKey(stock);
    }

    @Override
    public int updateStockByOptimisticLock(StockGoods stock) {
        return stockGoodsMapper.updateByOptimisticLock(stock);
    }

    @Override
    public int initDB() {
        return stockGoodsMapper.initDBBefore();
    }

    @Override
    public List<StockGoods> getStockGoods() {
        return stockGoodsMapper.queryAll(0, 10);
    }
}
