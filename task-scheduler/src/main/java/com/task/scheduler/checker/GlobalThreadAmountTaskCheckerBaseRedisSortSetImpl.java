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
 * 基于【Redis-sortSet】实现的【全局线程数】TaskChecker
 *
 * @author : jacksonz
 * @date : 2022/7/6 17:46
 * @description :
 */
@Slf4j
@Component(value = "redisSortSetTaskChecker")
public class GlobalThreadAmountTaskCheckerBaseRedisSortSetImpl implements TaskChecker<String> {

    private static final String APPLY_LUA_SCRIPT = ""
        + "local key = KEYS[1];"
        + "local maxLimit = ARGV[1];"
        + "local taskCode = ARGV[2];"
        + "if redis.call('SISMEMBER', key, taskCode) == 1 "
        + "then return true; "
        + "else if tonumber(redis.call('SCARD', key)) + 1 > tonumber(maxLimit) "
        + "   then return false; "
        + "   else if redis.call('SADD', key, taskCode) == 1 "
        + "      then return true; "
        + "      else return false "
        + "      end; "
        + "   end; "
        + "end; ";

    private static final String RELEASE_LUA_SCRIPT = ""
        + "local key = KEYS[1];"
        + "local taskCode = ARGV[1];"
        + "if redis.call('SISMEMBER', key, taskCode) == 0 "
        + "then return true; "
        + "else if redis.call('SREM', key, taskCode) == 1 "
        + "  then return true; "
        + "  else return false "
        + "  end; "
        + "end; "
        ;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings(value = "unchecked")
    public boolean tryPassTask(String key, int limitAmount, String acquireTaskCode) {
        AssertUtils.isTrue(Objects.nonNull(acquireTaskCode));
        if (limitAmount == -1) {
            return true;
        }
        Long currentSize = redisTemplate.opsForSet().size(key);
        currentSize = Objects.isNull(currentSize) ? 0L : currentSize;
        return currentSize + 1 <= limitAmount;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public boolean applyPassTask(String key, int limitAmount, String acquireTaskCode) {
        Object executeResult = redisTemplate.execute(RedisScript.of(APPLY_LUA_SCRIPT, Boolean.class), Collections.singletonList(key), String.valueOf(limitAmount), acquireTaskCode);
        return Boolean.TRUE.equals(executeResult);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public boolean reducePassedTaskAmount(String key, String reduceTaskCode) {
        Object executeResult = redisTemplate.execute(RedisScript.of(RELEASE_LUA_SCRIPT, Boolean.class), Collections.singletonList(key), reduceTaskCode);
        return Boolean.TRUE.equals(executeResult);
    }
}
