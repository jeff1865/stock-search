package com.yg.horus.schedule;

import com.yg.horus.data.elastic.DataManager;
import com.yg.horus.document.stock.StockDataCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CrawlJobManager {

    @Autowired
    private StockDataCrawler stockDataCrawler = null ;
    @Autowired
    private DataManager dataManager = null ;

    public CrawlJobManager() {
        ;
    }

    public void storeInvestmentData() {

    }


    public static void main(String ... v) {
        System.out.println("Active ..");

    }
}
