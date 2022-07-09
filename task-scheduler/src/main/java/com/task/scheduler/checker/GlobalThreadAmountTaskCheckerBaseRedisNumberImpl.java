package com.task.scheduler.checker;

import com.foundation.common.utils.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Objects;

/**
 * 基于【Redis-数量】实现的【全局线程数】TaskChecker
 *
 * @author : jacksonz
 * @date : 2022/6/21 15:19
 * @description :
 */
@Slf4j
@Component(value = "redisNumberTaskChecker")
public class GlobalThreadAmountTaskCheckerBaseRedisNumberImpl implements TaskChecker<Integer> {

    private static final String APPLY_ADD_AMOUNT_LUA_SCRIPT = ""
        + "local maxLimitAmount = ARGV[1];"
        + "local acquireAmount = ARGV[2];"
        + "local redisAmount = redis.call('GET', KEYS[1]);"
        + "local currentAmount = 0;"
        + "if (redisAmount == false) then currentAmount = 0 else currentAmount = redisAmount end;"
        + "if (tonumber(currentAmount) + tonumber(acquireAmount) > tonumber(maxLimitAmount)) then return false; else redis.call('INCR', KEYS[1]); return true; end;";

    private static final String REDUCE_AMOUNT_LUA_SCRIPT = ""
        + "local releaseAmount = ARGV[1];"
        + "local redisAmount = redis.call('GET', KEYS[1]);"
        + "local currentAmount = 0;"
        + "if (redisAmount == false) then currentAmount = 0 else currentAmount = redisAmount end;"
        + "local afterReleaseAmount = tonumber(currentAmount) - tonumber(releaseAmount);"
        + "if (afterReleaseAmount >= tonumber(0)) then redis.call('SET', KEYS[1], afterReleaseAmount); return true; else return false; end;";

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public boolean tryPassTask(String key, int limitAmount, Integer acquireAmount) {
        AssertUtils.isTrue(Objects.nonNull(acquireAmount));
        if (limitAmount == -1) {
            return true;
        }
        String currentTaskAmountStr = (String) redisTemplate.opsForValue().get(key);
        long currentTaskAmount = Objects.isNull(currentTaskAmountStr) ? 0L : Long.valueOf(currentTaskAmountStr);
        return currentTaskAmount + acquireAmount <= limitAmount;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public boolean applyPassTask(String key, int limitAmount, Integer acquireAmount) {
        AssertUtils.isTrue(Objects.nonNull(acquireAmount));
        Object executeResult = redisTemplate.execute(RedisScript.of(APPLY_ADD_AMOUNT_LUA_SCRIPT, Boolean.class), Collections.singletonList(key), String.valueOf(limitAmount), String.valueOf(acquireAmount));
        return Boolean.TRUE.equals(executeResult);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public boolean reducePassedTaskAmount(String key, Integer reduceAmount) {
        AssertUtils.isTrue(Objects.nonNull(reduceAmount));
        Object executeResult = redisTemplate.execute(RedisScript.of(REDUCE_AMOUNT_LUA_SCRIPT, Boolean.class), Collections.singletonList(key), String.valueOf(reduceAmount));
        return Boolean.TRUE.equals(executeResult);
    }
}
