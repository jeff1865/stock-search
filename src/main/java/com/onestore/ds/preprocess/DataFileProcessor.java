package com.onestore.ds.preprocess;

import au.com.bytecode.opencsv.CSVReader;

import java.io.*;

/**
 * Created by a1000074 on 06/01/2020.
 */
public class DataFileProcessor {

    public static void main(String ... v) {
        System.out.println("Active ..");

//        try {
//            FileWriter fw = new FileWriter(new File("sample/oney_comment_1E5.csv"));
//
//
//            CSVReader reader = new CSVReader(new FileReader("sample/comment_sam100000_without_book.csv")) ;
//            String[] tokens ;
//            long cnt = 0;
//            while((tokens = reader.readNext()) != null) {
//                if(tokens.length > 2) {
//                    System.out.println(cnt++ + "\t" + tokens[1]);
//
//                    String comment = tokens[1] ;
//                    comment = comment.replaceAll("\n", " ") ;
//                    fw.write(comment + "\n");
//
//                    System.out.println(cnt + "\t -> " + comment);
//                } else {
//                    System.out.println("Invalid :" + cnt);
//                }
//
////                if(cnt > 100) break ;
//            }
//
//            fw.flush();
//            fw.close();;
//
//            reader.close();
//
//            System.out.println("Successfully completed ..");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        processingOneColumnCsv2txt();
        processLine2File();
    }

    public static void processLine2File() {
        File sentenceLinesFile = new File("/Users/a1000074/dev/temp-comment/train/voc/train/cmt_voc_train500.txt");

        BufferedReader br = null;
        int lineNo = 0;
        try {
            br = new BufferedReader(new FileReader(sentenceLinesFile));
            String line = null;

            while((line = br.readLine()) != null) {
                System.out.println("Processing : " + line);
                FileWriter fw = new FileWriter("/Users/a1000074/dev/temp-comment/train/voc/train/sentence_" + lineNo++ + "");
                fw.write(line);
                fw.flush();
                fw.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public static void processingOneColumnCsv2txt() {
        System.out.println("Active ..");

        try {
            FileWriter fw = new FileWriter(new File("/Users/a1000074/dev/temp-comment/latest_comment_5e6_20200107.txt"));

            CSVReader reader = new CSVReader(new FileReader("/Users/a1000074/dev/temp-comment/latest_comment_5e6.csv")) ;
            String[] tokens ;
            long cnt = 0;
            while((tokens = reader.readNext()) != null) {
                if(tokens.length == 1) {

                    String comment = tokens[0] ;
                    comment = comment.replaceAll("\n", " ") ;
                    fw.write(comment + "\n");

                    System.out.println(cnt + "\t -> " + comment);
                } else {
                    System.out.println("Invalid :" + cnt);
                }

            }

            fw.flush();
            fw.close();;

            reader.close();

            System.out.println("Successfully completed ..");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
