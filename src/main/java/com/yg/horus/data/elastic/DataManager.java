package com.yg.horus.data.elastic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yg.horus.data.elastic.data.News;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * Created by 1002000 on 2019. 8. 19..
 */
@Service
public class DataManager {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${internal.elastic.host}")
    private String elasticHost = "localhost" ;
    @Value("${internal.elastic.port}")
    private int elasticPort = 9200 ;

    private ObjectMapper mapper = new ObjectMapper();

    public DataManager() {
        ;
    }

    public void putNews(News news) {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(elasticHost, elasticPort, "http")));
//        PutMappingRequest request = new PutMappingRequest("test_news");
        IndexRequest request = new IndexRequest("test_news3");

        try {
//            request.id("123");
            request.source(this.mapper.writeValueAsString(news), XContentType.JSON);
            System.out.println("-->" + request.source().utf8ToString());

            IndexResponse response = client.index(request, RequestOptions.DEFAULT);

            client.close();
            System.out.println("Response -> " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> String putData(String index, String id, T data) {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(elasticHost, elasticPort, "http")));
        IndexRequest request = new IndexRequest(index);

        String resId = null ;
        try {
            request.id(id) ;
            request.source(this.mapper.writeValueAsString(data), XContentType.JSON);

            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            resId = response.getId() ;

            client.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return resId ;
    }

    public static void main(String ... v) {
        System.out.println("Active System ..");

        DataManager test = new DataManager() ;

        News news = new News() ;
//        news.setTitle("뉴스 테스트 :: 서민일보");
//        news.setContent("TESTTESTTESTTESTTESTTESTTESTTESTTEST TEST");
//        news.setAnchorTitle("충격 뉴스가 테스트를???");
//        news.setPostTime("NA");

        test.putNews(news);
    }

    public static void main1(String ... v) throws Exception {
        System.out.println("Active System .. " + System.currentTimeMillis()) ;

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));


        // Index Data
//        IndexRequest request = new IndexRequest("posts");
////        request.id("100");
//        String jsonString = "{" +
//                "\"user\":\"kimchy\"," +
//                "\"postDate\":\"2015-01-30\"," +
//                "\"message\":\"trying out Elasticsearch for kimchy\"" +
//                "}";
//        request.source(jsonString, XContentType.JSON);
//        final IndexResponse resIdx = client.index(request, RequestOptions.DEFAULT);
//        System.out.println("Response -> " + resIdx.toString());

        // Get Data
//        GetRequest getRequest = new GetRequest("posts", "1");
//        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
//
//        System.out.println("Response -> " + response.getSourceAsString());


        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy"));
        sourceBuilder.from(0);
        sourceBuilder.size(5);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("posts");
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println("SearchResponse -> " + searchResponse.toString());

        System.out.println(" --------------- ") ;

        searchResponse.getHits().forEach(hit -> {
            System.out.println("========");
            System.out.println(hit.getSourceAsString());
        });

        System.out.println(" --------------- ") ;

        client.close();
    }
}
