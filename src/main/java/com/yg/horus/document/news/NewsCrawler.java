package com.yg.horus.document.news;

import com.yg.horus.document.news.data.HeadLineNews;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1002000 on 2019. 3. 25..
 */
public class NewsCrawler {

    public NewsCrawler() {
        ;
    }

    public static void main(String ... v) {
        NewsCrawler test = new NewsCrawler() ;

        List<HeadLineNews> headLineNewses = test.extractNaverStockNewsList();

        headLineNewses.forEach(news -> {
            if(news != null) System.out.println("CrawledNews -> " + news);
        });

    }

    public List<HeadLineNews> extractNaverStockNewsList() {
        ArrayList<HeadLineNews> lstNews = new ArrayList<>() ;

        String url = "https://finance.naver.com/news/mainnews.nhn?date=2019-03-29";


        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();

            Elements newsBody = doc.select("div.mainNewsList");

            Elements blocks = newsBody.select("li.block1");

            blocks.forEach(element -> {
                System.out.println("---------------------------------");
                System.out.println(element.text()) ;
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lstNews ;
    }
}
