package com.hy.project.demo.web.controller.monitor;

import java.util.List;

import com.hy.project.demo.monitor.facade.model.docker.ContainerBase;
import com.hy.project.demo.monitor.facade.service.DockerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rick.wl
 * @date 2021/08/28
 */
@RestController
@RequestMapping("/docker")
public class DockerController {
    //
    //private final static Logger LOGGER = LoggerFactory.getLogger(DockerController.class);
    //
    //@Autowired
    //DockerService dockerService;
    //
    //@GetMapping("/container/list.json")
    //public List<ContainerBase> listContainers() {
    //    return dockerService.listContainers();
    //}

}