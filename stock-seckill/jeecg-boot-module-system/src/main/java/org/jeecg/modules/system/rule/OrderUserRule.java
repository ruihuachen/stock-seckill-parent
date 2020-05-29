package org.jeecg.modules.system.rule;


import org.apache.commons.lang.math.RandomUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrderUserRule {

    public static String getOrderUserRule() {
        String prefix = "OUR";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        int random = RandomUtils.nextInt(90) + 10;
        String value = prefix + format.format(new Date()) + random;
        return value;
    }

}
