package com.foundation.theory.returnvalue;

import java.util.Arrays;
import java.util.List;

/**
 * @author : jacksonz
 * @date : 2021/10/11 14:11
 */
public class ReturnValueDesignDemo {

    /**
     * 返回错误码
     * 这种方式需要占用返回值，不推荐使用
     */
    static enum ErrorCode {
        SUCCESS("200", "success"),
        ERROR("500", "error");
        private String code;
        private String name;

        ErrorCode(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    public ErrorCode returnErrorCode() {
        boolean checkFlag = false;
        if (/*something wrong*/ checkFlag) {
            return ErrorCode.ERROR;
        }
        return ErrorCode.SUCCESS;
    }

    /**
     * 返回NULL
     * 当函数是search，find，get，select，query，list时返回NULL可以代表数据不存在
     * 弊端就是使用者必须添加一些Null值判断避免NPE
     */
    public List<String> getIds() {
        // do some search
        boolean dataNotExistFlag = true;
        if (/*data not exist*/ dataNotExistFlag) {
            return null;
        }
        return Arrays.asList("123", "#21");
    }

    /**
     * 返回空对象
     * 应对当无数据时返回null引发的NPE，采用返回空对象来表示无所惧
     */
    public List<String> getIds2() {
        // do some search
        boolean dataNotExistFlag = true;
        if (/*data not exist*/ dataNotExistFlag) {
            return Arrays.asList();
        }
        return Arrays.asList("123", "#21");
    }

    /**
     * 当程序出现异常时，抛出非受检异常
     * 弊端:
     * 1. 由于灵活性，完全依赖调用方自己处理异常，因此有可能会漏处理
     */
    public void throwUncheckException() {
        throw new RuntimeException("test");
    }

    /**
     * 当程序出现异常时，抛出受检异常
     * 弊端：
     * 1. 调用者必须显示处理异常，因此代码要加try-catch。
     * 在一定程度上会影响开闭原则(给一个方法添加受检异常后会影响调用者的代码)。
     * 2. 方法定义长度很长(throws ...), 影响可读性
     */
    public void throwCheckException() throws Exception {
        throw new Exception("test");
    }

    /**
     * 异常处理方式一: 吃掉异常
     * 使用场景:
     *  当调用者不关心原方法的异常（不影响后续流程），可以直接吃掉
     */
    public void handelException1() {
        try {
            throwCheckException();
        } catch (Exception e) {
            // 吃掉异常 + 打日志
        }
    }

    /**
     * 异常处理方式二: 原封不动抛出
     * 使用场景：
     *  当调用者关心原方法的异常，影响后续处理，可以选择直接抛出
     */
    public void handelException2() throws Exception {
        try {
            throwCheckException();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 异常处理方式三: 封装一下抛出
     * 使用场景：
     *  当调用者关心原方法的异常，影响后续处理，
     *      但原方法异常比较无业务意义，可以选择封装一下
     */
    public void handelException3() throws Exception {
        try {
            throwCheckException();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
