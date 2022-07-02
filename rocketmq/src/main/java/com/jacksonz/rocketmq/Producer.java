package com.foundation.rocketmq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 基本步骤
 * 1. 创建producer，指定生产者组名
 * 2. 指定NameServer地址
 * 3. 启动producer
 * 4. 创建消息对象，指定topic,tag,消息体
 * 5. 发送消息
 *
 * @author : jacksonz
 * @date : 2022/1/8 11:28
 */
public class Producer {

    /**
     * 同步发送
     */
    private class SyncProducer {
        public void send() throws MQClientException {
            DefaultMQProducer producer = new DefaultMQProducer("生产组组名");
            producer.setNamesrvAddr("nameServer地址");
            producer.start();

            // 创建消息体
            Message msg = new Message();
            msg.setTopic("");
            msg.setTags("");
            msg.setBody("".getBytes());

            // 发送消息
            SendResult result = null;
            try {
                result = producer.send(msg);
            } catch (RemotingException e) {
                e.printStackTrace();
            } catch (MQBrokerException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (Objects.equals(SendStatus.SEND_OK, result.getSendStatus())) {
                System.out.println("success");
            }

            producer.shutdown();
        }
    }

    /**
     * 异步发送
     */
    private class AsyncProducer {
        public void send() throws MQClientException {
            DefaultMQProducer producer = new DefaultMQProducer("生产组组名");
            producer.setNamesrvAddr("nameServer地址");
            producer.start();
            // 异步发送失败重试
            producer.setRetryTimesWhenSendAsyncFailed(0);

            // 创建消息体
            Message msg = new Message();
            msg.setTopic("");
            msg.setTags("");
            msg.setBody("".getBytes());

            // 异步发送消息
            try {
                producer.send(msg, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        // 异步成功回调
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        // 异步异常回调
                    }
                });
            } catch (RemotingException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            producer.shutdown();

        }
    }

    /**
     * 单向发送
     */
    private class OneWayProducer {
        public void send() throws MQClientException {
            DefaultMQProducer producer = new DefaultMQProducer("生产组组名");
            producer.setNamesrvAddr("nameServer地址");
            producer.start();
            // 异步发送失败重试
            producer.setRetryTimesWhenSendAsyncFailed(0);

            // 创建消息体
            Message msg = new Message();
            msg.setTopic("");
            msg.setTags("");
            msg.setBody("".getBytes());

            // 异步发送消息
            try {
                producer.sendOneway(msg);
            } catch (RemotingException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            producer.shutdown();

        }
    }

    /**
     * 指定分区发送消息
     */
    private class SequenceProducer {

        public void send() throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
            DefaultMQProducer producer = new DefaultMQProducer("生产组组名");
            producer.setNamesrvAddr("nameServer地址");
            producer.start();

            // 创建消息体
            Message msg = new Message();
            msg.setTopic("TopicName");
            msg.setTags("TagInfos");
            msg.setBody("Body".getBytes());

            // 根据TopicName获取对应的分区信息
            List<MessageQueue> messageQueues = producer.fetchPublishMessageQueues("TopicName");

            /**
             * 决定分区
             * @see MessageQueueSelector
             */
            SelectMessageQueueByHash selectMessageQueueByHash = new SelectMessageQueueByHash();
            /**
             * 参数解析
             * @param 1. 队列集合
             * @param 2. 消息对象
             * @param 1. 业务标识参数
             */
            MessageQueue messageQueue = selectMessageQueueByHash.select(messageQueues, msg, null);

            // 发送消息
            SendResult result = producer.send(msg, messageQueue);
            if (Objects.equals(SendStatus.SEND_OK, result.getSendStatus())) {
                System.out.println("success");
            }

            // 关闭发送者
            producer.shutdown();
        }
    }

    /**
     * 发送延迟消息
     */
    private class DelayProducer {
        public void send() throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
            DefaultMQProducer producer = new DefaultMQProducer("生产组组名");
            producer.setNamesrvAddr("nameServer地址");
            producer.start();

            // 创建消息体
            Message msg = new Message();
            msg.setTopic("TopicName");
            msg.setTags("TagInfos");
            msg.setBody("Body".getBytes());
            // 延迟时间: 1s 5s 10s 30s 1m 2m 3m 4m 5m  6m 7m 8m 9m 10m 20m 30m 1h 2h
            // 延迟等级: 1  2   3   4   5  6  7  8  9  10 11 12 13 14  15  16  17 18
            msg.setDelayTimeLevel(1);

            // 发送消息
            SendResult result = producer.send(msg);
            if (Objects.equals(SendStatus.SEND_OK, result.getSendStatus())) {
                System.out.println("success");
            }

            // 关闭发送者
            producer.shutdown();
        }

    }

    /**
     * 批量发送消息
     */
    private class BatchProducer {
        public void send() throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
            DefaultMQProducer producer = new DefaultMQProducer("生产组组名");
            producer.setNamesrvAddr("nameServer地址");
            producer.start();

            // 创建消息体
            Message msg = new Message();
            msg.setTopic("TopicName");
            msg.setTags("TagInfos");
            msg.setBody("Body".getBytes());

            List<Message> messages = Collections.singletonList(msg);

            // 发送消息
            SendResult result = producer.send(messages);
            if (Objects.equals(SendStatus.SEND_OK, result.getSendStatus())) {
                System.out.println("success");
            }

            // 关闭发送者
            producer.shutdown();
        }

    }

    /**
     * 发送特殊标识消息，使consumer可以过滤
     */
    private class TagProducer {
        public void send() throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
            DefaultMQProducer producer = new DefaultMQProducer("生产组组名");
            producer.setNamesrvAddr("nameServer地址");
            producer.start();

            // 创建消息体
            Message msg = new Message();
            msg.setTopic("TopicName");
            msg.setTags("TagInfos");
            msg.setBody("Body".getBytes());
            // 设置特定标识
            msg.putUserProperty("a", "123");

            List<Message> messages = Collections.singletonList(msg);

            // 发送消息
            SendResult result = producer.send(messages);
            if (Objects.equals(SendStatus.SEND_OK, result.getSendStatus())) {
                System.out.println("success");
            }

            // 关闭发送者
            producer.shutdown();
        }

    }

    /**
     * 发送事务消息
     */
    private class TransactionProducer {
        public void send() throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
            TransactionMQProducer transactionProducer = new TransactionMQProducer("生产者组名");
            // 设置NameServer地址
            transactionProducer.setNamesrvAddr("NameServer地址");
            // 设置回调监听器
            transactionProducer.setTransactionListener(new TransactionListener() {
                /**
                 * 这里执行本地事务
                 *
                 * @param message
                 * @param o
                 * @return
                 */
                @Override
                public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                    return null;
                }

                /**
                 * MQ检查本地事务状态
                 * @param messageExt
                 * @return
                 */
                @Override
                public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                    return null;
                }
            });

            transactionProducer.start();

            Message message = new Message();
            message.setTopic("");
            message.setTags("");
            message.setBody("".getBytes());

            SendResult send = transactionProducer.sendMessageInTransaction(message, null);
        }

    }
}
