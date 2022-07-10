package com.task.scheduler.common;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author : jacksonz
 * @date : 2022/7/9 16:03
 * @description :
 */
@Getter
public enum TaskPriorityTypeEnum {
    LOW(1, "low", 30L),
    NORMAL(2, "normal", 60L),
    HIGH(3, "high", 100L)
    ;

    private Integer id;

    private String name;

    private Long maxScore;


    TaskPriorityTypeEnum(Integer id, String name, Long maxScore) {
        this.id = id;
        this.name = name;
        this.maxScore = maxScore;
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

    public static TaskPriorityTypeEnum getByIdIfNotExistReturnNormalLevel(Integer id) {
        if (Objects.isNull(id)) {
            return TaskPriorityTypeEnum.NORMAL;
        }
        for (TaskPriorityTypeEnum typeEnum : TaskPriorityTypeEnum.values()) {
            if (Objects.equals(typeEnum.getId(), id)) {
                return typeEnum;
            }
        }
        return TaskPriorityTypeEnum.NORMAL;
    }

    public static TaskPriorityTypeEnum getByScore(Long score) {
        if (Objects.isNull(score)) {
            return TaskPriorityTypeEnum.LOW;
        }
        for (TaskPriorityTypeEnum typeEnum : TaskPriorityTypeEnum.values()) {
            if (typeEnum.getMaxScore().compareTo(score) >= 0) {
                return typeEnum;
            }
        }
        return TaskPriorityTypeEnum.LOW;
    }
}
