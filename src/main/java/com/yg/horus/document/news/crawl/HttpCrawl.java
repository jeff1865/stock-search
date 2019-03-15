package com.yg.horus.document.news.crawl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by 1002000 on 2019. 3. 14..
 */
public class HttpCrawl {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public HttpCrawl() {
        ;
    }

    public String crawlData(String targetUrl)  {
        StringBuffer sb = new StringBuffer() ;
        try {
            URL url = new URL(targetUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String input;

                while ((input = br.readLine()) != null){
                    sb.append(input + "\n");
                }
            }   catch(IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.error("Cannot get the parsable page-source from play.google.com : {}", targetUrl);
        }

        return sb.toString() ;
    }

    public static void main(String ... v) {
        HttpCrawl test = new HttpCrawl();
        System.out.println("crawled body ---> " + test.crawlData("https://stock.naver.com")) ;
    }
}
