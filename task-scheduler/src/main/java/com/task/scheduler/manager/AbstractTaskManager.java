package com.task.scheduler.manager;


import com.foundation.common.utils.AssertUtils;
import com.foundation.common.utils.GsonUtils;
import com.foundation.common.utils.MD5Utils;
import com.task.scheduler.TaskDBService;
import com.task.scheduler.checker.TaskChecker;
import com.task.scheduler.common.TaskProcessTypeEnum;
import com.task.scheduler.common.TaskProviderTypeEnum;
import com.task.scheduler.common.TaskStatusEnum;
import com.task.scheduler.converter.TaskConverter;
import com.task.scheduler.entity.Task;
import com.task.scheduler.entity.TaskExtendInfo;
import com.task.scheduler.exception.ConcurrencyReachesLimitException;
import com.task.scheduler.exception.FullTaskCantAddException;
import com.task.scheduler.exception.SubmitTooFastException;
import com.task.scheduler.param.TaskCreateParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author : jacksonz
 * @date : 2022/6/28 15:58
 * @description :
 */
@Slf4j
public abstract class AbstractTaskManager implements TaskManager, DisposableBean {

    private static final String ADD_TASK_LOCK_PREFIX = "add:task:lock:";

    private static final String TO_BE_PROCESSED_TASK_GLOBAL_AMOUNT_PATTERN = "task:amount:tobeprocessed:%s";

    private static final String PROCESSING_TASK_GLOBAL_AMOUNT_PATTERN = "task:amount:processing:%s";

    private static AssembleTaskInfoFunction DEFAULT_ASSEMBLE_TASK_INFO_FUNCTION = new AssembleTaskInfoFunction();

    @Resource(name = "redisNumberTaskChecker")
    private TaskChecker<Integer> numberTaskChecker;

    @Resource(name = "redisSortSetTaskChecker")
    private TaskChecker<String> sortSetTaskChecker;

    @Resource
    private TaskDBService taskDBService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 添加任务
     *
     * @param param 添加任务参数
     * @return 任务信息
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Task addTask(TaskCreateParam param) {
        return addTask(param, null);
    }

    /**
     * 添加任务
     *
     * @param param                    添加任务参数
     * @param assembleTaskInfoFunction 组装参数函数
     * @return 任务信息
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Task addTask(TaskCreateParam param, AssembleTaskInfoFunction assembleTaskInfoFunction) {
        Task Task = doAddTask(buildTask(param, assembleTaskInfoFunction));
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                doPushTask(Task);
            }
        });
        return Task;
    }

    /**
     * 任务开始
     *
     * @param task 任务信息
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void startTask(Task task) {
        AssertUtils.isTrue(Objects.nonNull(task));
        AssertUtils.isTrue(Objects.nonNull(task.getId()));

        // 更新数据库数据
        task.setTaskStatus(TaskStatusEnum.STARTING);
        task.setProcessStartTime(LocalDateTime.now());
        taskDBService.updateById(task);
    }

    /**
     * 完成任务
     *
     * @param Task 任务信息
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void finishTask(Task Task) {
        finishTask(Task, null);
    }

    /**
     * 完成任务
     *
     * @param task                     任务信息
     * @param assembleTaskInfoFunction 组装参数函数
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void finishTask(Task task, AssembleTaskInfoFunction assembleTaskInfoFunction) {
        // 更新信息
        task.setProcessEndTime(LocalDateTime.now());
        task.setTaskStatus(TaskStatusEnum.FINISHED);

        // 更新数据
        taskDBService.updateById(task);

        // 维护数据上下文数据
        reduceToBeProcessedTaskAmount(task.getProcessType(), 1);
        reduceProcessingTaskAmount(task.getProcessType(), task.getTaskCode());
    }

    /**
     * 执行任务失败
     *
     * @param task 任务信息
     * @param e    异常信息
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void executeTaskFail(Task task, Exception e) {
        // 更新信息
        task.setTaskStatus(TaskStatusEnum.FAILED);

        TaskExtendInfo extendInfo = task.getTaskExtendInfo();
        int failCount = Objects.nonNull(extendInfo) && Objects.nonNull(extendInfo.getFailCount()) ? extendInfo.getFailCount() : 0;
        extendInfo.setFailCount(failCount + 1);
        extendInfo.setLastFailMsg(e.getMessage());

        // 更新数据
        taskDBService.updateById(task);

        // 维护数据
        reduceToBeProcessedTaskAmount(task.getProcessType(), 1);
        reduceProcessingTaskAmount(task.getProcessType(), task.getTaskCode());
    }

    /**
     * 放弃执行任务
     *
     * @param task 任务信息
     */
    @Override
    public void abandonExecuteTask(Task task) {
        if (Objects.isNull(task)) {
            return;
        }
        // 维护数据
        reduceProcessingTaskAmount(task.getProcessType(), task.getTaskCode());
    }

