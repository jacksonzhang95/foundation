package com.task.scheduler.checker;

/**
 * @author : jacksonz
 * @date : 2022/6/21 15:14
 * @description :
 */
public interface TaskChecker<T> {

    boolean tryPassTask(String key, int limitAmount, T acquireObj);

    boolean applyPassTask(String key, int limitAmount, T acquireObj);

    boolean reducePassedTaskAmount(String key, T reduceObj);

}
