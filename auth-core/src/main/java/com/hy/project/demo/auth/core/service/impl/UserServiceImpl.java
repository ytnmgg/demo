package com.hy.project.demo.auth.core.service.impl;
import java.util.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;

import com.hy.project.demo.auth.core.mybatis.entity.UserRoleRelationDO;
import com.hy.project.demo.auth.core.repository.PermissionRepository;
import com.hy.project.demo.auth.core.repository.RolePermissionRelationRepository;
import com.hy.project.demo.auth.core.repository.RoleRepository;
import com.hy.project.demo.auth.core.repository.UserRepository;
import com.hy.project.demo.auth.core.repository.UserRoleRelationRepository;
import com.hy.project.demo.auth.facade.model.RoleBase;
import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.auth.facade.model.request.CreateNewUserRequest;
import com.hy.project.demo.common.model.RpcRequest;
import com.hy.project.demo.auth.facade.model.request.UpdateUserPasswordRequest;
import com.hy.project.demo.auth.facade.model.request.UpdateUserRoleRequest;
import com.hy.project.demo.common.model.RpcResult;
import com.hy.project.demo.auth.facade.service.RsaService;
import com.hy.project.demo.auth.facade.service.UserService;
import com.hy.project.demo.common.exception.DemoExceptionEnum;
import com.hy.project.demo.common.model.PageRequest;
import com.hy.project.demo.common.model.PageResult;
import com.hy.project.demo.common.service.redis.RedisService;
import com.hy.project.demo.common.util.AssertUtil;
import com.hy.project.demo.common.util.EnvUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import static com.hy.project.demo.auth.core.util.SsoUtil.createRedisUserInfoKey;
import static com.hy.project.demo.common.exception.DemoExceptionEnum.INVALID_PARAM_EXCEPTION;
import static com.hy.project.demo.common.exception.DemoExceptionEnum.UNEXPECTED;

/**
 * @author rick.wl
 * @date 2021/11/05
 */
