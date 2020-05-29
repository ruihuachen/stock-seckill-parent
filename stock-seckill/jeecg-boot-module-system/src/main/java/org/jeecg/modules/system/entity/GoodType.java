package org.jeecg.modules.system.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 票商品分类
 */
@Data
@TableName("good_type")
public class GoodType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 分类名字
     */
    @Excel(name = "分类名字", width = 15)
    private String name;

    /**
     * 描述
     */
    @Excel(name = "描述", width = 15)
    private String content;

    /**
     * 图片
     */
    @Excel(name = "图片", width = 15)
    private String picture;

    /**
     * 父级节点
     */
    @Excel(name = "父级节点", width = 15)
    private String pid;

    /**
     * 是否有子节点
     */
    @Excel(name = "是否有子节点", width = 15)
    private String hasChild;

    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    private String createBy;

    /**
     * 创建日期
     */
    @Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新人
     */
    @Excel(name = "更新人", width = 15)
    private String updateBy;

    /**
     * 更新日期
     */
    @Excel(name = "更新日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
