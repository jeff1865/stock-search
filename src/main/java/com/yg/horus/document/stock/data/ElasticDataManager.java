package com.yg.horus.document.stock.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;


/**
 * Created by 1002000 on 2019. 3. 8..
 */
public class ElasticDataManager {

    //The config parameters for the connection
    private static final String HOST = "localhost";
    private static final int PORT_ONE = 9200;
    private static final String SCHEME = "http";

    private static RestHighLevelClient restHighLevelClient;
    private static ObjectMapper objectMapper = new ObjectMapper();

    private static final String INDEX = "bank";


    public ElasticDataManager() {
        ;
    }

    public static void sampleGet() throws Exception {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(HOST, PORT_ONE, SCHEME)));

        GetRequest getRequest = new GetRequest(INDEX, "_doc", "1");

        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);

        System.out.println("Get Result :" + response.getIndex() + " / " + response.getType() + " / " + response.getId());
        if(response.isExists()) {
            System.out.println("Data :" + response.getSourceAsString());
        }

        client.close();
    }

    public static void main(String ... v) throws Exception {
        System.out.println("Active System .. 1");

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(HOST, PORT_ONE, SCHEME)));

        SearchRequest searchRequest = new SearchRequest("bank");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // (1)
//        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
//        searchSourceBuilder.query(QueryBuilders.termQuery("firstname", "Garcia"));
//        searchSourceBuilder.query(QueryBuilders.termQuery("Blake", false));

        // (2)
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("firstname", "Rachelle1");
        matchQueryBuilder.fuzziness(Fuzziness.AUTO);
        matchQueryBuilder.prefixLength(3);
        matchQueryBuilder.maxExpansions(10);
        searchSourceBuilder.query(matchQueryBuilder);

        // (3)
        SuggestionBuilder termSuggestionBuilder =
                SuggestBuilders.termSuggestion("firstname").text("Rachelle");
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("suggest_user", termSuggestionBuilder);
        searchSourceBuilder.suggest(suggestBuilder);


        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println("Response Status : " + searchResponse.status());

        SearchHits hits = searchResponse.getHits();

        System.out.println("Total Hits :" + hits.getTotalHits() + " , MaxScore :" + hits.getMaxScore());

        for(SearchHit hit : hits.getHits()) {
            System.out.println("Hit Result -> " + hit.getSourceAsString());
        }

        client.close();

    }
}
