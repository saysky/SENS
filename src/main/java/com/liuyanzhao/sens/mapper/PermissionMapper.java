package com.liuyanzhao.sens.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuyanzhao.sens.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author liuyanzhao
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据角色Id获得权限列表
     *
     * @param roleId 角色Id
     * @return 权限列表
     */
    List<Permission> findByRoleId(Long roleId);

    /**
     * 获得某个用户的权限列表
     *
     * @param userId
     * @return
     */
    List<Permission> findPermissionByUserId(Long userId);

    /**
     * 获得某个用户的权限列表
     *
     * @param userId
     * @param resourceType
     * @return
     */
    List<Permission> findPermissionByUserIdAndResourceType(@Param("userId") Long userId,
                                                           @Param("resourceType") String resourceType);
}

