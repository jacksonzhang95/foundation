package com.task.scheduler.manager;


import com.foundation.common.utils.AssertUtils;
import com.task.scheduler.common.TaskPriorityTypeEnum;
import com.task.scheduler.common.TaskProcessTypeEnum;
import com.task.scheduler.entity.Task;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
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
public class PushToRedisSortSetPriorityQueueTaskManager extends AbstractTaskManager {

    /**
     * task:queue:【priorityType】:priority:【processType】
     */
    private final static String TASK_QUEUE_KEY_PATTERN = "task:queue:%s";

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doPushTask(Task task) {
        TaskProcessTypeEnum processType;
        Long score;

        AssertUtils.isTrue(Objects.nonNull(task));
        AssertUtils.isTrue(Objects.nonNull(task.getTaskExtendInfo()));
        AssertUtils.isTrue(Objects.nonNull(processType = task.getProcessType()));
        AssertUtils.isTrue(Objects.nonNull(score = task.getTaskExtendInfo().getTaskPriorityScore()));

        String key = String.format(TASK_QUEUE_KEY_PATTERN, processType.getName());

        ZSetOperations<String, String> ops = redisTemplate.opsForZSet();
        ops.add(key, task.getTaskCode(), Double.valueOf(String.valueOf(score)));
    }

    @Override
    public String getPullingKey(TaskProcessTypeEnum processType, TaskPriorityTypeEnum taskPriorityType) {
        return String.format(TASK_QUEUE_KEY_PATTERN, processType.getName());
    }
}
