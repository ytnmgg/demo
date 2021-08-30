package com.hy.project.demo.cron.tasks;

import com.hy.project.demo.service.NginxAccessFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author rick.wl
 * @date 2021/08/30
 */
@Component
public class NginxAccessLogParseTask {

    @Autowired
    NginxAccessFileService nginxAccessFileService;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void doTask() {
        nginxAccessFileService.readAndStoreLines();
    }
}
