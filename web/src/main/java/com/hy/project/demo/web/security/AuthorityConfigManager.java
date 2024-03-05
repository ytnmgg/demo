package com.hy.project.demo.web.security;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.hy.project.demo.auth.facade.service.RsaService;
import com.hy.project.demo.common.constant.RedisConstants;
import com.hy.project.demo.common.service.redis.RedisService;
import com.hy.project.demo.web.config.NacosAuthConfig;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.security.util.InMemoryResource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;

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

    @Autowired
    NacosAuthConfig nacosAuthConfig;

    @DubboReference
    RsaService rsaService;

    @Value("${nacos.cluster}")
    String nacosCluster;

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    private void init() {
        try {
            // 1. 启动时加载一次nacos配置
            LOGGER.info("load authority config from nacos: {}", nacosAuthConfig.getMaps());
            // TODO 分布式唯一性操作
            redisService.remove(RedisConstants.KEY_AUTH_CONF);
            redisService.setHash(RedisConstants.KEY_AUTH_CONF, nacosAuthConfig.getMaps());

            // 2. 配置nacos监听，有变更的时候触发更新redis
            String dataId = "web-prod.yml";
            String group = "DEFAULT_GROUP";
            Properties properties = new Properties();
            properties.put("serverAddr", nacosCluster);
            ConfigService configService = NacosFactory.createConfigService(properties);
            configService.addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    // 接受变更字符串
                    LOGGER.info("receiveConfigInfo: {}", configInfo);
                    YamlPropertiesFactoryBean yamlFb = new YamlPropertiesFactoryBean();
                    yamlFb.setResources(new InMemoryResource(configInfo));
                    Properties p = yamlFb.getObject();

                    // 更新redis
                    Map<String, String> map = new HashMap<>();
                    for (Map.Entry<Object, Object> entry :  p.entrySet()) {
                        String key = String.valueOf(entry.getKey());
                        String value = String.valueOf(entry.getValue());

                        // 过滤无关配置
                        if (!key.startsWith("auth.maps.")) {
                            continue;
                        }

                        // key还包含了配置路径，需要去除
                        key = key.replace("auth.maps.", "");

                        // 为了和初次加载得到的配置一致，删除key里面的斜杠
                        key = key.replaceAll("/", "");

                        map.put(key, value);
                    }

                    LOGGER.info("receiveConfigInfo to redis: {}", map);
                    redisService.remove(RedisConstants.KEY_AUTH_CONF);
                    redisService.setHash(RedisConstants.KEY_AUTH_CONF, map);
                }
                @Override
                public Executor getExecutor() {
                    return null;
                }
            });



        } catch (Throwable e) {
            LOGGER.error("config authority error", e);
        }
    }

    public String getAuthorities(String path) {
        // path是反斜杠分割的路径，nacos或redis中存储的需要去掉这个符号
        path = path.replaceAll("/", "");

        Object auths = redisService.getHash(RedisConstants.KEY_AUTH_CONF, path);
        return null == auths ? null : (String) auths;
    }

}
