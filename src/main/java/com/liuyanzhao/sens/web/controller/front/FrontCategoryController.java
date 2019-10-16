package com.liuyanzhao.sens.web.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.entity.Category;
import com.liuyanzhao.sens.entity.Post;
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

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     前台文章分类控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2018/4/26
 */
@Controller
@RequestMapping(value = "/category")
public class FrontCategoryController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TagService tagService;

    /**
     * 根据分类路径查询文章
     *
     * @param model model
     * @param id    分类ID
     * @return string
     */
    @GetMapping(value = "/{cateId}")
    public String categories(Model model,
                             @PathVariable("cateId") Long id,
                             @RequestParam(value = "size", defaultValue = "15") Integer pageSize,
                             @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                             @RequestParam(value = "order", defaultValue = "desc") String order) {
        return this.categories(model, id, 1, pageSize, sort, order);
    }

    /**
     * 根据分类目录查询所有文章 分页
     *
     * @param model      model
     * @param id         分类ID
     * @param pageNumber 页码
     * @return String
     */
    @GetMapping("/{cateId}/page/{pageNumber}")
    public String categories(Model model,
                             @PathVariable("cateId") Long id,
                             @PathVariable(value = "pageNumber") Integer pageNumber,
                             @RequestParam(value = "size", defaultValue = "15") Integer pageSize,
                             @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                             @RequestParam(value = "order", defaultValue = "desc") String order) {
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Post> posts;
        Category category = categoryService.get(id);
        if (null == category) {
            return this.renderNotFound();
        }
        posts = postService.findPostByCategory(category, page);
        List<Post> postList = posts.getRecords();
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        User user = userService.get(category.getUserId());
        postList.forEach(post -> {
            post.setCategories(categories);
            post.setUser(user);
        });
        model.addAttribute("posts", postList);
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));

        model.addAttribute("category", category);
        model.addAttribute("prefix", "/category/" + id);

        //侧边栏
        model.addAttribute("sidebarType", SidebarTypeEnum.CATEGORY.getValue());
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
        return this.render("category");
    }
}
