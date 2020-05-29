package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.OrderCustomer;
import org.jeecg.modules.system.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 用户表
 */
public interface SysUserMapper extends BaseMapper<SysUser> {
	/**
	 * 通过用户账号查询用户信息
	 */
	SysUser getUserByName(@Param("username") String username);


	/**
	 * 通过用户账号查询用户id
	 */
	String getUserIdByName(@Param("username") String username);

	/**
	 * 根据角色Id查询用户信息
	 */
	IPage<SysUser> getUserByRoleId(Page page, @Param("roleId") String roleId, @Param("username") String username);

	/**
	 * 根据票商品id查询用户（卖家）信息
	 */
	IPage<SysUser> getUserByGoodId(Page page, @Param("goodId") String goodId, @Param("username") String username);


	/**
	 * 根据手机号查询用户信息
	 * @param phone
	 * @return
	 */
	SysUser getUserByPhone(@Param("phone") String phone);
	
	
	/**
	 * 根据邮箱查询用户信息
	 */
	SysUser getUserByEmail(@Param("email")String email);

    /**
     * 查询 getUserByOrgCode 的Total
     */
    Integer getUserByOrgCodeTotal(@Param("orgCode") String orgCode, @Param("userParams") SysUser userParams);

    /**
	 * 批量删除角色与用户关系
     */
	void deleteBathRoleUserRelation(@Param("roleIdArray") String[] roleIdArray);

    /**
	 * 批量删除角色与权限关系
     */
	void deleteBathRolePermissionRelation(@Param("roleIdArray") String[] roleIdArray);

	/**
	 * 查询被逻辑删除的用户
	 */
	List<SysUser> selectLogicDeleted(@Param(Constants.WRAPPER) Wrapper<SysUser> wrapper);

	/**
	 * 还原被逻辑删除的用户
	 */
	int revertLogicDeleted(@Param("userIds") String userIds, @Param("entity") SysUser entity);

	/**
	 * 彻底删除被逻辑删除的用户
	 */
	int deleteLogicDeleted(@Param("userIds") String userIds);


	String getUserNameById(@Param("userId") String userId);


	OrderCustomer selectMessageForOrderCustomer(@Param("currentUserId") String currentUserId);


	int updatePersonalData(@Param("user") SysUser user, @Param("currentUserId") String currentUserId);

}
