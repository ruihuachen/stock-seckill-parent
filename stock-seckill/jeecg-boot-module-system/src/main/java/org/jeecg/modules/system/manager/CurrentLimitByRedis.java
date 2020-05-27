package org.jeecg.modules.system.manager;


import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.util.RedisPool;
import org.jeecg.modules.system.util.ScriptUtil;
import redis.clients.jedis.Jedis;

import java.util.Collections;

/**
 * create by Ernest on 2020/3/17.
 */
@Slf4j
public class CurrentLimitByRedis {

    private static final int FAIL_CODE = 0;

    private static Integer limit = 5;

    /**
     * Redis 限流
     * @return
     */
    public static Boolean limit() {
        Jedis jedis = null;
        Object res = null;
        try {
            //获取jedis实例
            jedis = RedisPool.getJedisFromPool();

            //解析lua文件
            String script = ScriptUtil.getScript("limit.lua");

            //请求限流
            String key = String.valueOf(System.currentTimeMillis() / 1000);

            //计数限流
            res = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(String.valueOf(limit)));
            if (FAIL_CODE != (Long)res) {
                log.info("成功获取令牌");
                return true;
            }
        }catch (Exception e) {
            log.error("limit 获取 Jedis 实例失败", e);
        }finally {
            RedisPool.closeRedisToPool(jedis);
        }
        return false;
    }
}
