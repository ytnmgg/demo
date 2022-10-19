package com.hy.project.demo.service.user.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;

import com.google.common.collect.Lists;
import com.hy.project.demo.exception.DemoExceptionEnum;
import com.hy.project.demo.model.PageResult;
import com.hy.project.demo.model.sso.User;
import com.hy.project.demo.model.user.Permission;
import com.hy.project.demo.model.user.Role;
import com.hy.project.demo.repository.PermissionRepository;
import com.hy.project.demo.repository.RolePermissionRelationRepository;
import com.hy.project.demo.repository.RoleRepository;
import com.hy.project.demo.repository.UserRepository;
import com.hy.project.demo.repository.UserRoleRelationRepository;
import com.hy.project.demo.security.LoginUser;
import com.hy.project.demo.security.SysUser;
import com.hy.project.demo.service.common.RedisService;
import com.hy.project.demo.service.user.UserService;
import com.hy.project.demo.util.AssertUtil;
import com.hy.project.demo.util.EnvUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.hy.project.demo.exception.DemoExceptionEnum.INVALID_PARAM_EXCEPTION;
import static com.hy.project.demo.exception.DemoExceptionEnum.UNEXPECTED;
import static com.hy.project.demo.util.SsoUtil.createRedisUserInfoKey;

/**
 * @author rick.wl
 * @date 2021/11/05
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String DEFAULT_REDIS_USER_INFO_TIMEOUT_MINUTES = "1440";

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRoleRelationRepository userRoleRelationRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    RolePermissionRelationRepository rolePermissionRelationRepository;

    @Autowired
    RedisService redisService;

    @Override
    public User getUserById(String uid) {
        return userRepository.getById(uid);
    }

    @Override
    public SysUser loadSysUserByName(String name) {
        SysUser sysUser = userRepository.findByName(name);

        // 补充role和permission信息
        getRoleAndPermission(sysUser);

        return sysUser;
    }

    @Override
    public SysUser loadSysUserByUserId(String userId) {
        SysUser sysUser = userRepository.findByUserId(userId);

        // 补充role和permission信息
        getRoleAndPermission(sysUser);

        return sysUser;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateSysUser(SysUser sysUser) {
        SysUser user = userRepository.lockByUserId(sysUser.getUserId());
        AssertUtil.notNull(user, INVALID_PARAM_EXCEPTION, "can not find user: %s", sysUser.getUserId());

        userRepository.updateUser(sysUser);
    }

    @Override
    public void touchUser(SysUser user) {
        String key = createRedisUserInfoKey(user.getUserId());

        if (!redisService.exists(key)) {
            LOGGER.info("user not exist in cache, create new cache");
            cacheUser(user);
            return;
        }

        // 更新
        // TODO. 暂无需要更新的东西，先刷新一下过期时间
        redisService.expire(key, getUserExpireTime(), TimeUnit.MINUTES);
    }

    @Override
    public SysUser touchUser(String userId) {
        String key = createRedisUserInfoKey(userId);

        Object user = redisService.get(key);

        if (null != user) {
            // 刷新缓存时间
            redisService.expire(key, getUserExpireTime(), TimeUnit.MINUTES);
            return JSONObject.toJavaObject((JSONObject)user, SysUser.class);
        } else {

            // 从db捞取用户
            SysUser sysUser = loadSysUserByUserId(userId);
            AssertUtil.notNull(sysUser, UNEXPECTED, "未找到用户信息：%s", userId);

            // 保存用户到缓存
            cacheUser(sysUser);

            return sysUser;
        }
    }

    @Override
    public SysUser getCacheUser(String userId) {
        return null;
    }

    @Override
    public SysUser getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication && !(authentication.getPrincipal() instanceof LoginUser)) {
            return null;
        }
        LoginUser loginUser = (LoginUser)authentication.getPrincipal();
        return loginUser.getUser();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createNewUser(String name, String password) {
        SysUser sysUser = new SysUser();
        sysUser.setUserType("00");
        sysUser.setNickName(name);
        sysUser.setUserName(name);
        sysUser.setPassword(password);
        sysUser.setStatus("0");
        sysUser.setDelFlag("0");


        // TODO, 干掉，用uk行锁解决
        SysUser existed = userRepository.findByName(name);
        AssertUtil.isNull(existed, INVALID_PARAM_EXCEPTION, "用户已经存在: %s", name);

        String userId = userRepository.insert(sysUser);

        // 保存用户角色关系 TODO mock
        userRoleRelationRepository.insert(userId, "1120220909000001");

        return userId;
    }

    @Override
    public PageResult<List<SysUser>> pageListUsers(int pageIndex, int pageSize) {
        return userRepository.pageList(pageIndex, pageSize);
    }

    private void cacheUser(SysUser user) {
        String key = createRedisUserInfoKey(user.getUserId());
        boolean result = redisService.set(key, user, getUserExpireTime(), TimeUnit.MINUTES);
        AssertUtil.isTrue(result, DemoExceptionEnum.REDIS_EXCEPTION, "cache user info error");
    }

    private Long getUserExpireTime() {
        String userExpireTime = EnvUtil.getEnvValue("sso.user.expireTime");
        if (StringUtils.isBlank(userExpireTime)) {
            LOGGER.error("failed to get config: sso.user.expireTime, use default value");
            userExpireTime = DEFAULT_REDIS_USER_INFO_TIMEOUT_MINUTES;
        }
        return Long.parseLong(userExpireTime);
    }

    private void getRoleAndPermission(SysUser user) {
        if (null == user) {
            return;
        }

        // TODO 缓存起来

        List<String> roleIds = userRoleRelationRepository.findRolesByUserId(user.getUserId());
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }

        List<Role> roles = roleRepository.findByRoleIds(roleIds);

        user.setRoles(roles);

        List<String> permissionIds = rolePermissionRelationRepository.findPermissionsByRoleIds(roleIds);
        if (CollectionUtils.isEmpty(permissionIds)) {
            return;
        }

        List<Permission> permissions = permissionRepository.findByPermissionIds(permissionIds);
        user.setPermissions(permissions);

    }
}
