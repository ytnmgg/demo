package com.hy.project.demo.service;

import java.util.List;

import com.hy.project.demo.model.docker.ContainerBase;

/**
 * @author rick.wl
 * @date 2021/08/28
 */
public interface DockerService {

    /**
     * 查询container列表
     *
     * @return 结果
     */
    List<ContainerBase> listContainers();
}
