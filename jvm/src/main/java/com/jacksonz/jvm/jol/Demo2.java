package com.foundation.jvm.jol;

/**
 * @author by jacksonz
 * @classname Demo2
 * @description 测试查看对全局变量操作的字节码指令
 * @date 2020/10/24 14:12
 */
public class Demo2 {

    private static volatile int i = 1;

    /**
     * 查看汇编指令 JVM参数: -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -Xcomp
     * @param args
     */
    public static void main(String[] args) {
        for (int j = 0 ; j < 10 ; j++) {
            new Thread(() -> {
                for(int k = 0; k < 10000; k++){Demo2.i++;}
            }).start();
        }
        System.out.println(i);
    }
}
