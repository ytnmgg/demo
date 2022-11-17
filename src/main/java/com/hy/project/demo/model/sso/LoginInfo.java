package com.hy.project.demo.model.sso;

import java.io.Serializable;

/**
 * @author rick.wl
 * @date 2022/11/16
 */
public class LoginInfo implements Serializable {
    private static final long serialVersionUID = 8982549039957269336L;
    private String token;
    private String userId;
    private String userName;
    private String loginIp;
    private String loginTime;
    private String userAgent;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
