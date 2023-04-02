package com.hy.project.demo.web.controller.monitor;

import java.util.List;

import com.hy.project.demo.common.service.quartz.JobInfo;
import com.hy.project.demo.common.service.quartz.QuartzService;
import com.hy.project.demo.common.model.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rick.wl
 * @date 2021/11/30
 */
@RestController
@RequestMapping("/task")
public class TaskController {
    //
    //@Autowired
    //QuartzService quartzService;
    //
    //@GetMapping("/list.json")
    //public AjaxResult listUsers() throws Throwable {
    //    List<JobInfo> jobs = quartzService.listJobs();
    //    return AjaxResult.success(jobs);
    //}

}
