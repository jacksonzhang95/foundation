package com.foundation.elasticsearch.retrieve.message;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author by jacksonz
 * @classname CanalKafkaListener
 * @description 使用canel - kafka 获取binlog
 * @date 2020/6/7 16:48
 */
@Component
public class CanalKafkaListener {

    @KafkaListener(topics = "test_user", groupId = "test01")
    public void exampleTopicOnMessage(String message){
        //insertIntoDb(buffer);//这里为插入数据库代码
        System.out.println("消费到信息: " + message);
    }

    @KafkaListener(topics = "test_sender_01", groupId = "test01")
    public void testSender01OnMessage(String message){
        //insertIntoDb(buffer);//这里为插入数据库代码
        System.out.println("消费到信息: " + message);
    }
}
