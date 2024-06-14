package com.liuyanzhao.sens.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.liuyanzhao.sens.common.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 邮件验证码
 * @author 言曌
 * @date 2018/2/23 上午10:24
 */

@Data
@TableName("sens_mail_retrieve")
public class MailRetrieve extends BaseEntity  {

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * Email
     */
    private String email;

    /**
     * 验证码
     */
    private String code;

    /**
     * 过期时间
     */
    private Date outTime;


