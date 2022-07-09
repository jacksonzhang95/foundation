package com.task.scheduler.manager;


import com.foundation.common.utils.AssertUtils;
import com.task.scheduler.common.TaskPriorityTypeEnum;
import com.task.scheduler.common.TaskProcessTypeEnum;
import com.task.scheduler.entity.Task;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author : jacksonz
 * @date : 2022/6/30 20:50
 * @description :
 */
public class PushToRedisListQueueTaskManager extends AbstractTaskManager {

    /**
     * task:queue:processType
     */
    private final static String TASK_QUEUE_KEY_PATTERN = "task:queue:%s";

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doPushTask(Task task) {
        TaskProcessTypeEnum processType;

        AssertUtils.isTrue(Objects.nonNull(task));
        AssertUtils.isTrue(Objects.nonNull(processType = task.getProcessType()));

        String key = String.format(TASK_QUEUE_KEY_PATTERN, processType.getName());
        ListOperations<String, String> ops = redisTemplate.opsForList();
        ops.rightPush(key, task.getTaskCode());
    }

    @Override
    public String getPullingKey(TaskProcessTypeEnum processType, TaskPriorityTypeEnum taskPriorityType) {
        AssertUtils.isTrue(Objects.nonNull(processType));

        return String.format(TASK_QUEUE_KEY_PATTERN, processType.getName());
    }
}
