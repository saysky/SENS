package com.liuyanzhao.sens.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liuyanzhao.sens.common.base.BaseEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 *     文章／页面
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/14
 */
@Data
@TableName("sens_post")
public class Post  extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文章标题
     */
    private String postTitle;

    /**
     * 文章类型
     * post  文章
     * page  页面
     */
    private String postType;

    /**
     * 文章内容 html格式
     */
    private String postContent;

    /**
     * 文章路径
     */
    private String postUrl;

    /**
     * 文章摘要
     */
    private String postSummary;


    /**
     * 缩略图
     */
    private String postThumbnail;

    /**
     * 0 已发布
     * 1 草稿
     * 2 回收站
     */
    private Integer postStatus;

    /**
     * 文章访问量
     */
    private Long postViews;

    /**
     * 点赞访问量
     */
    private Long postLikes;

    /**
     * 评论数量(冗余字段，加快查询速度)
     */
    private Long commentSize;

    /**
     * 文章来源（原创1，转载2，翻译3）
     */
    private Integer postSource;

    /**
     * 是否允许评论（允许1，禁止0）
     */
    private Integer allowComment;

    /**
     * 发表用户 多对一
     */
    @TableField(exist = false)
    private User user;

    /**
     * 文章所属分类
     */
    @TableField(exist = false)
    private List<Category> categories = new ArrayList<>();

    /**
     * 文章所属标签
     */
    @TableField(exist = false)
    private List<Tag> tags = new ArrayList<>();

    /**
     * 文章的评论
     */
    @TableField(exist = false)
    private List<Comment> comments = new ArrayList<>();


}
