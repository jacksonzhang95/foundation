package com.foundation.algorithm.ratelimit.fixwindow;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 限流算法-本地内存固定窗口
 *
 * @author : jacksonz
 * @date : 2022/5/6 9:26
 */
public class LocalMemoryFixWindow {

    /**
     * QPS
     */
    private final static Integer QPS = 2;

    /**
     * 时间窗口（毫秒）
     */
    private final static long TIME_WINDOWS = 1000;

    /**
     * 本地计算器
     */
    private static AtomicInteger REQ_COUNT = new AtomicInteger();

    /**
     * 窗口起始时间
     */
    private static long START_TIME = System.currentTimeMillis();

    /*
        1. 判断是否超出窗口时间
            是，窗口重置

        2. 判断是否超过QPS限制
            是，限流
     */
    public synchronized static boolean tryAcquire() {
        if ((System.currentTimeMillis() - START_TIME) > TIME_WINDOWS) {
            REQ_COUNT.set(0);
            START_TIME = System.currentTimeMillis();
        }
        return REQ_COUNT.incrementAndGet() <= QPS;
    }
}
