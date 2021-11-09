package com.hy.project.demo.service.common.impl;

import java.util.Properties;

import com.hy.project.demo.service.common.DynamicConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

/**
 * @author rick.wl
 * @date 2021/09/08
 */
@Service
public class DynamicConfigServiceImpl implements DynamicConfigService {
    private final static Logger LOGGER = LoggerFactory.getLogger(DynamicConfigServiceImpl.class);

    @Autowired
    Environment env;

    @Override
    public String get(String key) {
        try {
            String filePath = env.getProperty("dynamic.config.path");
            FileSystemResource fileSystemResource = new FileSystemResource(filePath);
            Properties properties = PropertiesLoaderUtils.loadProperties(fileSystemResource);
            return properties.getProperty(key);

        } catch (Throwable e) {
            LOGGER.error("get dynamic property exception", e);
        }
        return null;
    }
}
