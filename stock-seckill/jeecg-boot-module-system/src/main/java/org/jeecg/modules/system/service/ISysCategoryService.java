package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.system.entity.SysCategory;
import org.jeecg.modules.system.model.TreeSelectModel;

import java.util.List;
import java.util.Map;

/**
 * 分类字典
 */
public interface ISysCategoryService extends IService<SysCategory> {

    /**
     * 根节点父ID的值
     */
    public static final String ROOT_PID_VALUE = "0";

    void addSysCategory(SysCategory sysCategory);

    void updateSysCategory(SysCategory sysCategory);

    /**
     * 根据父级编码加载分类字典的数据
     */
    List<TreeSelectModel> queryListByCode(String pcode) throws JeecgBootException;

    /**
     * 根据pid查询子节点集合
     */
    List<TreeSelectModel> queryListByPid(String pid);

    /**
     * 根据pid查询子节点集合,支持查询条件
     */
    List<TreeSelectModel> queryListByPid(String pid, Map<String, String> condition);

    /**
     * 根据code查询id
     */
    String queryIdByCode(String code);

}
