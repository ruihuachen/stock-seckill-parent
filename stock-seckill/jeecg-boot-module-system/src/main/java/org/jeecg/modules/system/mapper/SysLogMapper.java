package org.jeecg.modules.system.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysLog;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 系统日志表
 */
public interface SysLogMapper extends BaseMapper<SysLog> {

	/**
	 * 清空所有日志记录
	 */
	public void removeAll();

	/**
	 * 获取系统总访问次数
	 */
	Long findTotalVisitCount();

	/**
	 * 获取系统今日访问次数
	 */
	Long findTodayVisitCount(@Param("dayStart") Date dayStart, @Param("dayEnd") Date dayEnd);

	/**
	 * 获取系统今日访问 IP数
	 */
	Long findTodayIp(@Param("dayStart") Date dayStart, @Param("dayEnd") Date dayEnd);

	/**
	 * 首页：根据时间统计访问数量/ip数量
	 */
	List<Map<String,Object>> findVisitCount(@Param("dayStart") Date dayStart, @Param("dayEnd") Date dayEnd, @Param("dbType") String dbType);
}
