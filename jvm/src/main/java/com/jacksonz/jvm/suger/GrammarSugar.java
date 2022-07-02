package com.foundation.jvm.suger;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author by jacksonz
 * @classname TestFanXing
 * @description 测试 语法糖 编译
 * @date 2020/10/18 11:32
 */
public class GrammarSugar {

    public static void main(String[] args) throws Exception {
//        List<Integer> testList = Arrays.asList(1,2,3,4);

//        List<String> testList2 = new ArrayList<>();
//        testList2.add("1");
//        testList2.add("2");
//
//        System.out.println(testList.getClass() == testList2.getClass());
//
//
//        Map<String, Integer> map = new HashMap();
//        map.put("1", 1);
//        map.put("2", 2);
//        System.out.println(map.get("1"));

//        Class aClass = testList.getClass();
//        Method addMethod = aClass.getMethod("add", Object.class);
//        addMethod.invoke(testList,"test");
//
//        for (Object obj : testList) {
//            System.out.println(obj);
//        }
//
//        Method addMethod2 = aClass.getMethod("add", Integer.class);

//        for (Integer obj : testList) {
//            int a = obj;
//            System.out.println(a);
//        }

        PriorityBlockingQueue<Integer> priorityBlockingQueue = new PriorityBlockingQueue<>();
        priorityBlockingQueue.offer(1);
        priorityBlockingQueue.offer(2);
        priorityBlockingQueue.offer(4);
        priorityBlockingQueue.offer(22);
        priorityBlockingQueue.offer(3);
        priorityBlockingQueue.offer(21);
        priorityBlockingQueue.offer(28);
        priorityBlockingQueue.offer(25);
        priorityBlockingQueue.offer(24);
        priorityBlockingQueue.offer(26);
        priorityBlockingQueue.offer(20);
        priorityBlockingQueue.offer(27);


        priorityBlockingQueue.poll();
    }
}
