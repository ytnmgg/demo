package com.hy.project.demo.quartz;

/**
 * @author rick.wl
 * @date 2022/11/23
 */
public enum TaskMeta {
    /**
     * 登录过期处理定时任务
     */
    LOGIN_EXPIRE("LOGIN_EXPIRE", "JOB_LOGIN_EXPIRE", "DEFAULT_JOB_GROUP", "TRIGGER_LOGIN_EXPIRE",
        "DEFAULT_TRIGGER_GROUP", "0 */1 * * * ?"),

    ;

    private String taskCode;
    private String jobId;
    private String jobGroup;
    private String triggerId;
    private String triggerGroup;
    private String defaultCronExpression;

    TaskMeta(String taskCode, String jobId, String jobGroup, String triggerId, String triggerGroup,
        String defaultCronExpression) {
        this.taskCode = taskCode;
        this.jobId = jobId;
        this.jobGroup = jobGroup;
        this.triggerId = triggerId;
        this.triggerGroup = triggerGroup;
        this.defaultCronExpression = defaultCronExpression;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
    }

    public String getTriggerGroup() {
        return triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    public String getDefaultCronExpression() {
        return defaultCronExpression;
    }

    public void setDefaultCronExpression(String defaultCronExpression) {
        this.defaultCronExpression = defaultCronExpression;
    }
}
