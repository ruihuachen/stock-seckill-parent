package org.jeecg.modules.system.service;

import java.util.List;

import org.jeecg.modules.system.entity.SysPermissionDataRule;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 菜单权限规则 服务类
 */
public interface ISysPermissionDataRuleService extends IService<SysPermissionDataRule> {

	/**
	 * 根据菜单id查询其对应的权限数据
	 */
	List<SysPermissionDataRule> getPermRuleListByPermId(String permissionId);

	/**
	 * 根据页面传递的参数查询菜单权限数据
	 */
	List<SysPermissionDataRule> queryPermissionRule(SysPermissionDataRule permRule);
	
	
	/**
	  * 根据菜单ID和用户名查找数据权限配置信息
	 */
	List<SysPermissionDataRule> queryPermissionDataRules(String username,String permissionId);
	
	/**
	 * 新增菜单权限配置 修改菜单rule_flag
	 */
	void savePermissionDataRule(SysPermissionDataRule sysPermissionDataRule);
	
	/**
	 * 删除菜单权限配置 判断菜单还有无权限
	 */
	void deletePermissionDataRule(String dataRuleId);
}
