package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.GoodType;
import org.jeecg.modules.system.mapper.GoodTypeMapper;
import org.jeecg.modules.system.service.IGoodTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 票商品分类
 */
@Service
public class GoodTypeServiceImpl extends ServiceImpl<GoodTypeMapper, GoodType> implements IGoodTypeService {

	@Autowired
	private GoodTypeMapper goodTypeMapper;

	@Override
	public void addGoodType(GoodType goodType) {
		if(oConvertUtils.isEmpty(goodType.getPid())){
			goodType.setPid(IGoodTypeService.ROOT_PID_VALUE);
		}else{
			//如果当前节点父ID不为空 则设置父节点的hasChildren 为1
			GoodType parent = goodTypeMapper.selectById(goodType.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				goodTypeMapper.updateById(parent);
			}
		}
		goodTypeMapper.insert(goodType);
	}
	
	@Override
	public void updateGoodType(GoodType goodType) {
		GoodType entity = this.getById(goodType.getId());
		if(entity==null) {
			throw new JeecgBootException("未找到对应实体");
		}
		String old_pid = entity.getPid();
		String new_pid = goodType.getPid();
		if(!old_pid.equals(new_pid)) {
			updateOldParentNode(old_pid);
			if(oConvertUtils.isEmpty(new_pid)){
				goodType.setPid(IGoodTypeService.ROOT_PID_VALUE);
			}
			if(!IGoodTypeService.ROOT_PID_VALUE.equals(goodType.getPid())) {
				goodTypeMapper.updateTreeNodeStatus(goodType.getPid(), IGoodTypeService.HASCHILD);
			}
		}
		goodTypeMapper.updateById(goodType);
	}
	
	@Override
	public void deleteGoodType(String id) throws JeecgBootException {
		GoodType goodType = this.getById(id);
		if(goodType==null) {
			throw new JeecgBootException("未找到对应实体");
		}
		updateOldParentNode(goodType.getPid());
		goodTypeMapper.deleteById(id);
	}

	/**
	 * 根据所传pid查询旧的父级节点的子节点并修改相应状态值
	 * @param pid
	 */
	private void updateOldParentNode(String pid) {
		if(!IGoodTypeService.ROOT_PID_VALUE.equals(pid)) {
			Integer count = goodTypeMapper.selectCount(new QueryWrapper<GoodType>().eq("pid", pid));
			if(count==null || count<=1) {
				goodTypeMapper.updateTreeNodeStatus(pid, IGoodTypeService.NOCHILD);
			}
		}
	}

}
