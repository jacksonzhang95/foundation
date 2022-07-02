package com.foundation.id.generator.uuid;

import com.foundation.id.generator.IDGenerator;

import java.util.UUID;

/**
 * @author : jacksonz
 * @date : 2022/5/29 21:35
 * @description : uuid生成器
 */
public class UUIDGenerator implements IDGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }

}
