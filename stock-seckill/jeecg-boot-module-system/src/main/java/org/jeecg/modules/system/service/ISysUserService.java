package org.jeecg.modules.system.service;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.SysUserCacheInfo;
import org.jeecg.modules.system.entity.SysUser;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户表 服务类
 */
public interface ISysUserService extends IService<SysUser> {

	/**
	 * 新增用户
	 * 并添加用户和用户角色关系
	 */
	void addUserWithRole(SysUser user,String roles);

	/**
	 * 修改用户以及用户角色关系
	 */
	void editUserWithRole(SysUser user,String roles);

	/**
	 * 删除用户
	 */
	void deleteUser(String userId);

	/**
	 * 批量删除用户
	 */
	void deleteBatchUsers(String userIds);

	/**
	 * 查询所有逻辑删除的用户
	 */
	List<SysUser> queryLogicDeleted();

	/**
	 * 查询所有逻辑删除的用户（可拼装自己所需的查询条件）
	 */
	List<SysUser> queryLogicDeleted(LambdaQueryWrapper<SysUser> wrapper);

	/**
	 * 还原逻辑删除的用户
	 */
	boolean revertLogicDeleted(List<String> userIds, SysUser updateEntity);

	/**
	 * 彻底删除逻辑删除的用户
	 */
	boolean removeLogicDeleted(List<String> userIds);


	/**
	 * 根据用户账号模糊查询
	 * @param username
	 * @return
	 */
	SysUser getUserByName(String username);

	/**
	 * 重置密码
	 * @param username
	 * @param oldpassword
	 * @param newpassword
	 * @param confirmpassword
	 * @return
	 */
	Result<?> resetPassword(String username, String oldpassword, String newpassword, String confirmpassword);

	/**
	 * 修改密码
	 * @param sysUser
	 * @return
	 */
	public Result<?> changePassword(SysUser sysUser);

	/**
	 * 获取用户的授权角色
	 * @param username
	 * @return
	 */
	List<String> getRole(String username);

    /**
     * 根据部门 Id 和 QueryWrapper 查询
     *
     * @param page
     * @param departId
     * @param queryWrapper
     * @return
     */
    IPage<SysUser> getUserByDepartIdAndQueryWrapper(Page<SysUser> page, String departId, QueryWrapper<SysUser> queryWrapper);

	/**
	 * 根据角色Id查询
	 * @param
	 * @return
	 */
	IPage<SysUser> getUserByRoleId(Page<SysUser> page, String roleId, String username);

	/**
	 * 根据票商品Id查询
	 * @param goodId
	 * @param username
	 * @return
	 */
	IPage<SysUser> getUserByGoodId(Page<SysUser> page, String goodId, String username);

	/**
	 * 通过用户名获取用户角色集合
	 *
	 * @param username 用户名
	 * @return 角色集合
	 */
	Set<String> getUserRolesSet(String username);

	/**
	 * 通过用户名获取用户权限集合
	 *
	 * @param username 用户名
	 * @return 权限集合
	 */
	Set<String> getUserPermissionsSet(String username);
	
	/**
	 * 根据用户名设置部门ID
	 * @param username
	 * @param orgCode
	 */
	void updateUserDepart(String username,String orgCode);
	
	/**
	 * 根据手机号获取用户名和密码
	 */
	SysUser getUserByPhone(String phone);


	/**
	 * 根据邮箱获取用户
	 */
	SysUser getUserByEmail(String email);
	
	/**
	 * 校验用户是否有效
	 * @param sysUser
	 * @return
	 */
	Result checkUserIsEffective(SysUser sysUser);


	/**
	 * 查询用户信息
	 * @param username
	 * @return
	 */
	SysUserCacheInfo getCacheUser(String username);

	int updatePersonalData(SysUser user, String currentUserId);
}
