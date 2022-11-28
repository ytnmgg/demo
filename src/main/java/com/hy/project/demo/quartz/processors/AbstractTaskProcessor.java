package com.hy.project.demo.quartz.processors;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.hy.project.demo.quartz.QuartzService;
import com.hy.project.demo.quartz.Schedulers;
import com.hy.project.demo.quartz.Task;
import com.hy.project.demo.quartz.TaskProcessor;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;

import static com.hy.project.demo.quartz.TaskConstants.KEY_TASK_CODE;

/**
 * @author rick.wl
 * @date 2022/11/23
 */
@DependsOn(value = {"quartzServiceImpl"})
public abstract class AbstractTaskProcessor implements TaskProcessor {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractTaskProcessor.class);

    @Autowired
    QuartzService quartzService;

    @PostConstruct
    public void init() throws Throwable {
        LOGGER.info("processor init...");

        registerMe();
    }

    /**
     * 将本processor注册到quartz框架中
     *
     * @throws Throwable
     */
    private void registerMe() throws Throwable {

        if (!quartzService.checkJobExists(getSchedulerName(), getJobId(), getJobGroup()) && !quartzService
            .checkTriggerExists(getSchedulerName(), getTriggerId(), getTriggerGroup())) {

            Map<String, Object> jobData = buildSystemJobData();
            Map<String, Object> bizJobData = getJobData();
            if (MapUtils.isNotEmpty(bizJobData)) {
                jobData.putAll(bizJobData);
            }

            quartzService.addJob(getSchedulerName(), Task.class, jobData, getJobId(), getJobGroup(), getTriggerId(),
                getTriggerGroup(), getCronExpression());
        }

    }

    /**
     * JobData中设置任务系统参数
     *
     * @return
     */
    private Map<String, Object> buildSystemJobData() {
        Map<String, Object> jobData = new HashMap<>();
        jobData.put(KEY_TASK_CODE, getTaskMeta().getTaskCode());

        return jobData;
    }

    protected Map<String, Object> getJobData() {
        return null;
    }

    protected String getSchedulerName() {
        return Schedulers.DEFAULT_SCHEDULER.getName();
    }

    protected String getCronExpression() {
        return getTaskMeta().getDefaultCronExpression();
    }

    protected String getJobId() {
        return getTaskMeta().getJobId();
    }

    protected String getTriggerId() {
        return getTaskMeta().getTriggerId();
    }

    protected String getJobGroup() {
        return getTaskMeta().getJobGroup();
    }

    protected String getTriggerGroup() {
        return getTaskMeta().getTriggerGroup();
    }
}
