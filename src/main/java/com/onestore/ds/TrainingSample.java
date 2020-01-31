package com.onestore.ds;

import au.com.bytecode.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


/**
 * Created by a1000074 on 18/12/2019.
 */
public class TrainingSample {

    public static void sampleFiltering() {
        System.out.println("Active System ..");
        String filename = "/Users/a1000074/IdeaProjects/ClusterDoc/sample/clustered_comment.csv" ;
        try {
            CSVReader reader = new CSVReader(new FileReader(filename), ',', '"');
            PrintWriter pw = new PrintWriter(new File("sample/temp_comment_voc" + System.currentTimeMillis() + ".txt")) ;

            int i = 0;
            String[] tokens ;
            while((tokens = reader.readNext()) != null) {
                System.out.println(i++ + "\t-->" + tokens[0] + "/" + tokens[2]);
                if(tokens[0].startsWith("v")) {
                    pw.println(tokens[2]);
                }
            }

            reader.close();

            pw.flush();
            pw.close();

            System.out.println("Writing file completed ..");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ;
        }

    }

    public static void filterComment() {
        String[] posTokens = {"재밌", "존잼", "좋아", "좋았", "쉽고", "만족", "너무", "감사", "가즈아", "설쳤", "대박", "완성도 높",
            "편하", "좋습", "추천", "무과금", "해자", "굿", "괜찮", "좋은", "잘 봤", "잘봤", "꿀잼", "훈훈", "감동"};

        String[] negTokens = {"아쉬", "업데이트 좀", "패치", "현질", "부담", "멈추", "갈아탈", "짜증", "이런걸", "불편", "오래",
            "검은 화면", "적당히", "중지", "자꾸", "없네", "유도", "비추"};

        String[] vocTokens = {"설치 오류", "지급이", "로그인", "해결", "종료", "다운로드가", "주세요", "사유", "안되", "복구",
            "안돼", "인증", "설치오류", "오류", "고객", "실행이 안"};


        String[] matchToken = negTokens ;

        List<String> matchTokens = Arrays.asList(matchToken);


        String filename = "sample/comment_sam100000_without_book.csv" ;
        CSVReader reader = null ;
        PrintWriter pw = null ;

        try {
            reader = new CSVReader(new FileReader(filename), ',', '"');
            pw = new PrintWriter(new File("sample/f_neg_comment.txt")) ;

            int i = 0;
            int cnt = 0;
            String[] tokens ;
            while((tokens = reader.readNext()) != null) {
                i++;
                if(tokens.length > 2) {
                    String comment = tokens[1];

                    for(int j=0; j < matchToken.length; j++) {
                        if(comment.contains(matchToken[j])) {
                            System.out.println(i + "\t :" + matchToken[j] + "\t->" + comment);
                            comment = comment.replaceAll("\n", " ");

                            pw.write(comment + "\n");

                            cnt ++ ;
                            break ;
                        }
                    }
                }

            }
            System.out.println("Total Matched :" + cnt);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader != null) try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(pw != null) pw.close();
        }
    }


    public static void main(String ... v) {
        System.out.println("Active System ..");
        filterComment();
    }
}
