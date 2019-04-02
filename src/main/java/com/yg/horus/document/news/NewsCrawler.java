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
        String url = "https://finance.naver.com/news/mainnews.nhn?date=2019-04-02";

        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();

            Elements newsBody = doc.select("div.mainNewsList");

            Elements blocks = newsBody.select("li.block1");

            blocks.forEach(element -> {
                HeadLineNews hlNews = new HeadLineNews() ;

                Elements title = element.select(".articleSubject a");

                hlNews.setTitleAnchor(title.text());
                hlNews.setUrl(title.attr("abs:href"));
                hlNews.setSummary(element.select(".articleSummary").first().ownText());
                hlNews.setIssuer(element.select("span.press").text());
                hlNews.setTimestamp(element.select("span.wdate").text());

                lstNews.add(hlNews) ;
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lstNews ;
    }
}
