package com.task.scheduler.puller;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务拉取器配置类
 *
 * @author : jacksonz
 * @date : 2022/7/7 17:00
 * @description :
 */
@Slf4j
@Component
public class TaskPullerStartUpComponent implements SmartInitializingSingleton {

    @Autowired
    private ApplicationContext applicationContext;

    @Value("#{'${need.start.up.puller.class.list}'.split(',')}")
    private List<String> startUpPullerClassNameList;

    private ThreadPoolExecutor threadPool;

    @Override
    public void afterSingletonsInstantiated() {
        if (CollectionUtils.isEmpty(startUpPullerClassNameList)) {
            return;
        }

        ArrayBlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<>(1);

        ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder();
        threadFactoryBuilder.setNameFormat("task-puller-pool-thread-%d");
        ThreadFactory threadFactory = threadFactoryBuilder.build();

        int processTypeLength = startUpPullerClassNameList.size();
        threadPool = new ThreadPoolExecutor(processTypeLength, processTypeLength
            , 10, TimeUnit.MINUTES, taskQueue, threadFactory);

        threadPool.prestartCoreThread();

        for (String className : startUpPullerClassNameList) {
            try {
                Class<?> realClass = Class.forName(className);
                TaskPuller taskPuller = (TaskPuller) applicationContext.getBean(realClass);
                threadPool.execute(new TaskPullerRunningTask(taskPuller));

                log.info("taskPuller, className: {} 启动成功", className);

            } catch (ClassNotFoundException e) {
                log.error("taskPuller启动异常，无法找到实现类, className: {}", className);
            }

        }
    }

    public class TaskPullerRunningTask implements Runnable {

        private TaskPuller taskPuller;

        public TaskPullerRunningTask(TaskPuller taskPuller) {
            this.taskPuller = taskPuller;
        }

        @Override
        public void run() {
            taskPuller.pull();
        }
    }

}
