package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.OrderCustomer;
import org.jeecg.modules.system.entity.StockOrderGood;
import org.jeecg.modules.system.mapper.OrderCustomerMapper;
import org.jeecg.modules.system.mapper.StockGoodsMapper;
import org.jeecg.modules.system.mapper.StockOrderGoodMapper;
import org.jeecg.modules.system.mapper.StockOrderMapper;
import org.jeecg.modules.system.entity.StockOrder;
import org.jeecg.modules.system.service.IStockOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 订单表
 */
@Service
public class StockOrderServiceImpl extends ServiceImpl<StockOrderMapper, StockOrder> implements IStockOrderService {

    @Autowired
    private StockOrderMapper stockOrderMapper;

    @Autowired
    private StockOrderGoodMapper stockOrderGoodMapper;

    @Autowired
    private OrderCustomerMapper orderCustomerMapper;

    @Autowired
    private StockGoodsMapper stockGoodsMapper;

    @Override
    public int updateStatus(Integer status, String id) {
        return stockOrderMapper.updateStatus(status, id);
    }

    @Override
    @Transactional
    public void saveMain(StockOrder stockOrder, List<StockOrderGood> stockOrderGoodList,
                         List<OrderCustomer> orderCustomerList) {
        //新增主表-订单信息
        stockOrderMapper.insert(stockOrder);
        commonMethodsForInsert(stockOrder, stockOrderGoodList, orderCustomerList);

    }

    @Override
    @Transactional
    public void updateMain(StockOrder stockOrder, List<StockOrderGood> stockOrderGoodList,
                           List<OrderCustomer> orderCustomerList) {
        stockOrderMapper.updateById(stockOrder);

        //1.先删除子表数据
        stockOrderGoodMapper.deleteByMainId(stockOrder.getId());
        orderCustomerMapper.deleteByMainId(stockOrder.getId());

        //2.子表数据重新插入
        commonMethodsForInsert(stockOrder, stockOrderGoodList, orderCustomerList);
    }

    @Override
    @Transactional
    public void delMain(String id) {
        commonMethodsForDelete(id);
    }

    @Override
    @Transactional
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            commonMethodsForDelete(id.toString());
        }
    }

    private void commonMethodsForInsert(StockOrder stockOrder, List<StockOrderGood> stockOrderGoodList,
                                        List<OrderCustomer> orderCustomerList) {
        //判断一下
        if (oConvertUtils.listIsNotEmpty(stockOrderGoodList)) {
            for (StockOrderGood entity : stockOrderGoodList) {
                //外键设置
                //新增订单-票商品
                entity.setOrderMainId(stockOrder.getId());
                entity.setProId(stockGoodsMapper.getProIdByTitle(entity.getTitle()));
                stockOrderGoodMapper.insert(entity);
            }
        }

        if (oConvertUtils.listIsNotEmpty(orderCustomerList)) {
            for (OrderCustomer entity : orderCustomerList) {
                //新增订单-消费者
                entity.setOrderMainId(stockOrder.getId());
                orderCustomerMapper.insert(entity);
            }
        }
    }

    private void commonMethodsForDelete(String id) {
        stockOrderGoodMapper.deleteByMainId(id);
        orderCustomerMapper.deleteByMainId(id);
        stockOrderMapper.deleteById(id);
    }

}
