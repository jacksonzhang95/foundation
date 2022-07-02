package com.foundation.elasticsearch.config;

import org.springframework.context.annotation.Configuration;

/**
 * @author by jacksonz
 * @classname ElasticSearchClientConfig
 * @description TODO
 * @date 2020/6/10 22:46
 */

@Configuration
public class ElasticSearchClientConfig {
    /*
    @Value("${elasticsearch.http.host}")
    private String elasticSearchHttpList;

    @Value("${elasticsearch.tcp.host}")
    private String elasticSearchTcpList;

    @Bean
    public RestHighLevelClient restHighLevelClient() throws Exception {
        try {
            String[] httpList = elasticSearchHttpList.split(",");
            HttpHost[] httpHostList = new HttpHost[httpList.length];
            for (int i = 0 ; i < httpList.length ; i++) {
                String hostAndPort = httpList[i];
                String[] hostAndPortArrays = hostAndPort.split(":");
                HttpHost httpHost = new HttpHost(hostAndPortArrays[0], Integer.valueOf(hostAndPortArrays[1]));
                httpHostList[i] = httpHost;
            }
            return new RestHighLevelClient(RestClient.builder(httpHostList));
        } catch (Exception e) {
            throw new Exception("构建RestHighLevelClient时异常");
        }

    }
    */
}
