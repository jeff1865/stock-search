package com.onestore.ds.preprocess;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.documentiterator.LabelsSource;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.KoreanTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.*;
import java.util.Collection;
import java.util.Hashtable;

/**
 * Created by a1000074 on 10/01/2020.
 */
public class Doc2vecProcessor {

    public static void buildModel() {
        //        File file = new File("sample/oney_comment_1E5.csv");
//        File file = new File("/Users/a1000074/dev/temp-comment/latest_comment_5e6_20200107.txt");
        File file = new File("/Users/a1000074/dev/temp-comment/sample_comment10000.txt");
        try {
            Hashtable<String, String> sentenceMap = getSentenceMap(file);

            SentenceIterator iter = new BasicLineIterator(file);

            AbstractCache<VocabWord> cache = new AbstractCache<>();

            TokenizerFactory t = new KoreanTokenizerFactory();
//            t.setTokenPreProcessor(new CommonPreprocessor());

            LabelsSource source = new LabelsSource("DOC_");

            ParagraphVectors vec = new ParagraphVectors.Builder()
                    .minWordFrequency(2)
                    .iterations(5)
                    .epochs(1)
                    .layerSize(100)
                    .learningRate(0.025)
                    .labelsSource(source)
                    .windowSize(5)
                    .iterate(iter)
//                    .trainWordVectors(false)
                    .vocabCache(cache)
                    .tokenizerFactory(t)
//                    .sampling(0)
                    .build();

            vec.fit();

            System.out.println("Writing Paragraph Model to file ..");
            WordVectorSerializer.writeParagraphVectors(vec, "/Users/a1000074/dev/temp-comment/sample_comment10000.mdl");
            System.out.println("Writing model completed ..");

            Collection<String> nearLabels = vec.nearestLabels("왜ㅋㅋㅋ내컨설 지멋대로 삭제하심??ㅋㅋㅋ 걍 얼굴 셀카올린건데  일제대로하는거맞죠?ㅋㅋ", 20);

            nearLabels.forEach(label -> {
                System.out.println("-->" + label + " : " + sentenceMap.get(label));
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String ... v) {
        System.out.println("Active System ..");

//        buildModel();

        try {
            long ts = System.currentTimeMillis();
            ParagraphVectors vec = WordVectorSerializer.readParagraphVectors(new File("/Users/a1000074/dev/temp-comment/model_comment_5e6.mdl"));
//            ParagraphVectors vec = WordVectorSerializer.readParagraphVectors(new File("/Users/a1000074/dev/temp-comment/sample_comment10000.mdl"));
            vec.setTokenizerFactory(new KoreanTokenizerFactory());
            System.out.println("Read completed for " + (System.currentTimeMillis() - ts) + "msec");

            Collection<String> labels = vec.nearestLabels("왜ㅋㅋㅋ내컨설 지멋대로 삭제하심??ㅋㅋㅋ 걍 얼굴 셀카올린건데  일제대로하는거맞죠?ㅋㅋ", 5);
            System.out.println("Nearest Labels -> " + labels);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static Hashtable<String, String> getSentenceMap(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));

        Hashtable<String, String> htVec = new Hashtable<>();

        String line = null ;

        int i = 0;
        while((line = br.readLine()) != null) {
            htVec.put("DOC_" + i++, line) ;
        }

        return htVec ;
    }

}
