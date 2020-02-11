package com.onestore.ds;

import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.deeplearning4j.iterator.LabeledSentenceProvider;
import org.deeplearning4j.iterator.provider.FileLabeledSentenceProvider;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.GlobalPoolingLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.PoolingType;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.InvocationType;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.text.tokenization.tokenizerfactory.KoreanTokenizerFactory;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by a1000074 on 06/01/2020.
 */
public class CnnCommmentClassification {

    private static final String DATA_PATH = "/Users/a1000074/dev/data/comment_sample2";
    private static final String WORD_VECTORS_PATH = "/Users/a1000074/dev/data/comment_sample2/model/comment_1e5_model.mdl";

    private DataSetIterator trainIter ;
    private DataSetIterator testIter ;

    private ComputationGraph net ;

    public CnnCommmentClassification() {
        ;
    }

    public void load() {
        int batchSize = 32;
        int truncateReviewsToLength = 256;  //Truncate reviews with length (# words) greater than this
        Random rng = new Random(12345); //For shuffling repeatability
        long ts = System.currentTimeMillis() ;
        WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File(WORD_VECTORS_PATH));
        this.testIter = getDataSetIterator(true, wordVectors, batchSize, truncateReviewsToLength, rng);

        System.out.println("Load Completed #1 for " + (System.currentTimeMillis() - ts));

        ts = System.currentTimeMillis() ;
        try {
//            net = ComputationGraph.load(new File("/Users/a1000074/dev/data/comment_sample2/fin_model/cnnClass3a.mdl"), true) ;
            net = ComputationGraph.load(new File("/Users/a1000074/dev/data/comment_sample2/fin_model/cnnClass3b.mdl"), true) ;
            System.out.println("Load Completed #2 for " + (System.currentTimeMillis() - ts));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void train() {
        //Basic configuration
        int batchSize = 32;
        int vectorSize = 100;               //Size of the word vectors. 300 in the Google News model (ori : 300)
        int nEpochs = 1;                    //Number of epochs (full passes of training data) to train on
        int truncateReviewsToLength = 256;  //Truncate reviews with length (# words) greater than this

        int cnnLayerFeatureMaps = 40;      //Number of feature maps / channels / depth for each CNN layer (ori: 100)
        PoolingType globalPoolingType = PoolingType.MAX;
        Random rng = new Random(12345); //For shuffling repeatability

        //Set up the network configuration. Note that we have multiple convolution layers, each wih filter
        //widths of 3, 4 and 5 as per Kim (2014) paper.

        Nd4j.getMemoryManager().setAutoGcWindow(5000);

        ComputationGraphConfiguration config = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.RELU)
                .activation(Activation.LEAKYRELU)
                .updater(new Adam(0.01))
                .convolutionMode(ConvolutionMode.Same)      //This is important so we can 'stack' the results later
                .l2(0.0001)
                .graphBuilder()
                .addInputs("input")
                .addLayer("cnn3", new ConvolutionLayer.Builder()
                        .kernelSize(3,vectorSize)
                        .stride(1,vectorSize)
                        .nOut(cnnLayerFeatureMaps)
                        .build(), "input")
                .addLayer("cnn4", new ConvolutionLayer.Builder()
                        .kernelSize(4,vectorSize)
                        .stride(1,vectorSize)
                        .nOut(cnnLayerFeatureMaps)
                        .build(), "input")
                .addLayer("cnn5", new ConvolutionLayer.Builder()
                        .kernelSize(5,vectorSize)
                        .stride(1,vectorSize)
                        .nOut(cnnLayerFeatureMaps)
                        .build(), "input")
                //MergeVertex performs depth concatenation on activations: 3x[minibatch,100,length,300] to 1x[minibatch,300,length,300]
                .addVertex("merge", new MergeVertex(), "cnn3", "cnn4", "cnn5")
                //Global pooling: pool over x/y locations (dimensions 2 and 3): Activations [minibatch,300,length,300] to [minibatch, 300]
                .addLayer("globalPool", new GlobalPoolingLayer.Builder()
                        .poolingType(globalPoolingType)
                        .dropOut(0.5)
                        .build(), "merge")
                .addLayer("out", new OutputLayer.Builder()
                        .lossFunction(LossFunctions.LossFunction.MCXENT)
                        .activation(Activation.SOFTMAX)
                        .nOut(1)    //2 classes: positive or negative   // CHD
                        .build(), "globalPool")
                .setOutputs("out")
                //Input has shape [minibatch, channels=1, length=1 to 256, 300]
                .setInputTypes(InputType.convolutional(truncateReviewsToLength, vectorSize, 1))
                .build();

//        ComputationGraph net = new ComputationGraph(config);
        net = new ComputationGraph(config);
        net.init();

        System.out.println("Number of parameters by layer:");
        for(Layer l : net.getLayers() ){
            System.out.println("\t" + l.conf().getLayer().getLayerName() + "\t" + l.numParams());
        }

        System.out.println("Loading word vectors and creating DataSetIterators");
        WordVectors wordVectors = WordVectorSerializer.loadStaticModel(new File(WORD_VECTORS_PATH));    // Load genModel
//        this.trainIter = getDataSetIterator(true, wordVectors, batchSize, truncateReviewsToLength, rng);
//        this.testIter = getDataSetIterator(false, wordVectors, batchSize, truncateReviewsToLength, rng);
        this.trainIter = getVocDataSetIterator(true, wordVectors, batchSize, truncateReviewsToLength, rng);
        this.testIter = getVocDataSetIterator(false, wordVectors, batchSize, truncateReviewsToLength, rng);

        System.out.println("Starting training");
        net.setListeners(new ScoreIterationListener(100), new EvaluativeListener(testIter, 1, InvocationType.EPOCH_END));
        net.fit(trainIter, nEpochs);

        try {
            net.save(new File("/Users/a1000074/dev/data/comment_sample2/fin_model/cnnClass1VocOnly.mdl"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Successfully training completed ..");
    }

    public void anlayzePolarity(String comment) {
        long ts = System.currentTimeMillis() ;
        INDArray featuresFirstNegative = ((CnnSentenceDataSetIterator)testIter).loadSingleSentence(comment);

        INDArray predictionsFirstNegative = net.outputSingle(featuresFirstNegative);
        List<String> labels = testIter.getLabels();

        System.out.println("\n\nPredictions for review:\n" + comment);
        for( int i=0; i<labels.size(); i++ ){
            System.out.println("P(" + labels.get(i) + ") = " + predictionsFirstNegative.getDouble(i));
        }

        System.out.println("------------------>" + (System.currentTimeMillis() - ts)) ;
    }

    @Deprecated
    private DataSetIterator getVocDataSetIterator(boolean isTraining, WordVectors wordVectors, int minibatchSize,
                                               int maxSentenceLength, Random rng ){
//        String path = FilenameUtils.concat("/Users/a1000074/dev/temp-comment/train", (isTraining ? "train/" : "test/"));

        String path = "";
        if(isTraining) {
            path = "/Users/a1000074/dev/temp-comment/train/voc/train";
        } else {
            path = "/Users/a1000074/dev/temp-comment/train/voc/test";
        }

        String vocBaseDir = path;

        File fileVoc = new File(vocBaseDir);    // CHD

        Map<String,List<File>> reviewFilesMap = new HashMap<>();
//        reviewFilesMap.put("Positive", Arrays.asList(Objects.requireNonNull(filePositive.listFiles())));
//        reviewFilesMap.put("Negative", Arrays.asList(Objects.requireNonNull(fileNegative.listFiles())));
        reviewFilesMap.put("Voc", Arrays.asList(Objects.requireNonNull(fileVoc.listFiles())));  // CHD

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
            System.out.println("DSet ->" +ds.asList().toArray());
        });

        return build;
    }

    private DataSetIterator getDataSetIterator(boolean isTraining, WordVectors wordVectors, int minibatchSize,
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
            System.out.println("DSet ->" +ds.asList().toArray());
        });

