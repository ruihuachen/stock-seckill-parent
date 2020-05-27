package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.OrderCustomer;
import org.jeecg.modules.system.entity.StockOrderGood;
import org.jeecg.modules.system.entity.StockOrder;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 订单表
 */
public interface IStockOrderService extends IService<StockOrder> {

    int updateStatus(Integer status, String id);

    /**
     * 添加一对多
     */
    void saveMain(StockOrder stockOrder, List<StockOrderGood> stockOrderGoodList, List<OrderCustomer> orderCustomerList);

    /**
     * 修改一对多
     */
    void updateMain(StockOrder stockOrder, List<StockOrderGood> stockOrderGoodList, List<OrderCustomer> orderCustomerList);

    /**
     * 删除一对多
     */
    void delMain(String id);

    /**
     * 批量删除一对多
     */
    void delBatchMain(Collection<? extends Serializable> idList);

}
