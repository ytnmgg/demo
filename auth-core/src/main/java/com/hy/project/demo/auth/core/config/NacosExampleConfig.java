package com.hy.project.demo.auth.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author rick.wl
 * @date 2023/03/30
 */
@Component
@ConfigurationProperties(prefix = "example")
public class NacosExampleConfig {
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
