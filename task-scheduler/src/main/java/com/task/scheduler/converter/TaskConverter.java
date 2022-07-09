package com.task.scheduler.converter;

import com.foundation.common.utils.GsonUtils;
import com.task.scheduler.common.TaskPriorityTypeEnum;
import com.task.scheduler.common.TaskProcessTypeEnum;
import com.task.scheduler.common.TaskProviderTypeEnum;
import com.task.scheduler.common.TaskStatusEnum;
import com.task.scheduler.entity.Task;
import com.task.scheduler.entity.TaskExtendInfo;
import com.task.scheduler.param.TaskCreateParam;
import com.task.scheduler.vo.TaskVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author : jacksonz
 * @date : 2022/7/9 15:33
 * @description :
 */
@Mapper(imports = {GsonUtils.class, TaskProcessTypeEnum.class, TaskProviderTypeEnum.class, TaskPriorityTypeEnum.class})
public interface TaskConverter {

    TaskConverter INSTANCE = Mappers.getMapper(TaskConverter.class);

    @Mappings({
            @Mapping(target = "processParam", expression = "java(GsonUtils.GSON.toJson(s.getProcessParam()))"),
            @Mapping(target = "processType", expression = "java(TaskProcessTypeEnum.getById(s.getProcessType()))"),
            @Mapping(target = "providerType", expression = "java(TaskProviderTypeEnum.getById(s.getProviderType()))"),
    })
    Task convertToTask(TaskCreateParam s);

    TaskExtendInfo convertToTaskExtendInfo(TaskCreateParam s);

    @Mappings({
            @Mapping(target = "taskStatus", expression = "java(s.getTaskStatus().getId())"),
            @Mapping(target = "processType", expression = "java(s.getProcessType().getId())", nullValueCheckStrategy= NullValueCheckStrategy.ALWAYS),
            @Mapping(target = "providerType", expression = "java(s.getProviderType().getId())"),
            @Mapping(target = "taskExtendInfo", expression = "java(GsonUtils.GSON.toJson(s.getTaskExtendInfo()))"),
    })
    TaskVO convertToTaskVO(Task s);

}
