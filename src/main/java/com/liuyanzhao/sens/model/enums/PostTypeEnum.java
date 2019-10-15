package com.liuyanzhao.sens.model.enums;

/**
 * <pre>
 *     文章类型enum
 * </pre>
 *
 * @author : saysky
 * @date : 2018/7/1
 */
public enum PostTypeEnum {

    /**
     * 文章
     */
    POST_TYPE_POST("post"),

    /**
     * 页面
     */
    POST_TYPE_PAGE("page"),

    /**
     * 公告
     */
    POST_TYPE_NOTICE("notice");

    private String value;

    PostTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
