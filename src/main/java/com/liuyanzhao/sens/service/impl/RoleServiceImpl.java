package com.liuyanzhao.sens.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuyanzhao.sens.common.constant.RedisKeyExpire;
import com.liuyanzhao.sens.common.constant.RedisKeys;
import com.liuyanzhao.sens.entity.Permission;
import com.liuyanzhao.sens.entity.Role;
import com.liuyanzhao.sens.entity.RolePermissionRef;
import com.liuyanzhao.sens.mapper.RoleMapper;
import com.liuyanzhao.sens.service.RolePermissionRefService;
import com.liuyanzhao.sens.service.RoleService;
import com.liuyanzhao.sens.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     角色业务逻辑实现类
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/14
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionRefService rolePermissionRefService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public BaseMapper<Role> getRepository() {
        return roleMapper;
    }

    @Override
    public QueryWrapper<Role> getQueryWrapper(Role role) {
        //对指定字段查询
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        if (role != null) {
            if (StrUtil.isNotBlank(role.getRole())) {
                queryWrapper.eq("role", role.getRole());
            }
            if (StrUtil.isNotBlank(role.getDescription())) {
                queryWrapper.eq("description", role.getDescription());
            }
        }
        return queryWrapper;
    }

    @Override
    public void deleteByUserId(Long userId) {
        roleMapper.deleteByUserId(userId);
    }

    @Override
    public Role findByRoleId(Long roleId) {
        return roleMapper.selectById(roleId);
    }

    @Override
    public Role findByRoleName(String roleName) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("role", roleName);
        return roleMapper.selectOne(queryWrapper);
    }

    @Override
    public List<Role> listRolesByUserId(Long userId) {
        return roleMapper.findByUserId(userId);
    }

    @Override
    public Integer countUserByRoleId(Long roleId) {
        return roleMapper.countUserByRoleId(roleId);
    }

    @Override
    public List<Role> findAllWithCount() {
        return roleMapper.findAllWithCount();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role insert(Role role) {
        roleMapper.insert(role);
        if (role.getPermissions() != null && role.getPermissions().size() > 0) {
            List<RolePermissionRef> rolePermissionRefs = new ArrayList<>(role.getPermissions().size());
            for (Permission permission : role.getPermissions()) {
                rolePermissionRefs.add(new RolePermissionRef(role.getId(), permission.getId()));
            }
            rolePermissionRefService.batchSaveByRolePermissionRef(rolePermissionRefs);
        }
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role update(Role role) {
        roleMapper.updateById(role);
        if (role.getPermissions() != null && role.getPermissions().size() > 0) {
            rolePermissionRefService.deleteRefByRoleId(role.getId());
            List<RolePermissionRef> rolePermissionRefs = new ArrayList<>(role.getPermissions().size());
            for (Permission permission : role.getPermissions()) {
                rolePermissionRefs.add(new RolePermissionRef(role.getId(), permission.getId()));
            }
            rolePermissionRefService.batchSaveByRolePermissionRef(rolePermissionRefs);
        }
        return role;
    }

    @Override
    public Role insertOrUpdate(Role entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }

        redisUtil.delByKeys(RedisKeys.USER_PERMISSION_URLS);
        return entity;
    }

}
