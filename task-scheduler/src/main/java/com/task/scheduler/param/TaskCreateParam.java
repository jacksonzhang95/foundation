package com.task.scheduler.param;

import com.task.scheduler.common.TaskPriorityTypeEnum;
import lombok.Data;

/**
 * @author : jacksonz
 * @date : 2022/7/9 12:40
 * @description :
 */
@Data
public class TaskCreateParam<T> {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 供应商类型
     */
    private Integer providerType;

    /**
     * 处理类型
     */
    private Integer processType;

    /**
     * 优先级类型
     */
    private Integer priorityType;

    /**
     * 处理参数
     */
    private T processParam;

}
