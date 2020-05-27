package org.jeecg.modules.system.service;

import org.jeecg.modules.system.entity.OrderCustomer;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * 订单消费者表
 */
public interface IOrderCustomerService extends IService<OrderCustomer> {

	List<OrderCustomer> selectByMainId(String mainId);
}
