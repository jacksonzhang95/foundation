package com.foundation.spring.web.config;

import com.foundation.spring.web.interceptor.TestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author : jacksonz
 * @date : 2021/12/27 12:27
 */
@Service
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(new TestInterceptor());
        interceptorRegistration.addPathPatterns("/*");
    }
}
