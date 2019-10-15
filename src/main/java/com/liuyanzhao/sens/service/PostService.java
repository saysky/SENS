package com.liuyanzhao.sens.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.common.base.BaseService;
import com.liuyanzhao.sens.entity.Category;
import com.liuyanzhao.sens.entity.Post;
import com.liuyanzhao.sens.entity.Tag;
import com.liuyanzhao.sens.model.dto.Archive;
import com.liuyanzhao.sens.model.dto.CountDTO;
import com.liuyanzhao.sens.model.dto.PostSimpleDto;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     记录/页面业务逻辑接口
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/14
 */
public interface PostService extends BaseService<Post, Long> {


    /**
     * 修改记录状态
     *
     * @param postId postId
     * @param status status
     * @return Post
     */
    Post updatePostStatus(Long postId, Integer status);

    /**
     * 修改记录阅读量
     *
     * @param postId 记录Id
     * @return 记录访问量
     */
    void updatePostView(Long postId);

    /**
     * 批量修改摘要
     *
     * @param postSummary postSummary
     */
    void updateAllSummary(Integer postSummary);

    /**
     * 获取记录列表 不分页
     *
     * @param postType post or page
     * @return List
     */
    List<Post> findAllPosts(String postType);

    /**
     * 根据记录状态查询，不分页，用于站点地图
     *
     * @param status   0，1，2
     * @param postType post or page
     * @return List
     */
    List<Post> findPostByStatus(Integer status, String postType);

    /**
     * 根据编号和类型查询记录
     *
     * @param postId   postId
     * @param postType postType
     * @return Post
     */
    Post findByPostId(Long postId, String postType);

    /**
     * 根据Post路径查询
     *
     * @param postUrl 路径
     * @return Post
     */
    Post findByPostUrl(String postUrl);

    /**
     * 根据记录路径查询
     *
     * @param postUrl  路径
     * @param postType post or page
     * @return Post
     */
    Post findByPostUrl(String postUrl, String postType);

    /**
     * 查询前五条数据
     *
     * @return List
     */
    List<Post> findPostLatest();


    /**
     * 查询Id之后的记录
     *
     * @param postId 记录ID
     * @return 记录
     */
    Post findNextPost(Long postId, String postType);

    /**
     * 查询Id之前的记录
     *
     * @param postId   记录ID
     * @param postType 类型
     * @return 记录
     */
    Post findPreciousPost(Long postId, String postType);

    /**
     * 查询归档信息 根据年份和月份
     *
     * @return List
     */
    List<Archive> findPostGroupByYearAndMonth();

    /**
     * 查询归档信息 根据年份
     *
     * @return List
     */
    List<Archive> findPostGroupByYear();

    /**
     * 根据年份和月份查询记录
     *
     * @param year  year
     * @param month month
     * @return List
     */
    List<PostSimpleDto> findPostByYearAndMonth(String year, String month);


    /**
     * 根据年份和月份查询记录 分页
     *
     * @param year  year
     * @param month month
     * @param page  page
     * @return Page
     */
    Page<Post> findPostByYearAndMonth(String year, String month, Page<Post> page);

    /**
     * 根据年份查询记录
     *
     * @param year year
     * @return List
     */
    List<PostSimpleDto> findPostByYear(String year);

    /**
     * 根据分类目录查询记录
     *
     * @param category category
     * @param page     page
     * @return Page
     */
    Page<Post> findPostByCategory(Category category, Page<Post> page);

    /**
     * 根据标签查询记录
     *
     * @param tag  tag
     * @param page page
     * @return Page
     */
    Page<Post> findPostsByTags(Tag tag, Page<Post> page);

    /**
     * 根据ES搜索文章
     *
     * @param criteria
     * @return
     */
    Page<Post> findPostsByEs(Map<String, Object> criteria, Page<Post> page);

    /**
     * 当前记录的相同分类的记录
     *
     * @param post post
     * @return List
     */
    List<Post> listSameCategoryPosts(Post post);

    /**
     * 获取所有记录的阅读量
     *
     * @return Long
     */
    Long getTotalPostViews();

    /**
     * 根据状态和类型查询post数量
     *
     * @param postType 类型
     * @param status   记录状态
     * @return 记录数量
     */
    Integer countByPostTypeAndStatus(String postType, Integer status);

    /**
     * 根据状态和类型查询post,不分页
     *
     * @param postType 类型
     * @param status   记录状态
     * @return 记录数量
     */
    List<Post> findByPostTypeAndStatus(String postType, Integer status);

    /**
     * 根据状态和类型查询post,分页
     *
     * @param postType 类型
     * @param status   记录状态
     * @return 记录数量
     */
    Page<Post> pagingByPostTypeAndStatus(String postType, Integer status, Page<Post> page);

    /**
     * 根据记录状态查询文章数量
     *
     * @param userId 用户Id
     * @param status 记录状态
     * @return 记录数量
     */
    Integer countArticleByUserIdAndStatus(Long userId, Integer status);

    /**
     * 生成rss
     *
     * @param posts posts
     * @return String
     */
    String buildRss(List<Post> posts);

    /**
     * 生成sitemap
     *
     * @return String
     */
    String buildArticleSiteMap();


    /**
     * 更新记录评论数
     *
     * @param postId 记录Id
     */
    void resetCommentSize(Long postId);

    /**
     * 更新记录点赞量
     *
     * @param postId 记录Id
     */
    void incrPostLikes(Long postId);

    /**
     * 删除用户的记录
     *
     * @param userId 用户Id
     */
    void deleteByUserId(Long userId);

    /**
     * 获得用户的文章数
     *
     * @param userId
     * @return
     */
    Integer countByUserId(Long userId);

    /**
     * 获得今日新增数量
     * @return
     */
    Integer getTodayCount();

    /**
     * 获得文章阅读排名
     * @param limit
     * @return
     */
    List<PostSimpleDto> getPostRankingByPostView(Integer limit);

    /**
     * 获得文章阅读排名
     * @param userId
     * @param limit
     * @return
     */
    List<PostSimpleDto> getPostRankingByUserIdAndPostView(Long userId, Integer limit);

    /**
     * 根据分类名称查询
     * @param cateName 分类名称
     * @param page
     * @return
     */
    Page<Post> findPostByCateName(String cateName, Page<Post> page);

    /**
     * 获得所有的统计
     * @return
     */
    CountDTO getAllCount();
}
