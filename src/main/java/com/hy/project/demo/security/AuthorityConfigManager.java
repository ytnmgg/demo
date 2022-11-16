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

import static com.hy.project.demo.constant.RedisConstants.KEY_AUTH_CONF;

/**
 * @author rick.wl
 * @date 2022/10/18
 */
@Service
public class AuthorityConfigManager implements InitializingBean {
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthorityConfigManager.class);

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

            // TODO 分布式唯一性操作
            redisService.setHash(KEY_AUTH_CONF, properties);

        } catch (Throwable e) {
            LOGGER.error("config authority error", e);
        }
    }

    public String getAuthorities(String path) {
        Object auths = redisService.getHash(KEY_AUTH_CONF, path);
        return null == auths ? null : (String)auths;
    }

}
