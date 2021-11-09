package com.hy.project.demo.model.sso;

import com.hy.project.demo.model.ToString;

/**
 * @author rick.wl
 * @date 2021/11/04
 */
public class Account extends ToString {

    private static final long serialVersionUID = -3401374378507655148L;

    private String uid;
    private String name;
    private String role;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
