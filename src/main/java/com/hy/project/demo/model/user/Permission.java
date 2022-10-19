package com.hy.project.demo.model.user;

import java.io.Serializable;

/**
 * @author rick.wl
 * @date 2022/10/18
 */
public class Permission implements Serializable {

    private static final long serialVersionUID = 2412361779427331889L;

    private String permissionId;
    private String permissionName;
    private String permissionKey;

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionKey() {
        return permissionKey;
    }

    public void setPermissionKey(String permissionKey) {
        this.permissionKey = permissionKey;
    }
}
