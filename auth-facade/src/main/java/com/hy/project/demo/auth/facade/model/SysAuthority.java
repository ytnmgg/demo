package com.hy.project.demo.auth.facade.model;

import java.io.Serializable;


/**
 * @author rick.wl
 * @date 2022/10/17
 */
//public class SysAuthority implements GrantedAuthority, Serializable {
public class SysAuthority implements Serializable {

    private static final long serialVersionUID = 8756337377459824434L;

    private String authority;

    public SysAuthority(String authority) {
        this.authority = authority;
    }
    //
    //@Override
    //public String getAuthority() {
    //    return authority;
    //}

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