    /**
     * 申请执行任务
     *
     * @param taskCode 任务code
     * @return 任务信息
     */
    @Override
    public Task applyExecuteTask(String taskCode) {
        Task task = taskDBService.searchByTaskCode(taskCode);
        // 判断任务是否存在
        if (Objects.isNull(task)) {
            return null;
        }

        // 判断任务是否需要处理
        if (Objects.equals(TaskStatusEnum.FINISHED, task.getTaskStatus())) {
            return null;
        }

        // 判断任务能否执行
        if (!applyProcessTask(task.getProcessType(), taskCode)) {
            throw new ConcurrencyReachesLimitException(null);
        }
        return task;
    }

    @Override
    public void destroy() throws Exception {
        log.error("监听到容器关闭，触发taskManager注销");
    }

    /**
     * 构建task（数据转化）
     *
     * @param param    添加任务参数
     * @param function 组装参数函数
     * @return 任务信息
     */
    protected Task buildTask(TaskCreateParam param, AssembleTaskInfoFunction function) {
        if (Objects.isNull(function)) {
            function = DEFAULT_ASSEMBLE_TASK_INFO_FUNCTION;
        }
        Task task = function.assembleToBeCreatedTaskInfo(param);
        task.setTaskCode(function.generateTaskCode(param));
        TaskExtendInfo extendInfoDTO = function.assembleToBeCreatedTaskExtendInfo(param);
        if (Objects.nonNull(extendInfoDTO)) {
            task.setTaskExtendInfo(extendInfoDTO);
        }
        return task;
    }

    /**
     * 执行保存task
     *
     * @param task 任务信息
     * @return 任务信息
     */
    protected Task doAddTask(Task task) {
        AssertUtils.isTrue(Objects.nonNull(task));
        AssertUtils.isTrue(Objects.nonNull(task.getTaskCode()), "请先生成taskCode");

        String taskCode = task.getTaskCode();
        RLock lock = redissonClient.getLock(ADD_TASK_LOCK_PREFIX + taskCode);
        if (lock.tryLock()) {
            try {
                if (this.applyAddToBeProcessedTask(task.getProcessType(), 1)) {
                    Task existedTask = taskDBService.searchByTaskCode(taskCode);
                    if (Objects.nonNull(existedTask)) {
                        return existedTask;
                    }

                    // 创建任务
                    task.setTaskStatus(TaskStatusEnum.UN_START);
                    taskDBService.save(task);
                } else {
                    throw new FullTaskCantAddException("前方拥挤，请稍后重试");
                }
            } finally {
                lock.unlock();
            }
        } else {
            throw new SubmitTooFastException("该图片在处理中，请稍后");
        }
        return task;
    }

    /**
     * 执行推送task
     *
     * @param Task 目标任务
     */
    protected abstract void doPushTask(Task Task);

    /**
     * 申请添加任务（添加待执行任务数）【目前基于processType维度】
     *
     * @param processType 处理类型
     * @param addAmount   添加数量
     * @return 是否申请成功
     */
    private boolean applyAddToBeProcessedTask(TaskProcessTypeEnum processType, int addAmount) {
        AssertUtils.isTrue(Objects.nonNull(processType));

        String redisKey = String.format(TO_BE_PROCESSED_TASK_GLOBAL_AMOUNT_PATTERN, processType);

        return numberTaskChecker.applyPassTask(redisKey, getToBeProcessedTaskAmountLimitConfig(processType), addAmount);
    }

