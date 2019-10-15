package com.liuyanzhao.sens.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.common.base.BaseService;
import com.liuyanzhao.sens.entity.Tag;

import java.util.List;

/**
 * <pre>
 *     标签业务逻辑接口
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/12
 */
public interface TagService extends BaseService<Tag, Long> {


    /**
     * 热门标签
     *
     * @return 标签列表
     */
    List<Tag> findHotTags(Integer limit);

    /**
     * 根据标签名称查询
     *
     * @param tagName tagName
     * @return Tag
     */
    Tag findTagByTagName(String tagName);

    /**
     * 根据标签名称查询
     *
     * @param userId userId
     * @param tagName tagName
     * @return Tag
     */
    Tag findTagByUserIdAndTagName(Long userId, String tagName);

    /**
     * 转换标签字符串为实体集合
     *
     * @param userId 当前登录用户
     * @param tagList tagList
     * @return List
     */
    List<Tag> strListToTagList(Long userId, String tagList);

    /**
     * 根据文章Id获得标签列表
     *
     * @param postId 文章id
     * @return 分类列表
     */
    List<Tag> findByPostId(Long postId);

    /**
     * 根据用户ID获取
     * @param userId
     * @return
     */
    List<Tag> findByUserId(Long userId);

    /**
     * 获得今日新增数量
     * @return
     */
    Integer getTodayCount();

    /**
     * 获得某个用户的标签列表，带数量
     * @param userId
     * @return
     */
    Page<Tag> findByUserIdWithCount(Long userId, Page<Tag> page);

    /**
     * 根据用户ID删除
     * @param userId
     * @return
     */
    Integer deleteByUserId(Long userId);

    /**
     * 获得某个用户的标签排名列表
     * @param userId
     * @param limit
     * @return
     */
    List<Tag> getTagRankingByUserId(Long userId, Integer limit);
}
