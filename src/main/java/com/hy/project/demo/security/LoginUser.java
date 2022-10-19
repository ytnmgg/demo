package com.hy.project.demo.security;

import java.util.ArrayList;
import java.util.List;

import com.hy.project.demo.model.user.Permission;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * 登录用户身份权限
 * spring security框架使用，业务代码用SysUser
 *
 * @author rick.wl
 * @date 2022/09/07
 */
@Component
public class LoginUser implements UserDetails {

    private static final long serialVersionUID = -8083407673475502481L;

    /**
     * 用户信息
     */
    private SysUser user;

    /**
     * 权限列表
     */
    private List<SysAuthority> authorities;

    public LoginUser() {
        super();
    }

    public LoginUser(SysUser user) {
        this.user = user;
        authorities = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(user.getPermissions())) {
            for (Permission permission : user.getPermissions()) {
                authorities.add(new SysAuthority(permission.getPermissionKey()));
            }
        }
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    @Override
    public List<SysAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.getUser().getPassword();
    }

    @Override
    public String getUsername() {
        return this.getUser().getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setAuthorities(List<SysAuthority> authorities) {
        this.authorities = authorities;
    }
}
