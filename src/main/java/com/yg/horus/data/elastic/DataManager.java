package com.yg.horus.data.elastic;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.concurrent.TimeUnit;


/**
 * Created by 1002000 on 2019. 8. 19..
 */
public class DataManager {

    public static void main(String ... v) throws Exception {
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
