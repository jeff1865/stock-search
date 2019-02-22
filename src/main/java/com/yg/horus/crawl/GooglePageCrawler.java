package com.yg.horus.crawl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Created by 1002000 on 2018. 7. 19..
 */
@Service
public class GooglePageCrawler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String BASE_GOOGLE_APP_QRY_URL = "https://play.google.com/store/apps/details";



    public GooglePageCrawler() {
        this.initSslConnection();
    }

    private void initSslConnection() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
            public X509Certificate[] getAcceptedIssuers(){return new X509Certificate[0];}
            public void checkClientTrusted(X509Certificate[] certs, String authType){}
            public void checkServerTrusted(X509Certificate[] certs, String authType){}
        }};

        try {

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            log.info("Custom SSL setting is just initialized ..");

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String crawlAppPage(String googlePkgName)  {
        return this.crawlData(BASE_GOOGLE_APP_QRY_URL + "?id=" + googlePkgName) ;
    }

    public String crawlData(String targetUrl){
        StringBuffer sb = new StringBuffer() ;
        try {
            URL url = new URL(targetUrl);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
//            conn.addRequestProperty("Accept-Encoding", "gzip, deflate, br");
            conn.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            conn.addRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,fr-FR;q=0.8,fr;q=0.7,en-US;q=0.6,en;q=0.5");


            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String input;

                while ((input = br.readLine()) != null){
//                    System.out.println(input);
                    sb.append(input + "\n");
                }
            }   catch(IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
//            e.printStackTrace();
            log.error("Cannot get the parsable page-source from play.google.com : {}", targetUrl);
        }

        return sb.toString() ;
    }

    public static void main(String ... v) {
        GooglePageCrawler pageCrawler = new GooglePageCrawler();
        String targetUrl = "https://play.google.com/store/apps/details?id=com.netmarble.destiny" ;
        String crawledData = null;

        crawledData = pageCrawler.crawlData(targetUrl);


        System.out.println("CrawlDataMapper --> " + crawledData) ;
    }





}
