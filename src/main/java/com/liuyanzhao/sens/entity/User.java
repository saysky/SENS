package com.liuyanzhao.sens.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.liuyanzhao.sens.common.base.BaseEntity;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 *     博主信息
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/14
 */
@Data
@TableName("sens_user")
public class User  extends BaseEntity {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String userName;

    /**
     * 显示名称
     */
    private String userDisplayName;

    /**
     * 密码
     */
    private String userPass;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String userEmail;

    /**
     * 头像
     */
    private String userAvatar;

    /**
     * 说明
     */
    private String userDesc;

    /**
     * 个人主页
     */
    private String userSite;

    /**
     * 是否是管理员 1是，0否
     */
    private Integer isAdmin;

    /**
     * 是否禁用登录
     */
//    @JsonIgnore
    private String loginEnable = "true";

    /**
     * 最后一次登录时间
     */
    private Date loginLast;

    /**
     * 登录错误次数记录
     */
    private Integer loginError = 0;

    /**
     * 是否验证邮箱
     */
    private String emailEnable = "false";

    /**
     * 0 正常
     * 1 禁用
     * 2 已删除
     */
    private Integer status = 0;

    /**
     * 注册时间
     */
    private Date createTime;

    /**
     * 文章数
     */
    @TableField(exist = false)
    private Integer postCount;

    /**
     * 评论数
     */
    @TableField(exist = false)
    private Integer commentCount;
}
