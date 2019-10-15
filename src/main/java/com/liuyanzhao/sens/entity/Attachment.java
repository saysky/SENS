package com.liuyanzhao.sens.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuyanzhao.sens.common.base.BaseEntity;
import lombok.Data;


/**
 * <pre>
 *     附件
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/10
 */
@Data
@TableName("sens_attachment")
public class Attachment extends BaseEntity {


    /**
     * 附件名
     */
    private String attachName;

    /**
     * 附件路径
     */
    private String attachPath;

    /**
     * 附件缩略图路径
     */
    private String attachSmallPath;

    /**
     * 附件类型
     */
    private String attachType;

    /**
     * 附件后缀
     */
    private String attachSuffix;

    /**
     * 附件大小
     */
    private String attachSize;

    /**
     * 附件长宽
     */
    private String attachWh;

    /**
     * 附件存储地址
     */
    private String attachLocation;

    /**
     * 附件来源，0：上传，1：外部链接
     */
    private Integer attachOrigin = 0;

    /**
     * 所属用户
     */
    private Long userId;
}
