package com.liuysh.web;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.liuysh.pojo.IndexData;
import com.liuysh.pojo.Profit;
import com.liuysh.service.BackTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author Liuysh
 * @date 2022/6/16 12:10
 * @Description:
 */
@RestController
public class BackTestController {
    @Autowired
    BackTestService backTestService;
    @GetMapping("/simulate/{code}/{startDate}/{endDate}")
    @CrossOrigin
    public Map<String, Object> backTest(@PathVariable("code") String code
            ,@PathVariable("startDate") String strStartDate
            ,@PathVariable("endDate") String strEndDate) throws Exception {
        // 逐日数据
            // 所有日期的
        List<IndexData> allIndexDatas = backTestService.listIndexData(code);
            // 过滤出限定日期内的
        String indexStartDate = allIndexDatas.get(0).getDate();
        String indexEndDate = allIndexDatas.get(allIndexDatas.size()-1).getDate();
        allIndexDatas = filterByDateRange(allIndexDatas,strStartDate, strEndDate);
        // 逐日模拟
        int ma = 20;
        float sellRate = 0.95f;
        float buyRate = 1.05f;
        float serviceCharge = 0f;
        Map<String,?> simulateResult= backTestService.simulate(ma,sellRate, buyRate,serviceCharge, allIndexDatas);
        List<Profit> profits = (List<Profit>) simulateResult.get("profits");
        // return
        Map<String, Object> result = new HashMap<>();
        result.put("indexDatas", allIndexDatas);
        result.put("indexStartDate", indexStartDate);
        result.put("indexEndDate", indexEndDate);
        result.put("profits", profits);
        return result;
    }
    private List<IndexData> filterByDateRange(List<IndexData> allIndexDatas, String strStartDate, String strEndDate){
        // 时间限制不符合条件
        if(StrUtil.isBlankOrUndefined(strStartDate) || StrUtil.isBlankOrUndefined(strEndDate) ) {
            return allIndexDatas;
        }
        // 遍历筛选
        List<IndexData> result = new ArrayList<>();
        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);
        for (IndexData indexData : allIndexDatas) {
            Date date =DateUtil.parse(indexData.getDate());
            if(
                    date.getTime()>=startDate.getTime() &&
                            date.getTime()<=endDate.getTime()
            ) {
                result.add(indexData);
            }
        }
        return result;
    }
}
