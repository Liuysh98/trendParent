package com.liuysh.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.liuysh.client.IndexDataClient;
import com.liuysh.pojo.AnnualProfit;
import com.liuysh.pojo.IndexData;
import com.liuysh.pojo.Profit;
import com.liuysh.pojo.Trade;
import org.springframework.stereotype.Service;

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
        List<Trade> trades = new ArrayList<>();
        float initCash = 1000;
        float cash = initCash;
        float share = 0;
        float value = 0;
        int winCount = 0;
        float totalWinRate = 0;
        float avgWinRate = 0;
        float totalLossRate = 0;
        int lossCount = 0;
        float avgLossRate = 0;
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
                        // 记录交易信息
                        Trade trade = new Trade();
                        trade.setBuyDate(indexData.getDate());
                        trade.setBuyClosePoint(closePoint);
                        trade.setSellDate("n/a");
                        trade.setSellClosePoint(0);
                        trades.add(trade);
                    }
                    // 今天到了抛售阈值（max的九成）
                }else if(decrease_rate < sellRate) {
                    // 能卖的话就卖点
                    if(share != 0) {

                        cash = closePoint * share * (1 - serviceCharge);
                        share = 0;
                        // 记录交易信息
                        Trade trade = trades.get(trades.size() - 1);
                        trade.setSellDate(indexData.getDate());
                        trade.setSellClosePoint(indexData.getClosePoint());
                        trade.setRate(cash / initCash);
                        // 如果总体看是挣了
                        if(trade.getSellClosePoint()-trade.getBuyClosePoint()>0) {
                            totalWinRate +=(trade.getSellClosePoint()-trade.getBuyClosePoint())/trade.getBuyClosePoint();
                            winCount++;
                        // 如果总体看是赔了
                        }else {
                            totalLossRate +=(trade.getSellClosePoint()-trade.getBuyClosePoint())/trade.getBuyClosePoint();
                            lossCount ++;
                        }
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
            profit.setDate(indexData.getDate());
            profit.setValue(rate * init);
            System.out.println("profit.value:" + profit.getValue());
            profits.add(profit);
        }
        // 算算平均每次赢的交易是赢多少，输的交易是输多少
        avgWinRate = totalWinRate / winCount;
        avgLossRate = totalLossRate / lossCount;
        Map<String,Object> map = new HashMap<>();

        map.put("profits", profits);
        map.put("trades", trades);
        map.put("winCount", winCount);
        map.put("lossCount", lossCount);
        map.put("avgWinRate", avgWinRate);
        map.put("avgLossRate", avgLossRate);
        List<AnnualProfit> annualProfits = caculateAnnualProfits(indexDatas, profits);
        map.put("annualProfits", annualProfits);
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
    // 算这个指数的开始和结束时间
    public float getYear(List<IndexData> allIndexDatas) {
        float years;
        String sDateStart = allIndexDatas.get(0).getDate();
        String sDateEnd = allIndexDatas.get(allIndexDatas.size()-1).getDate();
        Date dateStart = DateUtil.parse(sDateStart);
        Date dateEnd = DateUtil.parse(sDateEnd);
        long days = DateUtil.between(dateStart, dateEnd, DateUnit.DAY);
        years = days/365f;
        return years;
    }
    // 拿到某个时间字符串里的年份
    private int getYear(String date) {
        String strYear= StrUtil.subBefore(date, "-", false);
        return Convert.toInt(strYear);
    }
    // 计算 某指数线某年的增长率
    private float getIndexIncome(int year, List<IndexData> indexDatas) {
        IndexData first=null;
        IndexData last=null;
        for (IndexData indexData : indexDatas) {
            String strDate = indexData.getDate();
//			Date date = DateUtil.parse(strDate);
            int currentYear = getYear(strDate);
            if(currentYear == year) {
                if(null==first)
                    first = indexData;
                last = indexData;
            }
        }
        return (last.getClosePoint() - first.getClosePoint()) / first.getClosePoint();
    }
    // 计算 某投资线某年的增长率
    private float getTrendIncome(int year, List<Profit> profits) {
        Profit first=null;
        Profit last=null;
        for (Profit profit : profits) {
            String strDate = profit.getDate();
            int currentYear = getYear(strDate);
            if(currentYear == year) {
                if(null==first)
                    first = profit;
                last = profit;
            }
            if(currentYear > year)
                break;
        }
        return (last.getValue() - first.getValue()) / first.getValue();
    }
    private List<AnnualProfit> caculateAnnualProfits(List<IndexData> indexDatas, List<Profit> profits) {
        List<AnnualProfit> result = new ArrayList<>();
        // 拿出时间的起止点
        String strStartDate = indexDatas.get(0).getDate();
        String strEndDate = indexDatas.get(indexDatas.size()-1).getDate();
        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);
        int startYear = DateUtil.year(startDate);
        int endYear = DateUtil.year(endDate);
        // 把每年的指数线增长率和投资线增长率生成annualProfit对象
        for (int year =startYear; year <= endYear; year++) {
            AnnualProfit annualProfit = new AnnualProfit();
            annualProfit.setYear(year);
            float indexIncome = getIndexIncome(year,indexDatas);
            float trendIncome = getTrendIncome(year,profits);
            annualProfit.setIndexIncome(indexIncome);
            annualProfit.setTrendIncome(trendIncome);
            result.add(annualProfit);
        }
        return result;
    }
}
