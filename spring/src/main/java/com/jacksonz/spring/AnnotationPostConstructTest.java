package com.foundation.spring;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.interceptor.InvocationContext;

/**
 * @author : jacksonz
 * @date : 2022/1/8 9:37
 */
@Service
public class AnnotationPostConstructTest {

    @PostConstruct
    private void init(InvocationContext a) {
        System.out.println("init exec " + a);
    }

}
