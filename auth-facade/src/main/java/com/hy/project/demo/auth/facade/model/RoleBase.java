package com.hy.project.demo.auth.facade.model;

import com.hy.project.demo.common.model.ToString;

/**
 * @author rick.wl
 * @date 2022/10/18
 */
public class RoleBase extends ToString {

    private static final long serialVersionUID = -1728092905946096429L;

    private String roleId;
    private String roleName;
    private String roleKey;
    private String createTime;

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
