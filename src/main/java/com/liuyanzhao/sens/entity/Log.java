package com.liuyanzhao.sens.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuyanzhao.sens.common.base.BaseEntity;
import lombok.Data;


/**
 * @author liuyanzhao
 */
@Data
@TableName("sens_log")
public class Log extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 方法操作名称
     */
    private String name;

    /**
     * 日志类型
     */
    private String logType;

    /**
     * 请求路径
     */
    private String requestUrl;

    /**
     * 请求类型
     */
    private String requestType;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 请求用户
     */
    private String username;

    /**
     * ip
     */
    private String ip;

    /**
     * ip信息
     */
    private String ipInfo;

    /**
     * 花费时间
     */
    private Integer costTime;


}
