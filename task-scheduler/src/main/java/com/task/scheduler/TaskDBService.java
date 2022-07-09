package com.task.scheduler;

import com.task.scheduler.common.TaskStatusEnum;
import com.task.scheduler.entity.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * DB操作
 *
 * @author : jacksonz
 * @date : 2022/7/9 13:56
 * @description :
 */
@Slf4j
@Component
public class TaskDBService {

    public void updateById(Task task) {

    }

    public Task searchByTaskCode(String taskCode) {
        return null;
    }

    public void save(Task task) {

    }
}
