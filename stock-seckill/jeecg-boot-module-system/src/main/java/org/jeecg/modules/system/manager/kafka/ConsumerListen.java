package org.jeecg.modules.system.manager.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jeecg.modules.system.entity.StockGoods;
import org.jeecg.modules.system.service.ISeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class ConsumerListen {

    private Gson gson = new GsonBuilder().create();

    @Autowired
    private ISeckillService seckillService;

    @KafkaListener(topics = "SECKILL-GOOD-TOPIC")
    public void listen(ConsumerRecord<String, String> record) throws Exception {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        //Object -> String
        String message = (String) kafkaMessage.get();
        //反序列化
        StockGoods stock = gson.fromJson(message, StockGoods.class);
        seckillService.consumerNewsToCreateOrderWithLimitByRedisAndKafka(stock);
    }
}
