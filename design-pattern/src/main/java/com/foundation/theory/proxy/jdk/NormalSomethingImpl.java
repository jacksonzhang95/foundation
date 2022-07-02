package com.foundation.theory.proxy.jdk;

/**
 * @author by jacksonz
 * @classname NormalLog
 * @description TODO
 * @date 2020/8/16 14:37
 */
public class NormalSomethingImpl implements ISomethingService {
    @Override
    public void doSomething() {
        System.out.println("normal error");
    }
}