    /**
     * 减少待执行任务数【目前基于processType维度】
     *
     * @param processType 处理类型
     */
    private boolean reduceToBeProcessedTaskAmount(TaskProcessTypeEnum processType, int reduceAmount) {
        AssertUtils.isTrue(Objects.nonNull(processType));

        String redisKey = String.format(TO_BE_PROCESSED_TASK_GLOBAL_AMOUNT_PATTERN, processType);
        return numberTaskChecker.reducePassedTaskAmount(redisKey, reduceAmount);
    }

    /**
     * 申请执行任务（添加处理中任务数）【目前基于processType维度】
     *
     * @param processType 处理类型
     * @param taskCode    任务code
     * @return 是否申请成功
     */
    private boolean applyProcessTask(TaskProcessTypeEnum processType, String taskCode) {
        AssertUtils.isTrue(Objects.nonNull(processType));
        AssertUtils.isTrue(Objects.nonNull(taskCode));

        String redisKey = String.format(PROCESSING_TASK_GLOBAL_AMOUNT_PATTERN, processType);

        return sortSetTaskChecker.applyPassTask(redisKey, getProcessingTaskAmountLimitConfig(processType), taskCode);
    }

    /**
     * 减少处理中任务数【目前基于processType维度】
     *
     * @param processType 处理类型
     */
    private boolean reduceProcessingTaskAmount(TaskProcessTypeEnum processType, String taskCode) {
        AssertUtils.isTrue(Objects.nonNull(processType));
        AssertUtils.isTrue(Objects.nonNull(taskCode));

        String redisKey = String.format(PROCESSING_TASK_GLOBAL_AMOUNT_PATTERN, processType);
        return sortSetTaskChecker.reducePassedTaskAmount(redisKey, taskCode);
    }

    private Integer getToBeProcessedTaskAmountLimitConfig(TaskProcessTypeEnum processType) {
        return 0;
    }

    private Integer getProcessingTaskAmountLimitConfig(TaskProcessTypeEnum processType) {
        return 0;
    }

    /**
     * 组装task信息函数
     * 目的：留一个口子自定义组装数据，避免继承
     */
    public static class AssembleTaskInfoFunction {

        /**
         * 组装任务主题信息
         */
        protected Task assembleToBeCreatedTaskInfo(TaskCreateParam param) {
            AssertUtils.isTrue(Objects.nonNull(param), "参数不能为空");

            // 解析主体信息
            Task task = TaskConverter.INSTANCE.convertToTask(param);
            task.setTaskStatus(TaskStatusEnum.UN_START);
            return task;
        }

        /**
         * 组装任务扩展信息
         */
        protected TaskExtendInfo assembleToBeCreatedTaskExtendInfo(TaskCreateParam param) {
            AssertUtils.isTrue(Objects.nonNull(param), "参数不能为空");

            // 解析扩展信息
            return TaskConverter.INSTANCE.convertToTaskExtendInfo(param);
        }

        /**
         * 生成taskCode
         *
         * @return
         */
        protected String generateTaskCode(TaskCreateParam param) {
            TaskProviderTypeEnum providerTypeEnum;
            TaskProcessTypeEnum processTypeEnum;

            AssertUtils.isTrue(Objects.nonNull(param.getUserId()));
            AssertUtils.isTrue(Objects.nonNull(providerTypeEnum = TaskProviderTypeEnum.getById(param.getProviderType())));
            AssertUtils.isTrue(Objects.nonNull(processTypeEnum = TaskProcessTypeEnum.getById(param.getProcessType())));

            List<String> generateKeys = new ArrayList<>();

            Object processParam = param.getProcessParam();
            if (Objects.nonNull(processParam)) {
                generateKeys.add(GsonUtils.GSON.toJson(processParam));
            }

            String md5OriginData = null;
            if (generateKeys.size() > 0) {
                StringBuilder builder = new StringBuilder();
                for (String key : generateKeys) {
                    builder.append(key).append(":");
                }
                md5OriginData = builder.toString();
            }

            if (Objects.isNull(md5OriginData) || StringUtils.isBlank(md5OriginData)) {
                md5OriginData = UUID.randomUUID().toString();
            }

            Long userId = param.getUserId();

            return userId + ":" + providerTypeEnum.getId() + ":" + processTypeEnum.getId() + ":" + MD5Utils.MD5(md5OriginData);
        }

    }
}
