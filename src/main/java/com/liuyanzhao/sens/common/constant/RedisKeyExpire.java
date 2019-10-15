package com.liuyanzhao.sens.common.constant;

/**
 * 缓存过期时间，单位秒
 *
 * @author 言曌
 * @date 2019-10-10 16:37
 */

public class RedisKeyExpire {

    /**
     * 所有的统计,1小时
     */
    public static final int ALL_COUNT =  3600;

    /**
     * 所有设置,24小时
     */
    public static final int ALL_OPTIONS = 86400;

    /**
     * 所有文章根据访问量排名,24小时
     */
    public static final int ALL_POST_RANKING_BY_VIEWS = 86400;

    /**
     * 某个用户所有文章根据访问量排名,6小时
     */
    public static final int USER_POST_RANKING_BY_VIEWS = 21600;

    /**
     * 小工具,86400
     */
    public static final int WIDGET = 86400;

    /**
     * 用户
     */
    public static final int USER = 86400;

    /**
     * 前台菜单
     */
    public static final int FRONT_MENU = 86400;

    /**
     * 后台角色菜单
     */
    public static final int ROLE_ADMIN_MENU = 86400;

    /**
     * 所有的友情链接
     */
    public static final int ALL_LINK = 86400;

    /**
     * 幻灯片
     */
    public static final int SLIDE = 86400;

    /**
     * 所有标签排名
     */
    public static final int ALL_TAG_RANKING = 86400;

    /**
     * 某个用户标签排名
     */
    public static final int USER_TAG_RANKING = 21600;

    /**
     * 某个文章的分类
     */
    public static final int POST_CATEGORY = 86400;

    /**
     * 某个文章的标签
     */
    public static final int POST_TAG = 86400;

    /**
     * 某个用户的权限URL列表
     */
    public static final int USER_PERMISSION_URLS = 86400;


}
