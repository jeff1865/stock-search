package com.yg.horus.document.nlp;

import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.plot.BarnesHutTsne;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.NGramTokenizer;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.KoreanTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.NGramTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.primitives.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by agibsonccc on 10/9/14.
 *
 * Neural net that processes text into wordvectors. See below url for an in-depth explanation.
 * https://deeplearning4j.org/word2vec.html
 */
public class Word2vecSample {

    private static Logger log = LoggerFactory.getLogger(Word2vecSample.class);

    public static void main(String[] args) throws Exception {

        // Strip white space before and after for each line
        SentenceIterator iter = new LineSentenceIterator(new File("/Users/a1000074/Downloads/map_gp_one_apps2.csv"));
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
                .windowSize(15)
                .iterate(iter)
//                .tokenizerFactory(t)
                .tokenizerFactory(t)
                .workers(6)
                .build();

        log.info("Fitting Word2Vec model....");
        vec.fit();

        log.info("Writing word vectors to text file....");

        WordVectorSerializer.writeWordVectors(vec, "vectorMatchMap.txt");
        log.info("Job Completed ..");

//        log.info("Closest Words:");
//        Collection<String> lst = vec.wordsNearest("way", 10);
//        System.out.println(lst);

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

    public static void visualize() {

    }
}