package com.hy.project.demo.common.service.quartz.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import com.hy.project.demo.common.service.quartz.JobInfo;
import com.hy.project.demo.common.service.quartz.QuartzService;
import com.hy.project.demo.common.service.quartz.Schedulers;
import com.hy.project.demo.common.util.AssertUtil;
import com.hy.project.demo.common.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.core.jmx.JobDataMapSupport;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import static com.hy.project.demo.common.constant.TaskConstants.DEFAULT_QUARTZ_CONF_FILE;
import static com.hy.project.demo.common.exception.DemoExceptionEnum.CONFIGURATION_EXCEPTION;
import static com.hy.project.demo.common.util.DateUtil.STANDARD_STR;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author rick.wl
 * @date 2022/11/22
 */
@Service
@DependsOn(value = {"envUtil"})
public class QuartzServiceImpl implements QuartzService {
    private final static Logger LOGGER = LoggerFactory.getLogger(QuartzServiceImpl.class);

    private final ConcurrentMap<String, SchedulerFactoryBean> schedulerFactoryBeans = new ConcurrentHashMap<>();

    @Autowired
    DataSource dataSource;

    //@PostConstruct
    //public void init() throws Throwable {
    //    LOGGER.info("QuartzService init...");
    //
    //    if (isDevEnv()) {
    //        LOGGER.info("will not init quartz at env of dev...");
    //        return;
    //    }
    //
    //    initDefaultScheduler();
    //}

    @Override
    public void addJob(String schedulerName, Class<? extends Job> jobClass, Map<String, Object> jobData, String jobId,
        String jobGroup, String triggerId, String triggerGroup, String cronExpression) throws Throwable {

        JobDataMap jobDataMap = JobDataMapSupport.newJobDataMap(jobData);
        JobDetail jobDetail = newJob(jobClass).withIdentity(jobId, jobGroup)
            .setJobData(jobDataMap).build();

        Trigger trigger = newTrigger().withIdentity(triggerId, triggerGroup)
            .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();

        this.getScheduler(schedulerName).scheduleJob(jobDetail, trigger);
    }

    @Override
    public void startScheduler(String schedulerName) throws Throwable {
        this.getScheduler(schedulerName).startDelayed(60);
    }

    @Override
    public void initSchedulerFactoryBean(String schedulerName, Properties quartzProperties, DataSource dataSource) {
        AssertUtil.isTrue(!schedulerFactoryBeans.containsKey(schedulerName), CONFIGURATION_EXCEPTION,
            "SchedulerFactoryBean already exist: %s", schedulerName);

        try {
            SchedulerFactoryBean factory = new SchedulerFactoryBean();
            factory.setSchedulerName(schedulerName);
            factory.setQuartzProperties(quartzProperties);
            factory.setDataSource(dataSource);
            factory.setAutoStartup(false);
            factory.setStartupDelay(6);
            factory.afterPropertiesSet();
            schedulerFactoryBeans.put(schedulerName, factory);
        } catch (Exception e) {
            LOGGER.error(String.format("init SchedulerFactoryBean error: %s", schedulerName), e);
        }
    }

    @Override
    public boolean checkJobExists(String schedulerName, String jobId, String jobGroup) throws Throwable {
        return this.getScheduler(schedulerName).checkExists(new JobKey(jobId, jobGroup));
    }

    @Override
    public boolean checkTriggerExists(String schedulerName, String triggerId, String triggerGroup) throws Throwable {
        return this.getScheduler(schedulerName).checkExists(new TriggerKey(triggerId, triggerGroup));
    }

    @Override
    public List<JobInfo> listJobs() throws Throwable {
        List<JobInfo> jobs = new ArrayList<>();
        for (String schedulerName : Schedulers.getNames()) {
            Scheduler scheduler = this.getScheduler(schedulerName);

            JobInfo jobInfo = new JobInfo();
            jobInfo.setSchedulerName(schedulerName);

            SchedulerMetaData schedulerMetaData = scheduler.getMetaData();
            jobInfo.setSchedulerInstanceId(schedulerMetaData.getSchedulerInstanceId());
            jobInfo.setStarted(schedulerMetaData.isStarted());
            jobInfo.setShutdown(schedulerMetaData.isShutdown());

            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyJobGroup());

            for (JobKey jobKey : jobKeys) {
                jobInfo.setJobName(jobKey.getName());
                jobInfo.setJobGroup(jobKey.getGroup());

                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    jobInfo.setTriggerState(scheduler.getTriggerState(trigger.getKey()).name());
                    jobInfo.setStartTime(DateUtil.format(trigger.getStartTime(), STANDARD_STR));
                    jobInfo.setPrevFireTime(DateUtil.format(trigger.getPreviousFireTime(), STANDARD_STR));
                    jobInfo.setNextFireTime(DateUtil.format(trigger.getNextFireTime(), STANDARD_STR));

                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger)trigger;
                        jobInfo.setCronExpression(cronTrigger.getCronExpression());
                    }

                    jobs.add(jobInfo);
                }
            }
        }
        return jobs;
    }

    @Override
    public Scheduler getScheduler(String schedulerName) {
        if (StringUtils.isEmpty(schedulerName)) {
            return null;
        }

        SchedulerFactoryBean schedulerFactoryBean = schedulerFactoryBeans.get(schedulerName);
        if (null == schedulerFactoryBean) {
            return null;
        }
        return schedulerFactoryBean.getScheduler();
    }

    @Override
    public String initAndStartScheduler(Schedulers scheduler) throws Throwable {
        String schedulerName = scheduler.getName();

        Scheduler exist = getScheduler(schedulerName);
        if (null != exist) {
            LOGGER.info("scheduler {} already exist, skip init and start", schedulerName);
            return schedulerName;
        }

        Resource resource = new ClassPathResource(DEFAULT_QUARTZ_CONF_FILE);
        Properties properties = new Properties();
        properties.load(resource.getInputStream());

        initSchedulerFactoryBean(schedulerName, properties, dataSource);

        startScheduler(schedulerName);

        return schedulerName;
    }
}
