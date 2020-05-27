package org.jeecg.modules.system.vo;


import lombok.Data;
import org.jeecg.modules.system.entity.StockOrder;
import org.jeecg.modules.system.enums.SeckillStateEnum;

/**
 * 封装秒杀执行后的结果
 */
@Data
public class SeckillExecution {

    //抢票商品的ID
    private String stockGoodId;

    //秒杀执行结果状态
    private int state;

    //状态表示
    private String stateInfo;

    //秒杀成功对象
    private StockOrder stockOrder;

    public SeckillExecution(String stockGoodId, SeckillStateEnum seckillStateEnum, StockOrder stockOrder) {
        this.stockGoodId = stockGoodId;
        this.state = seckillStateEnum.getState();
        this.stateInfo = seckillStateEnum.getStateInfo();
        this.stockOrder = stockOrder;
    }

    public SeckillExecution(String stockGoodId, SeckillStateEnum seckillStateEnum) {
        this.stockGoodId = stockGoodId;
        this.state = seckillStateEnum.getState();
        this.stateInfo = seckillStateEnum.getStateInfo();
    }

    public SeckillExecution() {

    }
}
