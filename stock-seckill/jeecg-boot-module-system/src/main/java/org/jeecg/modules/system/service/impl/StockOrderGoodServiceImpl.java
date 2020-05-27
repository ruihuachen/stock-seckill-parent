package org.jeecg.modules.system.service.impl;

import org.jeecg.modules.system.entity.StockOrderGood;
import org.jeecg.modules.system.mapper.StockOrderGoodMapper;
import org.jeecg.modules.system.service.IStockOrderGoodService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 订单票商品表
 */
@Service
public class StockOrderGoodServiceImpl extends ServiceImpl<StockOrderGoodMapper, StockOrderGood> implements IStockOrderGoodService {
	
	@Autowired
	private StockOrderGoodMapper stockOrderGoodMapper;
	
	@Override
	public List<StockOrderGood> selectByMainId(String mainId) {
		return stockOrderGoodMapper.selectByMainId(mainId);
	}
}
