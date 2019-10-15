package com.liuyanzhao.sens.model.dto;

import lombok.Data;

/**
 * @author 言曌
 * @date 2019-10-10 16:18
 */
@Data
public class CountDTO {

    /**
     * 用户总数
     */
    private Integer userCount;

    /**
     * 文章总数
     */
    private Integer postCount;

    /**
     * 友情链接总数
     */
    private Integer linkCount;

    /**
     * 评论总数
     */
    private Integer commentCount;

    /**
     * 标签总数
     */
    private Integer tagCount;

    /**
     * 分类总数
     */
    private Integer categoryCount;

    /**
     * 访问量统计
     */
    private Long viewCount;
}
