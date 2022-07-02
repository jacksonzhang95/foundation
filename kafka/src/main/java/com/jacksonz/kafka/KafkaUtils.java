package com.foundation.kafka;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author by jacksonz
 * @classname KafkaUtils
 * @description 用于kafka
 * @date 2020/6/7 14:25
 */
public class KafkaUtils {

    @Value("${kafka.bootstrap.servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${kafka.group.id:defaultGroup}")
    private String groupId;

    @Value("${kafka.enable.auto.commit:true}")
    private String enableAutoCommit;

    @Value("${kafka.auto.commit.interval.ms:1000}")
    private String autoCommitIntervalMs;

    @Value("${kafka.session.timeout.ms:30000}")
    private String sessionTimeoutMs;

    @Value("${kafka.max.poll.records:1000")
    private String maxPollRecords;

    @Value("${kafka.auto.offset.reset:earliest}")
    private String autoOffsetReset;

    @Value("${kafka.key.deserializer:org.apache.kafka.common.serialization.StringSerializer}")
    private String keyDeserializer;

    @Value("${kafka.value.deserializer:org.apache.kafka.common.serialization.StringSerializer}")
    private String valueDeserializer;

    @Value("${kafka.producer.client.id:test.client}")
    private String producerClientId;

    /**
     * 原生kafka-client-consumer
     *
     * @throws InterruptedException
     */
    public KafkaConsumer<Object, Object> createKafkaClientConsumer() throws InterruptedException {
        Properties props = new Properties();
        // 配置常量 -> ConsumerConfig.xxx
        // kafka的地址
        props.put("bootstrap.servers", bootstrapServers);
        // 消费组id，ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
        props.put("group.id", groupId);
        // 是否自动提交，默认为true
        props.put("enable.auto.commit", enableAutoCommit);
        // 从poll(拉)的回话处理时长
        props.put("auto.commit.interval.ms", autoCommitIntervalMs);
        // 超时时间
        props.put("session.timeout.ms", sessionTimeoutMs);
        // 一次最大拉取的条数
        props.put("max.poll.records", maxPollRecords);
        // 找不到offset时处理策略
        props.put("auto.offset.reset", autoOffsetReset);
        // 键反序列化
        props.put("key.deserializer", keyDeserializer);
        // 值反序列化
        props.put("value.deserializer", valueDeserializer);

        KafkaConsumer<Object, Object> consumer = new KafkaConsumer<>(props);
        return consumer;
    }

    /**
     *  消费消息 - demo
     *
     * @param kafkaConsumer
     * @param topics
     * @throws Exception
     */
    public void kafkaConsumeMessage(KafkaConsumer<Object, Object> kafkaConsumer, List<String> topics) throws Exception {
        if (kafkaConsumer == null) {
            throw new Exception("kafkaConsumer is null");
        }
        // 订阅主题, 并创建一个重平衡监听器
        kafkaConsumer.subscribe(topics, new ConsumerRebalanceListener() {
            /**
             * 这个方法会在再均衡开始之前和消费者停止读取消息之后被调用
             *
             * @param partitions
             */
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {

            }

            /**
             * 这个方法会在重新分配分区之后和消费者开始读取消费之前被调用
             *
             * @param partitions
             */
            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

            }
        });

        // 订阅某主题的特定分区: x主题-12分区
        TopicPartition topicPartition = new TopicPartition("主题名", 12);
        kafkaConsumer.assign(Collections.singleton(topicPartition));

        // 查询某主题元数据
        List<PartitionInfo> partitionInfos = kafkaConsumer.partitionsFor("主题名");
        partitionInfos.forEach(partitionInfo -> {
            /**
             * topic 表示主题名称
             * partition 代表分区编号
             * leader 代表分区的 leader 副本所在的位置
             * replicas 代表分区的 AR 集合
             * inSyncReplicas 代表分区 ISR 集合
             * offlineReplicas 代表分区的 OSR 集合
             */
        });

        // 取消订阅
        kafkaConsumer.unsubscribe();

        int pollBatch = 1;
        while (pollBatch < 200) {
            System.out.println("开始消费");
            // 消费消息
            ConsumerRecords<Object, Object> records = kafkaConsumer.poll(10);
            System.out.println("获取消息");
            System.out.println(JSONObject.toJSONString(records));

            if (records != null && records.count() > 0) {
                List<ConsumerRecord<Object, Object>> topicRecords = (List<ConsumerRecord<Object, Object>>) records.records("指定topic");
                records.records("指定topic某分区");

                for (ConsumerRecord<Object, Object> record : records) {
                    System.out.println("第" + pollBatch + "次, " + "=======receive: key = " + record.key() + ", value = " + record.value() + " offset===" + record.offset());
                }

                // 获取该批次最大offset
                long offset = topicRecords.get(topicRecords.size() - 1).offset();
                // 手动显式提交位移(同步)
                kafkaConsumer.commitSync();

            } else {
                Thread.sleep(10000);
            }
            pollBatch++;
        }
    }

    /**
     * 创建kafka-producer - demo
     *
     * @return
     */
    public KafkaProducer<Object,Object> createKafkaClientSender() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // key的序列化器
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keyDeserializer);
        // value的序列化器
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueDeserializer);
        // 指定分区器
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DefaultPartitioner.class.getName());
        // 指定拦截器
        /*
            props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, "");
         */
        props.put(ProducerConfig.CLIENT_ID_CONFIG, producerClientId);
        return new KafkaProducer<>(props);
    }

    /**
     *  发送消息 - demo
     * @param record
     * @param producer
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public boolean sendMessage(ProducerRecord<Object,Object> record, KafkaProducer<Object,Object> producer) throws ExecutionException, InterruptedException {
        // public ProducerRecord(String topic, Integer partition, Long timestamp, K key, V value, Iterable<Header> headers){...}

        //异步
        Future<RecordMetadata> sendFuture = producer.send(record, (recordMetadata, e) -> {
            // callback 处理
            System.out.println("12321312");
        });

        while (!sendFuture.isDone()) {
        }
        RecordMetadata recordMetadata = sendFuture.get();

        // 同步发送
        RecordMetadata recordMetadata1 = producer.send(record, (recordMetadata2, e) -> {
            // callback 处理
            System.out.println("12321312");
        }).get();


        return true;
    }
}


/**
 * 自定义序列化器
 */
class CustomizeSerializer implements Serializer<String> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, String data) {
        return new byte[0];
    }

    @Override
    public void close() {

    }
}

/**
 * 自定义反序列化器
 */
class CustomizeDeserializer implements Deserializer<String> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public String deserialize(String topic, byte[] data) {
        return null;
    }

    @Override
    public void close() {

    }
}

/**
 * 自定义分区器 ——》 判断消息应该分陪到哪个分区
 */
class CustomizePartitioner implements Partitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        return 0;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}

/**
 * 拦截器
 */
class CustomizeProducerInterceptor implements ProducerInterceptor {

    /**
     * 将消息序列化和计算分区之前会调用这个方法，对消息进行相应的定制化操作
     * @param record
     * @return
     */
    @Override
    public ProducerRecord onSend(ProducerRecord record) {
        return null;
    }

    /**
     * 消息被应答之前或消息发送失败时会触发这个方法，优先于CallBack
     * @param metadata
     * @param exception
     */
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}

/**
 * 拦截器
 */
class CustomizeConsumerInterceptor implements ConsumerInterceptor {

    @Override
    public ConsumerRecords onConsume(ConsumerRecords records) {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public void onCommit(Map offsets) {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
