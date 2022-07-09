package com.foundation.elasticsearch.retrieve.message;

import com.foundation.elasticsearch.DemoElasticSearchApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author by jacksonz
 * @classname CanalRetrieveMessageServiceTest
 * @description TODO
 * @date 2020/6/7 11:46
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {DemoElasticSearchApplication.class})
public class CanalRetrieveMessageServiceTest {

    @Autowired
    private CanalClientRetrieveMessageService canalRetrieveMessApi;

    @Test
    public void testRetrieve() {
        canalRetrieveMessApi.canalConnector();
    }
}