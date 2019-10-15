package com.liuyanzhao.sens.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liuyanzhao.sens.common.base.BaseEntity;
import lombok.Data;

import javax.servlet.http.Cookie;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * <pre>
 *     评论
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/22
 */
@Data
@TableName("sens_comment")
public class Comment  extends BaseEntity {

    /**
     * 文章ID
     */
    private Long postId;

    /**
     * 评论人
     */
    @NotBlank(message = "评论用户名不能为空")
    private String commentAuthor;

    /**
     * 评论人的邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String commentAuthorEmail;

    /**
     * 评论人的主页
     */
    private String commentAuthorUrl;

    /**
     * 评论人的ip
     */
    private String commentAuthorIp;

    /**
     * Email的md5，用于gavatar
     */
    private String commentAuthorEmailMd5;

    /**
     * 评论人头像
     */
    private String commentAuthorAvatar;


    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String commentContent;

    /**
     * 评论者ua信息
     */
    private String commentAgent;

    /**
     * 上一级
     */
    private Long commentParent = 0L;

    /**
     * 评论状态，0：正常，1：待审核，2：回收站
     */
    private Integer commentStatus = 1;

    /**
     * 是否是博主的评论 0:不是 1:是
     */
    private Integer isAdmin;

    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 关系路径
     */
    private String pathTrace;

    /**
     * 接受者用户Id
     */
    private Long acceptUserId;

    /**
     * 评论类型(匿名评论0，登录评论1)
     */
    private Integer commentType;

    /**
     * 评论文章
     */
    @TableField(exist = false)
    private Post post;

    /**
     * 当前评论下的所有子评论
     */
    @TableField(exist = false)
    private List<Comment> childComments;

}
