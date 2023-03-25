package com.hy.project.demo.common.service.quartz;

import org.quartz.JobDataMap;

/**
 * @author rick.wl
 * @date 2022/11/23
 */
public class TaskProcessContext {

    private String taskCode;
    private JobDataMap jobDataMap;

    public static TaskProcessContext of(String taskCode, JobDataMap jobDataMap) {
        TaskProcessContext context = new TaskProcessContext();
        context.setTaskCode(taskCode);
        context.setJobDataMap(jobDataMap);
        return context;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public JobDataMap getJobDataMap() {
        return jobDataMap;
    }

    public void setJobDataMap(JobDataMap jobDataMap) {
        this.jobDataMap = jobDataMap;
    }
}
