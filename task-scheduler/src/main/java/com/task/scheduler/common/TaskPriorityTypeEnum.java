package com.task.scheduler.common;

import lombok.Getter;

import java.util.Objects;

/**
 * @author : jacksonz
 * @date : 2022/7/9 16:03
 * @description :
 */
@Getter
public enum TaskPriorityTypeEnum {
    LOW(1, "low"),
    NORMAL(2, "normal"),
    HIGH(3, "high")
    ;

    private Integer id;

    private String name;


    TaskPriorityTypeEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static TaskPriorityTypeEnum getById(Integer id) {
        if (Objects.isNull(id)) {
            return null;
        }
        for (TaskPriorityTypeEnum typeEnum : TaskPriorityTypeEnum.values()) {
            if (Objects.equals(typeEnum.getId(), id)) {
                return typeEnum;
            }
        }

        return null;
    }
}
