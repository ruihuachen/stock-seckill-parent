package org.jeecg.modules.system.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jeecg.modules.system.entity.SysLog;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 系统日志表
 */
public interface ISysLogService extends IService<SysLog> {

	/**
	 * 清空所有日志记录
	 */
	void removeAll();
	
	/**
	 * 获取系统总访问次数
	 */
	Long findTotalVisitCount();

	/**
	 * 获取系统今日访问次数
	 */
	Long findTodayVisitCount(Date dayStart, Date dayEnd);

	/**
	 * 获取系统今日访问 IP数
	 */
	Long findTodayIp(Date dayStart, Date dayEnd);

	/**
	 * 首页：根据时间统计访问数量/ip数量
	 */
	List<Map<String,Object>> findVisitCount(Date dayStart, Date dayEnd);
}
