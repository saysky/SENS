package com.liuyanzhao.sens.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liuyanzhao
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {


    /**
     * 查询所有,包括文章数
     * @return 分类
     */
    List<Category> findAllWithCount();

    /**
     * 查询所有,包括文章数
     * @return 分类
     */
    List<Category> findByUserIdWithCount(@Param("userId") Long userId, Page page);

    /**
     * 获得某篇文章的分类列表
     *
     * @param postId 文章Id
     * @return List
     */
    List<Category> findByPostId(Long postId);


    /**
     * 获得子分类Id列表
     *
     * @param  pathTrace /138/ 这种格式
     * @return 子分类Id列表
     */
    List<Long> selectChildCateIds(@Param("pathTrace") String pathTrace);

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
}

