package com.liuysh.controller;

import com.liuysh.config.IpConfiguration;
import com.liuysh.pojo.Index;
import com.liuysh.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.midi.Soundbank;
import java.util.List;

/**
 * @author Liuysh
 * @date 2022/6/15 15:37
 * @Description:
 */
@RestController
public class IndexController {
    @Autowired
    IndexService indexService;
    @Autowired
    IpConfiguration ipConfiguration;

    @GetMapping("/codes")
    @CrossOrigin
    public List<Index> get() {
        System.out.println("the current instance's port is " + ipConfiguration.getServerPort());
        return indexService.get();
    }
}
