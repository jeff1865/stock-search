package com.yg.horus.document.stock;

import com.yg.horus.document.stock.data.DailyInvestorValues;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1002000 on 2018. 8. 24..
 */
public class StockDataCrawler {

    // 날짜 개인 외국인 기관계 금융투자 보험 투신 은행 연기금 국가 기타법인
    public static void crawlAmtOfinvestors() {
        ;
    }

    // 날짜 체결가 전일비 등락률 거래량 거래대금
    public static void kospiIndex() {
        ;
    }

    private DecimalFormat dc = new DecimalFormat("###,###,###,###") ;


    public static void main(String ... v) {
        StockDataCrawler test = new StockDataCrawler() ;


        System.out.println("Active System");
//        String url = "http://finance.daum.net/quote/investor_yyyymmdd.daum?page=20&stype=P&type=null";
//
//        Document doc = null;
//        try {
//            doc = Jsoup.connect(url).get();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(doc.title());
//        Elements datas = doc.select("td.datetime2");
//        for (Element data : datas) {
//            System.out.println(">>" + data.ownText());
//
//            Elements siblings = data.siblingElements();
//            for(Element sib : siblings) {
//                System.out.println("\t->" + sib.text());
//            }
//        }
        List<DailyInvestorValues> lstDVal = test.getDailyInvesterData();
        lstDVal.forEach(dival -> {
            System.out.println("->" + dival) ;
        });

    }

    public List<DailyInvestorValues> getDailyInvesterData() {
        String url = "https://finance.naver.com/sise/investorDealTrendDay.nhn?bizdate=20190325&sosok=&page=1";

        ArrayList<DailyInvestorValues> lstDailyIntest = new ArrayList<DailyInvestorValues>() ;

        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();

            Elements elems = doc.select("table tr");

//            System.out.println("selected -> " + table.size() + " ---> " + table.toString());
            elems.forEach(element -> {
                String dtStamp = element.select("td.date2").text() ;

                if(dtStamp != null && dtStamp.length() > 0) {
//                    System.out.println("Date -> " + dtStamp);
                    DailyInvestorValues diVal = new DailyInvestorValues() ;
                    diVal.setDateStamp(dtStamp);

                    Elements tds = element.select("td");

                    try {
                        diVal.setIndividual(this.dc.parse(tds.get(1).text()).intValue()) ;
                        diVal.setForeigner(this.dc.parse(tds.get(2).text()).intValue()) ;
                        diVal.setInstitutional(this.dc.parse(tds.get(3).text()).intValue()); ;
                        diVal.setFinance(this.dc.parse(tds.get(4).text()).intValue()); ;
                        diVal.setInsurance(this.dc.parse(tds.get(5).text()).intValue()); ;
                        diVal.setTrust(this.dc.parse(tds.get(6).text()).intValue()); ;
                        diVal.setBank(this.dc.parse(tds.get(7).text()).intValue()); ;
                        diVal.setEtcFinance(this.dc.parse(tds.get(8).text()).intValue()); ;
                        diVal.setPension(this.dc.parse(tds.get(9).text()).intValue()); ;

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    lstDailyIntest.add(diVal);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lstDailyIntest ;
    }

}
