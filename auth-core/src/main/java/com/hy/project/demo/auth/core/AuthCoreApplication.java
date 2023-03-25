package com.hy.project.demo.auth.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.hy.project.demo"})
@MapperScan("com.hy.project.demo.auth.core.mybatis.mapper")
public class AuthCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthCoreApplication.class, args);
    }
}
