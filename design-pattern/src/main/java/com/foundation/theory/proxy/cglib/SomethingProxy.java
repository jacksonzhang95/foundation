package com.foundation.theory.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author by jacksonz
 * @classname SomethingProxy
 * @description TODO
 * @date 2020/8/16 15:38
 */
public class SomethingProxy {

    /**
     * api文档
     *  http://cglib.sourceforge.net/apidocs/
     * @return
     */
    public static SomethingClass createServiceProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SomethingClass.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
//                System.out.println("start");
//                Object invoke = method.invoke(o, objects);
                /**
                 * 这里如果使用 method.invoker 会抛出InvocationTargetException
                 */
//                return method.invoke(o, objects);

                /**
                 * 如果这里使用 methodProxy.invoke 会抛出StackOverflowError
                 */
//                return methodProxy.invoke(o, objects);

                if (method.getName().contains("2")) {
                    return methodProxy.invokeSuper(o, objects);
                }
                System.out.println("Start");
                Object invoke = methodProxy.invokeSuper(o, objects);
                System.out.println("end");
                return invoke;
            }
        });
        return (SomethingClass)enhancer.create();
    }

    public static void main(String[] args) {
//        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY,"G:\\test");
        SomethingClass serviceProxy = SomethingProxy.createServiceProxy();
        serviceProxy.doSomething2();
        serviceProxy.doSomething();
    }
}
