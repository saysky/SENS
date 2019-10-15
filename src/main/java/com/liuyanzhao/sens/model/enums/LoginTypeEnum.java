package com.liuyanzhao.sens.model.enums;

/**
 * @author 言曌
 * @date 2019/1/24 下午5:08
 */

public enum LoginTypeEnum {

    /**
     * 密码登录
     */
    NORMAL("Normal"),

    /**
     * 免密码登录
     */
    FREE("Free");


    private String value;

    LoginTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
