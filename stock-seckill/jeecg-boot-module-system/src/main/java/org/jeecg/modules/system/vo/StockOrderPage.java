package org.jeecg.modules.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.modules.system.entity.OrderCustomer;
import org.jeecg.modules.system.entity.StockOrderGood;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * 订单表
 */
@Data
@ApiModel(value = "stock_orderPage对象", description = "订单表")
public class StockOrderPage {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private java.lang.String id;

    /**
     * 订单编码
     */
    @Excel(name = "订单编码", width = 15)
    @ApiModelProperty(value = "订单编码")
    private java.lang.String orderId;

    /**
     * 订单状态
     */
    @Excel(name = "订单状态", width = 15)
    @ApiModelProperty(value = "订单状态")
    private java.lang.Integer status;

    /**
     * 下单时间
     */
    @Excel(name = "下单时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "下单时间")
    private java.util.Date orderTime;

    /**
     * 订单总金额
     */
    @Excel(name = "订单总金额", width = 15)
    @ApiModelProperty(value = "订单总金额")
    private java.lang.Double total;

    /**
     * 订单备注
     */
    @Excel(name = "订单备注", width = 15)
    @ApiModelProperty(value = "订单备注")
    private java.lang.String remark;

    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;

    /**
     * 创建日期
     */
    @Excel(name = "创建日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;

    /**
     * 更新人
     */
    @Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;

    /**
     * 更新日期
     */
    @Excel(name = "更新日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;

    @ExcelCollection(name = "订单票商品表")
    @ApiModelProperty(value = "订单票商品表")
    private List<StockOrderGood> stockOrderGoodList;

    @ExcelCollection(name = "订单消费者表")
    @ApiModelProperty(value = "订单消费者表")
    private List<OrderCustomer> orderCustomerList;

}
