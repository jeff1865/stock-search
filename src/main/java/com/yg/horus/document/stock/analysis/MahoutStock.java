package com.yg.horus.document.stock.analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by 1002000 on 2018. 8. 27..
 */
public class MahoutStock {

    public static void preProcessing() {
        try(
            BufferedReader br = new BufferedReader(new FileReader("/Users/1002000/dev/ai/stock/KOSPI.csv"));
            FileWriter fw = new FileWriter("/Users/1002000/dev/ai/stock/KOSPI2.csv");
        ) {
            br.lines().forEach(line -> {
                String[] tokens = line.split(",");
                // Date,Open,High,Low,Close,Adj Close,Volume
                if(tokens.length != 7) return ;

                StringBuffer sb = new StringBuffer();
                sb.append(tokens[0]).append(",");
                sb.append(tokens[1]).append(",");
                sb.append(tokens[2]).append(",");
                sb.append(tokens[3]).append(",");
                sb.append(tokens[4]).append(",");
                sb.append(tokens[6]).append(",");
                sb.append(tokens[5]);

                try {
                    fw.write(sb.toString() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            System.out.println("File Processing Completed ..") ;
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String ... v) {
        System.out.println("Active System ..");
        preProcessing();
    }
}
