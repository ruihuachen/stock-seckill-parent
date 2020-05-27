package org.jeecg.modules.quartz.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quartz.entity.QuartzJob;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 定时任务在线管理
 */
public interface QuartzJobMapper extends BaseMapper<QuartzJob> {

    List<QuartzJob> findByJobClassName(@Param("jobClassName") String jobClassName);

}
