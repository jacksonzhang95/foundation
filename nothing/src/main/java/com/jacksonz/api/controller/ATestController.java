package com.foundation.api.controller;

import com.foundation.api.service.IATestToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/ATest")
public class ATestController {

    @Autowired
    private IATestToolService ATestToolService;

    @RequestMapping(value = "/generateWeeklyPublication", method = RequestMethod.POST)
    public String generateWeeklyPublication (@RequestParam("file") MultipartFile file) throws Exception {
        return ATestToolService.generateWeeklyPublication(file);
    }

    @RequestMapping(value = "/generateWeeklyPublication", method = RequestMethod.GET)
    public String an () throws Exception {
        return "fuck";
    }
}
