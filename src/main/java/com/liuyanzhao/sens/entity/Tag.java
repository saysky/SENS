package com.liuyanzhao.sens.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liuyanzhao.sens.common.base.BaseEntity;
import lombok.Data;


/**
 * <pre>
 *     文章标签
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/12
 */
@Data
@TableName("sens_tag")
public class Tag  extends BaseEntity {

    /**
     * 标签名称
     */
    private String tagName;

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
