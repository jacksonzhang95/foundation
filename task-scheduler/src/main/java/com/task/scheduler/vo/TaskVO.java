package com.task.scheduler.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author : jacksonz
 * @date : 2022/7/9 16:29
 * @description :
 */
@Data
public class TaskVO {

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
    private Integer taskStatus;

    /**
     * 供应商类型
     */
    private Integer providerType;

    /**
     * 处理类型
     */
    private Integer processType;

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
    private String taskExtendInfo;

    /**
     * 处理开始时间
     */
    private LocalDateTime processStartTime;

    /**
     * 处理结束时间
     */
    private LocalDateTime processEndTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    private Boolean delete;
}
