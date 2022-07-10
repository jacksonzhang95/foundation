package com.task.scheduler.puller;

import com.foundation.common.utils.AssertUtils;
import com.task.scheduler.common.TaskProcessTypeEnum;
import com.task.scheduler.manager.PushToRedisListQueueTaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : jacksonz
 * @date : 2022/7/9 20:23
 * @description :
 */
@Component
public class TaskPullerFactory {

    @Autowired
    private ApplicationContext applicationContext;

    public TaskPuller createRedisListQueuePuller(TaskProcessTypeEnum processType, ThreadPoolExecutor threadPool) {
        AssertUtils.isTrue(Objects.nonNull(processType));
        AssertUtils.isTrue(Objects.nonNull(threadPool));

        RedisListQueuePuller puller = applicationContext.getBean(RedisListQueuePuller.class);
        puller.setProcessTypeEnum(processType);
        puller.setThreadPool(threadPool);
        return puller;
    }

    public TaskPuller createRedisSortSetQueuePuller(TaskProcessTypeEnum processType, ThreadPoolExecutor threadPool) {
        AssertUtils.isTrue(Objects.nonNull(processType));
        AssertUtils.isTrue(Objects.nonNull(threadPool));

        RedisSortSetQueuePuller puller = applicationContext.getBean(RedisSortSetQueuePuller.class);
        puller.setProcessTypeEnum(processType);
        puller.setThreadPool(threadPool);
        return puller;
    }

}
