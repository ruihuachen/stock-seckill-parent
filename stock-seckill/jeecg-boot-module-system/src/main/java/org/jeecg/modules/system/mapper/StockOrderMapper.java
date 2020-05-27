package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.StockOrder;

/**
 * 订单表
 */
public interface StockOrderMapper extends BaseMapper<StockOrder> {

    int updateStatus(@Param("status") Integer status, @Param("id") String id);

}
