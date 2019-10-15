package com.liuyanzhao.sens.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.entity.Post;
import com.liuyanzhao.sens.model.dto.Archive;
import com.liuyanzhao.sens.model.dto.PostSimpleDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;



import java.util.Date;
import java.util.List;

/**
 * @author liuyanzhao
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 查询前五条文章
     *
     * @return List
     */
    List<Post> findTopFive();

    /**
     * 获得年列表
     *
     * @return 年列表
     */
    List<Integer> listYears();

    /**
     * 查询文章归档信息 根据年份和月份
     *
     * @return List
     */
    List<Archive> findPostGroupByYearAndMonth();

    /**
     * 查询文章归档信息 根据年份
     *
     * @return List
     */
    List<Archive> findPostGroupByYear();

    /**
     * 根据年份和月份查询文章
     *
     * @param year  year
     * @param month month
     * @return List
     */
    List<PostSimpleDto> findPostByYearAndMonth(@Param("year") String year, @Param("month") String month);

    /**
     * 根据年份查询文章
     *
     * @param year year
     * @return List
     */
    List<PostSimpleDto> findPostByYear(@Param("year") String year);

    /**
     * 根据年份和月份查询文章 分页
     *
     * @param year  year
     * @param month month
     * @param page  分页
     * @return Page
     */
    List<Post> pagingPostByYearAndMonth(@Param("year") String year, @Param("month") String month, Page page);

    /**
     * 根据分类目录查询文章
     *
     * @param cateIds 分类id集合
     * @param status  status
     * @param page    分页
     * @return Page
     */
    List<Post> pagingPostByCategoryIdsAndPostStatus(@Param("cateIds") List<Long> cateIds,
                                                    @Param("status") Integer status,
                                                    Page page);
    /**
     * 根据标签查询文章，分页
     *
     * @param tagId  tagId
     * @param status status
     * @param page   分页
     * @return Page
     */
    List<Post> pagingPostsByTagIdAndPostStatus(@Param("tagId") Long tagId,
                                               @Param("status") Integer status, Page page);

    /**
     * 获取所有文章阅读量总和
     *
     * @return Long
     */
    Long getPostViewsSum();

    /**
     * 根据分类目录查询文章
     *
     * @param cateId 分类id
     * @return 文章列表
     */
    List<Post> findPostsByCategoryId(Long cateId);

    /**
     * 查询之后的一篇文章或公告
     *
     * @param postId Id
     * @param postType 类型
     * @return Post
     */
    Post findByPostIdAfter(@Param("postId") Long postId, @Param("postType") String postType);

    /**
     * 查询之前的一篇文章或公告
     *
     * @param postId Id
     * @param postType 类型
     * @return Post
     */
    Post findByPostIdBefore(@Param("postId") Long postId, @Param("postType") String postType);

    /**
     * 查询最大的更新
     *
     * @return
     */
    Date selectMaxPostUpdate();

    /**
     * 获得所有的PostId列表
     *
     * @return PostId列表
     */
    List<Long> selectAllPostIds();

    /**
     * 重置评论数量
     *
     * @return 数量
     */
    Integer resetCommentSize(Long postId);

    /**
     * 根据用户Id删除
     *
     * @return 影响行数
     */
    Integer deleteByUserId(Long userId);


    /**
     * 获得某个用户的所有文章Id
     *
     * @param userId 用户Id
     * @return Id列表
     */
    List<Long> selectIdsByUserId(Long userId);

    /**
     * 文章点赞量+1
     * @param postId
     * @return
     */
    Integer incrPostLikes(Long postId);

    /**
     * 文章访问量+1
     * @param postId
     * @return
     */
    Integer incrPostViews(Long postId);
    /**
     * 更新摘要
     * @param postId
     * @param summary
     * @return
     */
    Integer updatePostSummary(@Param("postId") Long postId,
                              @Param("summary") String summary);

    /**
     * 获得今日新增数量
     * @return
     */
    Integer getTodayCount();

    /**
     * 根据文章访问量获得排行榜
     * @param limit
     * @return
     */
    List<PostSimpleDto> getPostRankingByPostView(Integer limit);

    /**
     * 根据文章访问量获得排行榜
     * @param userId
     * @param limit
     * @return
     */
    List<PostSimpleDto> getPostRankingByUserIdAndPostView(@Param("userId") Long userId,
                                                 @Param("limit") Integer limit);

    /**
     * 根据分类URL查询文章
     * @param cateName
     * @param page
     * @return
     */
    List<Post> findPostByCateName(@Param("cateName") String cateName, Page page);
}

