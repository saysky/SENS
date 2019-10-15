package com.liuyanzhao.sens.web.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import com.liuyanzhao.sens.entity.Comment;
import com.liuyanzhao.sens.entity.Post;
import com.liuyanzhao.sens.entity.User;
import com.liuyanzhao.sens.model.dto.SensConst;
import com.liuyanzhao.sens.model.dto.JsonResult;
import com.liuyanzhao.sens.model.enums.*;
import com.liuyanzhao.sens.service.CommentService;
import com.liuyanzhao.sens.service.MailService;
import com.liuyanzhao.sens.service.PostService;
import com.liuyanzhao.sens.service.UserService;
import com.liuyanzhao.sens.utils.CommentUtil;
import com.liuyanzhao.sens.utils.OwoUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HtmlUtil;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <pre>
 *     前台评论控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2018/4/26
 */
@Slf4j
@Controller
public class FrontCommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    /**
     * 获取文章的评论
     *
     * @param postId postId 文章编号
     * @return List
     */
    @GetMapping(value = "/getComment/{postId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<Comment> getComment(@PathVariable(value = "postId") Long postId) {
        Post post = postService.get(postId);
        Page page = new Page(0, 999);
        Page<Comment> comments = commentService.pagingCommentsByPostAndCommentStatus(post.getId(), CommentStatusEnum.PUBLISHED.getCode(), page);
        return CommentUtil.getComments(comments.getRecords());
    }

    /**
     * 加载评论
     *
     * @param page 页码
     * @param post 当前文章
     * @return List
     */
    @GetMapping(value = "/loadComment")
    @ResponseBody
    public List<Comment> loadComment(@RequestParam(value = "page") Integer page,
                                     @RequestParam(value = "post") Post post) {
        Page pagination = new Page(page, 10);
        Page<Comment> comments = commentService.pagingCommentsByPostAndCommentStatus(post.getId(), CommentStatusEnum.PUBLISHED.getCode(), pagination);
        return comments.getRecords();
    }

    /**
     * 提交新评论
     *
     * @param comment comment实体
     * @param request request
     * @return JsonResult
     */
    @PostMapping(value = "/newComment")
    @ResponseBody
    public JsonResult newComment(@ModelAttribute("comment") Comment comment,
                                 HttpServletRequest request) {

        boolean isEmailToAdmin = true;
        boolean isEmailToParent = comment.getCommentParent() > 0;
        //1.判断字数，应该小于1000字
        if (comment != null && comment.getCommentContent() != null && comment.getCommentContent().length() > 1000) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), "评论字数太长，请删减或者分条发送！");
        }

        //2.垃圾评论过滤
        String commentContent = comment.getCommentContent();
        String rubbishWords = SensConst.OPTIONS.get(BlogPropertiesEnum.COMMENT_RUBBISH_WORDS.getProp());
        if (!Strings.isNullOrEmpty(rubbishWords)) {
            String[] arr = rubbishWords.split(",");
            for (int i = 0; i < arr.length; i++) {
                if (commentContent.indexOf(arr[i]) != -1) {
                    return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), "你的评论已经提交，待博主审核之后可显示。");
                }
            }
        }

        //3.检查重复评论
        String ip = ServletUtil.getClientIP(request);
        Comment latestComment = commentService.getLatestCommentByIP(ip);
        if (latestComment != null && Objects.equals(latestComment.getCommentContent(), comment.getCommentContent())) {
            return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), "评论成功！");
        }

        //2.检查文章是否存在
        User loginUser = getLoginUser();
        Comment lastComment = null;
        Post post = postService.get(comment.getPostId());
        if (post == null) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), "文章不存在！");
        }
        comment.setIsAdmin(0);

        //3.判断是评论还是回复
        //回复评论
        if (comment.getCommentParent() > 0) {
            lastComment = commentService.get(comment.getCommentParent());
            if (lastComment == null) {
                return new JsonResult(ResultCodeEnum.FAIL.getCode(), "回复的评论不存在！");
            }
            comment.setAcceptUserId(lastComment.getUserId());
            comment.setPathTrace(lastComment.getPathTrace() + lastComment.getId() + "/");
            String lastContent = "<a href='#comment-id-" + lastComment.getId() + "'>@" + lastComment.getCommentAuthor() + "</a>";
            comment.setCommentContent(lastContent + OwoUtil.markToImg(HtmlUtil.escape(comment.getCommentContent())));
        }
        //评论
        else {
            comment.setAcceptUserId(post.getUserId());
            comment.setPathTrace("/");
            //将评论内容的字符专为安全字符
            comment.setCommentContent(OwoUtil.markToImg(HtmlUtil.escape(comment.getCommentContent())));
        }

        //4.判断是否登录
        //如果已登录
        if (loginUser != null) {
            if (Objects.equals(post.getUserId(), loginUser.getId())) {
                comment.setIsAdmin(1);
            }
            comment.setUserId(loginUser.getId());
            comment.setCommentAuthorEmail(loginUser.getUserEmail());
            comment.setCommentAuthor(loginUser.getUserDisplayName());
            comment.setCommentAuthorUrl(loginUser.getUserSite());
            comment.setCommentAuthorAvatar(loginUser.getUserAvatar());
            //如果评论的是自己的文章，不发邮件
            if (Objects.equals(loginUser.getId(), post.getUserId())) {
                isEmailToAdmin = false;
            }
            //如果回复的是自己评论，不发邮件
            if (lastComment != null && Objects.equals(loginUser.getId(), lastComment.getUserId())) {
                isEmailToParent = false;
            }
        }
        //匿名评论
        else {
            comment.setCommentAuthorEmail(HtmlUtil.escape(comment.getCommentAuthorEmail()).toLowerCase());
            comment.setCommentAuthor(HtmlUtil.escape(comment.getCommentAuthor()));
            if (StringUtils.isNotEmpty(comment.getCommentAuthorUrl())) {
                comment.setCommentAuthorUrl(URLUtil.formatUrl(comment.getCommentAuthorUrl()));
            }
            comment.setUserId(0L);
            if (StringUtils.isNotBlank(comment.getCommentAuthorEmail())) {
                comment.setCommentAuthorEmailMd5(SecureUtil.md5(comment.getCommentAuthorEmail()));
            }
            //如果回复的是自己评论，不发邮件
            if (lastComment != null && Objects.equals(comment.getCommentAuthorEmail(), lastComment.getCommentAuthorEmail())) {
                isEmailToParent = false;
            }
        }
        comment.setPostId(post.getId());
        comment.setCommentAuthorIp(ip);


        //5.保存分类信息
        if (StringUtils.equals(SensConst.OPTIONS.get(BlogPropertiesEnum.NEW_COMMENT_NEED_CHECK.getProp()), TrueFalseEnum.TRUE.getValue()) || SensConst.OPTIONS.get(BlogPropertiesEnum.NEW_COMMENT_NEED_CHECK.getProp()) == null) {
            if (isEmailToAdmin || isEmailToParent) {
                comment.setCommentStatus(CommentStatusEnum.PUBLISHED.getCode());
            } else {
                comment.setCommentStatus(CommentStatusEnum.CHECKING.getCode());
            }
        } else {
            comment.setCommentStatus(CommentStatusEnum.PUBLISHED.getCode());
        }
        commentService.insertOrUpdate(comment);
        //6.邮件通知
        if (isEmailToParent) {
            new EmailToParent(comment, lastComment, post).start();
        }
        //评论自己的文章不要发邮箱
        if (isEmailToAdmin) {
            new EmailToAdmin(comment, post).start();
        }
        if (StringUtils.equals(SensConst.OPTIONS.get(BlogPropertiesEnum.NEW_COMMENT_NEED_CHECK.getProp()), TrueFalseEnum.TRUE.getValue()) || SensConst.OPTIONS.get(BlogPropertiesEnum.NEW_COMMENT_NEED_CHECK.getProp()) == null) {
            return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), "你的评论已经提交，待博主审核之后可显示。");
        }
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), "评论成功", comment);

    }

    /**
     * 发送邮件给博主
     */
    class EmailToAdmin extends Thread {
        private Comment comment;
        private Post post;

        private EmailToAdmin(Comment comment, Post post) {
            this.comment = comment;
            this.post = post;
        }

        @Override
        public void run() {
            if (StringUtils.equals(SensConst.OPTIONS.get(BlogPropertiesEnum.SMTP_EMAIL_ENABLE.getProp()), TrueFalseEnum.TRUE.getValue()) && StringUtils.equals(SensConst.OPTIONS.get(BlogPropertiesEnum.NEW_COMMENT_NOTICE.getProp()), TrueFalseEnum.TRUE.getValue())) {
                try {
                    //发送邮件到博主
                    User user = userService.get(post.getUserId());
                    if (user != null && user.getUserEmail() != null) {
                        Map<String, Object> map = new HashMap<>(5);
                        map.put("author", user.getUserDisplayName());
                        map.put("pageName", post.getPostTitle());
                        if (StringUtils.equals(post.getPostType(), PostTypeEnum.POST_TYPE_POST.getValue())) {
                            map.put("pageUrl", SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_URL.getProp()) + "/article/" + post.getId() + "#comment-id-" + comment.getId());
                        } else if (StringUtils.equals(post.getPostType(), PostTypeEnum.POST_TYPE_NOTICE.getValue())) {
                            map.put("pageUrl", SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_URL.getProp()) + "/notice/" + post.getId() + "#comment-id-" + comment.getId());
                        } else {
                            map.put("pageUrl", SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_URL.getProp()) + "/p/" + post.getPostUrl() + "#comment-id-" + comment.getId());
                        }
                        map.put("visitor", comment.getCommentAuthor());
                        map.put("commentContent", comment.getCommentContent());
                        mailService.sendTemplateMail(user.getUserEmail(), "有新的评论", map, "common/mail_template/mail_admin.ftl");
                    }
                } catch (Exception e) {
                    log.error("邮件服务器未配置：{}", e.getMessage());
                }
            }
        }
    }

    /**
     * 发送邮件给被评论方
     */
    class EmailToParent extends Thread {
        private Comment comment;
        private Comment lastComment;
        private Post post;

        private EmailToParent(Comment comment, Comment lastComment, Post post) {
            this.comment = comment;
            this.lastComment = lastComment;
            this.post = post;
        }

        @Override
        public void run() {
            //发送通知给对方
            if (StringUtils.equals(SensConst.OPTIONS.get(BlogPropertiesEnum.SMTP_EMAIL_ENABLE.getProp()), TrueFalseEnum.TRUE.getValue()) && StringUtils.equals(SensConst.OPTIONS.get(BlogPropertiesEnum.NEW_COMMENT_NOTICE.getProp()), TrueFalseEnum.TRUE.getValue())) {
                if (Validator.isEmail(lastComment.getCommentAuthorEmail())) {
                    Map<String, Object> map = new HashMap<>(8);
                    map.put("blogTitle", SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_TITLE.getProp()));
                    map.put("commentAuthor", lastComment.getCommentAuthor());
                    map.put("pageName", post.getPostTitle());
                    if (StringUtils.equals(post.getPostType(), PostTypeEnum.POST_TYPE_POST.getValue())) {
                        map.put("pageUrl", SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_URL.getProp()) + "/article/" + post.getId() + "#comment-id-" + comment.getId());
                    } else if (StringUtils.equals(post.getPostType(), PostTypeEnum.POST_TYPE_NOTICE.getValue())) {
                        map.put("pageUrl", SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_URL.getProp()) + "/notice/" + post.getId() + "#comment-id-" + comment.getId());
                    } else {
                        map.put("pageUrl", SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_URL.getProp()) + "/p/" + post.getPostUrl() + "#comment-id-" + comment.getId());
                    }
                    map.put("commentContent", lastComment.getCommentContent());
                    map.put("replyAuthor", comment.getCommentAuthor());
                    map.put("replyContent", comment.getCommentContent());
                    map.put("blogUrl", SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_URL.getProp()));
                    mailService.sendTemplateMail(
                            lastComment.getCommentAuthorEmail(), "您在" + SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_TITLE.getProp()) + "的评论有了新回复", map, "common/mail_template/mail_reply.ftl");
                }
            }
        }
    }

}

