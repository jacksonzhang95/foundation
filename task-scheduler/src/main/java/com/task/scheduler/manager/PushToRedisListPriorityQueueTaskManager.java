package com.task.scheduler.manager;


import com.foundation.common.utils.AssertUtils;
import com.task.scheduler.common.TaskPriorityTypeEnum;
import com.task.scheduler.common.TaskProcessTypeEnum;
import com.task.scheduler.entity.Task;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author : jacksonz
 * @date : 2022/6/30 20:50
 * @description :
 */
@Primary
@Component
public class PushToRedisListPriorityQueueTaskManager extends AbstractTaskManager {

    /**
     * task:queue:【priorityType】:priority:【processType】
     */
    private final static String TASK_QUEUE_KEY_PATTERN = "task:queue:%s:priority:%s";

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doPushTask(Task task) {
        TaskProcessTypeEnum processType;
        TaskPriorityTypeEnum priorityType;

        AssertUtils.isTrue(Objects.nonNull(task));
        AssertUtils.isTrue(Objects.nonNull(task.getTaskExtendInfo()));
        AssertUtils.isTrue(Objects.nonNull(processType = task.getProcessType()));
        AssertUtils.isTrue(Objects.nonNull(priorityType = task.getTaskExtendInfo().getTaskPriorityType()));

        String key = String.format(TASK_QUEUE_KEY_PATTERN, priorityType.getName(), processType.getName());

        ListOperations<String, String> ops = redisTemplate.opsForList();
        ops.rightPush(key, task.getTaskCode());
    }

    @Override
    public String getPullingKey(TaskProcessTypeEnum processType, TaskPriorityTypeEnum taskPriorityType) {
        if (Objects.isNull(taskPriorityType)) {
            taskPriorityType = TaskPriorityTypeEnum.NORMAL;

        }
        return String.format(TASK_QUEUE_KEY_PATTERN, taskPriorityType.getName(), processType.getName());
    }
}
