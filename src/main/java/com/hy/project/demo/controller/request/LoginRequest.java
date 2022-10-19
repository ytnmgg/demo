package com.hy.project.demo.controller.request;

/**
 * @author rick.wl
 * @date 2021/11/08
 */
public class LoginRequest {
    private String username;
    private String password;
    private String callback;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }
}
