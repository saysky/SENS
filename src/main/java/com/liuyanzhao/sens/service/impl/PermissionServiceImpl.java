package com.liuyanzhao.sens.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuyanzhao.sens.common.constant.RedisKeyExpire;
import com.liuyanzhao.sens.common.constant.RedisKeys;
import com.liuyanzhao.sens.entity.Permission;
import com.liuyanzhao.sens.entity.RolePermissionRef;
import com.liuyanzhao.sens.mapper.PermissionMapper;
import com.liuyanzhao.sens.mapper.RolePermissionRefMapper;
import com.liuyanzhao.sens.service.PermissionService;
import com.liuyanzhao.sens.utils.PermissionUtil;
import com.liuyanzhao.sens.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <pre>
 *     角色业务逻辑实现类
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/14
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RolePermissionRefMapper rolePermissionRefMapper;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public List<Permission> listPermissionsByRoleId(Long roleId) {
        return permissionMapper.findByRoleId(roleId);
    }

    @Override
    public Set<String> findPermissionUrlsByUserId(Long userId) {
        String value = redisUtil.get(RedisKeys.USER_PERMISSION_URLS + userId);
        // 先从缓存取，缓存没有从数据库取
        if (StringUtils.isNotEmpty(value)) {
            return JSON.parseObject(value, Set.class);
        }
        List<Permission> permissions = permissionMapper.findPermissionByUserId(userId);
        Set<String> urls = permissions.stream().map(p -> p.getUrl()).collect(Collectors.toSet());
        redisUtil.set(RedisKeys.USER_PERMISSION_URLS + userId, JSON.toJSONString(urls), RedisKeyExpire.USER_PERMISSION_URLS);
        return urls;
    }

    @Override
    public List<Permission> findPermissionTreeByUserIdAndResourceType(Long userId, String resourceType) {
        List<Permission> permissions = permissionMapper.findPermissionByUserIdAndResourceType(userId, resourceType);
        return PermissionUtil.getPermissionTree(permissions);
    }

    @Override
    public List<Permission> findByResourceType(String resourceType) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("resource_type", resourceType);
        return permissionMapper.selectByMap(map);
    }

    @Override
    public BaseMapper<Permission> getRepository() {
        return permissionMapper;
    }

    @Override
    public QueryWrapper<Permission> getQueryWrapper(Permission permission) {
        //对指定字段查询
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        if (permission != null) {
            if (StrUtil.isNotBlank(permission.getResourceType())) {
                queryWrapper.eq("resource_type", permission.getResourceType());
            }
            if (StrUtil.isNotBlank(permission.getTarget())) {
                queryWrapper.eq("target", permission.getTarget());
            }
            if (StrUtil.isNotBlank(permission.getResourceType())) {
                queryWrapper.eq("resource_type", permission.getResourceType());
            }
            if (StrUtil.isNotBlank(permission.getUrl())) {
                queryWrapper.eq("url", permission.getUrl());
            }
            if (StrUtil.isNotBlank(permission.getName())) {
                queryWrapper.eq("name", permission.getName());
            }
        }
        return queryWrapper;
    }

    @Override
    public Permission insertOrUpdate(Permission entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        permissionMapper.deleteById(id);
        rolePermissionRefMapper.deleteByPermissionId(id);
        // 删除所有的用户的权限列表缓存
        redisUtil.delByKeys(RedisKeys.USER_PERMISSION_URLS);
    }

    @Override
    public List<Permission> findPermissionListWithLevel(Integer resourceType) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (resourceType != null) {
            queryWrapper.eq("resource_type", resourceType);
            queryWrapper.orderByDesc("sort");
        }
        List<Permission> permissionList = permissionMapper.selectList(queryWrapper);
        return PermissionUtil.getPermissionList(permissionList);

    }
}
