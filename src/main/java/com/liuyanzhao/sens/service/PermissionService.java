package com.liuyanzhao.sens.service;

import com.liuyanzhao.sens.common.base.BaseService;
import com.liuyanzhao.sens.entity.Permission;

import java.util.List;
import java.util.Set;

/**
 * <pre>
 *     权限逻辑接口
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/14
 */
public interface PermissionService extends BaseService<Permission, Long> {

    /**
     * 根据角色Id获得权限列表
     *
     * @param roleId 角色Id
     * @return 权限列表
     */
    List<Permission> listPermissionsByRoleId(Long roleId);

    /**
     * 获得某个用户的权限URL列表
     * @param userId
     * @return
     */
    Set<String> findPermissionUrlsByUserId(Long userId);

    /**
     * 获得某个用户的用户ID和资源类型
     * @param userId
     * @param resourceType
     * @return
     */
    List<Permission> findPermissionTreeByUserIdAndResourceType(Long userId, String resourceType);

    /**
     * 根据资源类型获得权限
     * @param resourceType
     * @return
     */
    List<Permission> findByResourceType(String resourceType);

    /**
     * 获得权限列表
     * @param resourceType
     * @return
     */
    List<Permission> findPermissionListWithLevel(Integer resourceType);
}
