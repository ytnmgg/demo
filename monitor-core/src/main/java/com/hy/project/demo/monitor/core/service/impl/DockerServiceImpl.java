package com.hy.project.demo.monitor.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.hy.project.demo.common.util.AssertUtil;
import com.hy.project.demo.common.util.DateUtil;
import com.hy.project.demo.monitor.core.util.HttpClientUtil;
import com.hy.project.demo.monitor.facade.model.docker.ContainerBase;
import com.hy.project.demo.monitor.facade.service.DockerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import static com.hy.project.demo.common.exception.DemoExceptionEnum.CONFIGURATION_EXCEPTION;
import static com.hy.project.demo.common.util.DateUtil.STANDARD_STR;

/**
 * @author rick.wl
 * @date 2021/08/28
 */
@Service
public class DockerServiceImpl implements DockerService {

    @Autowired
    private Environment env;

    @Override
    public List<ContainerBase> listContainers() {

        String urlPrefix = env.getProperty("docker.remote.host");
        AssertUtil.notBlank(urlPrefix, CONFIGURATION_EXCEPTION, "未找到配置：docker.remote.host");

        String version = env.getProperty("docker.remote.host.version");
        AssertUtil.notBlank(version, CONFIGURATION_EXCEPTION, "未找到配置：docker.remote.host.version");

        String url = String.format("%s/%s/containers/json", urlPrefix, version);
        Map<String, String> params = new HashMap<>();
        params.put("all", "true");

        String response = HttpClientUtil.getJson(url, params, HttpClientUtil.buildCommonHeaders());

        List<ContainerBase> containerBases = new ArrayList<>();
        if (StringUtils.isBlank(response)) {
            return containerBases;
        }

        List<JSONObject> result = JSON.parseArray(response, JSONObject.class);

        result.forEach(c -> {
            ContainerBase containerBase = new ContainerBase();
            containerBase.setId(c.getString("Id"));

            List<String> names = JSON.parseArray(c.getString("Names"), String.class);
            containerBase.setName(String.join(",", names));

            containerBase.setImageName(c.getString("Image"));
            containerBase.setImageId(c.getString("ImageID"));

            Long date = c.getLong("Created");
            containerBase.setGmtCreate(DateUtil.format(date * 1000, STANDARD_STR));

            containerBase.setCommand(c.getString("Command"));

            Map<String, String> status = new HashMap<>();
            status.put("code", c.getString("State"));
            status.put("description", c.getString("Status"));
            containerBase.setStatus(status);

            containerBases.add(containerBase);
        });

        return containerBases;
    }
}
