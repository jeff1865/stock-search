package com.onestore.paragraph;

import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.KoreanTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.primitives.Pair;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by a1000074 on 30/01/2020.
 */
public class P2vClassifier {
    private ParagraphVectors paragraphVectors;
    private LabelAwareIterator iterator;
    private TokenizerFactory tokenizerFactory;


//    public static String dataLocalPath = "/Users/a1000074/dev/sample/dl4j-examples-data/dl4j-examples/nlp/";

    public P2vClassifier() {
        tokenizerFactory = new KoreanTokenizerFactory() ;
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
        ;
    }

    public void loadParagraphVectors(String modelFile) throws IOException {
        long ts = System.currentTimeMillis() ;
        this.paragraphVectors = WordVectorSerializer.readParagraphVectors(new File(modelFile));
        System.out.println("---> Load model completed for " + (System.currentTimeMillis() - ts));
    }


    public static void main(String ... v) {
        P2vClassifier test = new P2vClassifier() ;

        try {
            test.loadParagraphVectors("/Users/a1000074/dev/temp-comment/model/p2v_comment_voc_train_1e3_clean.mdl");
//            test.loadParagraphVectors("/Users/a1000074/dev/temp-comment/sample/p2v_voc_comment_sample_14e3.mdl");
//            test.makeParagraphVectors();

            List<String> labels = Arrays.asList("voc");

            test.checkSentences(new File("/Users/a1000074/dev/temp-comment/latest_comment_5e6_20200107.txt"), 10000, labels);

//            test.checkSentence("저장하기 누르면 다른이름으로 저장됩니다 넘 불편하네요", "neg", labels);
//            test.checkSentence("게임을 가장한 도박장. 버그도 너무 많은데 운영도 발로 함. 확률조작 뽑기로 과금유도만 하는 개돼지겜. 돈마블이 돈에 미쳐서 만든 겜ㅋ", "neg", labels);
//
//            test.checkSentence("방금 결제 했는데 아이템이 안들어 왔어요 환불해주세요.", "voc", labels);
//            test.checkSentence("게임이 실행이 되지않고 계속 검은화면 입니다. 확인해주세요.", "voc", labels);
//            test.checkSentence("계속 점검중이라 뜨는데 뭐지.", "voc", labels);
//            test.checkSentence("업데이트 해두 실행이 안되요 답장좀", "voc", labels);
//
//            test.checkSentence("내차팔기에서 유명한 헤이딜러! 가입도 없고 이용방법도 쉽고 무엇보다 딜러 견적제시가 빨라서 좋았스요", "pos", labels);
//            test.checkSentence("이런 게임이 나왔었다니 이걸 왜 이제야 알았지ㅠㅠ 재밌네요", "pos", labels);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void makeParagraphVectors() throws Exception {
//        File resource = new File("/Users/a1000074/IdeaProjects/ClusterDoc/sample/f_voc_comment.txt");
        File resource = new File("/Users/a1000074/dev/temp-comment/train/comment_voc_train_1000.txt");

        // build a iterator for our dataset
//        iterator = new FileLabelAwareIterator.Builder()
//                .addSourceFolder(resource)
//                .build();
        FileLineSentenceLabelAwareIterator iterator1 = new FileLineSentenceLabelAwareIterator(resource, "voc") ;
        iterator1.load();

        this.iterator = iterator1;

        System.out.println("Labels -> " + iterator.getLabelsSource().getLabels());

//        tokenizerFactory = new DefaultTokenizerFactory();
//        tokenizerFactory = new KoreanTokenizerFactory() ;
//        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        // ParagraphVectors training configuration
        paragraphVectors = new ParagraphVectors.Builder()
                .windowSize(7)
                .learningRate(0.025)
                .minLearningRate(0.001)
                .batchSize(1000)
                .epochs(20)
                .labelsSource(iterator.getLabelsSource())   // Added manually
                .iterate(iterator)
                .trainWordVectors(true)
                .tokenizerFactory(tokenizerFactory)
                .build();

        // Start model training
        paragraphVectors.fit();

        System.out.println("Writing model ..");
        WordVectorSerializer.writeParagraphVectors(paragraphVectors, "/Users/a1000074/dev/temp-comment/model/p2v_comment_voc_train_1e3_clean.mdl");
    }

    public void checkSentences(File lineTxtFile, long limit, List<String> checkModelLabels) {
        MeansBuilder meansBuilder = new MeansBuilder(
                (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable(),
                tokenizerFactory);
//        LabelSeeker seeker = new LabelSeeker(iterator.getLabelsSource().getLabels(),
//                (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable());

        LabelSeeker seeker = new LabelSeeker(checkModelLabels,
                (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable());

        LabelledDocument document ;
        INDArray documentAsCentroid ;
        List<Pair<String, Double>> scores ;
        String latestLine = null;

        try(BufferedReader br = new BufferedReader(new FileReader(lineTxtFile))) {
            String line = null ;
            long cnt = 0;
            while((line = br.readLine()) != null) {
                cnt ++ ;
                if(limit-- < 0) break ;
                latestLine = line ;
                if(line == null || line.length() <= 1) {
                    continue;
                }

                try {

                    document = new LabelledDocument();
                    document.setContent(line);
                    documentAsCentroid = meansBuilder.documentAsVector(document);
                    scores = seeker.getScores(documentAsCentroid);

                    for (Pair<String, Double> score : scores) {
                        if (score.getSecond() > 0.3) {
                            System.out.println(cnt + "\t" + score.getFirst() + ": " + score.getSecond() + " --> " + line);
                        }
                    }
                }catch (Exception e) {
                    System.out.println("Invalid Comment -> " + line + " .. #" + cnt);
                    continue;
                }

            }

            System.out.println("Check completed ..");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Line >" + latestLine);
        }
    }


    public void checkSentence(String sentence, String label, List<String> modelLabels) {
        MeansBuilder meansBuilder = new MeansBuilder(
                (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable(),
                tokenizerFactory);
//        LabelSeeker seeker = new LabelSeeker(iterator.getLabelsSource().getLabels(),
//                (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable());

        LabelSeeker seeker = new LabelSeeker(modelLabels,
                (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable());

        LabelledDocument document = new LabelledDocument();
        document.setContent(sentence);

        INDArray documentAsCentroid = meansBuilder.documentAsVector(document);
        List<Pair<String, Double>> scores = seeker.getScores(documentAsCentroid);

        for (Pair<String, Double> score : scores) {
            System.out.println(label + "/" + score.getFirst() + ": " + score.getSecond() + " --> " + sentence);
        }

        System.out.println("---------------------");
    }
}
