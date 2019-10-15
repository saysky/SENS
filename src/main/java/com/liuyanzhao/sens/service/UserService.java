package com.liuyanzhao.sens.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.common.base.BaseService;
import com.liuyanzhao.sens.entity.User;
import com.liuyanzhao.sens.utils.Response;

import java.util.Date;
import java.util.List;

/**
 * <pre>
 *     用户业务逻辑接口
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/14
 */
public interface UserService extends BaseService<User, Long> {

    /**
     * 根据用户名获得用户
     *
     * @param userName 用户名
     * @return 用户
     */
    User findByUserName(String userName);


    /**
     * 根据邮箱查找用户
     *
     * @param userEmail 邮箱
     * @return User
     */
    User findByEmail(String userEmail);

    /**
     * 更新密码
     *
     * @param userId 用户Id
     * @param password 密码
     */
    void updatePassword(Long userId, String password);

    /**
     * 分页获取所有用户
     *
     * @param roleName 角色名称
     * @param condition 查询条件
     * @param page 分页信息
     * @return 用户列表
     */
    Page<User> findByRoleAndCondition(String roleName, User condition, Page<User> page);

    /**
     * 根据用户编号和密码查询
     *
     * @param userId   userid
     * @param userPass userpass
     * @return User
     */
    User findByUserIdAndUserPass(Long userId, String userPass);

    /**
     * 修改禁用状态
     *
     * @param enable enable
     */
    void updateUserLoginEnable(User user, String enable);

    /**
     * 增加登录错误次数
     *
     * @return 登录错误次数
     */
    Integer updateUserLoginError(User user);

    /**
     * 修改用户的登录状态为正常
     *
     * @return User
     */
    User updateUserLoginNormal(User user);

    /**
     * 获得今日新增数量
     * @return
     */
    Integer getTodayCount();

    /**
     * 获得用户文章数排行榜
     * limit 前几名
     * @return
     */
    List<User> getUserPostRanking(Integer limit);

    /**
     * 获得最新注册用户
     * @param limit
     * @return
     */
    List<User> getLatestRegisterUser(Integer limit);

}
