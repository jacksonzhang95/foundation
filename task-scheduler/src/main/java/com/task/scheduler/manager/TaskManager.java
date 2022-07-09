package com.task.scheduler.manager;

import com.task.scheduler.common.TaskPriorityTypeEnum;
import com.task.scheduler.common.TaskProcessTypeEnum;
import com.task.scheduler.entity.Task;
import com.task.scheduler.param.TaskCreateParam;

/**
 * @author : jacksonz
 * @date : 2022/7/2 13:28
 * @description :
 */
public interface TaskManager {

    Task addTask(TaskCreateParam imageProcessParamDTO);

    Task addTask(TaskCreateParam imageProcessParamDTO, AbstractTaskManager.AssembleTaskInfoFunction assembleTaskInfoFunction);

    void finishTask(Task Task);

    void finishTask(Task Task, AbstractTaskManager.AssembleTaskInfoFunction assembleTaskInfoFunction);

    void executeTaskFail(Task Task, Exception e);

    void startTask(Task Task);

    void abandonExecuteTask(Task Task);

    Task applyExecuteTask(String taskCode);

    String getPullingKey(TaskProcessTypeEnum processType, TaskPriorityTypeEnum taskPriorityType);

}
