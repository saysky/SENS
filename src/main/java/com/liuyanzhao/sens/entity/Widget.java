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
@TableName("sens_widget")
public class Widget  extends BaseEntity {

    /**
     * 小工具标题
     */
    private String widgetTitle;

    /**
     * 小工具内容
     */
    private String widgetContent;

    /**
     * 是否显示(1是，0否)
     */
    private Integer isDisplay = 1;

    /**
     * 位置
     */
    private Integer widgetType;


}
