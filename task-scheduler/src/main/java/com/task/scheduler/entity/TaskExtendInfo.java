package com.task.scheduler.entity;

import com.task.scheduler.common.TaskPriorityTypeEnum;
import lombok.Data;

/**
 * @author : jacksonz
 * @date : 2022/7/9 15:39
 * @description :
 */
@Data
public class TaskExtendInfo {

    private Integer failCount;

    private String lastFailMsg;

    private TaskPriorityTypeEnum taskPriorityType;
}
