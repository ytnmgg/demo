package com.hy.project.demo.cron.tasks;

import com.hy.project.demo.controller.TestController;
import com.hy.project.demo.service.NginxAccessFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
@Component
public class NginxAccessLogParseTask {
    private final static Logger LOGGER = LoggerFactory.getLogger(NginxAccessLogParseTask.class);

    @Autowired
    NginxAccessFileService nginxAccessFileService;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void doTask() {
        try {
            nginxAccessFileService.readAndStoreLines();
        } catch (Throwable e) {
            LOGGER.error(String.format("failed to execute task NginxAccessLogParseTask：%s", e.getMessage()), e);
        }
    }
}
