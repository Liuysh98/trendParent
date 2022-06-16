package com.liuysh.service;

import com.liuysh.client.IndexDataClient;
import com.liuysh.pojo.IndexData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author Liuysh
 * @date 2022/6/16 12:07
 * @Description:
 */
@Service
public class BackTestService {
    @Autowired
    IndexDataClient indexDataClient;
    public List<IndexData> listIndexData(String code) {
        List<IndexData> result = indexDataClient.getIndexData(code);
        Collections.reverse(result);
        for (IndexData indexData : result) {
            System.out.println(indexData);
        }
        return result;
    }
}