        return build;
    }

    public static void main(String ... v) {
        System.out.println("Active System ..") ;

        CnnCommmentClassification test = new CnnCommmentClassification() ;
        test.train();
//        test.load();

        test.anlayzePolarity("진짜 좋아요 강추");
        test.anlayzePolarity("과금 유도가 심하여 바로 지웠다");
        test.anlayzePolarity("재미는 있는데 잠시하다 접종할거같음. 버그나 문제 있어서 신고해도 운영자 처리 안해줌 공식까페 들가서 확인해보고 하세요 현질유도 넘 심하고 현질안하면 클수가 없네요");
        test.anlayzePolarity("운영자가 일을 안함");
        test.anlayzePolarity("중국 양산형 vip");
        test.anlayzePolarity("플레이 스토어로 하다 넘어옴. 신세계 체험 중...");
        //

        test.anlayzePolarity("시작한지 세달만에 나름 고인물이라 할수있는 상태가 되었다. 지금까진 무과금이었는데 이번달 처음으로 월정액 구매. 한달 5천원 미만의 극소과금으로도 스킨도 사고 할거 다 할수있네 지금껏 해본 모든 모발겜 중에서 가장 정상적이고 게임다운 게임이다");
        test.anlayzePolarity("버그 천지임. 허지만 운영진 마인드가 갓임. 고로 갓겜. 이대로만 운영해주오 ( 물론 돈 버는 만큼 버그는 잡아줘요)");
        test.anlayzePolarity("고인물 천지");
        test.anlayzePolarity("짱 재미남");

        test.anlayzePolarity("쿠폰 주세요");
        test.anlayzePolarity("다운로드가 안되요");
        test.anlayzePolarity("화면이 멈춰있고 실생도 안되요");
        test.anlayzePolarity("결제했는데 아이템은 왜 안주는거임?");

        test.anlayzePolarity("빨리복구하세요");
        test.anlayzePolarity("설치중에서 10분넘어가도 안되고 취소도안되는데 어쩌자는건가요;;용량만 크고 광고만해대더니 이렇게 형편없을수가 있나요;;");
        test.anlayzePolarity("설치 왤캐안되!!!?");
        test.anlayzePolarity("게임 실행이 안되네요 하라는건지 ");
        test.anlayzePolarity("업데이트 이후로 지속적으로 튕깁니다. 삭제 후 다시 받으려고 재설치중에도 튕깁니다. 빠른 해결 부탁드립니다");
        test.anlayzePolarity("22000원 결제했는데~ 뭐죠? 해결해주세요~ 아니면환불해주세여~ㅠㅠ");
        test.anlayzePolarity("계좌인증에서 진행안되요 벌써40분째 막혀있음");


        System.out.println("Finished ..");
    }
}
