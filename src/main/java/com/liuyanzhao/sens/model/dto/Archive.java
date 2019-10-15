package com.liuyanzhao.sens.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <pre>
 *     文章归档
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/20
 */
@Data
public class Archive implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 年份
     */
    private String year;

    /**
     * 月份
     */
    private String month;

    /**
     * 这个月对应的文章数
     */
    private String count;

    /**
     * 对应的文章
     */
    private List<PostSimpleDto> posts;
}
