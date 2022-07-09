package com.task.scheduler.provider;

import com.task.scheduler.entity.Task;
import org.springframework.stereotype.Component;

/**
 * @author : jacksonz
 * @date : 2022/7/9 11:43
 * @description :
 */
@Component
public class TaskProcessorProviderFactory {

    public TaskProcessor getProvider(Task task) {
        return null;
    }
}
