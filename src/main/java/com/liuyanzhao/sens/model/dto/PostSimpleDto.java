package com.liuyanzhao.sens.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章部分信息
 * @author 言曌
 * @date 2018/12/16 下午3:46
 */
@Data
public class PostSimpleDto implements Serializable{

    private static final long serialVersionUID = -8041345763938068216L;
    /**
     * 文章id
     */
    private Long id;

    /**
     * 文章标题
     */
    private String postTitle;

    /**
     * 文章url
     */
    private String postUrl;

    /**
     * 访问量
     */
    private Integer postViews;

    /**
     * 评论数
     */
    private Integer commentSize;

    /**
     * 发表日期
     */
    private Date createTime;
}
