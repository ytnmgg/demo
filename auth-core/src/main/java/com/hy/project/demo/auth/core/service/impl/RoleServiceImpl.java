package com.hy.project.demo.auth.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.google.common.collect.Lists;
import com.hy.project.demo.auth.core.mybatis.entity.RolePermissionRelationDO;
import com.hy.project.demo.auth.core.repository.PermissionRepository;
import com.hy.project.demo.auth.core.repository.RolePermissionRelationRepository;
import com.hy.project.demo.auth.core.repository.RoleRepository;
import com.hy.project.demo.auth.facade.model.Permission;
import com.hy.project.demo.auth.facade.model.Role;
import com.hy.project.demo.auth.facade.model.RoleBase;
import com.hy.project.demo.auth.facade.model.request.CreateNewRoleRequest;
import com.hy.project.demo.auth.facade.model.request.RpcRequest;
import com.hy.project.demo.auth.facade.model.request.UpdateRolePermissionRequest;
import com.hy.project.demo.auth.facade.model.result.RpcResult;
import com.hy.project.demo.auth.facade.service.RoleService;
import com.hy.project.demo.common.model.PageRequest;
import com.hy.project.demo.common.model.PageResult;
import com.hy.project.demo.common.service.redis.RedisService;
import com.hy.project.demo.common.util.AssertUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.hy.project.demo.common.constant.RedisConstants.KEY_ROLES;
import static com.hy.project.demo.common.exception.DemoExceptionEnum.INVALID_PARAM_EXCEPTION;

/**
 * @author rick.wl
 * @date 2022/11/10
 */
