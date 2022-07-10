package com.task.scheduler.puller;

import com.task.scheduler.common.TaskProcessTypeEnum;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : jacksonz
 * @date : 2022/7/9 20:16
 * @description :
 */
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class RedisSortSetQueuePuller extends AbstractTaskPuller {

    private final String SORT_SET_B_POP_MAX_SCORE_LUA = "redis.call('BZPOPMAX', KEYS[1], ARGV[1])";

    private TaskProcessTypeEnum processTypeEnum;
    private ThreadPoolExecutor threadPool;

    @SuppressWarnings(value = "unchecked")
    @Override
    protected PullResult doPull() {
        String targetQueue;
        String taskCode;
        List result = (List) redisTemplate.execute(RedisScript.of(SORT_SET_B_POP_MAX_SCORE_LUA, List.class), Collections.singletonList(getQueueKey(null)), getWaitTimeByProcessTypeAndPriorityType(getProcessType(), null));

        PullResult pullResult = new PullResult();
        return pullResult;
    }

    @Override
    protected void pushBackWhenFlowControl(PullResult pullResult) {
        redisTemplate.opsForZSet().add(pullResult.getQueueName(), pullResult.getTaskCode(), Double.valueOf(String.valueOf(pullResult.getPriorityScore())));
    }

    @Override
    protected void pushBackWhenError(PullResult pullResult) {
        redisTemplate.opsForZSet().add(pullResult.getQueueName(), pullResult.getTaskCode(), Double.valueOf(String.valueOf(pullResult.getPriorityScore())));
    }

    @Override
    protected ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ThreadPoolExecutor threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public TaskProcessTypeEnum getProcessType() {
        return processTypeEnum;
    }

    public void setProcessTypeEnum(TaskProcessTypeEnum processTypeEnum) {
        this.processTypeEnum = processTypeEnum;
    }
}
