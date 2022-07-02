package com.foundation.id.generator.numbersegment;

import lombok.Data;

import java.util.Objects;
import java.util.Stack;

/**
 * @author : jacksonz
 * @date : 2022/6/3 10:58
 * @description :
 */
/*
    create db table

    CREATE TABLE id_generator (
      id int(10) NOT NULL,
      max_id bigint(20) NOT NULL COMMENT '当前最大id',
      biz_type	int(20) NOT NULL COMMENT '业务类型',
      version int(20) NOT NULL COMMENT '版本号',
      PRIMARY KEY (`id`)
    )
 */
public class DbNumberSegmentIDGenerator implements NumberSegmentIDGenerator {

    private final Object updateFlagObj = new Object();
    private Integer step;
    private String bizType;
    private Stack<Long> idStack = new Stack<>();

    public DbNumberSegmentIDGenerator(Integer step, String bizType) {
        this.step = step;
        this.bizType = bizType;
    }

    @Override
    public String generate() {
        while (true) {
            Long preId = idStack.pop();
            if (Objects.isNull(preId)) {
                synchronized (updateFlagObj) {
                    if (idStack.empty()) {
                        retrieveId();
                    }
                }
                continue;
            }
            return String.valueOf(preId);
        }
    }

    private void retrieveId() {
        IdGenerator currStep = getCurrStep();
        Long version = currStep.getVersion();
        Long maxId = currStep.getMaxId();
        Long newMaxId = maxId + step;
        if (updateStep(newMaxId, version)) {
            for (long i = maxId; i < newMaxId; i++) {
                idStack.push(i);
            }
        }
    }

    public IdGenerator getCurrStep() {
        return new IdGenerator();
    }

    public boolean updateStep(Long newStep, Long version) {
        /*
            execute sql

            update id_generator
            set max_id = #{newStep}, version = version + 1
            where version = # {version} and biz_type = XXX
         */
        return true;
    }
}

@Data
class IdGenerator {
    private Long id;

    private Long maxId;

    private String bizType;

    private Long version;
}
