package com.foundation.common.exception;

/**
 * @author : jacksonz
 * @date : 2022/7/9 13:45
 * @description :
 */
public class BaseException extends RuntimeException {

    private Integer code;

    private String msg;

    public BaseException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BaseException(Throwable cause, Integer code, String msg) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }
}
