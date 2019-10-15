package com.liuyanzhao.sens.model.dto;

/**
 * <pre>
 *     附件存储地址enum
 * </pre>
 *
 * @author : Yawn
 * @date : 2018/12/4
 */
public enum AttachLocationEnum {

    /**
     * 服务器
     */
    SERVER(0,"server"),

    /**
     * 七牛
     */
    QINIU(1,"qiniu"),

    /**
     * 又拍云
     */
    UPYUN(2,"upyun");

    private Integer code;
    private String value;

    AttachLocationEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