@Service
@DubboService
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

    @Autowired
    RsaService rsaService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Override
    public RpcResult<SysUser> loadSysUserByName(RpcRequest<String> request) {
        SysUser sysUser = userRepository.findByName(request.getData());

        // 补充role信息
        addRoles(sysUser);

        return RpcResult.success(sysUser);
    }

    @Override
    public RpcResult<SysUser> loadSysUserByUserId(RpcRequest<String> request) {
        SysUser sysUser = userRepository.findByUserId(request.getData());

        // 补充role信息
        addRoles(sysUser);

        return RpcResult.success(sysUser);
    }

    //@Transactional(rollbackFor = Exception.class)
    @Override
    public RpcResult<Void> updateSysUser(RpcRequest<SysUser> request) {
        SysUser sysUser = request.getData();
        transactionTemplate.execute(
            new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    SysUser user = userRepository.lockByUserId(sysUser.getUserId());
                    AssertUtil.notNull(user, INVALID_PARAM_EXCEPTION, "can not find user: %s"
                        , sysUser.getUserId());

                    userRepository.updateUser(sysUser);
                }
            }
        );
        return RpcResult.success(null);
    }

    @Override
    public RpcResult<Void> touchUser(RpcRequest<SysUser> request) {
        SysUser user = request.getData();
        String key = createRedisUserInfoKey(user.getUserId());

        if (!redisService.exists(key)) {
            LOGGER.info("user not exist in cache, create new cache");
            cacheUser(user);
            return RpcResult.success(null);
        }

        // 更新
        // TODO. 暂无需要更新的东西，先刷新一下过期时间
        redisService.expire(key, getUserExpireTime(), TimeUnit.MINUTES);
        return RpcResult.success(null);
    }

    @Override
    public RpcResult<SysUser> touchUserById(RpcRequest<String> request) {
        String userId = request.getData();
        String key = createRedisUserInfoKey(userId);

        Object user = redisService.get(key);

        if (null != user) {
            // 刷新缓存时间
            redisService.expire(key, getUserExpireTime(), TimeUnit.MINUTES);
            return RpcResult.success(JSONObject.toJavaObject((JSONObject)user, SysUser.class));
        } else {

            // 从db捞取用户
            RpcResult<SysUser> sysUserResult = loadSysUserByUserId(RpcRequest.of(userId));
            SysUser sysUser = sysUserResult.getData();
            AssertUtil.notNull(sysUser, UNEXPECTED, "未找到用户信息：%s", userId);

            // 保存用户到缓存
            cacheUser(sysUser);

            return RpcResult.success(sysUser);
        }
    }

    @Override
    public RpcResult<SysUser> getCacheUser(RpcRequest<SysUser> request) {
        return null;
    }

    @Override
    public RpcResult<Void> clearUser(RpcRequest<String> request) {
        String key = createRedisUserInfoKey(request.getData());
        redisService.remove(key);
        return RpcResult.success(null);
    }

    //@Transactional(rollbackFor = Exception.class)
    @Override
    public RpcResult<Void> deleteUser(RpcRequest<String> request) {
        String userId = request.getData();
        transactionTemplate.execute(
            new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    userRepository.deleteByUserId(userId);
                    userRoleRelationRepository.deleteByUserId(userId);
                }
            }
        );
        return RpcResult.success(null);
    }

    //@Transactional(rollbackFor = Exception.class)
    @Override
    public RpcResult<Void> updateUserPassword(RpcRequest<UpdateUserPasswordRequest> request) {
        transactionTemplate.execute(
            new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    SysUser user = userRepository.lockByUserId(request.getData().getUserId());
                    AssertUtil.notNull(user, INVALID_PARAM_EXCEPTION, "can not find user: %s",
                        request.getData().getUserId());

                    // 用RSA私钥解密前端加密后的用户登录密码
                    RpcResult<String> result = rsaService.decryptByPrivateKey(
                        RpcRequest.of(Base64.decodeBase64(request.getData().getPassword())));
                    // 加密用户密码
                    String encoded = bCryptPasswordEncoder.encode(result.getData());

                    user.setPassword(encoded);
                    userRepository.updateUser(user);
                }
            }
        );
        return RpcResult.success(null);
    }

    //@Transactional(rollbackFor = Exception.class)
    @Override
    public RpcResult<Void> updateUserRoles(RpcRequest<UpdateUserRoleRequest> request) {
        String userId = request.getData().getUserId();
        List<String> roleIds = request.getData().getRoleIds();
        transactionTemplate.execute(
            new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    SysUser user = userRepository.lockByUserId(userId);
                    AssertUtil.notNull(user, INVALID_PARAM_EXCEPTION, "can not find user: %s", userId);

                    userRoleRelationRepository.deleteByUserId(userId);

                    if (CollectionUtils.isNotEmpty(roleIds)) {
                        createUserRoleRelations(userId, roleIds);
                    }
                }
            }
        );
        return RpcResult.success(null);
    }

    //@Transactional(rollbackFor = Exception.class)
    @Override
    public RpcResult<String> createNewUser(RpcRequest<CreateNewUserRequest> request) {
        String name = request.getData().getName();
        String password = request.getData().getPassword();
        List<String> roleIds = request.getData().getRoleIds();
        SysUser sysUser = new SysUser();
        sysUser.setUserType("00");
        sysUser.setNickName(name);
        sysUser.setUserName(name);
        sysUser.setPassword(password);
        sysUser.setStatus("0");
        sysUser.setDelFlag("0");

        String userId = transactionTemplate.execute(
            new TransactionCallback<String>() {
                @Override
                public String doInTransaction(TransactionStatus status) {
                    // TODO, 干掉，用uk行锁解决
                    SysUser existed = userRepository.findByName(name);
                    AssertUtil.isNull(existed, INVALID_PARAM_EXCEPTION, "用户已经存在: %s", name);

                    String userId = userRepository.insert(sysUser);

                    // 保存用户角色关系
                    List<UserRoleRelationDO> userRoleRelations = new ArrayList<>();
                    Date now = new Date();
                    for (String roleId : roleIds) {
                        UserRoleRelationDO relationDO = new UserRoleRelationDO();
                        relationDO.setUserId(userId);
                        relationDO.setRoleId(roleId);
                        relationDO.setCreateBy("admin");
                        relationDO.setCreateTime(now);
                        relationDO.setUpdateTime(now);
                        userRoleRelations.add(relationDO);
                    }
                    userRoleRelationRepository.insertAll(userRoleRelations);
                    return userId;
                }
            }
        );
        return RpcResult.success(userId);
    }

    @Override
    public RpcResult<PageResult<List<SysUser>>> pageListUsers(RpcRequest<PageRequest> request) {
        PageResult<List<SysUser>> result = userRepository.pageList(request.getData().getPageIndex(),
            request.getData().getPageSize());
        if (null == result || CollectionUtils.isEmpty(result.getData())) {
            return RpcResult.success(result);
        }

        List<String> userIds = result.getData().stream().map(SysUser::getUserId).filter(Objects::nonNull).collect(
            Collectors.toList());
        List<UserRoleRelationDO> relations = userRoleRelationRepository.findByUserIds(userIds);
        if (CollectionUtils.isEmpty(relations)) {
            return RpcResult.success(result);
        }

        Map<String, Set<String>> relationMap = new HashMap<>();
        Set<String> allRoleIds = new HashSet<>();
        relations.forEach(relation -> {
            String userId = relation.getUserId();
            String roleId = relation.getRoleId();
            Set<String> roleIds = relationMap.getOrDefault(userId, new HashSet<>());
            roleIds.add(roleId);
            relationMap.put(userId, roleIds);
            allRoleIds.add(roleId);
        });

        List<RoleBase> allRoles = roleRepository.findByRoleIds(new ArrayList<>(allRoleIds));

        Map<String, RoleBase> roleMap = allRoles.stream().collect(
            Collectors.toMap(RoleBase::getRoleId, Function.identity(), (k1, k2) -> k2));

        result.getData().forEach(user -> {
            Set<String> roleIds = relationMap.get(user.getUserId());
            if (CollectionUtils.isEmpty(roleIds)) {
                return;
            }
            List<RoleBase> roles = roleIds.stream().map(roleMap::get).filter(
                Objects::nonNull).collect(Collectors.toList());
            user.setRoles(roles);
        });

        return RpcResult.success(result);
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

    private void addRoles(SysUser user) {
        if (null == user) {
            return;
        }

        List<UserRoleRelationDO> userRoleRelations = userRoleRelationRepository.findByUserId(user.getUserId());
        if (CollectionUtils.isEmpty(userRoleRelations)) {
            return;
        }

        List<String> roleIds = userRoleRelations.stream().map(UserRoleRelationDO::getRoleId).collect(
            Collectors.toList());

        List<RoleBase> roles = roleRepository.findByRoleIds(roleIds);
        user.setRoles(roles);
    }

    private void createUserRoleRelations(String userId, List<String> roleIds) {
        // 验证传入的权限id是否真正的对应有角色信息
        List<RoleBase> roles = roleRepository.findByRoleIds(roleIds);
        AssertUtil.equals(roles.size(), roleIds.size(), INVALID_PARAM_EXCEPTION, "角色信息异常");

        List<UserRoleRelationDO> relations = new ArrayList<>();
        roleIds.forEach(roleId -> relations.add(UserRoleRelationDO.of(userId, roleId)));
        userRoleRelationRepository.insertAll(relations);
    }
}
