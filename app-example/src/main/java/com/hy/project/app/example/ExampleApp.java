package com.hy.project.app.example;

import com.hy.project.app.example.config.EnvValues;
import com.hy.project.app.sdk.App;
import com.hy.project.app.sdk.model.AppRequest;
import com.hy.project.app.sdk.model.AppResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Component
public class ExampleApp implements App {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleApp.class);

    @Autowired
    private EnvValues envValues;

    @Autowired
    private Environment environment;

    @Override
    public void init() {
        LOGGER.info("in example app init");
        LOGGER.info("env values: {}", envValues.toString());
    }

    @Override
    public void before(AppRequest request, AppResult<?> appResult) {
        LOGGER.info("in example app before");
    }

    @Override
    public void after(AppRequest request, AppResult<?> appResult) {
        LOGGER.info("in example app after");
    }
}
