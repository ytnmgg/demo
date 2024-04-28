package com.hy.project.app.example.actions;

import com.hy.project.app.example.ExampleApp;
import com.hy.project.app.sdk.AppAction;
import com.hy.project.app.sdk.model.AppRequest;
import com.hy.project.app.sdk.model.AppResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("SomeAction")
public class SomeAction implements AppAction<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SomeAction.class);

    @Override
    public AppResult<String> call(AppRequest request) {
        LOGGER.info("in some action call");

        AppResult<String> result = new AppResult<>();
        result.setData("some data");
        return result;
    }
}
