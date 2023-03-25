package com.hy.project.demo.common.service.quartz;

import java.io.Serializable;

/**
 * @author rick.wl
 * @date 2022/11/28
 */
public class JobInfo implements Serializable {
    private static final long serialVersionUID = -4721241979724541633L;

    private String schedulerName;
    private String schedulerInstanceId;
    private String jobName;
    private String jobGroup;

    private String startTime;
    private String prevFireTime;
    private String nextFireTime;

    private String triggerState;

    private boolean started;
    private boolean shutdown;
    private String cronExpression;

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public String getSchedulerInstanceId() {
        return schedulerInstanceId;
    }

    public void setSchedulerInstanceId(String schedulerInstanceId) {
        this.schedulerInstanceId = schedulerInstanceId;
    }

    public String getTriggerState() {
        return triggerState;
    }

    public void setTriggerState(String triggerState) {
        this.triggerState = triggerState;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPrevFireTime() {
        return prevFireTime;
    }

    public void setPrevFireTime(String prevFireTime) {
        this.prevFireTime = prevFireTime;
    }

    public String getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(String nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }
}
