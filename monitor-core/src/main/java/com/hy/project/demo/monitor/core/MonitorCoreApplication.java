package com.hy.project.demo.monitor.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hy.project.demo.monitor.core.mybatis.mapper")
public class MonitorCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorCoreApplication.class, args);
    }

}
