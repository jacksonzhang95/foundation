package com.task.scheduler.puller;

import com.task.scheduler.common.TaskProcessTypeEnum;

public interface TaskPuller {

    TaskProcessTypeEnum getProcessType();

    void pull();
}
