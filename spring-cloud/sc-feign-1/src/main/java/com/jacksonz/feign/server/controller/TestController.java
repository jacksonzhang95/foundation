package com.foundation.feign.server.controller;

import com.foundation.feign.server.dto.UserDto;
import org.springframework.web.bind.annotation.*;

/**
 * @author : jacksonz
 * @date : 2021/9/4 13:56
 */
@RestController
@RequestMapping("/fegin/server/alpha")
public class TestController {

    @RequestMapping(value = "/call", method = RequestMethod.GET)
    public String call(String name){
        return "get out " + name;
    }

    @RequestMapping(value = "/call/json", method = RequestMethod.POST)
    public String callJson(@RequestBody UserDto userDto){
        return "get out " + userDto.toString();
    }
}