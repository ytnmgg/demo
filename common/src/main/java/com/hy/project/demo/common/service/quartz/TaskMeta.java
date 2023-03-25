package com.hy.project.demo.common.service.quartz;

/**
 * @author rick.wl
 * @date 2023/03/24
 */
public interface TaskMeta {
    String getTaskCode();

    String getJobId();

    String getJobGroup();

    String getTriggerId();

    String getTriggerGroup();

    String getDefaultCronExpression();
}
