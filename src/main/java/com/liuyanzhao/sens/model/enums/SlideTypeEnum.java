package com.liuyanzhao.sens.model.enums;

/**
 * <pre>
 *     文章类型enum
 * </pre>
 *
 * @author : saysky
 * @date : 2018/7/1
 */
public enum SlideTypeEnum {

    /**
     * 前台幻灯片
     */
    INDEX_SLIDE(0);

    private Integer code;

    SlideTypeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
