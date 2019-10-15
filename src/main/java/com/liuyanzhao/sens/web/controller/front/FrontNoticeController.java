package com.liuyanzhao.sens.web.controller.front;

import cn.hutool.core.util.PageUtil;
import com.liuyanzhao.sens.entity.*;
import com.liuyanzhao.sens.model.dto.ListPage;
import com.liuyanzhao.sens.model.dto.SensConst;
import com.liuyanzhao.sens.model.enums.*;
import com.liuyanzhao.sens.service.*;
import com.liuyanzhao.sens.utils.CommentUtil;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *     前台公告归档控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2018/4/26
 */
@Slf4j
@Controller
@RequestMapping("/notice")
public class FrontNoticeController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    /**
     * 渲染公告详情
     *
     * @param postId 公告Id
     * @param model  model
     * @return 模板路径/themes/{theme}/notice
     */
    @GetMapping(value = {"{postId}"})
    public String getNotice(@PathVariable(value = "postId") Long postId,
                            @RequestParam(value = "page", defaultValue = "1") Integer page,
                            Model model) {
        //1、查询公告
        Post post = postService.get(postId);


        if (null == post || !post.getPostStatus().equals(PostStatusEnum.PUBLISHED.getCode())) {
            return this.renderNotFound();
        }

        //2、上一篇下一篇
        Post beforePost = postService.findPreciousPost(post.getId(), PostTypeEnum.POST_TYPE_NOTICE.getValue());
        Post afterPost = postService.findNextPost(post.getId(), PostTypeEnum.POST_TYPE_NOTICE.getValue());
        model.addAttribute("beforePost", beforePost);
        model.addAttribute("afterPost", afterPost);

        //3、评论列表
        List<Comment> comments = commentService.findCommentsByPostAndCommentStatus(post.getId(), CommentStatusEnum.PUBLISHED.getCode());

        //默认显示10条
        Integer size = 10;
        //获取每页评论条数
        if (!StringUtils.isBlank(SensConst.OPTIONS.get(BlogPropertiesEnum.INDEX_COMMENTS.getProp()))) {
            size = Integer.parseInt(SensConst.OPTIONS.get(BlogPropertiesEnum.INDEX_COMMENTS.getProp()));
        }
        //评论分页
        ListPage<Comment> commentsPage = new ListPage<Comment>(CommentUtil.getComments(comments), page, size);
        int[] rainbow = PageUtil.rainbow(page, commentsPage.getTotalPage(), 10);

        //5.公告访问量
        postService.updatePostView(post.getId());

        //6、作者
        User user = userService.get(post.getUserId());
        post.setUser(user);

        //7、是否是作者
        User loginUser = getLoginUser();
        if (loginUser != null && Objects.equals(loginUser.getId(), post.getUserId())) {
            model.addAttribute("isAuthor", Boolean.TRUE);
        }

        model.addAttribute("post", post);
        model.addAttribute("comments", commentsPage);
        model.addAttribute("commentCount", comments.size());
        model.addAttribute("rainbow", rainbow);

        //侧边栏
        model.addAttribute("sidebarType", SidebarTypeEnum.NOTICE.getValue());
        model.addAttribute("noticeList", postService.findAllPosts(PostTypeEnum.POST_TYPE_NOTICE.getValue()));
        return this.render("notice");
    }


}
