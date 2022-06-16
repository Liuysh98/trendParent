package com.liuysh.client;

import cn.hutool.core.collection.CollectionUtil;
import com.liuysh.pojo.IndexData;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Liuysh
 * @date 2022/6/16 12:03
 * @Description:
 */
@Component
public class IndexDataClientFeignHystrix implements IndexDataClient{

    @Override
    public List<IndexData> getIndexData(String code) {
        IndexData indexData= new IndexData();
        indexData.setClosePoint(0);
        indexData.setDate("0000-00-00");
        return CollectionUtil.toList(indexData);
    }
}
