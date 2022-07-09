package com.task.scheduler.common;

import lombok.Data;
import lombok.Getter;

import java.util.Objects;

/**
 * @author : jacksonz
 * @date : 2022/7/9 14:01
 * @description :
 */
@Getter
public enum TaskStatusEnum {

    UN_START(0, "未开始"),
    STARTING(1, "进行中"),
    FINISHED(2, "已完成"),
    FAILED(3, "执行失败")
    ;

    private Integer id;

    private String name;

    TaskStatusEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static TaskStatusEnum getById(Integer id) {
        if (Objects.isNull(id)) {
            return null;
        }
        for (TaskStatusEnum typeEnum : TaskStatusEnum.values()) {
            if (Objects.equals(typeEnum.getId(), id)) {
                return typeEnum;
            }
        }

        return null;
    }
}
