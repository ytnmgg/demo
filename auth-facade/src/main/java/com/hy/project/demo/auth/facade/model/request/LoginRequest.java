package com.hy.project.demo.auth.facade.model.request;

import com.hy.project.demo.common.model.BaseRequest;

/**
 * @author rick.wl
 * @date 2023/04/02
 */
public class LoginRequest extends BaseRequest {
    private static final long serialVersionUID = 471444906244860299L;

    private String name;
    private String password;
    private String clientIp;
    private String userAgent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
