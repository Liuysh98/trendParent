package com.liuysh.controller;

import com.liuysh.pojo.Index;
import com.liuysh.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Liuysh
 * @date 2022/6/11 15:25
 * @Description:
 */
@RestController
public class IndexController {
    @Autowired
    IndexService indexService;

    @GetMapping("/getCodes")
    public List<Index> get() throws Exception{
        return indexService.fetchIndexesFromThird();
    }
}
