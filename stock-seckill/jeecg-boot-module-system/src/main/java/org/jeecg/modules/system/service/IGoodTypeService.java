package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.system.entity.GoodType;

/**
 * 票商品分类
 */
public interface IGoodTypeService extends IService<GoodType> {

    /**
     * 根节点父ID的值
     */
    String ROOT_PID_VALUE = "0";

    /**
     * 树节点有子节点状态值
     */
    String HASCHILD = "1";

    /**
     * 树节点无子节点状态值
     */
    String NOCHILD = "0";

    /**
     * 新增节点
     */
    void addGoodType(GoodType goodType);

    /**
     * 修改类型
     */
    void updateGoodType(GoodType goodType) throws JeecgBootException;

    /**
     * 删除节点
     */
    void deleteGoodType(String id) throws JeecgBootException;

}
