package com.foundation.algorithm.ratelimit.slidingwindow;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : jacksonz
 * @date : 2022/5/6 14:12
 */
public class LocalMemorySlidingWindow {

    /**
     * 阈值
     */
    private final static int QPS = 2;

    /**
     * 时间窗口总大小（毫秒）
     */
    private final static long WINDOW_SIZE = 1000;

    /**
     * 子窗口数量
     */
    private final static Integer WINDOW_COUNT = 10;

    /**
     * 窗口列表
     */
    private final static WindowInfo[] WINDOW_ARRAY = new WindowInfo[WINDOW_COUNT];

    public LocalMemorySlidingWindow() {
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < WINDOW_ARRAY.length; i++) {
            WINDOW_ARRAY[i] = new WindowInfo(currentTimeMillis, new AtomicInteger(0));
        }
    }

    /**
     * 1. 计算当前时间窗口
     * 2. 更新当前窗口计数 & 重置过期窗口计数
     * 3. 当前 QPS 是否超过限制
     *
     * @return
     */
    public synchronized boolean tryAcquire() {

        long currentTimeMillis = System.currentTimeMillis();

        // 1. 计算当前时间窗口
        int currentIndex = (int)(currentTimeMillis % WINDOW_SIZE / (WINDOW_SIZE / WINDOW_COUNT));

        // 2.  更新当前窗口计数 & 重置过期窗口计数
        int sum = 0;
        for (int i = 0; i < WINDOW_ARRAY.length; i++) {
            WindowInfo windowInfo = WINDOW_ARRAY[i];
            if ((currentTimeMillis - windowInfo.getTime()) > WINDOW_SIZE) {
                windowInfo.getNumber().set(0);
                windowInfo.setTime(currentTimeMillis);
            }
            if (currentIndex == i && windowInfo.getNumber().get() < QPS) {
                windowInfo.getNumber().incrementAndGet();
            }
            sum = sum + windowInfo.getNumber().get();
        }

        // 3. 当前 QPS 是否超过限制
        return sum <= QPS;
    }

    private class WindowInfo {

        // 窗口开始时间
        private Long time;

        // 计数器
        private AtomicInteger number;

        public WindowInfo(long time, AtomicInteger number) {
            this.time = time;
            this.number = number;
        }

        public Long getTime() {
            return time;
        }

        public void setTime(Long time) {
            this.time = time;
        }

        public AtomicInteger getNumber() {
            return number;
        }

        public void setNumber(AtomicInteger number) {
            this.number = number;
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 64; i++) {
            System.out.println("alter table `cart_db`.`user_cart_new_" + i + "` add column `app_source` varchar(50) default 'ibigfan' comment '应用';");
        }
    }
}
