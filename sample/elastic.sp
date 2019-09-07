package com.onestore.monitor.core;

import org.apache.http.HttpHost;
import org.apache.lucene.queryparser.xml.builders.RangeQueryBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by 1002000 on 2019. 8. 20..
 */
@Service
public class LogDataManager {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${internal.elastic.host}")
    private String elasticHost = null ;
    @Value("${internal.elastic.port}")
    private int elasticPort = 9200 ;
    @Value("${internal.elastic.default-log-index}")
    private String defaultLogIndex = null ;
    @Autowired
    private DataUtil dataUtil = null ;

    public LogDataManager() {
        ;
    }

    public String getEsIndexName(String svcGroupNmae, long beforeTime) {
        StringBuffer sb = new StringBuffer() ;
        sb.append(svcGroupNmae);
        sb.append("-") ;
        sb.append("7.2.0") ;
        sb.append("-") ;
        sb.append(this.dataUtil.convertUtmDayStr(new Date(System.currentTimeMillis() - beforeTime))) ;

        return sb.toString() ;
    }

    public List<LogData> getLogs(String indexName, long fromOffset, int limit, SortOrder sortOrder) {
        log.info("Get Logs from offset: {} limit: {} order by {}", fromOffset, limit, sortOrder) ;
        ArrayList<LogData> lstResult = new ArrayList<>() ;

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(elasticHost, elasticPort, "http")));

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder qryBuilder = QueryBuilders.boolQuery();
//        qryBuilder.must(QueryBuilders.matchAllQuery()).must(QueryBuilders.rangeQuery("log.offset").gte(fromOffset));
        qryBuilder.must(QueryBuilders.matchAllQuery());

        sourceBuilder.query(qryBuilder) ;
        qryBuilder.filter(QueryBuilders.rangeQuery("log.offset").gte(fromOffset)) ;
//        qryBuilder.filter(QueryBuilders.rangeQuery("@timestamp").gt(fromDate)) ;
        sourceBuilder.from(0);
        sourceBuilder.size(limit);

        sourceBuilder.sort(SortBuilders.fieldSort("log.offset").order(sortOrder)) ;

        SearchRequest searchRequest = new SearchRequest(indexName);

        log.info("Check Exception ES Query : {}", sourceBuilder.toString());
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            searchResponse.getHits().forEach(hit -> {
                Map<String, Object> mapRes =  hit.getSourceAsMap();

                System.out.println(mapRes.get("@timestamp") + " -> " + ((Map<String, Object>)mapRes.get("log")).get("offset"));
//                System.out.println(hit.getScore() + "-->" + mapRes.get("message"));
                String retData = ((Map<String, Object>)mapRes.get("log")).get("offset") + "\t" + hit.getSourceAsMap().get("message") ;

                long offset = (int)((Map<String, Object>)mapRes.get("log")).get("offset") ;
                lstResult.add(new LogData(offset, hit.getSourceAsMap().get("message").toString()));

            });

            System.out.println(" --------------- ");
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return lstResult ;
    }


    public List<LogData> getMatchedLogs(String indexName, String fromDateStr, String matchWord,
                                int fromOffset, int limit, SortOrder sortOrder) {
        log.info("Get Logs on {} from {} with matched {} and offset {} limit {} order {}", indexName, fromDateStr,
                matchWord, fromOffset,limit, sortOrder);
        ArrayList<LogData> lstResult = new ArrayList<>() ;

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(elasticHost, elasticPort, "http")));

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder qryBuilder = QueryBuilders.boolQuery();
        if(matchWord != null) {
            if(fromOffset > 0) {
                qryBuilder.must(QueryBuilders.matchQuery("message", matchWord))
                        .filter(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("@timestamp").gt(fromDateStr))
                                .must(QueryBuilders.rangeQuery("log.offset").gt(fromOffset)));
            } else {
                qryBuilder.must(QueryBuilders.matchQuery("message", matchWord)).filter(QueryBuilders.rangeQuery("@timestamp").gt(fromDateStr));

//                qryBuilder.must(QueryBuilders.regexpQuery("message",  ".*" + matchWord + ".*"))
//                        .filter(QueryBuilders.rangeQuery("@timestamp").gt(fromDateStr));

            }
        } else {
            if(fromOffset > 0) {

                qryBuilder.must(QueryBuilders.matchAllQuery())
                        .filter(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("@timestamp").gt(fromDateStr))
                                .must(QueryBuilders.rangeQuery("log.offset").gt(fromOffset)));
            } else {
                qryBuilder.must(QueryBuilders.matchAllQuery())
                        .filter(QueryBuilders.rangeQuery("@timestamp").gt(fromDateStr));
            }
        }
        sourceBuilder.query(qryBuilder) ;
