package com.hy.project.app.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvValues {

    @Value("${key_not_exist:default_value_123}")
    private String keyFromEnvNotExist;

    @Value("${my_env_key_01:}")
    private String keyFromEnvExist;

    @Value("${env_config.app_key_01}")
    private String keyFromConfigFile;

    public String getKeyFromEnvNotExist() {
        return keyFromEnvNotExist;
    }

    public void setKeyFromEnvNotExist(String keyFromEnvNotExist) {
        this.keyFromEnvNotExist = keyFromEnvNotExist;
    }

    public String getKeyFromEnvExist() {
        return keyFromEnvExist;
    }

    public void setKeyFromEnvExist(String keyFromEnvExist) {
        this.keyFromEnvExist = keyFromEnvExist;
    }

    public String getKeyFromConfigFile() {
        return keyFromConfigFile;
    }

    public void setKeyFromConfigFile(String keyFromConfigFile) {
        this.keyFromConfigFile = keyFromConfigFile;
    }

    @Override
    public String toString() {
        return "EnvValues [keyFromEnvNotExist=" + keyFromEnvNotExist +
                ", keyFromEnvExist=" + keyFromEnvExist +
                ", keyFromConfigFile=" + keyFromConfigFile + "]";
    }
}
