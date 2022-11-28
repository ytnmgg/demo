package com.hy.project.demo.quartz;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hy.project.demo.util.AssertUtil;
import com.hy.project.demo.util.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.hy.project.demo.exception.DemoExceptionEnum.CONFIGURATION_EXCEPTION;
import static com.hy.project.demo.quartz.TaskConstants.KEY_TASK_CODE;

/**
 * @author rick.wl
 * @date 2022/11/22
 */
public class Task implements Job {
    private final static Logger LOGGER = LoggerFactory.getLogger(Task.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String taskCode = jobDataMap.getString(KEY_TASK_CODE);
        AssertUtil.notBlank(taskCode, CONFIGURATION_EXCEPTION, "taskCode should not be empty");

        Map<String, TaskProcessor> processors = SpringUtil.getBeans(TaskProcessor.class);
        List<TaskProcessor> taskProcessors = processors.values().stream().filter(
            p -> StringUtils.equals(p.getTaskMeta().getTaskCode(), taskCode)).collect(Collectors.toList());

        AssertUtil.isTrue(taskProcessors.size() == 1, CONFIGURATION_EXCEPTION,
            "taskProcessor should not be null or multiple: %s", taskCode);

        try {
            LOGGER.info("start to run task: {}", taskCode);
            taskProcessors.get(0).process(TaskProcessContext.of(taskCode, jobDataMap));
            LOGGER.info("task run success: {}", taskCode);

        } catch (Exception e) {
            LOGGER.error(String.format("task run error: %s", taskCode), e);
        }
    }
}
