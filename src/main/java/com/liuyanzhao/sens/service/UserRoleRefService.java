package com.liuyanzhao.sens.service;

import com.liuyanzhao.sens.common.base.BaseService;
import com.liuyanzhao.sens.entity.UserRoleRef;

/**
 * @author 言曌
 * @date 2019/1/25 下午8:07
 */

public interface UserRoleRefService extends BaseService<UserRoleRef, Long> {

    /**
     * 根据用户Id删除
     *
     * @param userId 用户Id
     */
    void deleteByUserId(Long userId);


}
