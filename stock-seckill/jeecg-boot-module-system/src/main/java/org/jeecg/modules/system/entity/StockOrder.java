package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单表
 */
@ApiModel(value = "stock_order对象", description = "订单表")
@Data
@TableName("stock_order")
public class StockOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "id")
    private String id;

    /**
     * 订单编码
     */
    @Excel(name = "订单编码", width = 15)
    @ApiModelProperty(value = "订单编码")
    private String orderId;

    /**
     * 订单状态
     * 使用数据字典（0-待发货 1-已发货 2-已结束 3-冻结）
     * 无论什么状态 -> 冻结 -> 取消冻结回归始点-待发货
     */
    @Excel(name = "订单状态", width = 15, dicCode = "order_status")
    @Dict(dicCode = "order_status")
    @ApiModelProperty(value = "订单状态")
    private Integer status;

    /**
     * 下单时间
     */
    @Excel(name = "下单时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "下单时间")
    private Date orderTime;

    /**
     * 订单总金额
     */
    @Excel(name = "订单总金额", width = 15)
    @ApiModelProperty(value = "订单总金额")
    private Double total;

    /**
     * 订单备注
     */
    @Excel(name = "订单备注", width = 15)
    @ApiModelProperty(value = "订单备注")
    private String remark;

    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**
     * 创建日期
     */
    @Excel(name = "创建日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
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
    @Excel(name = "更新日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;

    @ApiModelProperty(value = "消费者id")
    private String userId;

    @ApiModelProperty(value = "票商品id")
    private String goodsId;

    public StockOrder(String orderId, Date orderTime, Double total) {
        this.orderId = orderId;
        this.orderTime = orderTime;
        this.total = total;
    }

    public StockOrder() {
    }
}
