package com.liuyanzhao.sens.model.enums;

/**
 * <pre>
 *     备份类型enum
 * </pre>
 *
 * @author : saysky
 * @date : 2018/7/22
 */
public enum BackupTypeEnum {

    /**
     * 资源文件
     */
    RESOURCES("resources"),

    /**
     * 数据库
     */
    DATABASES("databases"),

    /**
     * 文章
     */
    POSTS("posts");

    private String value;

    BackupTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
