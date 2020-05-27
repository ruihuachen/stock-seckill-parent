package org.jeecg.modules.demo.test.service;

import java.util.List;

import org.jeecg.modules.demo.test.entity.JeecgOrderTicket;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单机票
 */
public interface IJeecgOrderTicketService extends IService<JeecgOrderTicket> {
	
	List<JeecgOrderTicket> selectTicketsByMainId(String mainId);
}
