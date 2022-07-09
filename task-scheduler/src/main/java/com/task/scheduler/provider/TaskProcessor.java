package com.task.scheduler.provider;

import com.task.scheduler.entity.Task;

/**
 * @author : jacksonz
 * @date : 2022/7/9 11:46
 * @description :
 */
public interface TaskProcessor<T> {

    T processTask(Task task);
}
