package com.hy.project.demo.model.user;

import java.io.Serializable;
import java.util.List;

/**
 * @author rick.wl
 * @date 2022/10/18
 */
public class Role implements Serializable {

    private static final long serialVersionUID = 7763728662242275215L;

    private String roleId;
    private String roleName;
    private String roleKey;

    private List<Permission> permissions;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
