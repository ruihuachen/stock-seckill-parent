package org.jeecg.modules.demo.test.service;

import java.util.List;

import org.jeecg.modules.demo.test.entity.JeecgOrderCustomer;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单客户
 */
public interface IJeecgOrderCustomerService extends IService<JeecgOrderCustomer> {
	
	List<JeecgOrderCustomer> selectCustomersByMainId(String mainId);
}
