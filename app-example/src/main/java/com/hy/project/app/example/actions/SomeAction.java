package com.hy.project.app.example.actions;

import com.hy.project.app.example.service.SomeService;
import com.hy.project.app.sdk.AppAction;
import com.hy.project.app.sdk.annotation.AppLogger;
import com.hy.project.app.sdk.model.AppRequest;
import com.hy.project.app.sdk.model.AppResult;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("SomeAction")
public class SomeAction implements AppAction<String> {

    @Autowired
    SomeService someService;

    @AppLogger
    Logger logger;

    @Override
    public AppResult<String> call(AppRequest request) {
        String hello = someService.sayHello("SomeAction");
        logger.info("in some action call: " + hello);

        AppResult<String> result = new AppResult<>();
        result.setData("some data " + hello);
        return result;
    }
}
