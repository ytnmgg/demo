package com.hy.project.app.container.manage.decorator;

import com.hy.project.app.container.manage.AppSpringContextDecorateContext;
import com.hy.project.app.container.manage.AppSpringContextDecorator;
import com.hy.project.app.sdk.annotation.AppLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

@Component
public class LoggerDecorator implements AppSpringContextDecorator, BeanPostProcessor {

    private String appName;

    @Override
    public void decorate(AppSpringContextDecorateContext context) {
        this.appName = context.getAppName();
        context.getAppSpringContext().getBeanFactory().addBeanPostProcessor(this);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        final String appName = this.appName;
        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (field.isAnnotationPresent(AppLogger.class)) {
                    ReflectionUtils.makeAccessible(field);
                    Logger logger = LoggerFactory.getLogger("app." + appName);
                    field.set(bean, logger);
                }
            }
        });
        return bean;
    }
}
