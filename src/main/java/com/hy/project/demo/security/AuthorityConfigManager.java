package com.hy.project.demo.security;

import java.util.Properties;

import com.hy.project.demo.service.common.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * @author rick.wl
 * @date 2022/10/18
 */
@Service
public class AuthorityConfigManager implements InitializingBean {
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthorityConfigManager.class);

    private final static String REDIS_AUTH_CONF_KEY = "AUTH";

    @Autowired
    RedisService redisService;

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    private void init() {
        try {
            // 加载配置文件
            Resource resource = new ClassPathResource("authority.conf");
            Properties properties = new Properties();
            properties.load(resource.getInputStream());

            LOGGER.info("load authority config from file: {}", properties);

            properties.forEach((k, v) -> {
                if (null != k && null != v) {
                    String path = (String)k;
                    // 设置到redis中
                    redisService.set(buildRedisKey(path), v);
                }
            });

        } catch (Throwable e) {
            LOGGER.error("config authority error", e);
        }
    }

    public String getAuthorities(String path) {
        Object auths = redisService.get(buildRedisKey(path));
        return null == auths ? null : (String)auths;
    }

    private String buildRedisKey(String path) {
        return String.format("%s_%s", REDIS_AUTH_CONF_KEY, path);
    }

}
