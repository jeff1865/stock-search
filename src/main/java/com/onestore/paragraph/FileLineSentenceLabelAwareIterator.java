package com.onestore.paragraph;

import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.documentiterator.LabelsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by a1000074 on 30/01/2020.
 */
public class FileLineSentenceLabelAwareIterator implements LabelAwareIterator {
    protected static final Logger log = LoggerFactory.getLogger(FileLineSentenceLabelAwareIterator.class);
    private LabelsSource generator;

    private List<String> sentenceLines = new ArrayList<>();

    private File sourceFile = null ;
    private String fixedLableString = null ;

    private int idx = 0;

    public FileLineSentenceLabelAwareIterator(File sentenceLineFile, String fixedLabel) {
        this.sourceFile = sentenceLineFile;
        this.fixedLableString = fixedLabel ;
    }

    public void load() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.sourceFile))) {
           br.lines().forEach(line -> {
                sentenceLines.add(line) ;
           });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean hasNextDocument() {
        return sentenceLines.size() > idx;
    }

    @Override
    public LabelledDocument nextDocument() {
        String line = this.sentenceLines.get(idx++);

        LabelledDocument document = new LabelledDocument();
        document.setId(String.valueOf(this.idx));
        document.addLabel(this.fixedLableString);
        document.setContent(line);

        return document;
    }

    @Override
    public void reset() {
        this.idx = 0;
    }

    @Override
    public LabelsSource getLabelsSource() {
        return new LabelsSource(Arrays.asList(this.fixedLableString));
    }

    @Override
    public void shutdown() {
        this.idx = this.sentenceLines.size();
    }

    @Override
    public boolean hasNext() {
        return this.hasNextDocument();
    }

    @Override
    public LabelledDocument next() {
        return this.nextDocument();
    }
}
