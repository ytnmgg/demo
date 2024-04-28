package com.hy.project.app.sdk;

import com.hy.project.app.sdk.model.AppRequest;
import com.hy.project.app.sdk.model.AppResult;

public interface AppAction<T> {

    AppResult<T> call(AppRequest request);
}
