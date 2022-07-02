package com.foundation.theory.callback;

import java.util.function.Function;

/**
 * 回调模型
 *
 * 样例
 * @see org.springframework.jdbc.core.JdbcTemplate#execute(org.springframework.jdbc.core.StatementCallback)
 *
 * @author : jacksonz
 * @date : 2021/10/19 9:05
 */
public class CallBackDemo<T,R> {

    private T params;

    public void test(Function<T,R> function) {
        function.apply(params);
    }
}
