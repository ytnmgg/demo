package com.hy.project.demo.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author rick.wl
 * @date 2023/03/30
 */
@Component
@ConfigurationProperties(prefix = "auth")
public class NacosAuthConfig {
    private Map<String, String> maps;

    public Map<String, String> getMaps() {
        return maps;
    }

    public void setMaps(Map<String, String> maps) {
        this.maps = maps;
    }
}
