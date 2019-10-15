package com.liuyanzhao.sens.model.enums;

/**
 * <pre>
 *     评论状态enum
 * </pre>
 *
 * @author : saysky
 * @date : 2018/7/1
 */
public enum CommentStatusEnum {

    /**
     * 已发布
     */
    PUBLISHED(0),

    /**
     * 待审核
     */
    CHECKING(1),

    /**
     * 回收站
     */
    RECYCLE(2);

    private Integer code;

    CommentStatusEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}
