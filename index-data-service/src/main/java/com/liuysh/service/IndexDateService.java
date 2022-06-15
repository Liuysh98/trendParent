package com.liuysh.service;

import cn.hutool.core.collection.CollUtil;
import com.liuysh.pojo.IndexData;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Liuysh
 * @date 2022/6/15 16:38
 * @Description:
 */
@Service
@CacheConfig(cacheNames = "index_data")
public class IndexDateService {
    @Cacheable(key = "'indexData-code-'+ #p0")
    public List<IndexData> get(String code) {
        return CollUtil.toList();
    }
}
