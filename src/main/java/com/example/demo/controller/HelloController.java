package com.example.demo.controller;

import com.example.demo.entity.T_TEST_TABLE_ENTITY;
import com.example.demo.mapper.T_TEST_MAPPER;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HelloController {
    private static final Logger LOG = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private T_TEST_MAPPER mapper;

    @RequestMapping("index")
    public String hello(ModelMap map){
        LOG.info("==========print log==========");
        List<T_TEST_TABLE_ENTITY> tableList = mapper.getTestTable();
        map.put("test",tableList.get(0).getTEST());
        map.put("creator",tableList.get(0).getCREATOR());

        return "test/test";
    }

    /**
     * 功能描述:处理add请求<br/>
     * @param test
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/add",method= RequestMethod.POST)
    public String add(@ModelAttribute T_TEST_TABLE_ENTITY test){
        String username = test.getTEST();
        String password = test.getCREATOR();
        return username+"__"+password;
    }
}
