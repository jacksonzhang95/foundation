package com.foundation.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.filter.FilterContext;
import org.apache.rocketmq.common.filter.MessageFilter;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.IOException;
import java.util.List;

/**
 * 主要步骤
 * 1. 创建Consumer，指定消费组名
 * 2. 指定NameServer地址
 * 3. 订阅主题topic和tag
 * 4，设置回调函数，处理消息
 * 5. 启动消费者consumer
 *
 * @author : jacksonz
 * @date : 2022/1/8 11:33
 */
public class Consumer {

    /**
     * 同步消费者(推) + 并发消费
     */
    private class SyncConcurrentConsumer {
        public void consume() throws MQClientException {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
            consumer.setNamesrvAddr("nameServer 地址");
            // 设置订阅信息
            consumer.subscribe("主题名", "Tag信息");

            // 设置消费模式(广播/集群)
//            consumer.setMessageModel(MessageModel.BROADCASTING);
//            consumer.setMessageModel(MessageModel.CLUSTERING);

            // 监听消息监听
            consumer.registerMessageListener(/*并发消费*/new MessageListenerConcurrently() {
                // 接收消息内容
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                    // 消息处理
                    // 返回消费结果
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

        }
    }

    /**
     * 同步消费者(推) + 顺序消费
     */
    private class SyncOrderConsumer {
        public void consume() throws MQClientException {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
            consumer.setNamesrvAddr("nameServer 地址");
            // 设置订阅信息
            consumer.subscribe("主题名", "Tag信息");

            // 设置消费模式(广播/集群)
//            consumer.setMessageModel(MessageModel.BROADCASTING);
//            consumer.setMessageModel(MessageModel.CLUSTERING);

            // 监听消息监听
            consumer.registerMessageListener(/*并发消费*/new MessageListenerOrderly() {
                @Override
                public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
                    // 消息处理
                    // 返回消费结果
                    return ConsumeOrderlyStatus.SUCCESS;
                }

            });

        }
    }

    /**
     * 消费指定消息消费者
     * 用于指定消费topic下的某些消息
     */
    private class FilterMsgConsumer {
        public void consume() throws MQClientException {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
            consumer.setNamesrvAddr("nameServer 地址");
            /*
                设置订阅信息

                1. 消费目标tag的消息，expression = "tagInfos"
                2. 消费多个tag的消息，expression = "tag1 || tag2"
                3. 消费所有消息，expression = "*"
                4. 指定消息UserProperty过滤消费, expression =MessageSelector.bySql("propertyA = xxx || propertyB < 10")
                    数值比较： >,>=,<,<=,=,BETWEEN
                    字符串: =,<>,IN
                    判空: IS NULL, IS NOT NULL
                    逻辑符号: AND,OR,NOT
             */
            consumer.subscribe("主题名", MessageSelector.bySql("propertyA = xxx || propertyB < 10"));
            // 监听消息监听
            consumer.registerMessageListener(new MessageListenerOrderly() {
                @Override
                public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
                    // 消息处理
                    // 返回消费结果
                    return ConsumeOrderlyStatus.SUCCESS;
                }
            });
        }

        public void consumer2() throws MQClientException, IOException {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ConsumerGroupName");
            // 使用 Java代码，在服务器做消息过滤
            String filterCode = MixAll.file2String("自定义过滤服务class文件地址[MyMessageFilterImpl文件路径]");
            consumer.subscribe("TopicFilter7", "com.alibaba.rocketmq.example.filter.MessageFilterimpl", filterCode);
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                    System.out.println(Thread.currentThread().getName() + "Receive New Messages" + msgs);
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();
        }

        class MyMessageFilterImpl implements MessageFilter {
            @Override
            public boolean match(MessageExt messageExt, FilterContext filterContext) {
                return false;
            }
        }

        public void consumer3() throws MQClientException, IOException, RemotingException, InterruptedException, MQBrokerException {
            DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("ConsumerGroupName");
            // 根据msgId查询msg
            MessageExt messageExt = consumer.viewMessage("Topic", "keys");
            consumer.start();
        }
    }

}