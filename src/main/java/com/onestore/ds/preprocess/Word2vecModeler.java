package com.onestore.ds.preprocess;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by a1000074 on 03/01/2020.
 */
public class Word2vecModeler {

    private static Logger log = LoggerFactory.getLogger(Word2vecModeler.class);

    public static String dataLocalPath;

    public static void main(String ... v) throws Exception {
//        // Strip white space before and after for each line
//        org.deeplearning4j.text.sentenceiterator.SentenceIterator iter = new BasicLineIterator(
//                new File("/Users/a1000074/dev/temp-comment/latest_comment_5e6_20200107.txt"));
//        // Split on white spaces in the line to get words
////        TokenizerFactory t = new DefaultTokenizerFactory();
////        t.setTokenPreProcessor(new CommonPreprocessor());
//
//        TokenizerFactory t = new KoreanTokenizerFactory();
//        t.setTokenPreProcessor(new CommonPreprocessor());
//
//        // manual creation of VocabCache and WeightLookupTable usually isn't necessary
//        // but in this case we'll need them
//        InMemoryLookupCache cache = new InMemoryLookupCache();
//        WeightLookupTable<VocabWord> table = new InMemoryLookupTable.Builder<VocabWord>()
//                .vectorLength(100)
//                .useAdaGrad(false)
//                .cache(cache)
//                .lr(0.025f).build();
//
//        log.info("Building model....");
//        Word2Vec vec = new Word2Vec.Builder()
//                .minWordFrequency(5)
//                .iterations(1)
//                .epochs(1)
//                .layerSize(100)
//                .seed(42)
//                .windowSize(6)
//                .iterate(iter)
//                .tokenizerFactory(t)
//                .lookupTable(table)
//                .vocabCache(cache)
//
//                .build();
//
//        log.info("Fitting Word2Vec model....");
//        vec.fit();
//
//
//        Collection<String> lst = vec.wordsNearest("설치", 20);
//        System.out.println("Closest words to 'day' on 1st run: " + lst);
//
//        lst = vec.wordsNearest("쿠폰", 20);
//        System.out.println("Closest words to 'day' on 1st run: " + lst);
//
//        WordVectorSerializer.writeWord2VecModel(vec, new File("/Users/a1000074/dev/temp-comment/model/w2v_comment_5e6.txt"));
//
//        System.out.println("Job Completed");

        System.out.println("Active System ... ");
        loadW2v();

    }

    public static void printNearTokens(Word2Vec vec, String token, int topN) {
        Collection<String> lst = vec.wordsNearest(token, topN);

        lst.stream().forEach(nearToken -> {
            System.out.println(token + " --> " +nearToken + " : " + vec.similarity(token, nearToken));
        });


        System.out.println("Closest words to "  + token + " on 1st run: " + lst);
    }


    public static void loadW2v() throws IOException {
        Word2Vec vec = WordVectorSerializer.readWord2VecModel("/Users/a1000074/dev/temp-comment/model/w2v_comment_5e6.mdl");

        System.out.println("Sim -> " + vec.similarity("과금", "과금"));

        printNearTokens(vec, "안되", 40);
        printNearTokens(vec, "결제", 20);
        printNearTokens(vec, "설치", 20);
        printNearTokens(vec, "쿠폰", 20);
        printNearTokens(vec, "환불", 20);
        printNearTokens(vec, "과금", 20);
        printNearTokens(vec, "타격", 20);
        printNearTokens(vec, "그래픽", 20);
        printNearTokens(vec, "구글", 20);
        printNearTokens(vec, "다운로드", 20);
        printNearTokens(vec, "화면", 20);
        printNearTokens(vec, "오류", 20);
        printNearTokens(vec, "주세요", 20);


//        Collection<String> lst = vec.wordsNearest("설치", 20);
//        System.out.println("Closest words to '설치' on 1st run: " + lst);
//
//        lst = vec.wordsNearest("쿠폰", 20);
//        System.out.println("Closest words to '쿠폰' on 1st run: " + lst);
//
//        lst = vec.wordsNearest("환불", 20);
//        System.out.println("Closest words to '환불' on 1st run: " + lst);
//
//        lst = vec.wordsNearest("과금", 20);
//        System.out.println("Closest words to '과금' on 1st run: " + lst);
//
//        lst = vec.wordsNearest("타격", 20);
//        System.out.println("Closest words to '타격' on 1st run: " + lst);
//
//        lst = vec.wordsNearest("그래픽", 20);
//        System.out.println("Closest words to '그래픽' on 1st run: " + lst);
//
//        lst = vec.wordsNearest("무", 20);
//        System.out.println("Closest words to '무과금' on 1st run: " + lst);
//
//        lst = vec.wordsNearest("구글", 20);
//        System.out.println("Closest words to '구글' on 1st run: " + lst);
//
//        lst = vec.wordsNearest("스토어", 20);
//        System.out.println("Closest words to '원스토어' on 1st run: " + lst);
//
//        lst = vec.wordsNearest("다운로드", 20);
//        System.out.println("Closest words to '다운로드' on 1st run: " + lst);
//
//        lst = vec.wordsNearest("화면", 20);
//        System.out.println("Closest words to '화면' on 1st run: " + lst);
//
//        lst = vec.wordsNearest("오류", 20);
//        System.out.println("Closest words to '화면' on 1st run: " + lst);

    }

}
