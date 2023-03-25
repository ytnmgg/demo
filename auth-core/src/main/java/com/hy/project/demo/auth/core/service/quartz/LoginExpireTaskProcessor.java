package com.hy.project.demo.auth.core.service.quartz;

import com.hy.project.demo.auth.facade.service.TokenService;
import com.hy.project.demo.common.service.quartz.TaskMeta;
import com.hy.project.demo.common.service.quartz.TaskProcessContext;
import com.hy.project.demo.common.service.quartz.processors.AbstractTaskProcessor;
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
        return new LoginExpireTaskMeta();
    }

    @Override
    public void process(TaskProcessContext context) {
        tokenService.expireTokens();
    }

    private static class LoginExpireTaskMeta implements TaskMeta {

        @Override
        public String getTaskCode() {
            return "LOGIN_EXPIRE";
        }

        @Override
        public String getJobId() {
            return "JOB_LOGIN_EXPIRE";
        }

        @Override
        public String getJobGroup() {
            return "DEFAULT_JOB_GROUP";
        }

        @Override
        public String getTriggerId() {
            return "TRIGGER_LOGIN_EXPIRE";
        }

        @Override
        public String getTriggerGroup() {
            return "DEFAULT_TRIGGER_GROUP";
        }

        @Override
        public String getDefaultCronExpression() {
            return "0 */1 * * * ?";
        }
    }
}
