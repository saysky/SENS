package com.liuyanzhao.sens.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.liuyanzhao.sens.common.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author liuyanzhao
 */
@Data
@TableName("sens_rbac_role")
public class Role  extends BaseEntity {

    /**
     * 角色名称：admin，author，subscriber
     */
    private String role;

    /**
     * 描述：管理员，作者，订阅者
     */
    private String description;

    /**
     * 级别
     */
    private Integer level;

    /**
     * 该角色对应的用户数量，非数据库字段
     */
    @TableField(exist = false)
    private Integer count;

    /**
     * 当前角色的权限列表
     */
    @TableField(exist = false)
    private List<Permission> permissions;

}