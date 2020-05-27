package org.jeecg.modules.system.service.impl;

import org.jeecg.modules.system.entity.OrderCustomer;
import org.jeecg.modules.system.mapper.OrderCustomerMapper;
import org.jeecg.modules.system.service.IOrderCustomerService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 订单消费者表
 */
@Service
public class OrderCustomerServiceImpl extends ServiceImpl<OrderCustomerMapper, OrderCustomer> implements IOrderCustomerService {
	
	@Autowired
	private OrderCustomerMapper orderCustomerMapper;
	
	@Override
	public List<OrderCustomer> selectByMainId(String mainId) {
		return orderCustomerMapper.selectByMainId(mainId);
	}
}
