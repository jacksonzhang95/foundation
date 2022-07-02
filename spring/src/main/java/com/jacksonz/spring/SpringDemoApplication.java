package com.foundation.spring;


import com.foundation.spring.domain.Order;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author : jacksonz
 * @date : 2021/10/9 19:35
 */
//@SpringBootApplication
@ComponentScan
@Configuration
public class SpringDemoApplication {

    public static void main(String[] args) {
//        SpringApplication.run(SpringDemoApplication.class, args);

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringDemoApplication.class);
        Order bean = applicationContext.getBean(Order.class);
    }
}
