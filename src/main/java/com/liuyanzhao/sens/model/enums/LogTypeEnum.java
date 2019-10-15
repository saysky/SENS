package com.liuyanzhao.sens.model.enums;

/**
 * @author liuyanzhao
 */
public enum LogTypeEnum {

    /**
     * 默认0操作
     */
    OPERATION("operation"),

    /**
     * 1登录
     */
    LOGIN("login"),

    /**
     * 2注册
     */
    REGISTER("register"),

    /**
     * 3忘记密码
     */
    FORGET("forget"),

    /**
     * 4附件
     */
    ATTACHMENT("attachment");

    private String value;

    LogTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
