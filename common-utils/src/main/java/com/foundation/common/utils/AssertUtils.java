package com.foundation.common.utils;

import com.foundation.common.exception.BaseException;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author : jacksonz
 * @date : 2022/7/9 11:34
 * @description :
 */
public class AssertUtils {

    // TODO
    private static final int DEFAULT_ERROR_CODE = 5000;

    public static void isTrue(boolean expression) {
        isTrue(expression, "参数不正确");
    }

    public static void isTrue(boolean expression, String message) {
        isTrue(expression, DEFAULT_ERROR_CODE, message);
    }

    public static void isTrue(boolean condition, boolean expression, String message) {
        isTrue(condition, expression, DEFAULT_ERROR_CODE, message);
    }

    public static void isTrue(boolean expression, int code, String message) {
        isTrue(true, expression, code, message);
    }

    public static void isTrue(boolean condition, boolean expression, int code, String message) {
        doValidate(condition, () -> expression, code, message);
    }

    public static void isTrue(boolean condition, Supplier<Boolean> expressionSupplier, int code, String message) {
        doValidate(condition, expressionSupplier, code, message);
    }

    public static void notNull(Object object) {
        notNull(object, "参数不能为空");
    }

    public static void notNull(Object object, String message) {
        notNull(object, DEFAULT_ERROR_CODE, message);
    }

    public static void notNull(boolean condition, Object object, String message) {
        notNull(condition, object, DEFAULT_ERROR_CODE, message);
    }

    public static void notNull(Object object, int code, String message) {
        notNull(true, object, code, message);
    }

    public static void notNull(boolean condition, Object object, int code, String message) {
        // idea老是报警, 换一种实现方式
        if (!condition) {
            return;
        }
        if (object == null) {
            throw new BaseException(code, message);
        }
    }

    public static void notEmpty(Collection<?> collection) {
        notEmpty(collection, "参数不能为空");
    }

    public static void notEmpty(Collection<?> collection, String message) {
        notEmpty(collection, DEFAULT_ERROR_CODE, message);
    }

    public static void notEmpty(boolean condition, Collection<?> collection, String message) {
        notEmpty(condition, collection, DEFAULT_ERROR_CODE, message);
    }

    public static void notEmpty(Collection<?> collection, int code, String message) {
        notEmpty(true, collection, code, message);
    }

    public static void notEmpty(boolean condition, Collection<?> collection, int code, String message) {
        doValidate(condition, () -> collection != null && !collection.isEmpty(), code, message);
    }

    public static void notEmpty(Map<?, ?> map) {
        notEmpty(map, "参数不能为空");
    }

    public static void notEmpty(Map<?, ?> map, String message) {
        notEmpty(map, DEFAULT_ERROR_CODE, message);
    }

    public static void notEmpty(boolean condition, Map<?, ?> map, String message) {
        notEmpty(condition, map, DEFAULT_ERROR_CODE, message);
    }

    public static void notEmpty(Map<?, ?> map, int code, String message) {
        notEmpty(true, map, code, message);
    }

    public static void notEmpty(boolean condition, Map<?, ?> map, int code, String message) {
        doValidate(condition, () -> map != null && !map.isEmpty(), code, message);
    }

    public static void notEmpty(String string) {
        notEmpty(string, "参数不能为空");
    }

    public static void notEmpty(String string, String message) {
        notEmpty(string, DEFAULT_ERROR_CODE, message);
    }

    public static void notEmpty(boolean condition, String string, String message) {
        notEmpty(condition, string, DEFAULT_ERROR_CODE, message);
    }

    public static void notEmpty(String string, int code, String message) {
        notEmpty(true, string, code, message);
    }

    public static void notEmpty(boolean condition, String string, int code, String message) {
        doValidate(condition, () -> string != null && !string.isEmpty(), code, message);
    }

    public static void notBlank(String string) {
        notBlank(string, "参数不能为空");
    }

    public static void notBlank(String string, String message) {
        notBlank(string, DEFAULT_ERROR_CODE, message);
    }

    public static void notBlank(boolean condition, String string, String message) {
        notBlank(condition, string, DEFAULT_ERROR_CODE, message);
    }

    public static void notBlank(String string, int code, String message) {
        notBlank(true, string, code, message);
    }

    public static void notBlank(boolean condition, String string, int code, String message) {
        doValidate(condition, () -> StringUtils.isNotBlank(string), code, message);
    }

    private static void doValidate(boolean condition, Supplier<Boolean> expressionSupplier, int code, String message) {
        if (!condition) {
            return;
        }
        if (!expressionSupplier.get()) {
            throw new BaseException(code, message);
        }
    }

}
