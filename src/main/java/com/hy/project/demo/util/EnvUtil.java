package com.hy.project.demo.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author rick.wl
 * @date 2022/10/17
 */
@Component
public class EnvUtil implements InitializingBean {
    @Autowired
    private Environment env;

    private static EnvUtil instance;

    @Override
    public void afterPropertiesSet() throws Exception {
        instance = this;
    }

    private static EnvUtil getInstance() {
        return instance;
    }

    private Environment getEnv() {
        return env;
    }

    public static String getEnvValue(String key) {
        return getInstance().getEnv().getProperty(key);
    }

    public static boolean isDevEnv() {
        String profile = getEnvValue("spring.profiles.active");
        return StringUtils.isNotBlank(profile) && StringUtils.equals(profile, "dev");
    }
}
