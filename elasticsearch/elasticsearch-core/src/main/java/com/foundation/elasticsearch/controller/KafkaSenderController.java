package com.foundation.elasticsearch.controller;

import com.foundation.elasticsearch.domain.TestA;
import com.foundation.elasticsearch.kafka.sender.KafkaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author by jacksonz
 * @classname KafkaSenderController
 * @description TODO
 * @date 2020/6/7 17:24
 */
@RestController
@RequestMapping(value = "/test-kafka-send")
public class KafkaSenderController {

    @Autowired
    private KafkaSender kafkaSender;

    @RequestMapping(value = "/user/save", method = RequestMethod.GET)
    public boolean saveUser(String topic, String message) {
        return kafkaSender.send(topic, message);
    }

    @RequestMapping(value = "/test1", method = RequestMethod.POST)
    public String test1(String name, String age) {
        return name + age;
    }

    @RequestMapping(value = "/test2", method = RequestMethod.POST)
    public String test1(@RequestBody TestA a) {
        return a.getName();
    }
}
