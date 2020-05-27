package org.jeecg.modules.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.GoodType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 票商品分类
 */
public interface GoodTypeMapper extends BaseMapper<GoodType> {

	/**
	 * 编辑节点状态
	 * @param id
	 * @param status
	 */
	void updateTreeNodeStatus(@Param("id") String id,@Param("status") String status);

}
