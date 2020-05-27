package org.jeecg.modules.system.service;

import java.util.Map;

import org.jeecg.modules.system.entity.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户角色表
 */
public interface ISysUserRoleService extends IService<SysUserRole> {
	
	/**
	 * 查询所有的用户角色信息
	 */
	Map<String,String> queryUserRole();
}
