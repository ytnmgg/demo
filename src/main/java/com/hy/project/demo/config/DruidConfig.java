package com.hy.project.demo.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.sql.DataSource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.alibaba.druid.support.http.StatViewServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author rick.wl
 * @date 2022/09/19
 */
@Configuration
public class DruidConfig implements ServletContextInitializer {
    private final static Logger LOGGER = LoggerFactory.getLogger(DruidConfig.class);

    @Autowired
    Environment environment;

    @Bean
    @ConfigurationProperties("spring.datasource.druid.master")
    public DataSource masterDataSource() throws Throwable {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.slave")
    @ConditionalOnProperty(prefix = "spring.datasource.druid.slave", name = "enabled", havingValue = "true")
    public DataSource slaveDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // 注册监控页面servlet
        ServletRegistration initServlet = servletContext
            .addServlet("statViewServlet", StatViewServlet.class);
        initServlet.addMapping(environment.getProperty("spring.datasource.druid.master.stat-view-servlet.url-pattern"));
        initServlet.setInitParameter("loginUsername",
            environment.getProperty("spring.datasource.druid.master.stat-view-servlet.login-username"));
        initServlet.setInitParameter("loginPassword",
            environment.getProperty("spring.datasource.druid.master.stat-view-servlet.login-password"));
        initServlet.setInitParameter("resetEnable", "false");
    }
}
