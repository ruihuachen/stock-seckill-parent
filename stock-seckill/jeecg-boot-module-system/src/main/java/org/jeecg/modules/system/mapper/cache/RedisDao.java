package org.jeecg.modules.system.mapper.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.entity.StockGoods;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Slf4j
public class RedisDao {

    private final JedisPool jedisPool;

    public RedisDao(String ip, int port) {
        this.jedisPool = new JedisPool(ip, port);
    }

    private RuntimeSchema<StockGoods> schema = RuntimeSchema.createFrom(StockGoods.class);

    /**
     * 反序列化从Redis中获取对象
     * @param stockGoodId
     * @return
     */
    public StockGoods getSockGoodFromRedis(String stockGoodId) {
        //redis操作
        try {
            //数据库连接池
            Jedis jedis = jedisPool.getResource();

            try {
                String key = "stockGoodId:"+stockGoodId;
                //并没有实现内部序列化操作
                //get——> btye[]——> 反序列化——> object(Seckill)
                //采用自定义的序列化
                //protostuff:pojo
                byte[] bytes = jedis.get(key.getBytes());
                //缓存中获取
                if (bytes != null) {
                    //空对象
                    StockGoods stockGoods = schema.newMessage();
                    ProtobufIOUtil.mergeFrom(bytes, stockGoods, schema);

                    //seckill 反序列化
                    return stockGoods;
                }
            }finally {
                jedis.close();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }


    /**
     * 将从DB获取的对象 push在Redis缓存中
     * @param stockGoods
     * @return
     */
    public String putSockGoodToRedis(StockGoods stockGoods) {
        //set object(Seckill)——> 序列化——> btye[]
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "stockGoodId:"+stockGoods.getId();
                byte[] bytes = ProtobufIOUtil.toByteArray(stockGoods, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

                //设置超时缓存：超过一个小时则过期redis中缓存的数据，避免DB与redis数据不一致的问题
                int timeout = 60 * 60;

                //if success return ok
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;

            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
