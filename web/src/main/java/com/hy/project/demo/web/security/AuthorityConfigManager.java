package com.hy.project.demo.web.security;

import java.util.Properties;

import com.hy.project.demo.auth.facade.service.RsaService;
import com.hy.project.demo.common.constant.RedisConstants;
import com.hy.project.demo.common.service.redis.RedisService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

/**
 * @author rick.wl
 * @date 2022/10/18
 */
@Service
public class AuthorityConfigManager implements InitializingBean {
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthorityConfigManager.class);

    @Autowired
    RedisService redisService;

    //@Resource
    //FeignRsaServiceClient rsaServiceClient;

    @DubboReference
    RsaService rsaService;

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    private void init() {
        try {
            // 加载配置文件
            // TODO @RICK 走配置中心
            org.springframework.core.io.Resource resource = new ClassPathResource("authority.conf");
            Properties properties = new Properties();
            properties.load(resource.getInputStream());

            LOGGER.info("load authority config from file: {}", properties);

            // TODO 分布式唯一性操作
            redisService.setHash(RedisConstants.KEY_AUTH_CONF, properties);

        } catch (Throwable e) {
            LOGGER.error("config authority error", e);
        }
    }

    public String getAuthorities(String path) {
        Object auths = redisService.getHash(RedisConstants.KEY_AUTH_CONF, path);
        return null == auths ? null : (String)auths;
    }

}
