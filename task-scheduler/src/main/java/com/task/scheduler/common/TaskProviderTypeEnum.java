package com.task.scheduler.common;

import lombok.Getter;

import java.util.Objects;

/**
 * @author : jacksonz
 * @date : 2022/7/9 14:06
 * @description :
 */
@Getter
public enum TaskProviderTypeEnum {
    ;

    private Integer id;

    private String name;

    private String desc;

    TaskProviderTypeEnum(Integer id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public static TaskProviderTypeEnum getById(Integer id) {
        if (Objects.isNull(id)) {
            return null;
        }
        for (TaskProviderTypeEnum typeEnum : TaskProviderTypeEnum.values()) {
            if (Objects.equals(typeEnum.getId(), id)) {
                return typeEnum;
            }
        }

        return null;
    }
}
