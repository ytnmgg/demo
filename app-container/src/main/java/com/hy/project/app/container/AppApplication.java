package com.hy.project.app.container;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@EnableDubbo
@SpringBootApplication(scanBasePackages = {"com.hy.project.app.container"}, exclude = {SecurityAutoConfiguration.class})
@MapperScan("com.hy.project.app.container.mybatis.mapper")
public class AppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}
