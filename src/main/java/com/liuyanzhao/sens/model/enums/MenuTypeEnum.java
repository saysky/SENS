package com.liuyanzhao.sens.model.enums;

/**
 * <pre>
 *     文章类型enum
 * </pre>
 *
 * @author : saysky
 * @date : 2018/7/1
 */
public enum MenuTypeEnum {

    /**
     * 前台主要菜单
     */
    FRONT_MAIN_MENU(0),

    /**
     * 前台顶部菜单
     */
    FRONT_TOP_MENU(1);

    private Integer code;

    MenuTypeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
