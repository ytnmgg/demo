package com.hy.project.app.container.manage;

import com.google.common.collect.Lists;
import com.hy.project.app.sdk.service.ContainerServiceOne;
import com.hy.project.app.sdk.App;
import com.hy.project.app.sdk.AppAction;
import com.hy.project.app.sdk.model.AppRequest;
import com.hy.project.app.sdk.model.AppResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

@Component
public class AppManager implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppManager.class);

    private ConfigurableApplicationContext applicationContext;

    private Map<String, AnnotationConfigApplicationContext> appApplicationContexts = new HashMap<>();
    private Map<String, DemoAppClassLoader> appClassLoaders = new HashMap<>();

    @PostConstruct
    public void init() {
        // TODO：后续可以改走配置
        List<String> appNames = Lists.newArrayList("app-example-1.0.0");

        // TODO: 后续可以改走多线程
        appNames.forEach(this::loadApp);
    }

    private void loadApp(String appName) {
        try {

            String appDir = "D:\\work\\project\\demo\\app-example\\target\\";
            Path basePath = Paths.get(appDir);
            Path appPath = basePath.resolve(appName + ".jar");
            LOGGER.info("start to load app: {}", appPath);

            File jarFile = appPath.toFile();
            if (!jarFile.exists()) {
                LOGGER.error("jar file not exists: {}", appPath);
                return;
            }

            JarFile jar = new JarFile(jarFile);
            Manifest manifest = jar.getManifest();
            String mainClassName = manifest.getMainAttributes().getValue("App-Class");

            URL[] jarUrls = new URL[]{jarFile.toURI().toURL()};

            DemoAppClassLoader demoAppClassLoader = new DemoAppClassLoader(jarUrls, Thread.currentThread().getContextClassLoader());
            appClassLoaders.put(appName, demoAppClassLoader);

            Class<?> mainClass = demoAppClassLoader.loadClass(mainClassName);

            if (!App.class.isAssignableFrom(mainClass)) {
                throw new RuntimeException("invalid main class");
            }

            AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
            appApplicationContexts.put(appName, annotationConfigApplicationContext);

            annotationConfigApplicationContext.setClassLoader(demoAppClassLoader);
//            annotationConfigApplicationContext.setResourceLoader(new DefaultResourceLoader(demoAppClassLoader));
            Arrays.stream(applicationContext.getEnvironment().getActiveProfiles()).forEach(profile -> {
                annotationConfigApplicationContext.getEnvironment().addActiveProfile(profile);
            });

            // 装饰context
            AppSpringContextDecorateContext decorateContext = new AppSpringContextDecorateContext();
            decorateContext.setAppName(appName);
            decorateContext.setAppSpringContext(annotationConfigApplicationContext);
            decorateContext.setContainerSpringContext(applicationContext);
            decorateContext.setJarFile(jarFile);
            Map<String, AppSpringContextDecorator> decorators = applicationContext.getBeansOfType(AppSpringContextDecorator.class);
            decorators.forEach((name, decorator) ->decorator.decorate(decorateContext));

            // scan & refresh beans
            annotationConfigApplicationContext.scan(mainClass.getPackage().getName());
            annotationConfigApplicationContext.refresh();

            // 初始化app
            Map<String, App> beans = annotationConfigApplicationContext.getBeansOfType(App.class);
            if (beans.keySet().size() != 1) {
                throw new RuntimeException("invalid app number");
            }
            App app = beans.values().stream().findFirst().get();
            app.init();

            // 调用app，应该暴露给controller或其它服务，放到这里主要是作为调试用
            callApp(appName);

        } catch (Throwable e) {
            LOGGER.error("load app error", e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    public void callApp(String appName) {
//        DemoAppClassLoader classLoader = appClassLoaders.get(appName);
//        Thread.currentThread().setContextClassLoader(classLoader);
        MDC.put("appName", appName);

        AnnotationConfigApplicationContext applicationContext = appApplicationContexts.get(appName);
        AppAction<String> someAction = (AppAction<String>) applicationContext.getBean("SomeAction");
        AppResult<String> result = someAction.call(new AppRequest());
        LOGGER.info(result.getData());
    }
}
