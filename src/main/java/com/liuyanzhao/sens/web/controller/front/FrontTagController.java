package com.liuyanzhao.sens.web.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.entity.Post;
import com.liuyanzhao.sens.entity.Tag;
import com.liuyanzhao.sens.entity.User;
import com.liuyanzhao.sens.model.dto.SensConst;
import com.liuyanzhao.sens.model.enums.BlogPropertiesEnum;
import com.liuyanzhao.sens.model.enums.SidebarTypeEnum;
import com.liuyanzhao.sens.service.*;
import com.liuyanzhao.sens.utils.PageUtil;
import com.liuyanzhao.sens.utils.SensUtils;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <pre>
 *     前台标签控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2018/4/26
 */
@Controller
@RequestMapping(value = "/tag")
public class FrontTagController extends BaseController {

    @Autowired
    private TagService tagService;

    @Autowired
    private PostService postService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    /**
     * 标签
     *
     * @return 模板路径/themes/{theme}/tags
     */
    @GetMapping
    public String tags() {
        return this.render("tags");
    }

    /**
     * 根据标签路径查询所有文章
     *
     * @param id    id
     * @param model model
     * @return String
     */
    @GetMapping(value = "/{id}")
    public String tags(Model model,
                       @PathVariable("id") Long id,
                       @RequestParam(value = "size", defaultValue = "15") Integer pageSize,
                       @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                       @RequestParam(value = "order", defaultValue = "desc") String order) {
        return this.tags(model, id, 1, pageSize, sort, order);
    }

    /**
     * 根据标签路径查询所有文章 分页
     *
     * @param model      model
     * @param id         ID
     * @param pageNumber 页码
     * @return String
     */
    @GetMapping(value = "/{id}/page/{pageNumber}")
    public String tags(Model model,
                       @PathVariable("id") Long id,
                       @PathVariable(value = "pageNumber") Integer pageNumber,
                       @RequestParam(value = "size", defaultValue = "15") Integer pageSize,
                       @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                       @RequestParam(value = "order", defaultValue = "desc") String order) {
        Tag tag = tagService.get(id);
        if (null == tag) {
            return this.renderNotFound();
        }
        if (!StringUtils.isBlank(SensConst.OPTIONS.get(BlogPropertiesEnum.INDEX_POSTS.getProp()))) {
            pageSize = Integer.parseInt(SensConst.OPTIONS.get(BlogPropertiesEnum.INDEX_POSTS.getProp()));
        }
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Post> posts = postService.findPostsByTags(tag, page);
        List<Post> postList = posts.getRecords();
        postList.forEach(post -> post.setCategories(categoryService.findByPostId(post.getId())));
        model.addAttribute("posts", postList);
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));

        model.addAttribute("tag", tag);
        model.addAttribute("prefix", "/tag/" + id);


        //侧边栏
        model.addAttribute("sidebarType", SidebarTypeEnum.TAG.getValue());
        User user = userService.get(tag.getUserId());
        if (user != null) {
            //该用户的文章数
            Integer postCount = postService.countByUserId(user.getId());
            user.setPostCount(postCount);
            //该用户的评论数
            Integer commentCount = commentService.countByUserId(user.getId());
            user.setCommentCount(commentCount);
            //该用户上次登录时间
            model.addAttribute("lastLogin", SensUtils.getRelativeDate(user.getLoginLast()));
            model.addAttribute("author", user);
            model.addAttribute("tagRanking", tagService.getTagRankingByUserId(user.getId(), 100));
        }
        return this.render("tag");
    }
}
