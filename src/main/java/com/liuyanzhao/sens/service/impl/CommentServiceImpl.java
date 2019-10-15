package com.liuyanzhao.sens.service.impl;

import cn.hutool.core.util.PageUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.mapper.CommentMapper;
import com.liuyanzhao.sens.entity.Comment;
import com.liuyanzhao.sens.model.dto.CommentPageDTO;
import com.liuyanzhao.sens.model.dto.ListPage;
import com.liuyanzhao.sens.model.dto.SensConst;
import com.liuyanzhao.sens.model.enums.BlogPropertiesEnum;
import com.liuyanzhao.sens.model.enums.CommentStatusEnum;
import com.liuyanzhao.sens.service.CommentService;
import com.liuyanzhao.sens.service.PostService;
import com.liuyanzhao.sens.utils.CommentUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * <pre>
 *     评论业务逻辑实现类
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/22
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostService postService;


    @Override
    public BaseMapper<Comment> getRepository() {
        return commentMapper;
    }

    @Override
    public QueryWrapper<Comment> getQueryWrapper(Comment comment) {
        //对指定字段查询
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        if (comment != null) {
            if (comment.getUserId() != null && comment.getUserId() != -1) {
                queryWrapper.eq("user_id", comment.getUserId());
            }
            if (comment.getAcceptUserId() != null && comment.getAcceptUserId() != -1) {
                queryWrapper.eq("accept_user_id", comment.getAcceptUserId());
            }
            if (comment.getCommentType() != null && comment.getCommentType() != -1) {
                queryWrapper.eq("comment_type", comment.getCommentType());
            }
            if (StrUtil.isNotBlank(comment.getCommentContent())) {
                queryWrapper.like("comment_content", comment.getCommentContent());
            }
            if (StrUtil.isNotBlank(comment.getCommentAuthor())) {
                queryWrapper.like("comment_author", comment.getCommentAuthor());
            }
            if (StrUtil.isNotBlank(comment.getCommentAuthorEmail())) {
                queryWrapper.like("comment_author_email", comment.getCommentAuthorEmail());
            }
            if (StrUtil.isNotBlank(comment.getCommentAuthorIp())) {
                queryWrapper.like("comment_author_ip", comment.getCommentAuthorIp());
            }
            if (StrUtil.isNotBlank(comment.getCommentAuthorUrl())) {
                queryWrapper.like("comment_author_url", comment.getCommentAuthorUrl());
            }
            if (comment.getPostId() != null && comment.getPostId() != -1) {
                queryWrapper.eq("post_id", comment.getPostId());
            }
            if (comment.getCommentStatus() != null && comment.getCommentStatus() != -1) {
                queryWrapper.eq("comment_status", comment.getCommentStatus());
            }
        }
        return queryWrapper;
    }
    @Override
    public Comment updateCommentStatus(Long commentId, Integer status) {
        //子评论随父评论状态一起改变
        //1.修改该评论状态
        Comment comment = get(commentId);
        comment.setCommentStatus(status);
        commentMapper.updateById(comment);
        //2.修改该评论的子评论状态
        List<Long> childIds = commentMapper.selectChildCommentIds(comment.getPathTrace() + commentId + "/");
        childIds.forEach(id -> commentMapper.updateCommentStatus(id, status));
        //3.修改文章评论数
        postService.resetCommentSize(comment.getPostId());
        return comment;
    }

    @Override
    public Integer deleteByUserId(Long userId) {
        return commentMapper.deleteByUserId(userId);
    }

    @Override
    public Integer deleteByAcceptUserId(Long acceptId) {
        return commentMapper.deleteByAcceptUserId(acceptId);
    }

    @Override
    public Page<Comment> pagingByStatus(Integer status, Page<Comment> page) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("comment_status", status);
        return (Page<Comment>) commentMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Page<Comment> pagingBySendUserAndStatus(Long userId, Integer status, Page<Comment> page) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("comment_status", status);
        return (Page<Comment>) commentMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Page<Comment> pagingByAcceptUserAndStatus(Long userId, Integer status, Page<Comment> page) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("accept_user_id", userId);
        queryWrapper.eq("comment_status", status);
        return (Page<Comment>) commentMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<Comment> findByAcceptUserAndStatus(Long userId, Integer status) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("accept_user_id", userId);
        queryWrapper.eq("comment_status", status);
        return commentMapper.selectList(queryWrapper);
    }

    @Override
    public Page<Comment> pagingCommentsByPostAndCommentStatus(Long postId, Integer status, Page<Comment> page) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("post_id", postId);
        queryWrapper.eq("comment_status", status);
        return (Page<Comment>) commentMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<Comment> findCommentsByPostAndCommentStatus(Long postId, Integer status) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("post_id", postId);
        queryWrapper.eq("comment_status", status);
        return commentMapper.selectList(queryWrapper);
    }

    @Override
    public CommentPageDTO findCommentPageByPostId(Long postId, Integer pageNumber) {
        List<Comment> comments = this.findCommentsByPostAndCommentStatus(postId, CommentStatusEnum.PUBLISHED.getCode());
        //默认显示10条
        Integer size = 10;
        //获取每页评论条数
        if (!StringUtils.isBlank(SensConst.OPTIONS.get(BlogPropertiesEnum.INDEX_COMMENTS.getProp()))) {
            size = Integer.parseInt(SensConst.OPTIONS.get(BlogPropertiesEnum.INDEX_COMMENTS.getProp()));
        }
        //评论分页
        ListPage<Comment> commentsPage = new ListPage<Comment>(CommentUtil.getComments(comments), pageNumber, size);
        int[] rainbow = PageUtil.rainbow(pageNumber, commentsPage.getTotalPage(), 10);
        return new CommentPageDTO(commentsPage, rainbow);
    }
    @Override
    public List<Comment> findCommentsByPostAndCommentStatusNot(Long postId, Integer status) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("post_id", postId);
        queryWrapper.ne("comment_status", status);
        return commentMapper.selectList(queryWrapper);
    }

    @Override
    public List<Comment> findCommentsLatest(Integer limit) {
        return commentMapper.findLatestCommentByLimit(limit);
    }

    @Override
    public Integer countByStatus(Integer status) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("comment_status", status);
        return commentMapper.selectCount(queryWrapper);
    }


    @Override
    public List<Comment> findCommentsTop50() {
        return commentMapper.findLatestCommentByLimit(50);
    }

    @Override
    public Integer countByUserId(Long userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        return commentMapper.selectCount(queryWrapper);
    }

    @Override
    public Integer countByAcceptUserAndStatus(Long userId, Integer status) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("accept_user_id", userId);
        queryWrapper.eq("comment_status", status);
        return commentMapper.selectCount(queryWrapper);
    }

    @Override
    public Integer countByAcceptUser(Long userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("accept_user_id", userId);
        return commentMapper.selectCount(queryWrapper);
    }

    @Override
    public Comment getLatestCommentByIP(String ip) {
        return commentMapper.getLatestCommentByIP(ip);
    }

   
    @Override
    public Comment insert(Comment comment) {
        commentMapper.insert(comment);
        //修改文章评论数
        postService.resetCommentSize(comment.getPostId());
        return comment;
    }

    @Override
    public Comment update(Comment comment) {
        commentMapper.updateById(comment);
        //修改文章评论数
        postService.resetCommentSize(comment.getPostId());
        return comment;
    }

    @Override
    public void delete(Long commentId) {
        Comment comment = this.get(commentId);
        if (comment != null) {
            //1.删除评论
            commentMapper.deleteById(commentId);
            //2.修改文章的评论数量
            postService.resetCommentSize(comment.getPostId());
        }
    }

    @Override
    public Comment insertOrUpdate(Comment comment) {
        if(comment.getId() == null) {
            insert(comment);
        } else {
            update(comment);
        }
        return comment;
    }

    @Override
    public Integer getTodayCount() {
        return commentMapper.getTodayCount();
    }

    @Override
    public List<Comment> getLatestCommentByAcceptUser(Long userId, Integer limit) {
        return commentMapper.getLatestCommentByAcceptUser(userId, limit);
    }
}
