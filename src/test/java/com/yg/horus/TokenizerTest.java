package com.yg.horus;

import org.deeplearning4j.text.tokenization.tokenizer.KoreanTokenizer;

import java.io.*;

/**
 * Created by a1000074 on 01/11/2019.
 */
public class TokenizerTest {

    public static void main(String ... v) {
        KoreanTokenizer kTokenizer = new KoreanTokenizer("모바일버스");

        int i = 0;
        while(kTokenizer.hasMoreTokens()) {
            System.out.println(i++ + "\t" + kTokenizer.nextToken());
        }

//        reTokenizing();
    }

    public static void reTokenizing() {
        String targetFile = "/Users/a1000074/Downloads/map_gp_one_apps.csv" ;
        try(
                FileWriter fw = new FileWriter("/Users/a1000074/Downloads/map_gp_one_apps2.csv") ;
                BufferedReader br = new BufferedReader(new FileReader(targetFile));
        ) {
            String line = null ;
            while((line = br.readLine()) != null) {
                line = line.replaceAll("\\.", " ");
                fw.write(line + "\n");
            }

            fw.flush();
            System.out.println("Converting completed ..");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
