package com.liuyanzhao.sens.service;

import com.liuyanzhao.sens.common.base.BaseService;
import com.liuyanzhao.sens.entity.Role;
import com.liuyanzhao.sens.entity.User;

import java.util.List;

/**
 * <pre>
 *     角色逻辑接口
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/14
 */
public interface RoleService extends BaseService<Role, Long> {

    /**
     * 删除某个用户的所有关联
     *
     * @param userId 用户Id
     */
    void deleteByUserId(Long userId);
    /**
     * 根据编号查询单个权限
     *
     * @param roleId roleId
     * @return Role
     */
    Role findByRoleId(Long roleId);

    /**
     * 根据编号查询单个权限
     *
     * @param roleName roleName
     * @return Role
     */
    Role findByRoleName(String roleName);

    /**
     * 根据用户Id获得角色列表
     *
     * @param userId 用户Id
     * @return 角色列表
     */
    List<Role> listRolesByUserId(Long userId);

    /**
     * 统计这个角色的用户数
     *
     * @param roleId 角色Id
     */
    Integer countUserByRoleId(Long roleId);

    /**
     * 获得角色和对应数量
     * @return
     */
    List<Role> findAllWithCount();

}
