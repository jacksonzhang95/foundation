package com.task.scheduler.puller;


import com.foundation.common.utils.AssertUtils;
import com.task.scheduler.common.TaskPriorityTypeEnum;
import com.task.scheduler.entity.Task;
import com.task.scheduler.exception.ConcurrencyReachesLimitException;
import com.task.scheduler.executor.TaskExecutor;
import com.task.scheduler.executor.TaskExecutorFactory;
import com.task.scheduler.manager.TaskManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * puller
 * 不断的拉取自己类型的任务 执行
 */
@Slf4j
public abstract class AbstractTaskPuller implements TaskPuller {

    @Resource
    private TaskManager taskManager;

    @Resource
    RedisTemplate<String, String> redisTemplate;

    @Resource
    private TaskExecutorFactory taskExecutorFactory;

    @SneakyThrows
    @Override
    public void pull() {
        AssertUtils.isTrue(Objects.nonNull(getProcessType()));

        while (true) {
            // 先拉后申请
            String targetQueue = null;
            String taskCode = null;
            for (TaskPriorityTypeEnum priorityType : TaskPriorityTypeEnum.values()) {
                targetQueue = getQueueKey(priorityType);
                if (Objects.equals(priorityType, TaskPriorityTypeEnum.HIGH)) {
                    taskCode = redisTemplate.opsForList().leftPop(targetQueue, getWaitHighPriorityTime(), TimeUnit.SECONDS);
                } else {
                    taskCode = redisTemplate.opsForList().leftPop(targetQueue, getWaitHighPriorityTime(), TimeUnit.SECONDS);
                }
                if (StringUtils.isNotBlank(taskCode)) {
                    break;
                }
            }

            if (Objects.isNull(taskCode)) {
                log.warn("{} , 没有拉取到任务", this.getClass().getSimpleName());
                continue;
            }

            log.info("任务拉取，拉取任务成功，目标队列: {}, taskCode: {}", targetQueue, taskCode);

            Task task = null;
            try {
                // 申请任务执行
                task = taskManager.applyExecuteTask(taskCode);
                if (Objects.isNull(task)) {
                    return;
                }

                // 标记任务执行中
                taskManager.startTask(task);

                // 推送线程池
                TaskExecutor taskExecutor = taskExecutorFactory.createTaskExecutor(task);
                getThreadPool().execute(taskExecutor);

            } catch (ConcurrencyReachesLimitException concurrencyReachesLimitException) {
                log.info("任务拉取，并发数达到上限，taskCode:{} 回到队头，", taskCode);
                // 回到队头
                redisTemplate.opsForList().leftPush(targetQueue, taskCode);
                // 放弃执行任务
                taskManager.abandonExecuteTask(task);
                // 执行睡眠，避免快速连续拉取
                Thread.sleep(1000L);

            } catch (Exception e) {
                log.info("任务拉取，提交任务异常，taskCode:{} 回到队尾，异常信息: {}", taskCode, e);
                // 回到队尾
                redisTemplate.opsForList().rightPush(targetQueue, taskCode);
                // 放弃执行任务
                taskManager.abandonExecuteTask(task);
            }

        }
    }

    protected abstract ThreadPoolExecutor getThreadPool();

    protected abstract Long getWaitHighPriorityTime();

    protected String getQueueKey(TaskPriorityTypeEnum priorityType) {
        return taskManager.getPullingKey(getProcessType(), priorityType);
    }
}
