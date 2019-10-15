package com.liuyanzhao.sens.model.enums;

/**
 * @author 言曌
 * @date 2019-10-01 11:31
 */

public enum  SidebarTypeEnum {

    /**
     * 首页侧边栏
     */
    INDEX("index"),

    /**
     * 正文侧边栏
     */
    DETAIL("detail"),

    /**
     * 分类侧边栏
     */
    CATEGORY("category"),


    /**
     * 标签侧边栏
     */
    TAG("tag"),


    /**
     * 作者侧边栏
     */
    AUTHOR("author"),

    /**
     * 普通页面侧边栏
     */
    PAGE("page"),

    /**
     * 公告侧边栏
     */
    NOTICE("notice")

    ;

    private String value;

    SidebarTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
