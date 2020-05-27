package org.jeecg.modules.system.mapper;

import java.util.List;
import org.jeecg.modules.system.entity.StockOrderGood;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单票商品表
 */
public interface StockOrderGoodMapper extends BaseMapper<StockOrderGood> {

	boolean deleteByMainId(@Param("mainId") String mainId);
    
	List<StockOrderGood> selectByMainId(@Param("mainId") String mainId);
}
