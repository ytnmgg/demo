package com.hy.project.demo.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * @author rick.wl
 * @date 2022/09/07
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //SysUser user = userService.selectUserByUserName(username);
        //if (StringUtils.isNull(user))
        //{
        //    log.info("登录用户：{} 不存在.", username);
        //    throw new ServiceException("登录用户：" + username + " 不存在");
        //}
        //else if (UserStatus.DELETED.getCode().equals(user.getDelFlag()))
        //{
        //    log.info("登录用户：{} 已被删除.", username);
        //    throw new ServiceException("对不起，您的账号：" + username + " 已被删除");
        //}
        //else if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
        //{
        //    log.info("登录用户：{} 已被停用.", username);
        //    throw new ServiceException("对不起，您的账号：" + username + " 已停用");
        //}
        //
        //passwordService.validate(user);
        //

        SysUser mock = new SysUser();
        mock.setUserId("1");

        return createLoginUser(mock);

    }

    public UserDetails createLoginUser(SysUser user)
    {
        return new LoginUser(user.getUserId(),  user, null);
    }
}
