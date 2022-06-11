package com.liuysh.service;

import com.liuysh.pojo.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Liuysh
 * @date 2022/6/11 15:20
 * @Description:
 */
@Service
public class IndexService {
    @Autowired
    RestTemplate restTemplate;
    public List<Index> fetchIndexesFromThird() {
        List<Map> temp = restTemplate.getForObject("http://127.0.0.1:8090/indexes/codes.json", List.class);
        return Map2Index(temp);
    }
    private List<Index> Map2Index(List<Map> list) {
        List<Index> res = new ArrayList<>();
        for (Map map : list) {
            Index i = new Index();
            i.setCode(map.get("code").toString());
            i.setName(map.get("name").toString());
            res.add(i);
        }
        return res;
    }
}
