package com.hy.project.demo.quartz.processors;

import com.hy.project.demo.quartz.TaskMeta;
import com.hy.project.demo.quartz.TaskProcessContext;
import com.hy.project.demo.service.auth.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author rick.wl
 * @date 2022/11/23
 */
@Component
public class LoginExpireTaskProcessor extends AbstractTaskProcessor {

    @Autowired
    TokenService tokenService;

    @Override
    public TaskMeta getTaskMeta() {
        return TaskMeta.LOGIN_EXPIRE;
    }

    @Override
    public void process(TaskProcessContext context) {
        tokenService.expireTokens();
    }
}
