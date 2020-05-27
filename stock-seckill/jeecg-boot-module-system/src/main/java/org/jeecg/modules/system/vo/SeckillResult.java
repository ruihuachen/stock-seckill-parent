package org.jeecg.modules.system.vo;

import lombok.Data;

/**
 * 封装JSON
 * @param <T>
 */
@Data
public class SeckillResult<T> {

    private boolean success;

    private T data;

    private String error;

    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public SeckillResult() {

    }
}
