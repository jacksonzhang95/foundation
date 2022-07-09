package com.task.scheduler.executor;


import com.task.scheduler.entity.Task;

/**
 * @author : jacksonz
 * @date : 2022/6/21 15:14
 * @description :
 */
public interface TaskExecutor extends Runnable {


    void setTaskInfo(Task taskInfo);
}
