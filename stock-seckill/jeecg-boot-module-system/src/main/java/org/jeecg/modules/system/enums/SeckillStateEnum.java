package org.jeecg.modules.system.enums;

/**
 * 抢票枚举类
 */
public enum SeckillStateEnum {
    SUCCESS(1, "抢票成功"),
    END(0, "抢票结束"),
    REPEAT_KILL(-1, "重复抢票"),
    INNER_ERROR(-2, "系统异常"),
    DATA_REWRITE(-3, "数据篡改");

    private int state;

    private String stateInfo;

    SeckillStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }


    public static SeckillStateEnum stateOf(int index) {
        for (SeckillStateEnum stateEnum : values()) {
            if (stateEnum.getState() == index) {
                return stateEnum;
            }
        }
        return null;
    }
}
