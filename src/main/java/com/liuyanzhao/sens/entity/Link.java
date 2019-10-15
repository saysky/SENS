package com.liuyanzhao.sens.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuyanzhao.sens.common.base.BaseEntity;
import lombok.Data;

/**
 * <pre>
 *     友情链接
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/14
 */
@Data
@TableName("sens_link")
public class Link  extends BaseEntity {

    /**
     * 友情链接名称
     */
    private String linkName;

    /**
     * 友情链接地址
     */
    private String linkUrl;

    /**
     * 友情链接头像
     */
    private String linkPic;

    /**
     * 友情链接描述
     */
    private String linkDesc;
}
