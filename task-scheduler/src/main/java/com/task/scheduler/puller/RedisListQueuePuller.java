package com.task.scheduler.puller;

import com.task.scheduler.common.TaskPriorityTypeEnum;
import com.task.scheduler.common.TaskProcessTypeEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
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
public class RedisListQueuePuller extends AbstractTaskPuller {

    private TaskProcessTypeEnum processTypeEnum;

    private ThreadPoolExecutor threadPool;

    @Override
    protected PullResult doPull() {
        String targetQueue;
        String taskCode;
        for (TaskPriorityTypeEnum priorityType : TaskPriorityTypeEnum.values()) {
            targetQueue = getQueueKey(priorityType);
            long waitTime = getWaitTimeByProcessTypeAndPriorityType(getProcessTypeEnum(), priorityType);
            if (waitTime > 0) {
                taskCode = redisTemplate.opsForList().leftPop(targetQueue, waitTime, TimeUnit.SECONDS);
            } else {
                taskCode = redisTemplate.opsForList().leftPop(targetQueue);
            }
            if (StringUtils.isNotBlank(taskCode)) {
                PullResult pullResult = new PullResult();
                pullResult.setTaskCode(taskCode);
                pullResult.setQueueName(targetQueue);
                return pullResult;
            }
        }
        return null;
    }

    @Override
    protected void pushBackWhenFlowControl(PullResult pullResult) {
        // 回到队头
        redisTemplate.opsForList().leftPush(pullResult.getQueueName(), pullResult.getTaskCode());
    }

    @Override
    protected void pushBackWhenError(PullResult pullResult) {
        // 回到队尾
        redisTemplate.opsForList().rightPush(pullResult.getQueueName(), pullResult.getTaskCode());
    }

    @Override
    protected ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    @Override
    public TaskProcessTypeEnum getProcessType() {
        return processTypeEnum;
    }
}
