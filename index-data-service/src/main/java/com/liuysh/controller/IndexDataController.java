package com.liuysh.controller;

import com.liuysh.config.IpConfiguration;
import com.liuysh.pojo.IndexData;
import com.liuysh.service.IndexDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Liuysh
 * @date 2022/6/15 16:41
 * @Description:
 */
@RestController
public class IndexDataController {
    @Autowired
    IndexDateService indexDateService;
    @Autowired
    IpConfiguration ipConfiguration;
    @GetMapping("/data/{code}")
    public List<IndexData> get(@PathVariable("code") String code) {
        System.out.println("the current instance's port is " + ipConfiguration.getServerPort());
        return indexDateService.get(code);
    }
}
