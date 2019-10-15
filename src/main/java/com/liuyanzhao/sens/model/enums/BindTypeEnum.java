package com.liuyanzhao.sens.model.enums;

/**
 * @author : 第三方绑定类型
 * @date : 2018年09月08日
 */
public enum BindTypeEnum {

    /**
     * qq
     */
    QQ("QQ"),

    /**
     * GitHUb
     */
    GITHUB("GitHub");

    private String value;

    BindTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
