package com.yg.horus.document.news.crawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by 1002000 on 2019. 3. 25..
 */
public class NewsCrawler {

    public NewsCrawler() {
        ;
    }

    public static void main(String ... v) {
        NewsCrawler test = new NewsCrawler() ;

        test.extractNewsContents() ;
    }

    public void extractNewsContents() {
        String url = "https://finance.naver.com/news/mainnews.nhn?date=2019-03-22&page=1";


        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();

            Elements newsBody = doc.select("div.mainNewsList");

//            System.out.println("doc -> " + doc.text());

            System.out.println("NewsBody -> " + newsBody.text());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
