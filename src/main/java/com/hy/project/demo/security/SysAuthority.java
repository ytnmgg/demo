package com.hy.project.demo.security;

import java.io.Serializable;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author rick.wl
 * @date 2022/10/17
 */
public class SysAuthority implements GrantedAuthority, Serializable {

    private static final long serialVersionUID = 8756337377459824434L;

    private String authority;

    public SysAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
