package org.jeecg.modules.system.service;

import org.jeecg.modules.system.entity.StockOrderGood;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * 订单票商品表
 */
public interface IStockOrderGoodService extends IService<StockOrderGood> {

	List<StockOrderGood> selectByMainId(String mainId);
}
