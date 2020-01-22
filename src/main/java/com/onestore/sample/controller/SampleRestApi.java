package com.onestore.sample.controller;

import com.onestore.sample.controller.vo.CommonResult;
import com.onestore.sample.data.bo.User;
import com.onestore.sample.data.dao.SampleDbMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by a1000074 on 06/11/2019.
 */
@RestController
public class SampleRestApi {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired private SampleDbMapper sampleDbMapper = null;

    public SampleRestApi() {
        ;
    }

    @RequestMapping(value ={"/v1/hello"}, method = RequestMethod.POST)
    public String sayHello(@RequestBody User user) {
        log.info("Api detected request sayHello with {}", user.getName());
        return "Hello " + user.getName() ;
    }


    @RequestMapping(value ={"/v1/user"}, method = RequestMethod.POST)
    public @ResponseBody CommonResult addUser(@RequestBody User user) {
        log.info("Request AddUser : {}", user);

        CommonResult result = new CommonResult() ;
        try {
            this.sampleDbMapper.insertUser(user);
            result.setResultCode("SUCC");
        } catch(Exception e) {
            e.printStackTrace();
            result.setResultCode("FAIL");
        }

        return result ;
    }

    @RequestMapping(value ={"/v1/user"}, method = RequestMethod.GET)
    public List<User> getUsers(@RequestParam String name) {
        log.info("Request selectUser whose name is {}", name);

        List<User> users = this.sampleDbMapper.selectUser(name);

        return users ;
    }

}
