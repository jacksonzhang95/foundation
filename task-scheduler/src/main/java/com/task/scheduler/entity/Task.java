package com.task.scheduler.entity;

import com.task.scheduler.common.TaskProcessTypeEnum;
import com.task.scheduler.common.TaskProviderTypeEnum;
import com.task.scheduler.common.TaskStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author : jacksonz
 * @date : 2022/7/9 11:40
 * @description :
 */
@Data
public class Task implements Serializable {

    /**
     * 唯一id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 任务唯一编码
     */
    private String taskCode;

    /**
     * 任务状态
     */
    private TaskStatusEnum taskStatus;

    /**
     * 供应商类型
     */
    private TaskProviderTypeEnum providerType;

    /**
     * 处理类型
     */
    private TaskProcessTypeEnum processType;

    /**
     * 处理参数
     */
    private String processParam;

    /**
     * 处理结果
     */
    private String processedResult;

    /**
     * 任务扩展信息
     */
    private TaskExtendInfo taskExtendInfo;

    /**
     * 处理开始时间
     */
    private LocalDateTime processStartTime;

    /**
     * 处理结束时间
     */
    private LocalDateTime processEndTime;

}
