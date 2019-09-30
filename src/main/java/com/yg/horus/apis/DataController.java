package com.yg.horus.apis;

import com.yg.horus.data.elastic.DataManager;
import com.yg.horus.data.sample.Hello;
import com.yg.horus.data.sample.HelloDao;
import com.yg.horus.document.news.NewsCrawler;
import com.yg.horus.document.news.data.HeadLineNews;
import com.yg.horus.document.stock.StockDataCrawler;
import com.yg.horus.document.stock.data.DailyInvestorValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by 1002000 on 2018. 10. 27..
 */
@RestController
public class DataController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataManager dataManager = null ;
    @Autowired
    private StockDataCrawler stockDataCrawler = null ;
    @Autowired
    private NewsCrawler newsCrawler = null;


    public DataController() {
        ;
    }

    @RequestMapping("/addDailyInvs")
    public String addData(Model model, @RequestParam String day) {
        List<DailyInvestorValues> dailyInvesterData = this.stockDataCrawler.getDailyInvesterData(day);

        StringBuilder sb = new StringBuilder() ;
        dailyInvesterData.forEach(iv -> {

            String id = this.dataManager.putData("test-dailyinvs2", "IVS_" + iv.getDateStamp(), iv) ;
            System.out.println("Put Result : " + id);
            sb.append(iv.toString()).append("<br/>") ;
        });

        return sb.toString() ;
    }


    @RequestMapping("/addNews")
    public String addNews(Model model, @RequestParam String day) {
        List<HeadLineNews> headNews = this.newsCrawler.extractNaverStockNewsList(day);

        headNews.forEach(hn -> {
            String url = hn.getUrl();
            int st =  url.indexOf("article_id=") + "article_id=".length();
            String id = url.substring(st, url.indexOf("&", st)) ;

            System.out.println(id + " --> " + hn) ;
        });

        StringBuilder sb = new StringBuilder() ;

        return sb.toString() ;
    }

//    @Autowired
//    private HelloDao helloDao;
//
//    @RequestMapping("/add")
//    public Hello add(Hello hello) {
//
//        Hello helloData = helloDao.save(hello);
//
//        return helloData;
//    }
//
//    @RequestMapping("/list")
//    public List<Hello> list(Model model) {
//
//        List<Hello> helloList = helloDao.findAll();
//
//        return helloList;
//    }
//
//    @RequestMapping("/")
//    public String index() {
//        return "helloworld!";
//    }

}
