package com.yg.horus.apis;

import com.yg.horus.data.Hello;
import com.yg.horus.data.HelloDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by 1002000 on 2018. 10. 27..
 */
@RestController
public class DataController {

    @Autowired
    private HelloDao helloDao;

    @RequestMapping("/add")
    public Hello add(Hello hello) {

        Hello helloData = helloDao.save(hello);

        return helloData;
    }

    @RequestMapping("/list")
    public List<Hello> list(Model model) {

        List<Hello> helloList = helloDao.findAll();

        return helloList;
    }

    @RequestMapping("/")
    public String index() {
        return "helloworld!";
    }

}
