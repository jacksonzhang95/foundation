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
     * ????????????
     *
     * @param param ??????????????????
     * @return ????????????
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Task addTask(TaskCreateParam param) {
        return addTask(param, null);
    }

    /**
     * ????????????
     *
     * @param param                    ??????????????????
     * @param assembleTaskInfoFunction ??????????????????
     * @return ????????????
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
     * ????????????
     *
     * @param task ????????????
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void startTask(Task task) {
        AssertUtils.isTrue(Objects.nonNull(task));
        AssertUtils.isTrue(Objects.nonNull(task.getId()));

        // ?????????????????????
        task.setTaskStatus(TaskStatusEnum.STARTING);
        task.setProcessStartTime(LocalDateTime.now());
        taskDBService.updateById(task);
    }

    /**
     * ????????????
     *
     * @param Task ????????????
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void finishTask(Task Task) {
        finishTask(Task, null);
    }

    /**
     * ????????????
     *
     * @param task                     ????????????
     * @param assembleTaskInfoFunction ??????????????????
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void finishTask(Task task, AssembleTaskInfoFunction assembleTaskInfoFunction) {
        // ????????????
        task.setProcessEndTime(LocalDateTime.now());
        task.setTaskStatus(TaskStatusEnum.FINISHED);

        // ????????????
        taskDBService.updateById(task);

        // ???????????????????????????
        reduceToBeProcessedTaskAmount(task.getProcessType(), 1);
        reduceProcessingTaskAmount(task.getProcessType(), task.getTaskCode());
    }

    /**
     * ??????????????????
     *
     * @param task ????????????
     * @param e    ????????????
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void executeTaskFail(Task task, Exception e) {
        // ????????????
        task.setTaskStatus(TaskStatusEnum.FAILED);

        TaskExtendInfo extendInfo = task.getTaskExtendInfo();
        int failCount = Objects.nonNull(extendInfo) && Objects.nonNull(extendInfo.getFailCount()) ? extendInfo.getFailCount() : 0;
        extendInfo.setFailCount(failCount + 1);
        extendInfo.setLastFailMsg(e.getMessage());

        // ????????????
        taskDBService.updateById(task);

        // ????????????
        reduceToBeProcessedTaskAmount(task.getProcessType(), 1);
        reduceProcessingTaskAmount(task.getProcessType(), task.getTaskCode());
    }

    /**
     * ??????????????????
     *
     * @param task ????????????
     */
    @Override
    public void abandonExecuteTask(Task task) {
        if (Objects.isNull(task)) {
            return;
        }
        // ????????????
        reduceProcessingTaskAmount(task.getProcessType(), task.getTaskCode());
    }

    /**
     * ??????????????????
     *
     * @param taskCode ??????code
     * @return ????????????
     */
    @Override
    public Task applyExecuteTask(String taskCode) {
        Task task = taskDBService.searchByTaskCode(taskCode);
        // ????????????????????????
        if (Objects.isNull(task)) {
            return null;
        }

        // ??????????????????????????????
        if (Objects.equals(TaskStatusEnum.FINISHED, task.getTaskStatus())) {
            return null;
        }

        // ????????????????????????
        if (!applyProcessTask(task.getProcessType(), taskCode)) {
            throw new ConcurrencyReachesLimitException(null);
        }
        return task;
    }

    @Override
    public void destroy() throws Exception {
        log.error("??????????????????????????????taskManager??????");
    }

    /**
     * ??????task??????????????????
     *
     * @param param    ??????????????????
     * @param function ??????????????????
     * @return ????????????
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
     * ????????????task
     *
     * @param task ????????????
     * @return ????????????
     */
    protected Task doAddTask(Task task) {
        AssertUtils.isTrue(Objects.nonNull(task));
        AssertUtils.isTrue(Objects.nonNull(task.getTaskCode()), "????????????taskCode");

        String taskCode = task.getTaskCode();
        RLock lock = redissonClient.getLock(ADD_TASK_LOCK_PREFIX + taskCode);
        if (lock.tryLock()) {
            try {
                if (this.applyAddToBeProcessedTask(task.getProcessType(), 1)) {
                    Task existedTask = taskDBService.searchByTaskCode(taskCode);
                    if (Objects.nonNull(existedTask)) {
                        return existedTask;
                    }

                    // ????????????
                    task.setTaskStatus(TaskStatusEnum.UN_START);
                    taskDBService.save(task);
                } else {
                    throw new FullTaskCantAddException("??????????????????????????????");
                }
            } finally {
                lock.unlock();
            }
        } else {
            throw new SubmitTooFastException("?????????????????????????????????");
        }
        return task;
    }

    /**
     * ????????????task
     *
     * @param Task ????????????
     */
    protected abstract void doPushTask(Task Task);

    /**
     * ???????????????????????????????????????????????????????????????processType?????????
     *
     * @param processType ????????????
     * @param addAmount   ????????????
     * @return ??????????????????
     */
    private boolean applyAddToBeProcessedTask(TaskProcessTypeEnum processType, int addAmount) {
        AssertUtils.isTrue(Objects.nonNull(processType));

        String redisKey = String.format(TO_BE_PROCESSED_TASK_GLOBAL_AMOUNT_PATTERN, processType);

        return numberTaskChecker.applyPassTask(redisKey, getToBeProcessedTaskAmountLimitConfig(processType), addAmount);
    }

    /**
     * ???????????????????????????????????????processType?????????
     *
     * @param processType ????????????
     */
    private boolean reduceToBeProcessedTaskAmount(TaskProcessTypeEnum processType, int reduceAmount) {
        AssertUtils.isTrue(Objects.nonNull(processType));

        String redisKey = String.format(TO_BE_PROCESSED_TASK_GLOBAL_AMOUNT_PATTERN, processType);
        return numberTaskChecker.reducePassedTaskAmount(redisKey, reduceAmount);
    }

    /**
     * ???????????????????????????????????????????????????????????????processType?????????
     *
     * @param processType ????????????
     * @param taskCode    ??????code
     * @return ??????????????????
     */
    private boolean applyProcessTask(TaskProcessTypeEnum processType, String taskCode) {
        AssertUtils.isTrue(Objects.nonNull(processType));
        AssertUtils.isTrue(Objects.nonNull(taskCode));

        String redisKey = String.format(PROCESSING_TASK_GLOBAL_AMOUNT_PATTERN, processType);

        return sortSetTaskChecker.applyPassTask(redisKey, getProcessingTaskAmountLimitConfig(processType), taskCode);
    }

    /**
     * ???????????????????????????????????????processType?????????
     *
     * @param processType ????????????
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
     * ??????task????????????
     * ????????????????????????????????????????????????????????????
     */
    public static class AssembleTaskInfoFunction {

        /**
         * ????????????????????????
         */
        protected Task assembleToBeCreatedTaskInfo(TaskCreateParam param) {
            AssertUtils.isTrue(Objects.nonNull(param), "??????????????????");

            // ??????????????????
            Task task = TaskConverter.INSTANCE.convertToTask(param);
            task.setTaskStatus(TaskStatusEnum.UN_START);
            return task;
        }

        /**
         * ????????????????????????
         */
        protected TaskExtendInfo assembleToBeCreatedTaskExtendInfo(TaskCreateParam param) {
            AssertUtils.isTrue(Objects.nonNull(param), "??????????????????");

            // ??????????????????
            return TaskConverter.INSTANCE.convertToTaskExtendInfo(param);
        }

        /**
         * ??????taskCode
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
