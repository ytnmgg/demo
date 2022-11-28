package com.hy.project.demo.quartz;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.Job;

/**
 * @author rick.wl
 * @date 2022/11/22
 */
public interface QuartzService {

    void addJob(String schedulerName, Class<? extends Job> jobClass, Map<String, Object> jobData, String jobId,
        String jobGroup, String triggerId, String triggerGroup, String cronExpression) throws Throwable;

    void startScheduler(String schedulerName) throws Throwable;

    void initSchedulerFactoryBean(String schedulerName, Properties quartzProperties, DataSource dataSource);

    boolean checkJobExists(String schedulerName, String jobId, String jobGroup) throws Throwable;

    boolean checkTriggerExists(String schedulerName, String triggerId, String triggerGroup) throws Throwable;

    List<JobInfo> listJobs() throws Throwable;
}
