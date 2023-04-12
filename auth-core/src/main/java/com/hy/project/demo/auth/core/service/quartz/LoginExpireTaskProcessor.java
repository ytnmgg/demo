package com.hy.project.demo.auth.core.service.quartz;

import javax.annotation.Resource;

import com.hy.project.demo.auth.core.config.NacosExampleConfig;
import com.hy.project.demo.auth.facade.model.request.RpcRequest;
import com.hy.project.demo.auth.facade.service.TokenService;
import com.hy.project.demo.common.service.quartz.Schedulers;
import com.hy.project.demo.common.service.quartz.TaskMeta;
import com.hy.project.demo.common.service.quartz.TaskProcessContext;
import com.hy.project.demo.common.service.quartz.processors.AbstractTaskProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.hy.project.demo.common.util.EnvUtil.isDevEnv;

/**
 * @author rick.wl
 * @date 2022/11/23
 */
@Component
public class LoginExpireTaskProcessor extends AbstractTaskProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginExpireTaskProcessor.class);

    @Autowired
    TokenService tokenService;

    @Resource
    NacosExampleConfig nacosExampleConfig;

    @Override
    public TaskMeta getTaskMeta() {
        return new LoginExpireTaskMeta();
    }

    @Override
    public void process(TaskProcessContext context) {

        LOGGER.info("nacos example: {}, {}", nacosExampleConfig.getName(), nacosExampleConfig.getAge());

        tokenService.expireTokens(RpcRequest.of(null));
    }

    @Override
    public String getSchedulerName() {
        LOGGER.info("init auth-core quartz scheduler...");

        if (isDevEnv()) {
            LOGGER.info("will not init auth-core quartz scheduler at env of dev...");
            return null;
        }

        try {
            return quartzService.initAndStartScheduler(Schedulers.AUTH_CORE_SCHEDULER);
        } catch (Throwable e) {
            LOGGER.error("initAndStartScheduler failed", e);
            return null;
        }
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
