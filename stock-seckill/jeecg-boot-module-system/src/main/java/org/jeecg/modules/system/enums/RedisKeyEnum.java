package org.jeecg.modules.system.enums;

/**
 * Redis的键与值
 */
public enum RedisKeyEnum {
    STOCK_INVENTORY("stockGood_inventory_"),
    STOCK_SALE("stockGood_sale_"),
    STOCK_VERSION("stockGood_version_");

    private String keyInfo;

    RedisKeyEnum(String keyInfo) {
        this.keyInfo = keyInfo;
    }

    public String getKeyInfo() {
        return keyInfo;
    }

}
