package com.foundation.id.generator.numbersegment;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : jacksonz
 * @date : 2022/6/2 9:36
 * @description :
 */
public class RedisStrategy {

    private static final String REDIS_PREFIX = "kit-seq:";

    private final RedisConnectionFactory redisConnectionFactory;

    private final ConcurrentMap<String, RedisSequence> map = new ConcurrentHashMap<>();

    public RedisStrategy(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    public long nextId(String name) {
        return this.nextId(name, 1);
    }

    public long nextId(String name, int step) {
        try {
            RedisSequence redisSequence = map.computeIfAbsent(name, key -> new RedisSequence(key, step));
            return redisSequence.nextId();
        } catch (Exception e) {
            throw new RuntimeException("从Redis获取序列号异常", e);
        }
    }

    class RedisSequence {

        private long currentId;

        private volatile long maxId;

        private final int step;

        private final RedisAtomicLong counter;

        public RedisSequence(String name, int step) {
            this.counter = new RedisAtomicLong(REDIS_PREFIX + name, redisConnectionFactory);
            this.currentId = this.counter.getAndAdd(step);
            this.step = step;
            this.maxId = currentId + step;
        }

        private synchronized long nextId() {
            long value = ++currentId;
            if (value >= maxId) {
                currentId = counter.getAndAdd(step);
                maxId = currentId + step;
            }
            return value;
        }
    }
}
