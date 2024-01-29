package com.hy.project.demo.monitor.core;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@EnableDubbo
@SpringBootApplication(scanBasePackages = {"com.hy.project.demo"}, exclude = {SecurityAutoConfiguration.class})
@MapperScan("com.hy.project.demo.monitor.core.mybatis.mapper")
public class MonitorCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorCoreApplication.class, args);
    }

}
