package com.foundation.api.service;

import org.springframework.web.multipart.MultipartFile;

public interface IATestToolService {


    String generateWeeklyPublication(MultipartFile file) throws Exception;
}
