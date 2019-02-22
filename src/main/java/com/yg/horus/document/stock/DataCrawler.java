package com.yg.horus.document.stock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by 1002000 on 2018. 8. 24..
 */
public class DataCrawler {

    // 날짜 개인 외국인 기관계 금융투자 보험 투신 은행 연기금 국가 기타법인
    public static void crawlAmtOfinvestors() {
        ;
    }

    // 날짜 체결가 전일비 등락률 거래량 거래대금
    public static void kospiIndex() {
        ;
    }


    public static void main(String ... v) {
        System.out.println("Active System");
        String url = "http://finance.daum.net/quote/investor_yyyymmdd.daum?page=20&stype=P&type=null";

        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(doc.title());
        Elements datas = doc.select("td.datetime2");
        for (Element data : datas) {
            System.out.println(">>" + data.ownText());

            Elements siblings = data.siblingElements();
            for(Element sib : siblings) {
                System.out.println("\t->" + sib.text());
            }
        }

    }
}
