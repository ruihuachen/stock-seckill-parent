package org.jeecg.modules.system.mapper;

import java.util.List;
import org.jeecg.modules.system.entity.OrderCustomer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单消费者表
 */
public interface OrderCustomerMapper extends BaseMapper<OrderCustomer> {

	boolean deleteByMainId(@Param("mainId") String mainId);
    
	List<OrderCustomer> selectByMainId(@Param("mainId") String mainId);
}
