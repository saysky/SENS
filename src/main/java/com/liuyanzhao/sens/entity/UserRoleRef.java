package com.liuyanzhao.sens.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuyanzhao.sens.common.base.BaseEntity;
import lombok.Data;


/**
 * 用户和角色关联
 * @author liuyanzhao
 */
@Data
@TableName("sens_rbac_user_role_ref")
public class UserRoleRef  extends BaseEntity {


    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 角色Id
     */
    private Long roleId;

    public UserRoleRef(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public UserRoleRef() {
    }
}