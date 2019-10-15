package com.liuyanzhao.sens.web.controller.front;

import cn.hutool.http.HtmlUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.entity.*;
import com.liuyanzhao.sens.model.dto.QueryCondition;
import com.liuyanzhao.sens.model.dto.SensConst;
import com.liuyanzhao.sens.model.enums.*;
import com.liuyanzhao.sens.service.*;
import com.liuyanzhao.sens.utils.PageUtil;
import com.liuyanzhao.sens.utils.SensUtils;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <pre>
 *     前台首页控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2018/4/26
 */
@Slf4j
@Controller
public class FrontIndexController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TagService tagService;

    /**
     * 请求首页
     *
     * @param model model
     * @return 模板路径
     */
    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(value = "size", defaultValue = "15") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order) {
        //调用方法渲染首页
        return this.index(model, 1, pageSize, sort, order);
    }


    /**
     * 首页分页
     *
     * @param model model
     * @return 模板路径/themes/{theme}/index
     */
    @GetMapping(value = "/page/{pageNumber}")
    public String index(Model model,
                        @PathVariable(value = "pageNumber") Integer pageNumber,
                        @RequestParam(value = "size", defaultValue = "15") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order) {
        //文章分页排序
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        //1.文章列表
        Post condition = new Post();
        condition.setPostType(PostTypeEnum.POST_TYPE_POST.getValue());
        condition.setPostStatus(PostStatusEnum.PUBLISHED.getCode());
        Page<Post> posts = postService.findAll(page, new QueryCondition<>(condition));
        List<Post> postList = posts.getRecords();
        postList.forEach(post -> post.setCategories(categoryService.findByPostId(post.getId())));
        model.addAttribute("posts", postList);
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));

        model.addAttribute("prefix", "");

        //2.首页的公告列表
        List<Post> notices = postService.findByPostTypeAndStatus(PostTypeEnum.POST_TYPE_NOTICE.getValue(), PostStatusEnum.PUBLISHED.getCode());
        model.addAttribute("notices", notices);

        //3.侧边栏
        model.addAttribute("sidebarType", SidebarTypeEnum.INDEX.getValue());
        //统计
        model.addAttribute("allCount", postService.getAllCount());
        //文章排名
        model.addAttribute("postRanking", postService.getPostRankingByPostView(10));
        return this.render("index");
    }


    /**
     * 文章/页面 入口
     * 兼容老版本
     *
     * @param postUrl 文章路径名
     * @return 模板路径/themes/{theme}/post
     */
    @GetMapping(value = {"/{postUrl}.html", "post/{postUrl}"})
    public String getPost(@PathVariable String postUrl) {
        User loginUser = getLoginUser();
        Boolean isNumeric = StringUtils.isNumeric(postUrl);
        Post post;
        if (isNumeric) {
            post = postService.get(Long.valueOf(postUrl));
            if (post == null) {
                post = postService.findByPostUrl(postUrl);
            }
        } else {
            post = postService.findByPostUrl(postUrl, PostTypeEnum.POST_TYPE_POST.getValue());
        }

        // 文章不存在404
        if (null == post) {
            return this.renderNotFound();
        }
        // 文章存在但是未发布只有作者可以看
        if (!post.getPostStatus().equals(PostStatusEnum.PUBLISHED.getCode())) {
            if (loginUser == null || !loginUser.getId().equals(post.getUserId())) {
                return this.renderNotFound();
            }
        }

        // 如果是页面
        if (Objects.equals(post.getPostType(), PostTypeEnum.POST_TYPE_PAGE.getValue())) {
            return "redirect:/p/" + post.getPostUrl();
        }
        // 如果是公告
        else if (Objects.equals(post.getPostType(), PostTypeEnum.POST_TYPE_NOTICE.getValue())) {
            return "redirect:/notice/" + post.getPostUrl();
        }
        return "forward:/article/" + post.getId();
    }


    /**
     * 搜索文章
     *
     * @param keyword keyword
     * @param model   model
     * @return 模板路径/themes/{theme}/index
     */
    @GetMapping(value = "/search")
    public String search(
            @RequestParam(value = "size", defaultValue = "15") Integer pageSize,
            @RequestParam(value = "sort", defaultValue = "createTime") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestParam("keyword") String keyword,
            Model model) {
        return this.searchPage(model, 1, pageSize, sort, order, HtmlUtil.escape(keyword));
    }

    /**
     * 搜索
     *
     * @param model model
     * @return 模板路径/themes/{theme}/index
     */
    @GetMapping(value = "/search/page/{pageNumber}")
    public String searchPage(Model model,
                             @PathVariable(value = "pageNumber") Integer pageNumber,
                             @RequestParam(value = "size", defaultValue = "15") Integer pageSize,
                             @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                             @RequestParam(value = "order", defaultValue = "desc") String order,
                             @RequestParam("keyword") String keyword) {
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Post> posts = null;
        Long startTime = System.currentTimeMillis();
        //如果开启了ES
        if (StringUtils.equals(SensConst.OPTIONS.get(BlogPropertiesEnum.OPEN_ELASTICSEARCH.getProp()), TrueFalseEnum.TRUE.getValue())) {
            Map<String, Object> map = new HashMap<>(2);
            map.put("postStatus", PostStatusEnum.PUBLISHED.getCode());
            map.put("postTitle", keyword);
            posts = postService.findPostsByEs(map, page);
        } else {
            Post condition = new Post();
            condition.setPostTitle(keyword);
            condition.setPostContent(keyword);
            condition.setPostStatus(PostStatusEnum.PUBLISHED.getCode());
            posts = postService.findAll(page, new QueryCondition(condition));
        }
        model.addAttribute("time", (System.currentTimeMillis() - startTime) + "ms");
        List<Post> postList = posts.getRecords();
        postList.forEach(post -> post.setCategories(categoryService.findByPostId(post.getId())));
        model.addAttribute("prefix", "/search");
        model.addAttribute("suffix", "?keyword=" + keyword);
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("posts", postList);
        return this.render("search");
    }

    /**
     * 搜索文章
     *
     * @param userName userName
     * @param model    model
     * @return 模板路径/themes/{theme}/index
     */
    @GetMapping(value = "/author/{userName}")
    public String postsByUserName(Model model,
                                  @PathVariable(value = "userName") String userName,
                                  @RequestParam(value = "size", defaultValue = "15") Integer pageSize,
                                  @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                                  @RequestParam(value = "order", defaultValue = "desc") String order) {

        return this.postsByUserName(model, HtmlUtil.escape(userName), 1, pageSize, sort, order);
    }

    /**
     * 首页分页
     *
     * @param model model
     * @return 模板路径/themes/{theme}/index
     */
    @GetMapping(value = "/author/{userName}/page/{pageNumber}")
    public String postsByUserName(Model model,
                                  @PathVariable(value = "userName") String userName,
                                  @PathVariable(value = "pageNumber") Integer pageNumber,
                                  @RequestParam(value = "size", defaultValue = "15") Integer pageSize,
                                  @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                                  @RequestParam(value = "order", defaultValue = "desc") String order) {

        User user = userService.findByUserName(userName);
        if (user == null) {
            return renderNotFound();
        }
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);

        Post condition = new Post();
        condition.setUserId(user.getId());
        Page<Post> posts = postService.findAll(page, new QueryCondition<>(condition));
        List<Post> postList = posts.getRecords();
        postList.forEach(post -> post.setCategories(categoryService.findByPostId(post.getId())));

        //该用户的文章数
        Integer postCount = postService.countByUserId(user.getId());
        user.setPostCount(postCount);
        //该用户的评论数
        Integer commentCount = commentService.countByUserId(user.getId());
        user.setCommentCount(commentCount);

        model.addAttribute("prefix", "/author/" + userName);
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("posts", postList);

        //侧边栏
        model.addAttribute("sidebarType", SidebarTypeEnum.AUTHOR.getValue());
        model.addAttribute("author", user);
        model.addAttribute("lastLogin", SensUtils.getRelativeDate(user.getLoginLast()));
        model.addAttribute("tagRanking", tagService.getTagRankingByUserId(user.getId(), 100));
        return this.render("author");
    }


}
