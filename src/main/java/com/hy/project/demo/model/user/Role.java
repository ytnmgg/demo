package com.hy.project.demo.model.user;

import java.util.List;

/**
 * @author rick.wl
 * @date 2022/10/18
 */
public class Role extends RoleBase {

    private List<Permission> permissions;

    public static Role of(RoleBase roleBase) {
        Role role = new Role();
        role.setRoleId(roleBase.getRoleId());
        role.setRoleName(roleBase.getRoleName());
        role.setRoleKey(roleBase.getRoleKey());
        role.setCreateTime(roleBase.getCreateTime());
        return role;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

}
