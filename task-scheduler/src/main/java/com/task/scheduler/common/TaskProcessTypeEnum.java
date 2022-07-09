package com.task.scheduler.common;

import lombok.Getter;

import java.util.Objects;

/**
 * @author : jacksonz
 * @date : 2022/7/9 14:06
 * @description :
 */
@Getter
public enum TaskProcessTypeEnum {
    ;

    private Integer id;

    private String name;

    private String desc;

    TaskProcessTypeEnum(Integer id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public static TaskProcessTypeEnum getById(Integer id) {
        if (Objects.isNull(id)) {
            return null;
        }
        for (TaskProcessTypeEnum typeEnum : TaskProcessTypeEnum.values()) {
            if (Objects.equals(typeEnum.getId(), id)) {
                return typeEnum;
            }
        }

        return null;
    }
}
