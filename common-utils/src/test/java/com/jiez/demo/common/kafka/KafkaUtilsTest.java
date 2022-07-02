package com.foundation.demo.common.kafka;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author by jacksonz
 * @classname KafkaUtilsTest
 * @description TODO
 * @date 2020/6/7 14:33
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class KafkaUtilsTest {

    @Test
    public void test() throws InterruptedException, ClassNotFoundException {
//        new KafkaUtils().kafkaClientConsumer();
        Thread.currentThread().getContextClassLoader().loadClass("com.foundation.demo.common.utils.OkHttpUtil1");
    }

}