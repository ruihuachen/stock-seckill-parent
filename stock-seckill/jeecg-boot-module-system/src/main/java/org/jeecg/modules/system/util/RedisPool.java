package org.jeecg.modules.system.util;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Component
//配置redis连接池
public class RedisPool {

    private static JedisPool pool;

    private static Integer maxTotal = 200;

    private static Integer maxIdle = 100;

    private static Integer maxWait = 10000;

    private static Boolean testOnBorrow = true;

    private static String redisIP = "127.0.0.1";

    private static Integer redisPort = 6379;

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        //最大连接数
        config.setMaxTotal(maxTotal);

        //最大空闲连接数
        config.setMaxIdle(maxIdle);

        //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        config.setMaxWaitMillis(maxWait);

        //在获取连接的时候检查有效性, 默认false
        config.setTestOnBorrow(testOnBorrow);

        //连接耗尽时是否堵塞，false报异常，true则堵塞直到超时，默认true
        config.setBlockWhenExhausted(true);

        pool = new JedisPool(config, redisIP, redisPort, 1000 * 2);
    }

    //类加载到 jvm 时直接初始化连接池
    static {
        initPool();
    }

    //从连接池取到连接
    public static Jedis getJedisFromPool() {
        return pool.getResource();
    }

    //关闭连接 -> 放回连接
    public static void closeRedisToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
