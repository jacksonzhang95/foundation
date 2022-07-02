package com.foundation.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author : jacksonz
 * @date : 2021/10/9 19:35
 */
@EnableEurekaServer
@SpringBootApplication
public class BetaEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BetaEurekaServerApplication.class, args);
    }
}
