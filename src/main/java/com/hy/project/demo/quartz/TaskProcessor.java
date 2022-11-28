package com.hy.project.demo.quartz;

/**
 * @author rick.wl
 * @date 2022/11/22
 */
public interface TaskProcessor {

    /**
     * processor能处理的任务类型
     *
     * @return 结果
     */
    TaskMeta getTaskMeta();

    void process(TaskProcessContext context);
}
