package com.liuysh.service;

import com.liuysh.client.IndexDataClient;
import com.liuysh.pojo.IndexData;
import com.liuysh.pojo.Profit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Liuysh
 * @date 2022/6/16 12:07
 * @Description:
 */
@Service
public class BackTestService {
    @Resource
    IndexDataClient indexDataClient;
    public List<IndexData> listIndexData(String code) {
        List<IndexData> result = indexDataClient.getIndexData(code);
        Collections.reverse(result);
        for (IndexData indexData : result) {
            System.out.println(indexData);
        }
        return result;
    }
    public Map<String, Object> simulate(int ma, float sellRate, float buyRate, float serviceCharge, List<IndexData> indexDatas)  {
        List<Profit> profits = new ArrayList<>();
        float initCash = 1000;
        float cash = initCash;
        float share = 0;
        float value = 0;
        // 拿到一定时间段内的数据
        float init = 0;
        if(!indexDatas.isEmpty()) {
            init= indexDatas.get(0).getClosePoint();
        }
        // 逐天遍历
        for(int i = 0; i < indexDatas.size(); i++) {
            IndexData indexData = indexDatas.get(i);
            float closePoint = indexData.getClosePoint();
            // 通过今天附近的平均值和最大值，得到今天的情况
            float avg = getMA(i, ma, indexDatas);
            float max = getMax(i, ma, indexDatas);
            float increase_rate = closePoint/avg;
            float decrease_rate = closePoint/max;
            // 根据情况，买入卖出
            if(avg != 0) {
                // 今天势头不错（比平均）
                if(increase_rate > buyRate) {
                    // 没买的话就买点
                    if(share == 0) {
                        share = cash / closePoint;
                        cash = 0;
                    }
                    // 今天到了抛售阈值（max的九成）
                }else if(decrease_rate < sellRate) {
                    // 能卖的话就卖点
                    if(share != 0) {
                        cash = closePoint * share * (1 - serviceCharge);
                        share = 0;
                    }
                }else {
                    // 其他情况啥也不干
                }
            }
            // 睡觉前看看自己手上的资产，价值是多少点（而不是多少元，因为要画到图上）
            if(share != 0) {
                value = closePoint * share;
            }else {
                value = cash;
            }
            float rate = value/initCash;
            Profit profit = new Profit();
            profit.setData(indexData.getDate());
            profit.setValue(rate * init);
            System.out.println("profit.value:" + profit.getValue());
            profits.add(profit);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("profits", profits);
        return map;
    }

    private static float getMA(int i, int ma, List<IndexData> indexDatas) {
        // 定起始点
        int start = i - 1 - ma ;
        int now = i - 1;
        start = start< 0 ? 0 : start;
        // 算平均数
        float sum = 0;
        float avg = 0;
        for(int j = start; j < now; j++) {
            sum += indexDatas.get(j).getClosePoint();
        }
        avg = sum / (now - start);
        return avg;
    }
    private static float getMax(int i, int day, List<IndexData> list) {
        // 定起始点
        int start = i-1-day;
        if(start<0) {
            start = 0;
        }
        int now = i-1;
        // 算最大值
        float max = 0;
        for (int j = start; j < now; j++) {
            IndexData bean =list.get(j);
            if(bean.getClosePoint()>max) {
                max = bean.getClosePoint();
            }
        }
        return max;
    }
}
