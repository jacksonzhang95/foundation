package com.foundation.jvm.jol;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;

/**
 * @author by jacksonz
 * @classname JolDemo
 * @description TODO
 * @date 2020/9/27 20:20
 */
public class JolDemo {

    public static A obj = new A();


    public static void main(String[] args) throws Exception {
//        JolDemo.testAllocateInStack();
//        JolDemo.testCompressedOops();

        Thread.sleep(8000L);
//        new Thread(new Runnable(){
//            @Override
//            public void run() {
//                try {
//                    new JolDemo().testMarkWordValue("thread1");
//                }catch (Exception e) {
//                }
//            }
//        }).start();
//        new JolDemo().testMarkWordValue("thread2");

//        new JolDemo().testStaticFieldMarkWordValue();
    }

    public static class A {

    }


    /**
     * 测试JOL-APi
     */
    public static void jolApiTest() throws InterruptedException {
        A obj = new A();
        //查看对象内部信息
        System.out.println(ClassLayout.parseInstance(obj).toPrintable());

        //查看对象外部信息
        System.out.println(GraphLayout.parseInstance(obj).toPrintable());

        //获取对象总大小
        System.out.println("size : " + GraphLayout.parseInstance(obj).totalSize());
    }


    /**
     * 测试栈上分配
     * 设置JVM参数
     *  -Xmx 最大对空间
     *  -Xms 最小堆空间
     *  -XX:PrintGC 开启GC
     *  -server 开启JVM-server模式
     *  -XX:+DoEscapeAnalysis 开启逃逸分析
     */
    public static void testAllocateInStack(){
//        System.out.println("size : " + GraphLayout.parseInstance(new A()).totalSize());

        /**
         * 经上面验证 obj有16k大小
         * 10 * 1024 / 16 = 640个对象
         */
        for (int i = 0; i < 100000000 ; i++) {
            A obj = new A();
        }
        System.out.println("normally out");
    }

    /**
     * 测试指针压缩
     * JVM参数：
     *  -XX:+/-UseCompressedOops
     */
    public static void testCompressedOops() {
        A obj = new A();
        //查看对象内部信息
        System.out.println(ClassLayout.parseInstance(obj).toPrintable());
    }

    /**
     * 测试上锁前上锁后的MarkWord
     */
    public void testMarkWordValue(String tag) throws InterruptedException {
        System.out.println("上锁前" + tag + "\n" + ClassLayout.parseInstance(obj).toPrintable());
        System.out.println(obj.hashCode());
        System.out.println("上锁前,计算hashcode后" + tag + "\n" + ClassLayout.parseInstance(obj).toPrintable());
        synchronized (obj) {
            System.out.println("上锁中" + tag + "\n" + ClassLayout.parseInstance(obj).toPrintable());
            Thread.sleep(5000L);
        }
        System.out.println("上锁后" + tag + "\n" + ClassLayout.parseInstance(obj).toPrintable());
    }

}
