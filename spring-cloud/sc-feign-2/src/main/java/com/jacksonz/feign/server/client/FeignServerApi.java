package com.foundation.feign.server.client;

import com.foundation.feign.server.dto.UserDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author : jacksonz
 * @date : 2021/9/4 13:56
 */
@RestController
@RequestMapping("/feign/server/beta/api")
public class FeignServerApi {

    @Resource
    private FeignServerAlphaApiClient alphaApiClient;

    @RequestMapping(value = "/callAlpha", method = RequestMethod.GET)
    public String callA(String name) {
        return alphaApiClient.call(name);
    }

    @RequestMapping(value = "/callAlpha/json", method = RequestMethod.GET)
    public String callJson() {
        UserDto userDto = new UserDto();
        userDto.setAge(1);
        userDto.setName("1");
        return alphaApiClient.callJson(userDto);
    }
}