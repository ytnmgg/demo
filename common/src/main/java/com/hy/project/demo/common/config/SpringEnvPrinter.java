package com.hy.project.demo.common.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

/**
 * @author rick.wl
 * @date 2023/03/24
 */
@Component
public class SpringEnvPrinter {
    private final static Logger LOGGER = LoggerFactory.getLogger(SpringEnvPrinter.class);

    @Resource
    private ConfigurableEnvironment springEnv;

    @PostConstruct
    public void init() {
        MutablePropertySources propSrcs = springEnv.getPropertySources();
        // 获取所有配置
        Map<String, String> props = StreamSupport.stream(propSrcs.spliterator(), false)
            .filter(ps -> ps instanceof EnumerablePropertySource)
            .map(ps -> ((EnumerablePropertySource)ps).getPropertyNames())
            .flatMap(Arrays::stream)
            .distinct()
            .collect(Collectors.toMap(Function.identity(), springEnv::getProperty));

        // key 和 value 之间的最小间隙
        int interval = 20;
        int max = props.keySet().stream()
            .max(Comparator.comparingInt(String::length))
            .orElse("")
            .length();

        // 打印
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        props.keySet().stream()
            .sorted()
            .forEach(k -> {
                int i = max - k.length() + interval;
                String join = String.join("", Collections.nCopies(i, " "));
                sb.append(k);
                sb.append("=");
                sb.append(props.get(k));
                sb.append("\n");
                //LOGGER.info("{}{}{}", k, join, props.get(k));
            });
        LOGGER.info("Springboot Environment variables: {}", sb.toString());
    }
}
