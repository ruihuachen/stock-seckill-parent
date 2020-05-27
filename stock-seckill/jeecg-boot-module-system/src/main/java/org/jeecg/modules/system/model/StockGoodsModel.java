package org.jeecg.modules.system.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * create by Ernest on 2020/4/27.
 * 用于前端展示
 */
@Data
public class StockGoodsModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "主键")
    private String id;

    /**
     * 商品编码
     */
    @Excel(name = "商品编码", width = 15)
    @ApiModelProperty(value = "商品编码")
    private String proId;

    /**
     * 商品名称
     */
    @Excel(name = "商品名称", width = 15)
    @ApiModelProperty(value = "商品名称")
    private String title;

    /**
     * 商品描述
     */
    @Excel(name = "商品描述", width = 15)
    @ApiModelProperty(value = "商品描述")
    private String description;

    /**
     * 价格
     */
    @Excel(name = "价格", width = 15)
    @ApiModelProperty(value = "价格")
    private Double price;

    /**
     * 库存
     */
    @Excel(name = "库存", width = 15)
    @ApiModelProperty(value = "库存")
    private String inventory;

    /**
     * 状态
     * 使用数据字典（1-正常 2-禁用）
     */
    @Excel(name = "状态", width = 15, dicCode="goods_status")
    @ApiModelProperty(value = "状态")
    @Dict(dicCode = "goods_status")
    private Integer status;

    /**
     * 类型
     */
    @Excel(name = "类型", width = 15)
    @ApiModelProperty(value = "类型")
    private String type;

    /**
     * 点击量
     */
    @Excel(name = "点击量", width = 15)
    @ApiModelProperty(value = "点击量")
    private String pv;

    /**
     * 卖家ID
     */
    @Excel(name = "卖家ID", width = 15)
    @ApiModelProperty(value = "卖家ID")
    private String sellerId;

    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**
     * 创建日期
     */
    @Excel(name = "创建日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;

    /**
     * 更新人
     */
    @Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    /**
     * 更新日期
     */
    @Excel(name = "更新日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;

//    /**
//     * 登录账号
//     */
//    @Excel(name = "登录账号", width = 15)
//    private String username;
//
//    /**
//     * 电话
//     */
//    @Excel(name = "电话", width = 15)
//    private String phone;
//
//    /**
//     * 发货地址
//     */
//    @Excel(name = "发货地址", width = 15)
//    private String sendAddress;
//
//    /**
//     * 信用值（0：低 1：中 2：高）
//     */
//    @Excel(name="（1：高 2：中 3：低）",width = 15, dicCode = "identity_describe")
//    @Dict(dicCode = "identity_describe")
//    private Integer identity;
}
