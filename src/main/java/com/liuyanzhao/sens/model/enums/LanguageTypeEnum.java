package com.liuyanzhao.sens.model.enums;

/**
 * @author 言曌
 * @date 2019/1/24 下午5:08
 */

public enum LanguageTypeEnum {

    /**
     * 密码登录
     */
    CHINESE("zh_CN"),

    /**
     * 免密码登录
     */
    ENGLISH("en_US");


    private String value;

    LanguageTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
