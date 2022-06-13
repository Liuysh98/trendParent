package com.liuysh.job;

import com.liuysh.pojo.Index;
import com.liuysh.service.IndexDataService;
import com.liuysh.service.IndexService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

/**
 * @author Liuysh
 * @date 2022/6/13 18:03
 * @Description:
 */
public class IndexDataSyncJob extends QuartzJobBean {
    @Autowired
    IndexService indexService;
    @Autowired
    IndexDataService indexDataService;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<Index> indexes = indexService.fresh();
        for (Index index : indexes) {
            indexDataService.fresh(index.getCode());
        }
    }
}
