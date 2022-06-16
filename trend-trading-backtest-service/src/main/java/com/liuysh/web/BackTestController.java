package com.liuysh.web;

import com.liuysh.service.BackTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liuysh
 * @date 2022/6/16 12:10
 * @Description:
 */
@RestController
public class BackTestController {
    @Autowired
    BackTestService backTestService;
    @GetMapping("/simulate/{code}")
    @CrossOrigin
    public Map<String, Object> backTest(@PathVariable("code") String code) {
        Map<String, Object> result = new HashMap<>();
        result.put("indexDatas", backTestService.listIndexData(code));
        return result;
    }

}
