package com.liuyanzhao.sens.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.liuyanzhao.sens.common.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * <pre>
 *     系统设置
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/14
 */
@Data
@TableName("sens_options")
public class Options  extends BaseEntity {

    /**
     * 设置项名称
     */
    @TableId(type = IdType.INPUT)
    private String optionName;

    /**
     * 设置项的值
     */
    private String optionValue;
}
