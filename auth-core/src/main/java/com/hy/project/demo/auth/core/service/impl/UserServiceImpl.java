package com.hy.project.demo.auth.core.service.impl;

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
import com.hy.project.demo.auth.facade.service.RsaService;
import com.hy.project.demo.auth.facade.service.UserService;
import com.hy.project.demo.common.exception.DemoExceptionEnum;
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
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    //@Autowired
    //BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Override
    public SysUser loadSysUserByName(String name) {
        SysUser sysUser = userRepository.findByName(name);

        // 补充role信息
        addRoles(sysUser);

        return sysUser;
    }

    @Override
    public SysUser loadSysUserByUserId(String userId) {
        SysUser sysUser = userRepository.findByUserId(userId);

        // 补充role信息
        addRoles(sysUser);

        return sysUser;
    }

    //@Transactional(rollbackFor = Exception.class)
    @Override
    public void updateSysUser(SysUser sysUser) {
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
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //if (null != authentication && !(authentication.getPrincipal() instanceof LoginUser)) {
        //    return null;
        //}
        //LoginUser loginUser = (LoginUser)authentication.getPrincipal();
        //return loginUser.getUser();
        return null;
    }

    @Override
    public void clearUser(String userId) {
        String key = createRedisUserInfoKey(userId);
        redisService.remove(key);
    }

    //@Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteUser(String userId) {
        transactionTemplate.execute(
            new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    userRepository.deleteByUserId(userId);
                    userRoleRelationRepository.deleteByUserId(userId);
                }
            }
        );
    }

    //@Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserPassword(String userId, String newPwd) {
        transactionTemplate.execute(
            new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    SysUser user = userRepository.lockByUserId(userId);
                    AssertUtil.notNull(user, INVALID_PARAM_EXCEPTION, "can not find user: %s", userId);

                    // 用RSA私钥解密前端加密后的用户登录密码
                    String pwd = rsaService.decryptByPrivateKey(Base64.decodeBase64(newPwd));
                    // 加密用户密码
                    //String encoded = bCryptPasswordEncoder.encode(pwd);

                    //user.setPassword(encoded);
                    userRepository.updateUser(user);
                }
            }
        );
    }

    //@Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserRoles(String userId, List<String> roleIds) {
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
    }

    //@Transactional(rollbackFor = Exception.class)
    @Override
    public String createNewUser(String name, String password) {
        SysUser sysUser = new SysUser();
        sysUser.setUserType("00");
        sysUser.setNickName(name);
        sysUser.setUserName(name);
        sysUser.setPassword(password);
        sysUser.setStatus("0");
        sysUser.setDelFlag("0");

        return transactionTemplate.execute(
            new TransactionCallback<String>() {
                @Override
                public String doInTransaction(TransactionStatus status) {
                    // TODO, 干掉，用uk行锁解决
                    SysUser existed = userRepository.findByName(name);
                    AssertUtil.isNull(existed, INVALID_PARAM_EXCEPTION, "用户已经存在: %s", name);

                    String userId = userRepository.insert(sysUser);

                    //// 保存用户角色关系 TODO mock
                    //userRoleRelationRepository.insert(userId, "1120220909000001");

                    return userId;
                }
            }
        );
    }

    @Override
    public PageResult<List<SysUser>> pageListUsers(int pageIndex, int pageSize) {
        PageResult<List<SysUser>> result = userRepository.pageList(pageIndex, pageSize);
        if (null == result || CollectionUtils.isEmpty(result.getData())) {
            return result;
        }

        List<String> userIds = result.getData().stream().map(SysUser::getUserId).filter(Objects::nonNull).collect(
            Collectors.toList());
        List<UserRoleRelationDO> relations = userRoleRelationRepository.findByUserIds(userIds);
        if (CollectionUtils.isEmpty(relations)) {
            return result;
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

        return result;
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
