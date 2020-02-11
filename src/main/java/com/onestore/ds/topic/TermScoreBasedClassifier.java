package com.onestore.ds.topic;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.KoreanTokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * Created by a1000074 on 04/02/2020.
 */
public class TermScoreBasedClassifier {
    private static Logger log = LoggerFactory.getLogger(TermScoreBasedClassifier.class);
    private KoreanTokenizerFactory tokenizerFactory = new KoreanTokenizerFactory() ;
    private Word2Vec vec = null ;
    private Hashtable<String, Hashtable<String, Double>> termScoreMatrix = new Hashtable<>();
    private LinkedHashSet<String> nearTermSet = null ;
    private List<String> orderedTopics = null ;


    public TermScoreBasedClassifier() {
        ;
    }

    public void init(List<String> topics) {
        this.orderedTopics = topics ;

        log.info("load W2V model file..");
//        vec = WordVectorSerializer.readWord2VecModel("/Users/a1000074/dev/temp-comment/model/w2v_comment_5e6.mdl");
        vec = WordVectorSerializer.readWord2VecModel("/Users/a1000074/dev/temp-comment/model/w2v_comment_5e6_min2.mdl");
//        vec = WordVectorSerializer.readWord2VecModel("/Users/a1000074/dev/temp-comment/model/w2v_comment_5e6_min2_wsize20.mdl");

        nearTermSet = new LinkedHashSet<>() ;

        topics.forEach(topicTerm -> {
            Collection<String> nearTerms = vec.wordsNearest(topicTerm, 100);
            nearTerms.forEach(term ->{
                nearTermSet.add(term);
            });
        });


        ArrayList<String> lstTerms = new ArrayList<>() ;
        nearTermSet.forEach(term -> {
            lstTerms.add(term) ;
        });

        log.info("Size of near termList :{}", lstTerms.size());

        for(int i=0;i<topics.size();i++) {
            String topicTerm = topics.get(i) ;
            Hashtable<String, Double> htTermScore = new Hashtable<>();
            for(int j=0;j<lstTerms.size();j++) {
                String term = lstTerms.get(j);
                htTermScore.put(term, vec.similarity(topicTerm, term));
            }

            this.termScoreMatrix.put(topicTerm, htTermScore) ;
        }

        log.info("TermScore matrix loading completed .. {}", termScoreMatrix.size());
    }

    public Collection<String> getNearestTerms(String token, int topN) {
        return this.vec.wordsNearest(token, topN) ;
    }

    public void printTDM() {
        this.termScoreMatrix.forEach((key, val) -> {
            log.info("[TSM]" + key + " --> " + val.toString());
        });
    }

