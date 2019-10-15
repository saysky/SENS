package com.liuyanzhao.sens.common.constant;

/**
 * 缓存key名
 *
 * @author 言曌
 * @date 2019-10-10 16:37
 */

public class RedisKeys {

    public static final String BASE_PREFIX = "SENS_BLOG:";

    /**
     * 排名相关
     */
    public static final String RANKING = BASE_PREFIX + "RANKING:";

    /**
     * 统计相关
     */
    public static final String COUNT = BASE_PREFIX + "COUNT:";

    /**
     * 基本信息相关
     */
    public static final String OPTION = BASE_PREFIX + "OPTION:";

    /**
     * 小工具
     */
    public static final String WIDGET = BASE_PREFIX + "WIDGET:";

    /**
     * 前台菜单
     */
    public static final String FRONT_MENU = BASE_PREFIX + "FRONT_MENU:";

    /**
     * 后台权限菜单
     */
    public static final String ROLE_ADMIN_MENU = BASE_PREFIX + "ROLE_ADMIN_MENU:";

    /**
     * 友情链接
     */
    public static final String LINK = BASE_PREFIX + "LINK:";

    /**
     * 幻灯片
     */
    public static final String SLIDE = BASE_PREFIX + "SLIDE:";

    /**
     * 分类
     */
    public static final String CATEGORY = BASE_PREFIX + "CATEGORY:";

    /**
     * 标签
     */
    public static final String TAG = BASE_PREFIX + "TAG:";

    /**
     * 用户
     */
    public static final String USER = BASE_PREFIX + "USER:";

    /**
     * 权限
     */
    public static final String PERMISSION = BASE_PREFIX + "PERMISSION:";

    /**
     * 所有的统计
     */
    public static final String ALL_COUNT = COUNT + "ALL_COUNT";

    /**
     * 所有设置
     */
    public static final String ALL_OPTIONS = OPTION + "ALL_OPTIONS";

    /**
     * 所有的友情链接
     */
    public static final String ALL_LINK = LINK + "ALL_LINK";

    /**
     * 所有文章根据访问量排名
     */
    public static final String ALL_POST_RANKING_BY_VIEWS = RANKING + "ALL_POST_RANKING_BY_VIEWS";

    /**
     * 某个用户所有文章根据访问量排名
     */
    public static final String USER_POST_RANKING_BY_VIEWS = RANKING + "USER_POST_RANKING_BY_VIEWS:";

    /**
     * 所有标签排名
     */
    public static final String ALL_TAG_RANKING = RANKING + "ALL_TAG_RANKING";

    /**
     * 某个用户标签排名
     */
    public static final String USER_TAG_RANKING = RANKING + "USER_TAG_RANKING:";

    /**
     * 某个文章的分类
     */
    public static final String POST_CATEGORY = CATEGORY + "POST_CATEGORY:";

    /**
     * 某个文章的标签
     */
    public static final String POST_TAG = TAG + "POST_TAG:";


    /**
     * 某个用户的权限URL列表
     */
    public static final String USER_PERMISSION_URLS = BASE_PREFIX + "USER_PERMISSION_URLS:";


}
