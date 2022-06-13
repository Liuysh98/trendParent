package com.liuysh.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.liuysh.pojo.Index;
import com.liuysh.util.SpringContextUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
@CacheConfig(cacheNames = "indexes")
public class IndexService {
    @Autowired
    RestTemplate restTemplate;
    private List<Index> indexes;

    @HystrixCommand(fallbackMethod = "thirdNotConnected")
    public List<Index> fresh() {
        indexes = fetchIndexesFromThird();
        IndexService indexService = SpringContextUtil.getBean(IndexService.class);
        indexService.remove();
        return indexService.store();
    }

    public List<Index> thirdNotConnected() {
        System.out.println("thirdNotConnected");
        Index index = new Index();
        index.setName("无效指数代码");
        index.setCode("000000");
        return CollectionUtil.toList(index);
    }

    public List<Index> fetchIndexesFromThird() {
        List<Map> temp = restTemplate.getForObject("http://127.0.0.1:8090/indexes/codes.json", List.class);
        return Map2Index(temp);
    }
    @Cacheable(key = "'all_codes'")
    public List<Index> store() { return indexes; }
    @Cacheable(key = "'all_codes'") //如果缓存中有，就从缓存中拿
    public List<Index> get() { return CollUtil.toList(); }
    @CacheEvict(allEntries = true)
    public void remove(){}
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
