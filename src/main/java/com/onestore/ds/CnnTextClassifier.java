package com.onestore.ds;

import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.deeplearning4j.iterator.LabeledSentenceProvider;
import org.deeplearning4j.iterator.provider.FileLabeledSentenceProvider;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.conf.layers.PoolingType;
import org.deeplearning4j.text.tokenization.tokenizerfactory.KoreanTokenizerFactory;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.File;
import java.util.*;

/**
 * Created by a1000074 on 10/02/2020.
 */
public class CnnTextClassifier {
    private static final String DATA_PATH = "/Users/a1000074/dev/data/comment_sample2";
    private static final String WORD_VECTORS_PATH = "/Users/a1000074/dev/data/comment_sample2/model/comment_1e5_model.mdl";

    public CnnTextClassifier() {
        ;
    }

    public static DataSetIterator getDataSetIterator(boolean isTraining, WordVectors wordVectors, int minibatchSize,
                                               int maxSentenceLength, Random rng ){
        String path = FilenameUtils.concat(DATA_PATH, (isTraining ? "train/" : "test/"));
        String positiveBaseDir = FilenameUtils.concat(path, "pos");
        String negativeBaseDir = FilenameUtils.concat(path, "neg");
//        String vocBaseDir = FilenameUtils.concat(path, "voc");    // CHD

        File filePositive = new File(positiveBaseDir);
        File fileNegative = new File(negativeBaseDir);
//        File fileVoc = new File(vocBaseDir);    // CHD

        Map<String,List<File>> reviewFilesMap = new HashMap<>();
        reviewFilesMap.put("Positive", Arrays.asList(Objects.requireNonNull(filePositive.listFiles())));
        reviewFilesMap.put("Negative", Arrays.asList(Objects.requireNonNull(fileNegative.listFiles())));
//        reviewFilesMap.put("Voc", Arrays.asList(Objects.requireNonNull(fileVoc.listFiles())));  // CHD

        LabeledSentenceProvider sentenceProvider = new FileLabeledSentenceProvider(reviewFilesMap, rng);

        CnnSentenceDataSetIterator build = new CnnSentenceDataSetIterator.Builder(CnnSentenceDataSetIterator.Format.CNN2D)
                .tokenizerFactory(new KoreanTokenizerFactory())
                .sentenceProvider(sentenceProvider)     // MDFD
                .wordVectors(wordVectors)
                .minibatchSize(minibatchSize)
                .maxSentenceLength(maxSentenceLength)
                .useNormalizedWordVectors(false)
                .build();

        System.out.println("Craeted Labels ->" + build.getLabels());

        build.forEachRemaining(ds -> {
            System.out.println("DSet ->" +ds.asList());
        });

        return build;
    }

    public static void main(String ... v) {
        System.out.println("Activate ..");
        int batchSize = 32;
        int vectorSize = 100;               //Size of the word vectors. 300 in the Google News model (ori : 300)
        int nEpochs = 1;                    //Number of epochs (full passes of training data) to train on
        int truncateReviewsToLength = 256;  //Truncate reviews with length (# words) greater than this

        int cnnLayerFeatureMaps = 40;      //Number of feature maps / channels / depth for each CNN layer (ori: 100)
        PoolingType globalPoolingType = PoolingType.MAX;
        Random rng = new Random(12345); //For shuffling repeatability

        WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File(WORD_VECTORS_PATH));

        DataSetIterator dsi = getDataSetIterator(true, wordVectors, batchSize, truncateReviewsToLength, rng);
        System.out.println("-----");
        dsi.getLabels().forEach(label -> {
            System.out.println(new String(label.getBytes())) ;
        });

    }
}
