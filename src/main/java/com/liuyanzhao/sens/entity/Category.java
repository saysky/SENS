package com.liuyanzhao.sens.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liuyanzhao.sens.common.base.BaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <pre>
 *     文章分类
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/30
 */
@Data
@TableName("sens_category")
public class Category extends BaseEntity {

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String cateName;

    /**
     * 分类父节点
     */
    private Long catePid;

    /**
     * 分类排序号
     */
    private Integer cateSort;

    /**
     * 分类层级
     */
    private Integer cateLevel = 1;

    /**
     * 关系路径
     */
    private String pathTrace;

    /**
     * 分类描述
     */
    private String cateDesc;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 数量
     */
    @TableField(exist = false)
    private Integer count;

}
