package com.hy.project.demo.auth.core;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@EnableDubbo
@SpringBootApplication(scanBasePackages = {"com.hy.project.demo"}, exclude = {SecurityAutoConfiguration.class})
@MapperScan("com.hy.project.demo.auth.core.mybatis.mapper")
public class AuthCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthCoreApplication.class, args);
    }
}
