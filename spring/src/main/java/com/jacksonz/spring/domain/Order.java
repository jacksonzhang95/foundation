package com.foundation.spring.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.interceptor.InvocationContext;
import java.util.Objects;

/**1
 * @author : jacksonz
 * @date : 2021/12/20 8:52
 */
@Service
public class Order {

    @PostConstruct
    private void init(InvocationContext a) {
        System.out.println("order init" + a);
    }

}
