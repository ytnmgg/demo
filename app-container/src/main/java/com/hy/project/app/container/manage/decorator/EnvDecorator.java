package com.hy.project.app.container.manage.decorator;

import com.hy.project.app.container.manage.AppSpringContextDecorateContext;
import com.hy.project.app.container.manage.AppSpringContextDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

@Component
public class EnvDecorator implements AppSpringContextDecorator {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnvDecorator.class);


    @Override
    public void decorate(AppSpringContextDecorateContext context) {
        try {
            // 注入app jar包下面的配置文件：application.yml
            URL[] jarUrls = new URL[]{context.getJarFile().toURI().toURL()};
            URLClassLoader urlClassLoader = new URLClassLoader(jarUrls);
            URL configUrl = urlClassLoader.findResource("application.yml");
            Properties properties = new Properties();
            if (null != configUrl) {
                UrlResource configResource = new UrlResource(configUrl.toURI());
                YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
                yamlPropertiesFactoryBean.setResources(configResource);
                properties.putAll(yamlPropertiesFactoryBean.getObject());
            }
            PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer =
                    new PropertySourcesPlaceholderConfigurer();
            propertySourcesPlaceholderConfigurer.setProperties(properties);
            propertySourcesPlaceholderConfigurer.setEnvironment(context.getContainerSpringContext().getEnvironment());
            context.getAppSpringContext().addBeanFactoryPostProcessor(propertySourcesPlaceholderConfigurer);
        } catch (Throwable e) {
            LOGGER.error("env decorator error", e);
        }
    }
}
