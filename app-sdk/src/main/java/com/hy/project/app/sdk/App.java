package com.hy.project.app.sdk;

import com.hy.project.app.sdk.model.AppRequest;
import com.hy.project.app.sdk.model.AppResult;

public interface App {

    void init();

    void before(AppRequest request, AppResult<?> appResult);

    void after(AppRequest request, AppResult<?> appResult);

}
