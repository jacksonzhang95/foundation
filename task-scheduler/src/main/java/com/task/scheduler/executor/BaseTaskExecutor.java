package com.task.scheduler.executor;

import com.foundation.common.utils.GsonUtils;
import com.task.scheduler.entity.Task;
import com.task.scheduler.manager.AbstractTaskManager;
import com.task.scheduler.manager.TaskManager;
import com.task.scheduler.provider.TaskProcessor;
import com.task.scheduler.provider.TaskProcessorProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class BaseTaskExecutor<T> implements TaskExecutor {

    /**
     * 任务信息
     */
    private Task taskInfo;

    /**
     * 任务管理器
     */
    @Resource
    private TaskManager taskManager;

    /**
     * 任务执行器门面
     */
    @Resource
    private TaskProcessorProviderFactory taskProcessorProviderFactory;

    @Override
    public void setTaskInfo(Task taskInfo) {
        this.taskInfo = taskInfo;
    }

    @Override
    public void run() {
        if (Objects.isNull(taskInfo)) {
            return;
        }
        try {
            // 执行任务前
            doBeforeProcess(taskInfo);
            // 执行任务
            T t = doProcess(taskInfo);
            // 执行任务后
            doAfterProcess(taskInfo, t);
            // 完成任务
            taskManager.finishTask(taskInfo, getAssembleTaskInfoFunction());
        } catch (Exception e) {
            log.error("任务处理, 出现异常， taskCode: {}, task信息: {}, 异常信息: {}", taskInfo.getTaskCode(), GsonUtils.GSON.toJson(taskInfo), e);
            handleException(taskInfo, e);
        }
    }

    /**
     * 任务处理前
     *
     * @param task 任务信息
     */
    protected void doBeforeProcess(Task task) {
        // doNothing
    }

    /**
     * 任务处理
     *
     * @param task 任务信息
     * @return 任务处理后结果
     */
    @SuppressWarnings(value = "unchecked")
    protected T doProcess(Task task) {
        TaskProcessor<T> imageProcessor = taskProcessorProviderFactory.getProvider(task);
        try {
            return imageProcessor.processTask(task);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 任务处理后
     *
     * @param task 任务信息
     * @param t    任务处理后结果
     */
    @SuppressWarnings(value = "unchecked")
    protected abstract void doAfterProcess(Task task, T t);

    /**
     * 处理异常
     *
     * @param task
     * @param e
     */
    protected void handleException(Task task, Exception e) {
        taskManager.executeTaskFail(task, e);
    }

    /**
     * 获取数据组装函数
     * 用于自定义修改taskManger行为
     *
     * @return 数据组装函数
     */
    protected AbstractTaskManager.AssembleTaskInfoFunction getAssembleTaskInfoFunction() {
        return null;
    }
}
