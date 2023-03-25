package com.hy.project.demo.auth.facade.model;

import com.hy.project.demo.common.model.ToString;

/**
 * @author rick.wl
 * @date 2022/10/18
 */
public class Permission extends ToString {

    private static final long serialVersionUID = -1423010870888716672L;

    private String permissionId;
    private String permissionName;
    private String permissionKey;
    private String createTime;

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
