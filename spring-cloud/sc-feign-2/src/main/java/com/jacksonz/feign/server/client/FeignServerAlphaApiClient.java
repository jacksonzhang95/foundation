package com.foundation.feign.server.client;

import com.foundation.feign.server.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author : jacksonz
 * @date : 2021/9/5 9:16
 */
@FeignClient(name = "FEGIN-SERVER-ALPHA")
public interface FeignServerAlphaApiClient {

    @RequestMapping(value = "/fegin/server/alpha/call", method = RequestMethod.GET)
    String call(@RequestParam("name") String name);

    @RequestMapping(value = "/fegin/server/alpha/call/json", method = RequestMethod.GET)
    String callJson(@RequestBody UserDto userDto);
}