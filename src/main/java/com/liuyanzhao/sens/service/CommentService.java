package com.liuyanzhao.sens.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.common.base.BaseService;
import com.liuyanzhao.sens.entity.Comment;
import com.liuyanzhao.sens.model.dto.CommentPageDTO;

import java.util.List;

/**
 * <pre>
 *     评论业务逻辑接口
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/22
 */
public interface CommentService extends BaseService<Comment, Long> {

    /**
     * 根据用户Id删除评论
     *
     * @param userId 用户Id
     */
    Integer deleteByUserId(Long userId);

    /**
     * 根据评论接受人Id删除评论
     *
     * @param acceptId 用户Id
     */
    Integer deleteByAcceptUserId(Long acceptId);
    /**
     * 查询所有的评论，用于后台管理
     *
     * @param status status
     * @param page   page
     * @return Page
     */
    Page<Comment> pagingByStatus(Integer status, Page<Comment> page);

    /**
     * 获得某个用户发的的评论
     *
     * @param userId 用户Id
     * @param status 状态
     * @param page   分页信息
     * @return 评论
     */
    Page<Comment> pagingBySendUserAndStatus(Long userId, Integer status, Page<Comment> page);

    /**
     * 获得某个用户应该收到的评论
     *
     * @param userId 用户Id
     * @param status 状态
     * @param page   分页信息
     * @return 评论
     */
    Page<Comment> pagingByAcceptUserAndStatus(Long userId, Integer status, Page<Comment> page);

    /**
     * 根据评论状态查询评论
     *
     * @param userId 用户Id
     * @param status 评论状态
     * @return List
     */
    List<Comment> findByAcceptUserAndStatus(Long userId, Integer status);

    /**
     * 根据文章和评论状态查询评论 分页
     *
     * @param postId 文章id
     * @param page   page
     * @param status status
     * @return Page
     */
    Page<Comment> pagingCommentsByPostAndCommentStatus(Long postId, Integer status, Page<Comment> page);

    /**
     * 根据文章和评论状态查询评论 不分页
     *
     * @param postId 文章id
     * @param status status
     * @return List
     */
    List<Comment> findCommentsByPostAndCommentStatus(Long postId, Integer status);

    /**
     * 获得某篇文章评论
     * @param postId 文章ID
     * @param pageNumber 页码
     * @return
     */
    CommentPageDTO findCommentPageByPostId(Long postId, Integer pageNumber);
    /**
     * 根据文章和评论状态（为不查询的）查询评论 不分页
     *
     * @param postId 文章id
     * @param status status
     * @return List
     */
    List<Comment> findCommentsByPostAndCommentStatusNot(Long postId, Integer status);

    /**
     * 查询最新的前五条评论
     *
     * @return List
     */
    List<Comment> findCommentsLatest(Integer limit);


    /**
     * 根据评论状态查询数量
     *
     * @param status 评论状态
     * @return 评论数量
     */
    Integer countByStatus(Integer status);

    /**
     * 获得前50条评论
     *
     * @return 评论列表
     */
    List<Comment> findCommentsTop50();

    /**
     * 根据评论统计评论数
     *
     * @param userId 用户Id
     * @return 数量
     */
    Integer countByUserId(Long userId);

    /**
     * 统计评论数量
     *
     * @param userId 用户Id
     * @param status 状态
     * @return 数量
     */
    Integer countByAcceptUserAndStatus(Long userId, Integer status);

    /**
     * 统计评论数量
     *
     * @param userId 用户Id
     * @return 数量
     */
    Integer countByAcceptUser(Long userId);


    /**
     * 获得某个ip最新评论
     *
     * @param ip ip地址
     * @return 评论
     */
    Comment getLatestCommentByIP(String ip);

    /**
     * 更新评论状态
     *
     * @param commentId
     * @param status
     * @return
     */
    Comment updateCommentStatus(Long commentId, Integer status);

    /**
     * 获得今日新增数量
     *
     * @return
     */
    Integer getTodayCount();

    /**
     * 获得某个用户最新收到的评论
     *
     * @param userId 收到评论的用户
     * @param limit  查询数量
     * @return
     */
    List<Comment> getLatestCommentByAcceptUser(Long userId, Integer limit);

}
