package com.onestore.ds.util;

/**
 * Created by a1000074 on 21/01/2020.
 */
public class Exam {
    public static void main(String ... v) {
        try {
            String dataLocalPath = DownloaderUtility.NLPDATA.Download();
            System.out.println("Downloaded -> " + dataLocalPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