//        qryBuilder.filter(QueryBuilders.rangeQuery("@timestamp").gt(this.dataUtil.convertUtmStr(fromDate))) ;
//        qryBuilder.filter(QueryBuilders.rangeQuery("@timestamp").gt(fromDate)) ;
        sourceBuilder.from(0);
        sourceBuilder.size(limit);

//        sourceBuilder.sort(SortBuilders.fieldSort("@timestamp").order(sortOrder)) ;
//        sourceBuilder.sort(SortBuilders.fieldSort("log.offset").order(sortOrder)) ;

//        sourceBuilder.sort(SortBuilders.fieldSort("_id").order(sortOrder));

        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        if(indexName == null) {
            indexName = defaultLogIndex ;
        }

        SearchRequest searchRequest = new SearchRequest(indexName);

        log.info("Check Exception ES Query : {}", sourceBuilder.toString());
        searchRequest.source(sourceBuilder);

        try {

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            searchResponse.getHits().forEach(hit -> {
                Map<String, Object> mapRes =  hit.getSourceAsMap();

                System.out.println(mapRes.get("@timestamp") + " -> " + ((Map<String, Object>)mapRes.get("log")).get("offset"));
                System.out.println(hit.getScore() + "-->" + mapRes.get("message"));

                String retData = ((Map<String, Object>)mapRes.get("log")).get("offset") + "\t" + hit.getSourceAsMap().get("message") ;

                if((retData.contains("INFO") || retData.contains("ERROR")) && retData.contains(matchWord) && hit.getScore() > 50F) {
                    long offset = (int)((Map<String, Object>)mapRes.get("log")).get("offset") ;
                    lstResult.add(new LogData(offset, hit.getSourceAsMap().get("message").toString()));
                }
            });

            System.out.println(" --------------- ");
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return lstResult ;
    }

    public List<String> getLatestLog(long rangeTime) {
        log.info("Elastic Host:{}, Port:{}", this.elasticHost, this.elasticPort);
        ArrayList<String> lstResult = new ArrayList<>() ;


        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(elasticHost, elasticPort, "http")));

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
////        sourceBuilder.query(QueryBuilders.termQuery("message", "INFO"));
//        sourceBuilder.query(QueryBuilders.matchQuery("message", "INFO"));
//        QueryBuilders.rangeQuery()

        sourceBuilder.query(QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("message", "INFO"))
                .filter(QueryBuilders.rangeQuery("@timestamp").gt("2019-08-27T01:43:21.785Z")));
        sourceBuilder.from(0);
        sourceBuilder.size(1000);
//        sourceBuilder.sort("log.offset", SortOrder.DESC);
        sourceBuilder.sort(SortBuilders.fieldSort("@timestamp").order(SortOrder.DESC)) ;
        sourceBuilder.sort(SortBuilders.fieldSort("log.offset").order(SortOrder.DESC)) ;

        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        SearchRequest searchRequest = new SearchRequest("filebeat-7.2.0-2019.08.20-000001");
//        searchRequest.indices("filebeat-7.2.0-2019.08.20-000001");
        log.info(" ------> Query : {}", sourceBuilder.toString());
        searchRequest.source(sourceBuilder);

        try {

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

//            System.out.println("SearchResponse -> " + searchResponse.toString());
//            System.out.println(" --------------- ");

            searchResponse.getHits().forEach(hit -> {
//                System.out.println("========");
                System.out.println(hit.getSourceAsMap().get("@timestamp"));
                System.out.println(hit.getSourceAsString());

                String retData = hit.getId() + "\t" + hit.getSourceAsMap().get("message") ;
                lstResult.add(retData) ;

            });

            System.out.println(" --------------- ");
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return lstResult ;
    }


    public List<String> searchLog() {
        log.info("Elastic Host:{}, Port:{}", this.elasticHost, this.elasticPort);
        ArrayList<String> lstResult = new ArrayList<>() ;


        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(elasticHost, elasticPort, "http")));

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.sort("log.offset", SortOrder.DESC);
//        sourceBuilder.query(QueryBuilders.termQuery("message", "INFO"));
        sourceBuilder.query(QueryBuilders.matchQuery("message", "INFO"));
        sourceBuilder.from(0);
        sourceBuilder.size(20);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));


        SearchRequest searchRequest = new SearchRequest("filebeat-7.2.0-2019.08.20-000001");
//        searchRequest.indices("filebeat-7.2.0-2019.08.20-000001");
        log.info("Query : {}", sourceBuilder.toString());
        searchRequest.source(sourceBuilder);

        try {

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

//            System.out.println("SearchResponse -> " + searchResponse.toString());
//            System.out.println(" --------------- ");

            searchResponse.getHits().forEach(hit -> {
//                System.out.println("========");
                System.out.println(hit.getSourceAsString());

                String retData = hit.getId() + "\t" + hit.getSourceAsMap().get("message") ;
                lstResult.add(retData) ;

            });

            System.out.println(" --------------- ");
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return lstResult ;
    }



    public static void main(String ... v) {
        ;
    }
}
