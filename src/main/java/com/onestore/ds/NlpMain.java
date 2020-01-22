package com.onestore.ds;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.KoreanTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;

/**
 * Created by a1000074 on 09/12/2019.
 */
public class NlpMain {
    private static Logger log = LoggerFactory.getLogger(NlpMain.class);

    public static void main(String...v) throws Exception {

        // Strip white space before and after for each line
        SentenceIterator iter = new LineSentenceIterator(new File("/Users/a1000074/IdeaProjects/ClusterDoc/sample/" +
                "negative_comment.txt"));
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t = new KoreanTokenizerFactory();
        /*
            CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
            So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
            Additionally it forces lower case for all tokens.
         */
        t.setTokenPreProcessor(new CommonPreprocessor());

//        TokenizerFactory tFac = new NGramTokenizerFactory(t, 2, 8);


        log.info("Building model....");
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(1)
                .iterations(1)
                .layerSize(100)
                .seed(42)
                .windowSize(16)
                .iterate(iter)
                .tokenizerFactory(t)
//                .tokenizerFactory(t)
                .workers(6)
                .build();

        log.info("Fitting Word2Vec model....");
        vec.fit();

        log.info("Writing word vectors to text file....");

        WordVectorSerializer.writeWordVectors(vec, "commentVechMap.txt");
        log.info("Job Completed ..");

//        log.info("Closest Words:");
        Collection<String> lst = vec.wordsNearest("삭제", 10);
        System.out.println(lst);

        // TSNE logic

//        DataTypeUtil.setDTypeForContext(DataType.DOUBLE);
//        List<String> cacheList = new ArrayList<>(); //cacheList is a dynamic array of strings used to hold all words
//
//        Pair<InMemoryLookupTable,VocabCache> vectors = WordVectorSerializer.loadTxt(new File("vectorMatchMap.txt"));
//
//        VocabCache cache = vectors.getSecond();
//        INDArray weights = vectors.getFirst().getSyn0();    //seperate weights of unique words into their own list
//
//        for(int i = 0; i < cache.numWords(); i++)   //seperate strings of words into their own list
//            cacheList.add(cache.wordAtIndex(i));
//
//
//        //STEP 3: build a dual-tree tsne to use later
//        log.info("Build model....");
//        BarnesHutTsne tsne = new BarnesHutTsne.Builder()
//                .setMaxIter(500).theta(0.5)
//                .normalize(false)
//                .learningRate(500)
//                .useAdaGrad(false)
////                .usePca(false)
//                .build();
//
//        //STEP 4: establish the tsne values and save them to a file
//        log.info("Store TSNE Coordinates for Plotting....");
//        String outputFile = "tsne-standard-coords.csv";
//        tsne.plot(weights,2,cacheList,outputFile);


    }
}
