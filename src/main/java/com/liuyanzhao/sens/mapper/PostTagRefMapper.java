package com.liuyanzhao.sens.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuyanzhao.sens.entity.PostTagRef;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liuyanzhao
 */
@Mapper
public interface PostTagRefMapper extends BaseMapper<PostTagRef> {

    /**
     * 根据文章Id删除记录
     *
     * @param postId 文章Id
     * @return 影响行数
     */
    Integer deleteByPostId(Long postId);

    /**
     * 根据标签Id删除记录
     *
     * @param tagId 标签Id
     * @return 影响行数
     */
    Integer deleteByTagId(Long tagId);

    /**
     * 根据标签Id查询文章Id
     *
     * @param tagId 标签Id
     * @return 文章Id列表
     */
    List<Long> selectPostIdByTagId(Long tagId);

    /**
     * 根据文章Id查询标签Id
     * @param postId 文章Id
     * @return 标签Id列表
     */
    List<Long> selectTagIdByPostId(Long postId);
}

