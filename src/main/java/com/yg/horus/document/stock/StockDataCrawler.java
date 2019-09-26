package com.yg.horus.document.stock;

import com.yg.horus.document.stock.data.DailyInvestorValues;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1002000 on 2018. 8. 24..
 */
@Service
public class StockDataCrawler {
    private DecimalFormat dc = new DecimalFormat("###,###,###,###") ;

    public static void main(String ... v) {

        StockDataCrawler test = new StockDataCrawler() ;

        System.out.println("Active System");

        List<DailyInvestorValues> lstDVal = test.getDailyInvesterData("20190902");
        lstDVal.forEach(dival -> {
            System.out.println("StockValue->" + dival) ;
        });
    }

    public List<DailyInvestorValues> getDailyInvesterData(String yyyyMMdd) {
        String url = "https://finance.naver.com/sise/investorDealTrendDay.nhn?bizdate=" + yyyyMMdd + "&sosok=&page=1";

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
