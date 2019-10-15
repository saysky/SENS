package com.liuyanzhao.sens.web.controller.front;

import com.liuyanzhao.sens.entity.*;
import com.liuyanzhao.sens.model.dto.Archive;
import com.liuyanzhao.sens.model.dto.CommentPageDTO;
import com.liuyanzhao.sens.model.dto.SensConst;
import com.liuyanzhao.sens.model.dto.ListPage;
import com.liuyanzhao.sens.model.enums.BlogPropertiesEnum;
import com.liuyanzhao.sens.model.enums.CommentStatusEnum;
import com.liuyanzhao.sens.model.enums.PostTypeEnum;
import com.liuyanzhao.sens.model.enums.SidebarTypeEnum;
import com.liuyanzhao.sens.service.*;
import com.liuyanzhao.sens.utils.CommentUtil;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import cn.hutool.core.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <pre>
 *     前台内置页面，自定义页面控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2018/4/26
 */
@Controller
public class FrontPageController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    /**
     * 渲染自定义页面
     *
     * @param postUrl 页面路径
     * @param model   model
     * @return 模板路径/themes/{theme}/post
     */
    @GetMapping(value = "/p/{postUrl}")
    public String getPage(@PathVariable(value = "postUrl") String postUrl,
                          @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                          Model model) {
        Post post = postService.findByPostUrl(postUrl, PostTypeEnum.POST_TYPE_PAGE.getValue());
        if (null == post) {
            return this.renderNotFound();
        }
        //评论分页
        CommentPageDTO commentPage = commentService.findCommentPageByPostId(post.getId(), pageNumber);

        model.addAttribute("post", post);
        model.addAttribute("rainbow", commentPage.getRainbow());
        model.addAttribute("comments", commentPage.getCommentListPage());
        //文章访问量
        postService.updatePostView(post.getId());

        //侧边栏
        model.addAttribute("sidebarType", SidebarTypeEnum.PAGE.getValue());
        model.addAttribute("pageList", postService.findAllPosts(PostTypeEnum.POST_TYPE_PAGE.getValue()));
        return this.render("page");
    }

    /*==========================以下为内置页面==========================*/

    /**
     * 跳转到代码高亮页面
     *
     * @return 模板路径/themes/{theme}/highlight
     */
    @GetMapping(value = "/highlight")
    public String highlight() {
        return this.render("highlight");
    }


    /**
     * 文章归档页面
     *
     * @param model
     * @return
     */
    @GetMapping("/archive")
    public String archive(Model model) {
        List<Archive> archives = postService.findPostGroupByYearAndMonth();
        model.addAttribute("archives", archives);
        model.addAttribute("userCount", userService.getTotalCount());
        model.addAttribute("postCount", postService.getTotalCount());
        model.addAttribute("commentCount", commentService.getTotalCount());
        model.addAttribute("viewCount", postService.getTotalPostViews());

        //侧边栏
        model.addAttribute("sidebarType", SidebarTypeEnum.PAGE.getValue());
        model.addAttribute("pageList", postService.findAllPosts(PostTypeEnum.POST_TYPE_PAGE.getValue()));
        return this.render("archive");
    }

    /**
     * 最新评论
     *
     * @param model
     * @return
     */
    @GetMapping("/recent-comments")
    public String recentComments(Model model) {
        List<Comment> comments = commentService.findCommentsTop50();
        model.addAttribute("comments", comments);

        //侧边栏
        model.addAttribute("sidebarType", SidebarTypeEnum.PAGE.getValue());
        model.addAttribute("pageList", postService.findAllPosts(PostTypeEnum.POST_TYPE_PAGE.getValue()));
        return this.render("recent-comments");
    }


    /**
     * 留言版页面
     *
     * @return
     */
    @GetMapping("/message")
    public String message() {
        return this.render("message");
    }

    /**
     * 站点地图
     *
     * @return
     */
    @GetMapping("/sitemap")
    public String siteMap() {
        return this.render("sitemap");
    }

}
