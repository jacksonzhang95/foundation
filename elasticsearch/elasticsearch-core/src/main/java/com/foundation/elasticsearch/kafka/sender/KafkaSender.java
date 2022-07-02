package com.foundation.elasticsearch.kafka.sender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author by jacksonz
 * @classname KafkaSender
 * @description TODO
 * @date 2020/6/7 17:17
 */
@Service
public class KafkaSender {

    @Autowired
    private KafkaTemplate<Object,Object> kafkaTemplate;

    public boolean send(String topic, String message){
        kafkaTemplate.send(topic, message);
        return true;
    }
}
