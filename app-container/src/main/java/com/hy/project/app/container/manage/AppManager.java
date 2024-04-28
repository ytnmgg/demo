package com.hy.project.app.container.manage;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class AppManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppManager.class);

    @PostConstruct
    public void init() {
        // TODO：后续可以改走配置
        List<String> appNames = Lists.newArrayList("app-example-1.0.0");

        // TODO: 后续可以改走多线程
        appNames.forEach(this::loadApp);
    }

    private void loadApp(String appName) {
        String appDir = "/Users/ytnmgg/work";
        Path basePath = Paths.get(appDir);
        Path appPath = basePath.resolve(appName + ".jar");
        LOGGER.info("start to load app: {}", appPath);

        File jarFile = appPath.toFile();
        if (!jarFile.exists()) {
            LOGGER.error("jar file not exists: {}", appPath);
            return;
        }



    }

    public static void main(String[] args) {
        AppManager app = new AppManager();
        app.init();
    }
}
