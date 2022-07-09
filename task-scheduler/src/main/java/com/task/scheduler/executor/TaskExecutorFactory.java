package com.task.scheduler.executor;


import com.foundation.common.utils.AssertUtils;
import com.task.scheduler.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.util.Objects;

/**
 * TODO 使用对象池？
 *
 * @author : jacksonz
 * @date : 2022/7/7 11:56
 * @description :
 */
@Component
public class TaskExecutorFactory {

    @Autowired
    private ApplicationContext applicationContext;

    public TaskExecutor createTaskExecutor(Task task) {
        TaskExecutor taskExecutor = createTaskExecutor(task,null);
        return taskExecutor;
    }

    public TaskExecutor createTaskExecutor(Task task, Class<? extends TaskExecutor> taskExecutorClass) {
        AssertUtils.isTrue(Objects.nonNull(task));
        AssertUtils.isTrue(Objects.nonNull(taskExecutorClass));

        TaskExecutor taskExecutor = applicationContext.getBean(taskExecutorClass);
        taskExecutor.setTaskInfo(task);
        return taskExecutor;
    }
}
