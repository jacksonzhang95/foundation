package com.foundation.theory.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author by jacksonz
 * @classname TestProxy
 * @description TODO
 * @date 2020/8/16 15:23
 */
public class SomethingProxy {

    /**
     * JDK-Proxy 基于接口进行开发的
     * @return
     */
    public ISomethingService createServiceProxy() {

        /**
         * 获取classLoader
         */
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader classLoader = this.getClass().getClassLoader();
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

        ISomethingService instance = (ISomethingService)Proxy.newProxyInstance(classLoader, new Class[]{ISomethingService.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("开始");
                Object invoke = method.invoke(proxy, args);
                System.out.println("结束");
                return invoke;
            }
        });

        return instance;
    }
}