    public List<TermScore> getAllTopicScore(String sentence) {
        try {
//            System.out.println("CheckParagraph -> " + sentence);
            Tokenizer tokenizer = tokenizerFactory.create(sentence);

            double[] scores = new double[this.termScoreMatrix.size()];

            String token = null;
            int topicIndex = 0;
            while (tokenizer.hasMoreTokens()) {
                token = tokenizer.nextToken();
                topicIndex = 0;

                if (this.nearTermSet.contains(token)) {
                    for (; topicIndex < this.orderedTopics.size(); topicIndex++) {
                        String topic = this.orderedTopics.get(topicIndex);

                        double termScore = this.termScoreMatrix.get(topic).get(token);
                        scores[topicIndex] += termScore;
                    }
                }
            }

            List<TermScore> result = new ArrayList<>();
            // --
            for (int i = 0; i < scores.length; i++) {
                result.add(new TermScore(this.orderedTopics.get(i), scores[i]));
            }
            return result;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<TermScore> getAllTopicScore(List<String> lstTokens) {

        throw new RuntimeException("Not yet implemented ..") ;
    }

    public static void main(String ... v) {
        TermScoreBasedClassifier test = new TermScoreBasedClassifier() ;
        test.init(Arrays.asList("쿠폰", "환불", "과금", "타격", "그래픽", "다운로드", "사운드", "오류", "업데이트", "실행", "설치"));
        test.printTDM();

        Collection<String> nearestTerms = test.getNearestTerms("안되", 30);
        System.out.println("N -> " + nearestTerms);

//        long ts = System.currentTimeMillis();
//        List<TermScore> allTopicScore = test.getAllTopicScore("실행이 안되서 재 다운 받았는데 업테이트하라고해서클릭하니 항목을차을수없다고나오네요");
//        System.out.println("Takes mSec :" + (System.currentTimeMillis() - ts));
//        allTopicScore.forEach(System.out::println);
//        System.out.println("--------------------------------");
//
//        ts = System.currentTimeMillis();
//        allTopicScore = test.getAllTopicScore("왜 설치안되는지요 정말 고객센터 관리좀");
//        System.out.println("Takes mSec :" + (System.currentTimeMillis() - ts));
//        allTopicScore.forEach(System.out::println);
//        System.out.println("--------------------------------");
//
//        ts = System.currentTimeMillis();
//        allTopicScore = test.getAllTopicScore("계정날라갔어요ㅠ 로드8티어 뚜기숑입니다ㅠ 계정살려주세요");
//        System.out.println("Takes mSec :" + (System.currentTimeMillis() - ts));
//        allTopicScore.forEach(System.out::println);
//        System.out.println("--------------------------------");
//
//        ts = System.currentTimeMillis();
//        allTopicScore = test.getAllTopicScore("알수없는오류 뜨면서 안되는데요?");
//        System.out.println("Takes mSec :" + (System.currentTimeMillis() - ts));
//        allTopicScore.forEach(System.out::println);
//        System.out.println("--------------------------------");
//
//        ts = System.currentTimeMillis();
//        allTopicScore = test.getAllTopicScore("로딩이 너무느려요 실행이안돼요 환불하고싶어요");
//        System.out.println("Takes mSec :" + (System.currentTimeMillis() - ts));
//        allTopicScore.forEach(System.out::println);
//        System.out.println("--------------------------------");
//
//        ts = System.currentTimeMillis();
//        allTopicScore = test.getAllTopicScore("처음으로 후기남겨봄 잘사용하고 있고 내가하루에 적당히먹엇는지 과하게먹엇는지 알수잇고 심심할때하면 다좋은데 열라느려속터짐");
//        System.out.println("Takes mSec :" + (System.currentTimeMillis() - ts));
//        allTopicScore.forEach(System.out::println);
//        System.out.println("--------------------------------");
//
//        ts = System.currentTimeMillis();
//        allTopicScore = test.getAllTopicScore("정말  필요하고 유용한 앱이네요^^활용도 최고입니다");
//        System.out.println("Takes mSec :" + (System.currentTimeMillis() - ts));
//        allTopicScore.forEach(System.out::println);
//        System.out.println("--------------------------------");
//
//        ts = System.currentTimeMillis();
//        allTopicScore = test.getAllTopicScore("십분도못하고자꾸튕기는데 뭐가문젠가요? 짜증나려하네요 한시간째렙7인데..키우면튕기고튕기고ㅡㅡ");
//        System.out.println("Takes mSec :" + (System.currentTimeMillis() - ts));
//        allTopicScore.forEach(System.out::println);
//        System.out.println("--------------------------------");


//        String text = "실행이 안되서 재 다운 받았는데 업테이트하라고해서클릭하니 항목을차을수없다고나오네요";
//        text = "업데이트가 안되네요";
//
//
//        KoreanTokenizerFactory tokenizerFactory = new KoreanTokenizerFactory() ;
//        Tokenizer tokenizer = tokenizerFactory.create(text);
//
//        String token = null ;
//        while(tokenizer.hasMoreTokens()) {
//            token = tokenizer.nextToken() ;
//
//            System.out.println("--> " + token);
//        }
//
//        System.out.println("EJTokenizer ----------------------");
//        Analyzer.parseJava(text).forEach(node -> {
//            System.out.println("==>" + node.morpheme().copy$default$1());
//        });



    }
}
