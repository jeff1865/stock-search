package com.onestore.ds.preprocess;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by a1000074 on 16/01/2020.
 */
public class UsageFileProcessing {
    public static long CNT = 0;

    public static Hashtable<String, VoAppMeta> processingFile(String filePath) {

        Hashtable<String, String> htAppMap = new Hashtable<>();
        Hashtable<String, VoAppMeta> htOneApp = new Hashtable<>();
        Hashtable<String, VoAppMeta> htGglApp = new Hashtable<>();

        try {
//            FileWriter fw = new FileWriter(new File("/Users/a1000074/dev/temp-comment/latest_comment_5e6_20200107.txt"));

//            CSVReader reader = new CSVReader(new FileReader("/Users/a1000074/dev/temp_appusage_202001/app_usage_13_all.csv")) ;
            CSVReader reader = new CSVReader(new FileReader(filePath)) ;
            String[] tokens ;
            long cnt = 0;
            String mainPkgName = null ;
            while((tokens = reader.readNext()) != null) {
                try {
                    mainPkgName = tokens[0];

                    // Set Mapping Table
                    if (tokens[8] != null && tokens[9] != null)
                        htAppMap.put(tokens[8], tokens[9]);

                    if(tokens[6].startsWith("d")) continue;

                    VoAppMeta appMeta = new VoAppMeta();

                    appMeta.setUseSec(Long.parseLong(tokens[6]));
                    appMeta.setCntUsers(Long.parseLong(tokens[7]));

                    if (tokens[1] != null && tokens[1].trim().length() > 0) {
//                        System.out.println("detected one-app :" + mainPkgName);
                        appMeta.setMainCategory(tokens[1]);
                        appMeta.setSubCategory(tokens[2]);
                        appMeta.setProdId(tokens[3]);
                        appMeta.setProdName(tokens[4]);
                        appMeta.setRegDate(tokens[5]);
                        // Case of OneStore

                        htOneApp.put(mainPkgName, appMeta);
                    } else {
                        // Case of Google
//                        System.out.println("detected ggl-app :" + mainPkgName);
//                        htGglApp.put(mainPkgName, appMeta);
                    }
                    // putAll
                    htGglApp.put(mainPkgName, appMeta);
                }catch(Exception e) {
                    System.out.println("========" + tokens[6] + " " + tokens[7]);
                    e.printStackTrace();
                    continue;
                }
            }

            reader.close();

            System.out.println("Size of Map :" + htAppMap.size() + "\t One :" + htOneApp.size() +
                    "\t Ggl :" + htGglApp.size());

            System.out.println("Before OneApp ->" + htOneApp.get("com.nexon.fo4m.onestore"));
            System.out.println("Before GglApp ->" + htGglApp.get("com.nexon.fo4m"));
            System.out.println("-- Mapped GoogleApp -->" + htAppMap.get("com.nexon.fo4m.onestore"));

            CNT = 0;

            htOneApp.forEach((key, val) -> {
                //TODO ...
                String googlePkgNm = htAppMap.get(key);
                if(googlePkgNm != null && !key.equals(googlePkgNm)) {
                    VoAppMeta appGglMeta = htGglApp.get(googlePkgNm);
                    if(appGglMeta != null) {
                        System.out.println("Merged before :" + val);
                        val.setCntUsers(val.getCntUsers() + appGglMeta.getCntUsers());
                        val.setUseSec(val.getUseSec() + appGglMeta.getUseSec());
                        System.out.println("Merged after :" + val);
                        CNT ++;
                    } else {
//                        System.out.println("Map case null :" + googlePkgNm);
                    }

                }

            });

            System.out.println("Merged OneApp ->" + htOneApp.get("com.nexon.fo4m.onestore"));
            System.out.println("Merged :" + CNT);
            System.out.println("Successfully completed ..");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return htOneApp ;
    }

    public static void createCsv(Hashtable<String, VoAppMeta> htOneApp, String targetFilePath) {
        CSVWriter cw = null ;
        try {
//            cw = new CSVWriter(new FileWriter("sample/finUsage.csv"), ',', '"');
            cw = new CSVWriter(new FileWriter(targetFilePath), ',', '"');

            Enumeration<String> keys = htOneApp.keys();
            while(keys.hasMoreElements()) {
                String key = keys.nextElement();

                VoAppMeta appMeta = htOneApp.get(key);
                cw.writeNext(appMeta.getCsvElementSet());
            }

            System.out.println("Successfully writing CSV completed ..");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                cw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // /Users/a1000074/dev/temp_appusage_202001/result
    public static void main(String ... v) {
        System.out.println("Active ..");

        File dir = new File("/Users/a1000074/dev/temp_appusage_202001/latest1mon") ;

        int cnt = 0;
        for (File f : dir.listFiles()) {
            System.out.println("===========>" + cnt++ + "\t" + f.getName() + "\t" + f.getAbsolutePath()) ;

            Hashtable<String, VoAppMeta> oneStatistics = processingFile(f.getAbsolutePath());
            createCsv(oneStatistics, "/Users/a1000074/dev/temp_appusage_202001/result/d" + f.getName());
        }

        System.out.println("Job completed ..");
//        createCsv(processingFile(null));
    }
}
