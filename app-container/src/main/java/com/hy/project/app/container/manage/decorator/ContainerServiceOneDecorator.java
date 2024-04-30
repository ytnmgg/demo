package com.hy.project.app.container.manage.decorator;

import com.hy.project.app.container.manage.AppSpringContextDecorateContext;
import com.hy.project.app.container.manage.AppSpringContextDecorator;
import com.hy.project.app.sdk.service.ContainerServiceOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContainerServiceOneDecorator implements AppSpringContextDecorator {

    @Autowired
    ContainerServiceOne containerServiceOne;

    @Override
    public void decorate(AppSpringContextDecorateContext context) {

        // 注入app依赖的容器bean
        context.getAppSpringContext().getBeanFactory().registerSingleton("containerServiceOne",
                containerServiceOne);
    }
}
