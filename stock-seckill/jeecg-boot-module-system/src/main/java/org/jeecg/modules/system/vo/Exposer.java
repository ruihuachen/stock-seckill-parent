package org.jeecg.modules.system.vo;

import lombok.Data;

@Data
public class Exposer {

    //是否开启秒杀
    private boolean exposed;

    //对秒杀地址进行加密
    private String md5;

    //该票商品ID的秒杀地址
    private String stockGoodId;

    //系统当前时间(毫秒)
    private long now;

    private long start;

    private long end;

    public Exposer(boolean exposed, String md5, String stockGoodId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.stockGoodId = stockGoodId;
    }

    public Exposer(boolean exposed, String stockGoodId, long now, long start, long end) {
        this.exposed = exposed;
        this.stockGoodId = stockGoodId;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    public Exposer(boolean exposed, String stockGoodId) {
        this.exposed = exposed;
        this.stockGoodId = stockGoodId;
    }

    public Exposer(){

    }
}
