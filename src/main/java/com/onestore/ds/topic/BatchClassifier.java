package com.onestore.ds.topic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by a1000074 on 06/02/2020.
 */
public class BatchClassifier {

    public static void main(String ... v) {
        TermScoreBasedClassifier tsClassifier = new TermScoreBasedClassifier() ;
        tsClassifier.init(Arrays.asList("안되는", "타격", "그래픽", "사운드", "다운로드", "쿠폰", "환불", "과금","오류", "업데이트", "실행", "설치"));
        tsClassifier.printTDM();

        try(BufferedReader br = new BufferedReader(new FileReader("/Users/a1000074/dev/temp-comment/latest_comment_5e6_20200107.txt"))) {

            br.lines().forEach(line -> {
                List<TermScore> topicScore = tsClassifier.getAllTopicScore(line);
                StringBuilder sb = new StringBuilder() ;
                topicScore.forEach(ts -> {
                    sb.append(ts.getScore() + "\t") ;
                });

                System.out.println(line + "\t\t" + sb.toString()) ;

            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
