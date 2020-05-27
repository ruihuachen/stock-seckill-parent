package org.jeecg.modules.system.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 票商品表
 */
@Data
@TableName("stock_goods")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "stock_goods对象", description = "票商品表")
public class StockGoods implements Serializable {
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
    @Excel(name = "票商品编码", width = 15)
    @ApiModelProperty(value = "票商品编码")
    private String proId;

    /**
     * 商品名称
     */
    @Excel(name = "票商品名称", width = 15)
    @ApiModelProperty(value = "票商品名称")
    private String title;

    /**
     * 商品描述
     */
    @Excel(name = "票商品描述", width = 15)
    @ApiModelProperty(value = "票商品描述")
    private String description;

    /**
     * 状态
     * 使用数据字典（1-正常 2-禁用）
     */
    @Excel(name = "状态", width = 15, dicCode="goods_status")
    @ApiModelProperty(value = "状态")
    @Dict(dicCode = "goods_status")
    private Integer status;

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
    private Integer inventory;

    /**
     * 已出售数量
     */
    @Excel(name = "已出售数量", width = 15)
    @ApiModelProperty(value = "已出售数量")
    private Integer sale;

    /**
     * 乐观锁字段
     */
    @Excel(name = "版本号", width = 15)
    @ApiModelProperty(value = "版本号")
    private Integer version;

    /**
     * 类型
     */
    @Excel(name = "类型", width = 15, dictTable = "good_type", dicText = "name", dicCode = "id")
    @Dict(dictTable = "good_type", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "类型")
    private String type;

    /**
     * 点击量
     */
    @Excel(name = "点击量", width = 15)
    @ApiModelProperty(value = "点击量")
    private String pv;

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

    /**
     * 开抢时间
     * */
    @Excel(name = "开抢时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开抢时间")
    private java.util.Date startTime;

    /**
     * 结束开抢时间
     * */
    @Excel(name = "结束开抢时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束开抢时间")
    private java.util.Date endTime;

    public StockGoods(String id, Integer inventory, Integer sale, Integer version) {
        this.id = id;
        this.inventory = inventory;
        this.sale = sale;
        this.version = version;
    }

    public StockGoods() {
    }
}
