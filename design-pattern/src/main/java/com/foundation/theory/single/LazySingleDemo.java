package com.foundation.theory.single;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : jacksonz
 * @date : 2021/10/13 14:59
 */
public class LazySingleDemo {

    /**
     * 使用volatile保持多线程可见性，且阻止指令重排
     */
    private volatile static LazySingleDemo lazySingleDemo;

    private static Lock lock = new ReentrantLock();

    private LazySingleDemo() {

    }

    /**
     * case 1
     * @return
     */
    public static LazySingleDemo getInstance() {
        if (Objects.isNull(lazySingleDemo)) {
            return lazySingleDemo;
        }
        synchronized (LazySingleDemo.class) {
            if (Objects.isNull(lazySingleDemo)) {
                return lazySingleDemo;
            }

            lazySingleDemo = new LazySingleDemo();

            return lazySingleDemo;
        }
    }

    /**
     * case 2
     * @return
     */
    public static LazySingleDemo getInstance2() throws Exception {
        if (Objects.isNull(lazySingleDemo)) {
            return lazySingleDemo;
        }

        lock.lock();
        try {
            if (Objects.isNull(lazySingleDemo)) {
                return lazySingleDemo;
            }
            lazySingleDemo = new LazySingleDemo();
            return lazySingleDemo;

        } catch (Exception e) {
            throw new Exception("get lazySingleDemo has an error", e);

        } finally {
            lock.unlock();
        }
    }
}
