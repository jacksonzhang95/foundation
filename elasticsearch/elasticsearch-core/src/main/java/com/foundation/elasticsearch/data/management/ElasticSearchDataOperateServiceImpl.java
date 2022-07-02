package com.foundation.elasticsearch.data.management;

import com.foundation.elasticsearch.api.data.management.ElasticSearchDataOperateService;

/**
 * @author by jacksonz
 * @classname ElasticSearchDataManagement
 * @description TODO
 * @date 2020/6/9 22:28
 */
//@Service
public class ElasticSearchDataOperateServiceImpl implements ElasticSearchDataOperateService {
    /*
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public void createIndexTest() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("index_test");
        createIndexRequest.settings(Settings.builder()
                //分片
                .put("index.number_of_shards", 3)
                //副本集
                .put("index.number_of_replicas", 2)
                .build());
          {
              "mappings" : {
              7.0后取消type定义，所以默认就是_doc无法设置
                   "_doc" : {
                       "properties": {
                           "propertyName": {
                               "type": "",
                               "analyzer" : "" (可不填)
                           },
                           "propertyName": {
                               "type": "data",
                               "format" : "" (指定格式)
                           }
                       }
                   }
              }
          }
        String json = "{\n" +
                "    \"properties\":{\n" +
                "        \"title\":{\n" +
                "            \"type\":\"keyword\"\n" +
                "        },\n" +
                "        \"name\":{\n" +
                "            \"type\":\"text\",\n" +
                "            \"fielddata\":true\n" +
                "        }\n" +
                "    }\n" +
                "}";
        String formatJsonStr = JSONObject.parseObject(json).toJSONString();
        createIndexRequest.mapping(formatJsonStr, XContentType.JSON);
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse.isAcknowledged());
    }

    public void deleteIndexTest() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("index_test");
        AcknowledgedResponse acknowledgedResponse = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println(acknowledgedResponse.isAcknowledged());
    }

    public void createDocumentTest() throws IOException {
        String jsonStr = "{\n" +
                "\t\"title\": \"this is a title\",\n" +
                "\t\"name\": \"joney a \" \n" +
                "}";

        IndexRequest indexRequest = new IndexRequest();
        indexRequest
                .index("index_test")
                .type("_doc")
                .source(jsonStr, XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

    public void searchDocumentTest() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest
                .indices("index_test")
                .types("_doc")
                .source(searchSourceBuilder);
        searchSourceBuilder.aggregation(AggregationBuilders.terms("name").field("name"));

        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        search.getHits().getHits();
    }

    public void bulkCreateDocumentTest() throws IOException {
        String jsonStr = "{\n" +
                "\t\"title\": \"this is a title3\"\n" +
                "}";
        String jsonStr2 = "{\n" +
                "\t\"title\": \"this is a title4\"\n" +
                "}";

        BulkRequest bulkRequest = new BulkRequest();
        IndexRequest indexRequest1 = new IndexRequest();
        indexRequest1
                .index("index_test")
                .type("_doc")
                .source(jsonStr, XContentType.JSON);

        IndexRequest indexRequest2 = new IndexRequest();
        indexRequest2
                .index("index_test")
                .type("_doc")
                .source(jsonStr2, XContentType.JSON);

        bulkRequest.add(indexRequest1);
        bulkRequest.add(indexRequest2);

        BulkResponse responses = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        responses.hasFailures();

    }

    public void scrollSearchTest() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest
                .indices("index_test")
                .types("_doc")
                .scroll(TimeValue.timeValueMillis(1L));

        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        search.getHits();
    }

    */

}