@Service
@DubboService
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    RolePermissionRelationRepository rolePermissionRelationRepository;

    @Autowired
    RedisService redisService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RpcResult<String> createNewRole(RpcRequest<CreateNewRoleRequest> request) {
        RoleBase role = new RoleBase();
        role.setRoleName(request.getData().getName());
        role.setRoleKey(request.getData().getCode());
        String roleId = roleRepository.insert(role);

        if (CollectionUtils.isNotEmpty(request.getData().getPermissions())) {
            List<Permission> permissions = createRolePermissionRelations(roleId, request.getData().getPermissions());
            redisService.setHash(KEY_ROLES, roleId, JSON.toJSONString(permissions));
        }

        return RpcResult.success(roleId);
    }

    @Override
    public RpcResult<PageResult<List<Role>>> pageList(RpcRequest<PageRequest> request) {
        // 从db分页捞取角色
        PageResult<List<RoleBase>> roleBases = roleRepository.pageList(request.getData().getPageIndex(),
            request.getData().getPageSize());

        if (null == roleBases || CollectionUtils.isEmpty(roleBases.getData())) {
            return RpcResult.success(PageResult.of(Lists.newArrayList()));
        }

        // 设置角色权限信息
        List<Role> roles = buildAndCachePermissions(roleBases.getData());

        return RpcResult.success(
            PageResult.of(roles, roleBases.getPageIndex(), roleBases.getPageSize(), roleBases.getTotalCount()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RpcResult<Void> deleteRole(RpcRequest<String> request) {
        String id = request.getData();
        rolePermissionRelationRepository.deleteByRoleId(id);
        roleRepository.deleteById(id);

        redisService.removeHash(KEY_ROLES, id);

        return RpcResult.success(null);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RpcResult<Void> updateRolePermissions(RpcRequest<UpdateRolePermissionRequest> request) {
        String roleId = request.getData().getRoleId();
        List<String> permissionIds = request.getData().getPermissions();

        RoleBase roleBase = roleRepository.lockByRoleId(roleId);
        AssertUtil.notNull(roleBase, INVALID_PARAM_EXCEPTION, "can not find role: %s", roleId);

        rolePermissionRelationRepository.deleteByRoleId(roleId);

        List<Permission> permissions = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(permissionIds)) {
            permissions = createRolePermissionRelations(roleId, permissionIds);
        }

        redisService.setHash(KEY_ROLES, roleId, JSON.toJSONString(permissions));
        return RpcResult.success(null);
    }

    @Override
    public RpcResult<List<String>> getPermissions(RpcRequest<List<RoleBase>> request) {
        List<RoleBase> roleBases = request.getData();
        if (CollectionUtils.isEmpty(roleBases)) {
            return null;
        }

        List<Role> roleList = buildAndCachePermissions(roleBases);

        Set<String> allPermissionKeys = new HashSet<>();
        for (Role role : roleList) {
            for (Permission permission : role.getPermissions()) {
                allPermissionKeys.add(permission.getPermissionKey());
            }
        }

        return RpcResult.success(new ArrayList<>(allPermissionKeys));
    }

    private List<Permission> createRolePermissionRelations(String roleId, List<String> permissionIds) {
        // 验证传入的权限id是否真正的对应有权限信息
        List<Permission> permissions = permissionRepository.findByPermissionIds(permissionIds);
        AssertUtil.equals(permissions.size(), permissionIds.size(), INVALID_PARAM_EXCEPTION, "权限信息异常");

        List<RolePermissionRelationDO> relations = new ArrayList<>();
        permissionIds.forEach(permissionId -> relations.add(RolePermissionRelationDO.of(roleId, permissionId)));
        rolePermissionRelationRepository.insertAll(relations);
        return permissions;
    }

    private List<Role> buildAndCachePermissions(List<RoleBase> roleBases) {
        List<Role> result = Lists.newArrayList();

        if (CollectionUtils.isEmpty(roleBases)) {
            return result;
        }

        // 从redis捞取角色下挂的权限
        Map<String, Object> allRolePermissions = redisService.getHash(KEY_ROLES);

        List<Role> needGetPermissionFromDbRoles = new ArrayList<>();
        roleBases.forEach(roleBase -> {
            Role role = Role.of(roleBase);

            Object permissionsObj = allRolePermissions.get(roleBase.getRoleId());
            if (null != permissionsObj) {
                // 缓存不为空，直接使用
                List<Permission> permissions = JSON.parseArray(String.valueOf(permissionsObj), Permission.class);
                role.setPermissions(permissions);
                result.add(role);
            } else {
                // 缓存中没有，加入"从db获取"列表，等下统一从db查询
                needGetPermissionFromDbRoles.add(role);
            }
        });

        // 缓存没有，从db捞，并更新redis
        if (CollectionUtils.isNotEmpty(needGetPermissionFromDbRoles)) {

            setPermissionsToRoleFromDb(needGetPermissionFromDbRoles);
            result.addAll(needGetPermissionFromDbRoles);

            // 更新缓存
            Map<String, String> rolePermissionToRedis = new HashMap<>();
            needGetPermissionFromDbRoles.forEach(role -> {
                rolePermissionToRedis.put(role.getRoleId(), JSON.toJSONString(role.getPermissions()));
            });

            redisService.setHash(KEY_ROLES, rolePermissionToRedis);
        }

        return result;
    }

    private void setPermissionsToRoleFromDb(List<Role> needGetFromDbRoles) {
        if (CollectionUtils.isEmpty(needGetFromDbRoles)) {
            return;
        }

        List<String> roleIds = needGetFromDbRoles.stream().map(Role::getRoleId).distinct().collect(
            Collectors.toList());
        List<RolePermissionRelationDO> relations = rolePermissionRelationRepository.findPermissionsByRoleIds(
            roleIds);

        Set<String> allPermissionIds = new HashSet<>();
        Map<String, Set<String>> rolePermissionIds = new HashMap<>();
        relations.forEach(relation -> {
            allPermissionIds.add(relation.getPermissionId());
            Set<String> permissionIds = rolePermissionIds.getOrDefault(relation.getRoleId(), new HashSet<>());
            permissionIds.add(relation.getPermissionId());
            rolePermissionIds.put(relation.getRoleId(), permissionIds);
        });

        List<Permission> permissions = permissionRepository.findByPermissionIds(
            Lists.newArrayList(allPermissionIds));
        Map<String, Permission> permissionMap = permissions.stream().collect(
            Collectors.toMap(Permission::getPermissionId, Function.identity(), (k1, k2) -> k2));

        needGetFromDbRoles.forEach(role -> {
            Set<String> permissionIds = rolePermissionIds.get(role.getRoleId());
            List<Permission> p = permissionIds.stream().map(permissionMap::get).collect(
                Collectors.toList());
            role.setPermissions(p);
        });
    }
}
