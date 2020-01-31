package com.onestore.paragraph;


import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.text.documentiterator.FileLabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.primitives.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by a1000074 on 21/01/2020.
 */
public class ParagVectorsClassifier {
    ParagraphVectors paragraphVectors;
    LabelAwareIterator iterator;
    TokenizerFactory tokenizerFactory;

    private static final Logger log = LoggerFactory.getLogger(ParagVectorsClassifier.class);

    public static String dataLocalPath = "/Users/a1000074/dev/sample/dl4j-examples-data/dl4j-examples/nlp/";


    public static void main(String[] args) throws Exception {

//        dataLocalPath = DownloaderUtility.NLPDATA.Download();
        ParagVectorsClassifier app = new ParagVectorsClassifier();
        app.makeParagraphVectors();
        app.checkUnlabeledData();
        /*
                Your output should be like this:
                Document 'health' falls into the following categories:
                    health: 0.29721372296220205
                    science: 0.011684473733853906
                    finance: -0.14755302887323793
                Document 'finance' falls into the following categories:
                    health: -0.17290237675941766
                    science: -0.09579267574606627
                    finance: 0.4460859189453788
                    so,now we know categories for yet unseen documents
         */
    }

    void makeParagraphVectors() throws Exception {
        File resource = new File(dataLocalPath, "paravec/labeled");

        // build a iterator for our dataset
        iterator = new FileLabelAwareIterator.Builder()
                .addSourceFolder(resource)
                .build();

        System.out.println("Labels -> " + iterator.getLabelsSource().getLabels());

        tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        // ParagraphVectors training configuration
        paragraphVectors = new ParagraphVectors.Builder()
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
    }

    void checkUnlabeledData() throws IOException {
      /*
      At this point we assume that we have model built and we can check
      which categories our unlabeled document falls into.
      So we'll start loading our unlabeled documents and checking them
     */
        File unClassifiedResource = new File(dataLocalPath, "paravec/unlabeled");
        System.out.println("UnLabeled > " + unClassifiedResource.getAbsolutePath());
        FileLabelAwareIterator unClassifiedIterator = new FileLabelAwareIterator.Builder()
                .addSourceFolder(unClassifiedResource)
                .build();

        System.out.println("UnLabeledSource -> " + unClassifiedIterator.getLabelsSource().getLabels());

     /*
      Now we'll iterate over unlabeled data, and check which label it could be assigned to
      Please note: for many domains it's normal to have 1 document fall into few labels at once,
      with different "weight" for each.
     */
        MeansBuilder meansBuilder = new MeansBuilder(
                (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable(),
                tokenizerFactory);
        LabelSeeker seeker = new LabelSeeker(iterator.getLabelsSource().getLabels(),
                (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable());


        System.out.println("HasNext -> " + unClassifiedIterator.hasNextDocument());

        while (unClassifiedIterator.hasNextDocument()) {
            LabelledDocument document = unClassifiedIterator.nextDocument();
            INDArray documentAsCentroid = meansBuilder.documentAsVector(document);
            List<Pair<String, Double>> scores = seeker.getScores(documentAsCentroid);

         /*
          please note, document.getLabel() is used just to show which document we're looking at now,
          as a substitute for printing out the whole document name.
          So, labels on these two documents are used like titles,
          just to visualize our classification done properly
         */
            System.out.println("Document '" + document.getLabels() + "' falls into the following categories: ");
            for (Pair<String, Double> score : scores) {
                System.out.println("        " + score.getFirst() + ": " + score.getSecond());
            }
        }

    }
}
