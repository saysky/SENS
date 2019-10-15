package com.liuyanzhao.sens.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuyanzhao.sens.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * @author liuyanzhao
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {


    /**
     * 根据用户Id获得角色列表
     *
     * @param userId 用户Id
     * @return 角色列表
     */
    List<Role> findByUserId(Long userId);


    /**
     * 删除用户和角色管理
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    Integer deleteByUserId(Long userId);

    /**
     * 统计某个角色的用户数
     *
     * @param roleId 角色Id
     * @return 用户数
     */
    Integer countUserByRoleId(Long roleId);


    /**
     * 获得所有角色和对应用户数量
     * @return
     */
    List<Role> findAllWithCount();
}

