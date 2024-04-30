package com.hy.project.app.container.manage;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;

public class AppSpringContextDecorateContext {
    private String appName;

    private ConfigurableApplicationContext containerSpringContext;

    private AnnotationConfigApplicationContext appSpringContext;

    private File jarFile;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public ConfigurableApplicationContext getContainerSpringContext() {
        return containerSpringContext;
    }

    public void setContainerSpringContext(ConfigurableApplicationContext containerSpringContext) {
        this.containerSpringContext = containerSpringContext;
    }

    public AnnotationConfigApplicationContext getAppSpringContext() {
        return appSpringContext;
    }

    public void setAppSpringContext(AnnotationConfigApplicationContext appSpringContext) {
        this.appSpringContext = appSpringContext;
    }

    public File getJarFile() {
        return jarFile;
    }

    public void setJarFile(File jarFile) {
        this.jarFile = jarFile;
    }
}
