package com.liuyanzhao.sens.web.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Strings;
import com.liuyanzhao.sens.config.annotation.SystemLog;
import com.liuyanzhao.sens.entity.*;
import com.liuyanzhao.sens.exception.SensBusinessException;
import com.liuyanzhao.sens.model.dto.QueryCondition;
import com.liuyanzhao.sens.model.dto.SensConst;
import com.liuyanzhao.sens.model.dto.JsonResult;
import com.liuyanzhao.sens.model.enums.*;
import com.liuyanzhao.sens.model.vo.SearchVo;
import com.liuyanzhao.sens.service.*;
import com.liuyanzhao.sens.utils.PageUtil;
import com.liuyanzhao.sens.utils.SensUtils;
import com.liuyanzhao.sens.utils.LocaleMessageUtil;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HtmlUtil;
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
 *     后台文章管理控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2017/12/10
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/post")
public class PostController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;


    @Autowired
    private LocaleMessageUtil localeMessageUtil;

    public static final String TITLE = "title";

    public static final String CONTENT = "content";


    /**
     * 处理后台获取文章列表的请求
     *
     * @param model model
     * @return 模板路径admin/admin_post
     */
    @GetMapping
    public String posts(Model model,
                        @RequestParam(value = "status", defaultValue = "0") Integer status,
                        @RequestParam(value = "keywords", defaultValue = "") String keywords,
                        @RequestParam(value = "searchType", defaultValue = "") String searchType,
                        @RequestParam(value = "postSource", defaultValue = "-1") Integer postSource,
                        @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                        @RequestParam(value = "size", defaultValue = "15") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order,
                        @ModelAttribute SearchVo searchVo) {

        Long loginUserId = getLoginUserId();
        Post condition = new Post();
        if (!StringUtils.isBlank(keywords)) {
            if (TITLE.equals(searchType)) {
                condition.setPostTitle(keywords);
            } else {
                condition.setPostContent(keywords);
            }
        }
        condition.setPostType(PostTypeEnum.POST_TYPE_POST.getValue());
        condition.setPostStatus(status);
        condition.setUserId(loginUserId);
        condition.setPostSource(postSource);

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Post> posts = postService.findAll(
                page,
                new QueryCondition<>(condition, searchVo));

        //封装分类和标签
        model.addAttribute("posts", posts.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("publishCount", postService.countArticleByUserIdAndStatus(loginUserId, PostStatusEnum.PUBLISHED.getCode()));
        model.addAttribute("checkingCount", postService.countArticleByUserIdAndStatus(loginUserId, PostStatusEnum.CHECKING.getCode()));
        model.addAttribute("draftCount", postService.countArticleByUserIdAndStatus(loginUserId, PostStatusEnum.DRAFT.getCode()));
        model.addAttribute("trashCount", postService.countArticleByUserIdAndStatus(loginUserId, PostStatusEnum.RECYCLE.getCode()));
        model.addAttribute("status", status);
        model.addAttribute("keywords", keywords);
        model.addAttribute("searchType", searchType);
        model.addAttribute("postSource", postSource);
        model.addAttribute("order", order);
        model.addAttribute("sort", sort);
        return "admin/admin_post";
    }


    /**
     * 处理跳转到新建文章页面
     *
     * @return 模板路径admin/admin_editor
     */
    @GetMapping(value = "/new")
    public String newPost(Model model) {
        Long userId = getLoginUserId();
        //所有分类
        List<Category> allCategories = categoryService.findByUserIdWithLevel(userId);
        //所有标签
        List<Tag> allTags = tagService.findByUserId(userId);
        model.addAttribute("categories", allCategories);
        model.addAttribute("tags", allTags);
        return "admin/admin_post_editor";
    }

    /**
     * 添加/更新文章
     *
     * @param post    Post实体
     * @param cateIds 分类列表
     * @param tagList 标签列表
     */
    @PostMapping(value = "/save")
    @ResponseBody
    @SystemLog(description = "保存文章", type = LogTypeEnum.OPERATION)
    public JsonResult pushPost(@ModelAttribute Post post,
                               @RequestParam("cateList") List<Long> cateIds,
                               @RequestParam("tagList") String tagList) {

        checkCategoryAndTag(cateIds, tagList);
        User user = getLoginUser();
        Boolean isAdmin = loginUserIsAdmin();
        //1、如果开启了文章审核，非管理员文章默认状态为审核
        Boolean isOpenCheck = StringUtils.equals(SensConst.OPTIONS.get(BlogPropertiesEnum.OPEN_POST_CHECK.getProp()), TrueFalseEnum.TRUE.getValue());
        if (isOpenCheck && !isAdmin) {
            post.setPostStatus(PostStatusEnum.CHECKING.getCode());
        }
        post.setUserId(getLoginUserId());

        //2、非管理员只能修改自己的文章，管理员都可以修改
        Post originPost = null;
        if (post.getId() != null) {
            originPost = postService.get(post.getId());
            if (!Objects.equals(originPost.getUserId(), user.getId()) && !isAdmin) {
                return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.permission-denied"));
            }
            //以下属性不能修改
            post.setUserId(originPost.getUserId());
            post.setPostViews(originPost.getPostViews());
            post.setCommentSize(originPost.getCommentSize());
            post.setPostLikes(originPost.getPostLikes());
            post.setCommentSize(originPost.getCommentSize());
            post.setDelFlag(originPost.getDelFlag());
        }
        //3、提取摘要
        int postSummary = 100;
        if (StringUtils.isNotEmpty(SensConst.OPTIONS.get(BlogPropertiesEnum.POST_SUMMARY.getProp()))) {
            postSummary = Integer.parseInt(SensConst.OPTIONS.get(BlogPropertiesEnum.POST_SUMMARY.getProp()));
        }
        //文章摘要
        String summaryText = HtmlUtil.cleanHtmlTag(post.getPostContent());
        if (summaryText.length() > postSummary) {
            String summary = summaryText.substring(0, postSummary);
            post.setPostSummary(summary);
        } else {
            post.setPostSummary(summaryText);
        }

        //4、分类标签
        List<Category> categories = categoryService.cateIdsToCateList(cateIds, user.getId());
        post.setCategories(categories);
        if (StringUtils.isNotEmpty(tagList)) {
            List<Tag> tags = tagService.strListToTagList(user.getId(), StringUtils.deleteWhitespace(tagList));
            post.setTags(tags);
        }
        //当没有选择文章缩略图的时候，自动分配一张内置的缩略图
        if (StringUtils.equals(post.getPostThumbnail(), BlogPropertiesEnum.DEFAULT_THUMBNAIL.getProp())) {
            String staticUrl = SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_STATIC_URL.getProp());
            if (!Strings.isNullOrEmpty(staticUrl)) {
                post.setPostThumbnail(staticUrl + "/static/images/thumbnail/img_" + RandomUtil.randomInt(0, 14) + ".jpg");
            } else {
                post.setPostThumbnail("/static/images/thumbnail/img_" + RandomUtil.randomInt(0, 14) + ".jpg");
            }
        }
        post.setPostType(PostTypeEnum.POST_TYPE_POST.getValue());
        postService.insertOrUpdate(post);
        if (isOpenCheck && !isAdmin) {
            return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), "文章已提交审核");
        }
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.operation-success"));
    }

    /**
     * 限制一篇文章最多5个分类
     *
     * @param cateIdList
     * @param tagList
     */
    private void checkCategoryAndTag(List<Long> cateIdList, String tagList) {
        if (cateIdList.size() > 5) {
            throw new SensBusinessException("每篇文章最多5个分类");
        }
        String[] tags = tagList.split(",");
        if (tags.length > 5) {
            throw new SensBusinessException("每篇文章最多5个标签");
        }
        for (String tag : tags) {
            if (tag.length() > 20) {
                throw new SensBusinessException("每个标签长度最多为20个字符");
            }
        }
    }

    /**
     * 处理移至回收站的请求
     *
     * @param postId 文章编号
     * @return 重定向到/admin/post
     */
    @PostMapping(value = "/throw")
    @ResponseBody
    @SystemLog(description = "将文章移到回收站", type = LogTypeEnum.OPERATION)
    public JsonResult moveToTrash(@RequestParam("id") Long postId) {
        Post post = postService.get(postId);
        basicCheck(post);
        postService.updatePostStatus(postId, PostStatusEnum.RECYCLE.getCode());
        log.info("编号为" + postId + "的文章已被移到回收站");
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.operation-success"));
    }

    /**
     * 处理文章为发布的状态
     *
     * @param postId 文章编号
     * @return 重定向到/admin/post
     */
    @PostMapping(value = "/revert")
    @ResponseBody
    @SystemLog(description = "将文章改为已发布", type = LogTypeEnum.OPERATION)
    public JsonResult moveToPublish(@RequestParam("id") Long postId) {
        Post post = postService.get(postId);
        basicCheck(post);
        postService.updatePostStatus(postId, PostStatusEnum.PUBLISHED.getCode());
        log.info("编号为" + postId + "的文章已改变为发布状态");
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.operation-success"));
    }

    /**
     * 审核通过文章
     *
     * @param postId 文章编号
     * @return 重定向到/admin/post
     */
    @PostMapping(value = "/pass")
    @ResponseBody
    @SystemLog(description = "审核通过文章", type = LogTypeEnum.OPERATION)
    public JsonResult passCheck(@RequestParam("id") Long postId) {
        Post post = postService.get(postId);
        basicCheck(post);
        postService.updatePostStatus(postId, PostStatusEnum.PUBLISHED.getCode());
        log.info("编号为" + postId + "的文章已通过审核");
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.operation-success"));
    }


    /**
     * 处理删除文章的请求
     *
     * @param postId 文章编号
     * @return 重定向到/admin/post
     */
    @PostMapping(value = "/delete")
    @ResponseBody
    @SystemLog(description = "删除文章", type = LogTypeEnum.OPERATION)
    public JsonResult removePost(@RequestParam("id") Long postId) {
        Post post = postService.get(postId);
        basicCheck(post);
        postService.delete(postId);
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.delete-success"));
    }

    /**
     * 批量删除
     *
     * @param ids 文章ID列表
     * @return 重定向到/admin/post
     */
    @DeleteMapping(value = "/batchDelete")
    @ResponseBody
    @SystemLog(description = "批量删除文章", type = LogTypeEnum.OPERATION)
    public JsonResult batchDelete(@RequestParam("ids") List<Long> ids) {
        Long userId = getLoginUserId();
        //批量操作
        //1、防止恶意操作
        if (ids == null || ids.size() == 0 || ids.size() >= 100) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), "参数不合法!");
        }
        //2、检查用户权限
        //文章作者才可以删除
        List<Post> postList = postService.findByBatchIds(ids);
        for (Post post : postList) {
            if (!Objects.equals(post.getUserId(), userId)) {
                return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.permission-denied"));
            }
        }
        //3、如果当前状态为回收站，则删除；否则，移到回收站
        for (Post post : postList) {
            if (Objects.equals(post.getPostStatus(), PostStatusEnum.RECYCLE.getCode())) {
                postService.delete(post.getId());
            } else {
                post.setPostStatus(PostStatusEnum.RECYCLE.getCode());
                postService.update(post);
            }
        }
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.delete-success"));
    }


    /**
     * 检查文章是否存在和用户是否有权限控制
     *
     * @param post
     */
    private void basicCheck(Post post) {
        if (post == null) {
            throw new SensBusinessException(localeMessageUtil.getMessage("code.admin.common.post-not-exist"));
        }
        //只有创建者有权删除
        User user = getLoginUser();
        //管理员和文章作者可以删除
        Boolean isAdmin = loginUserIsAdmin();
        if (!Objects.equals(post.getUserId(), user.getId()) && !isAdmin) {
            throw new SensBusinessException(localeMessageUtil.getMessage("code.admin.common.permission-denied"));
        }
    }

    /**
     * 跳转到编辑文章页面
     *
     * @param postId 文章编号
     * @param model  model
     * @return 模板路径admin/admin_editor
     */
    @GetMapping(value = "/edit")
    public String editPost(@RequestParam("id") Long postId, Model model) {
        Long userId = getLoginUserId();
        Post post = postService.get(postId);
        basicCheck(post);
        //当前文章标签
        List<Tag> tags = tagService.findByPostId(postId);
        post.setTags(tags);
        //当前文章分类
        List<Category> categories = categoryService.findByPostId(postId);
        post.setCategories(categories);

        //所有分类
        List<Category> allCategories = categoryService.findByUserIdWithLevel(userId);
        //所有标签
        List<Tag> allTags = tagService.findByUserId(userId);

        model.addAttribute("post", post);
        model.addAttribute("categories", allCategories);
        model.addAttribute("tags", allTags);
        return "admin/admin_post_editor";
    }


    /**
     * 待审核文章列表
     *
     * @param model model
     * @return 模板路径admin/admin_post
     */
    @GetMapping("/check")
    public String postCheckList(Model model,
                                @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                                @RequestParam(value = "size", defaultValue = "15") Integer pageSize,
                                @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                                @RequestParam(value = "order", defaultValue = "desc") String order,
                                @ModelAttribute SearchVo searchVo) {
        Post condition = new Post();
        condition.setPostType(PostTypeEnum.POST_TYPE_POST.getValue());
        condition.setPostStatus(PostStatusEnum.CHECKING.getCode());
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Post> posts = postService.findAll(
                page,
                new QueryCondition<>(condition, searchVo));
        List<Post> postList = posts.getRecords();
        postList.forEach(post -> userService.get(post.getUserId()));
        model.addAttribute("posts", postList);
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("order", order);
        model.addAttribute("sort", sort);
        return "admin/admin_post_check";
    }

    /**
     * 验证文章路径是否已经存在
     *
     * @param postUrl 文章路径
     * @return JsonResult
     */
    @GetMapping(value = "/checkUrl")
    @ResponseBody
    public JsonResult checkUrlExists(@RequestParam("postUrl") String postUrl) {
        postUrl = urlFilter(postUrl);
        Post post = postService.findByPostUrl(postUrl, PostTypeEnum.POST_TYPE_POST.getValue());
        if (null != post) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.url-is-exists"));
        }
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), "");
    }


    /**
     * 更新所有摘要
     *
     * @param postSummary 文章摘要字数
     * @return JsonResult
     */
    @PostMapping(value = "/resetAllSummary")
    @ResponseBody
    @SystemLog(description = "更新所有摘要", type = LogTypeEnum.OPERATION)
    public JsonResult resetAllSummary(@RequestParam("postSummary") Integer postSummary) {
        postService.updateAllSummary(postSummary);
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.update-success"));
    }

    /**
     * 将所有文章推送到百度
     *
     * @param baiduToken baiduToken
     * @return JsonResult
     */
    @PostMapping(value = "/saveAllToBaidu")
    @ResponseBody
    @SystemLog(description = "将所有文章推送到百度", type = LogTypeEnum.OPERATION)
    public JsonResult pushAllToBaidu(@RequestParam("baiduToken") String baiduToken) {
        if (StringUtils.isEmpty(baiduToken)) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.post.no-baidu-token"));
        }
        String blogUrl = SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_URL.getProp());
        List<Post> posts = postService.findAllPosts(PostTypeEnum.POST_TYPE_POST.getValue());
        StringBuilder urls = new StringBuilder();
        for (Post post : posts) {
            urls.append(blogUrl);
            urls.append("/article/");
            urls.append(post.getPostUrl());
            urls.append("\n");
        }
        SensUtils.baiduPost(blogUrl, baiduToken, urls.toString());
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.post.push-to-baidu-success"));
    }


    /**
     * 去除html，htm后缀，以及将空格替换成-
     *
     * @param url url
     * @return String
     */
    private static String urlFilter(String url) {
        if (null != url) {
            final boolean urlEndsWithHtmlPostFix = url.endsWith(".html") || url.endsWith(".htm");
            if (urlEndsWithHtmlPostFix) {
                return url.substring(0, url.lastIndexOf("."));
            }
        }
        return StringUtils.replaceAll(url, " ", "-");
    }
}
