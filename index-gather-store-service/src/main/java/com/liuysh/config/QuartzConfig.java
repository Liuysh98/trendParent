package com.liuysh.config;

import com.liuysh.job.IndexDataSyncJob;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Liuysh
 * @date 2022/6/13 18:05
 * @Description:
 */
@Configuration
public class QuartzConfig {
    private static final int SECOND_INTERVAL = 5;
    @Bean
    public Trigger weatherDataSyncTrigger() {
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(SECOND_INTERVAL)
                .repeatForever();
        SimpleTrigger syncTrigger = TriggerBuilder
                .newTrigger()
                .forJob(weatherDataSyncJobDetail())
                .withIdentity("indexDataSyncTrigger")
                .withSchedule(simpleScheduleBuilder)
                .build();
        return syncTrigger;
    }
    @Bean
    public JobDetail weatherDataSyncJobDetail() {
        JobDetail indexDataSyncJob = JobBuilder.newJob(IndexDataSyncJob.class).withIdentity("IndexDataSyncJob").storeDurably().build();
        return indexDataSyncJob;
    }
}
