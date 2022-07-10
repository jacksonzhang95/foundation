package com.task.scheduler.puller;

import com.task.scheduler.common.TaskPriorityTypeEnum;
import com.task.scheduler.common.TaskProcessTypeEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RPriorityBlockingDeque;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : jacksonz
 * @date : 2022/7/9 20:16
 * @description :
 */
@Data
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class RedissonPriorityBlockingQueuePuller extends AbstractTaskPuller {

    private TaskProcessTypeEnum processTypeEnum;

    private ThreadPoolExecutor threadPool;

    @Autowired
    private RedissonClient redissonClient;

    private volatile RPriorityBlockingDeque<String> blockingDeque;

    @Override
    protected PullResult doPull() {
        RPriorityBlockingDeque<String> blockingDeque = getRPriorityBlockingDequeInstance();

        String taskCode = blockingDeque.pollFirst();

        PullResult pullResult = new PullResult();
        pullResult.setTaskCode(taskCode);
        pullResult.setQueueName(getQueueKey(null));
        return pullResult;
    }

    @Override
    protected void pushBackWhenFlowControl(PullResult pullResult) {
        RPriorityBlockingDeque<String> blockingDeque = getRPriorityBlockingDequeInstance();
        blockingDeque.addFirst(pullResult.getTaskCode());
    }

    @Override
    protected void pushBackWhenError(PullResult pullResult) {
        RPriorityBlockingDeque<String> blockingDeque = getRPriorityBlockingDequeInstance();
        blockingDeque.addLast(pullResult.getTaskCode());
    }

    @Override
    protected ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    @Override
    public TaskProcessTypeEnum getProcessType() {
        return processTypeEnum;
    }

    private synchronized RPriorityBlockingDeque<String> getRPriorityBlockingDequeInstance() {
        if (Objects.nonNull(blockingDeque)) {
            return blockingDeque;
        }
        blockingDeque = redissonClient.getPriorityBlockingDeque(getQueueKey(null));
        return blockingDeque;
    }
}
