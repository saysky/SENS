package com.liuyanzhao.sens.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.entity.Category;
import com.liuyanzhao.sens.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liuyanzhao
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 获得某篇文章的标签列表
     *
     * @param postId 文章Id
     * @return List
     */
    List<Tag> findByPostId(Long postId);

    /**
     * 获得所有包括统计文章数
     *
     * @return 标签列表
     */
    List<Tag> findAllWithCount(Integer limit);


    /**
     * 获得某个用户所有包括统计文章数
     *
     * @return 标签列表
     */
    List<Tag> findByUserIdWithCount(@Param("userId") Long userId, Page page);

    /**
     * 查询没有用过的标签
     *
     * @return 标签列表
     */
    List<Tag> findTagNotUse();

    /**
     * 获得今日新增数量
     * @return
     */
    Integer getTodayCount();

    /**
     * 根据用户ID删除
     * @param userId
     * @return
     */
    Integer deleteByUserId(Long userId);

    /**
     * 获得某个标签排名
     * @param userId
     * @param limit
     * @return
     */
    List<Tag> getTagRankingByUserId(@Param("userId") Long userId,
                                    @Param("limit") Integer limit);
}

