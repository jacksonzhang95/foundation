package com.foundation.spring.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : jacksonz
 * @date : 2021/12/27 12:44
 */
@RestController
public class TestController {

    @RequestMapping(value = "/test1",method = RequestMethod.GET)
    public String test1() {
        return "test1";
    }

    @RequestMapping(value = "/test2",method = RequestMethod.GET)
    public String test2() {
        return "test2";
    }
}
