package com.liuyanzhao.sens.model.enums;

/**
 * <pre>
 *     用户状态enum
 * </pre>
 *
 * @author : saysky
 * @date : 2018/7/1
 */
public enum UserStatusEnum {

    /**
     * 正常
     */
    NORMAL(0),

    /**
     * 禁止登录
     */
    BAN(1);


    private Integer code;

    UserStatusEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
