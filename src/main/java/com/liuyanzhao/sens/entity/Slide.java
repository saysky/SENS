package com.liuyanzhao.sens.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuyanzhao.sens.common.base.BaseEntity;
import lombok.Data;


/**
 * <pre>
 *     幻灯片
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/24
 */
@Data
@TableName("sens_slide")
public class Slide  extends BaseEntity {

    /**
     * 幻灯片名称
     */
    private String slideTitle;

    /**
     * 幻灯片链接
     */
    private String slideUrl;

    /**
     * 幻灯片图片地址
     */
    private String slidePictureUrl;

    /**
     * 排序编号
     */
    private Integer slideSort = 1;

    /**
     * 打开方式
     */
    private String slideTarget;

    /**
     * 幻灯片类型(首页幻灯片0)
     */
    private Integer slideType = 0;


}
