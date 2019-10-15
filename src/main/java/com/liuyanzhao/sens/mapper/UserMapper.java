package com.liuyanzhao.sens.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liuyanzhao
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据角色Id获得用户
     *
     * @param roleId 角色Id
     * @param page 分页信息
     * @return 用户列表
     */
    List<User> findByRoleId(@Param("roleId") Long roleId, Page page);

    /**
     * 根据角色Id获得用户
     *
     * @param roleId 角色Id
     * @param user 条件
     * @param page 分页信息
     * @return 用户列表
     */
    List<User> findByRoleIdAndCondition(@Param("roleId") Long roleId,
                                        @Param("user") User user, Page page);


    /**
     * 获得没有角色的用户
     *
     * @param page 角色
     * @return 用户列表
     */
    List<User> findByWithoutRole(Page page);

    /**
     * 获得今日新增数量
     * @return
     */
    Integer getTodayCount();

    /**
     * 获得用户文章数排名
     * @param limit 查询数量
     * @return
     */
    List<User> getUserPostRanking(Integer limit);

    /**
     * 获得最新注册用户
     * @param limit
     * @return
     */
    List<User> getLatestUser(Integer limit);

}

