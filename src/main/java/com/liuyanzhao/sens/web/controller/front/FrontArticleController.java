package com.liuyanzhao.sens.web.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.entity.*;
import com.liuyanzhao.sens.model.dto.CommentPageDTO;
import com.liuyanzhao.sens.model.dto.JsonResult;
import com.liuyanzhao.sens.model.dto.SensConst;
import com.liuyanzhao.sens.model.dto.ListPage;
import com.liuyanzhao.sens.model.enums.*;
import com.liuyanzhao.sens.service.*;
import com.liuyanzhao.sens.utils.CommentUtil;
import com.liuyanzhao.sens.utils.SensUtils;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <pre>
 *     前台文章控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2018/4/26
 */
@Slf4j
@Controller
@RequestMapping("/article")
public class FrontArticleController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 渲染文章详情
     *
     * @param postId 文章ID
     * @param model   model
     * @return 模板路径/themes/{theme}/post
     */
    @GetMapping(value = {"/{postId}.html", "{postId}"})
    public String getPost(@PathVariable Long postId,
                          @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                          Model model) {
        User loginUser = getLoginUser();
        //1、查询文章
        Post post = postService.findByPostId(postId, PostTypeEnum.POST_TYPE_POST.getValue());
        //文章不存在404
        if (null == post) {
            return this.renderNotFound();
        }
        //文章存在但是未发布只有作者可以看
        if (!post.getPostStatus().equals(PostStatusEnum.PUBLISHED.getCode())) {
            if (loginUser == null || !loginUser.getId().equals(post.getUserId())) {
                return this.renderNotFound();
            }
        }

        //标签
        List<Tag> tags = tagService.findByPostId(post.getId());
        post.setTags(tags);
        //分类
        List<Category> categories = categoryService.findByPostId(post.getId());
        post.setCategories(categories);

        //2、上一篇下一篇
        Post beforePost = postService.findPreciousPost(post.getId(), PostTypeEnum.POST_TYPE_POST.getValue());
        Post afterPost = postService.findNextPost(post.getId(), PostTypeEnum.POST_TYPE_POST.getValue());
        model.addAttribute("beforePost", beforePost);
        model.addAttribute("afterPost", afterPost);

        //3、评论列表
        CommentPageDTO commentPage = commentService.findCommentPageByPostId(postId, pageNumber);

        //4、获取文章的标签用作keywords
        List<String> tagWords = new ArrayList<>();
        if (tags != null) {
            for (Tag tag : tags) {
                tagWords.add(tag.getTagName());
            }
        }

        //5.文章访问量
        postService.updatePostView(post.getId());

        //6.相同分类的文章
        List<Post> sameCategoryPosts = postService.listSameCategoryPosts(post);
        if (sameCategoryPosts.size() > 4) {
            sameCategoryPosts = sameCategoryPosts.subList(0, 4);
        } else {
            sameCategoryPosts = sameCategoryPosts.subList(0, sameCategoryPosts.size());
        }

        //8.是否是作者
        if (loginUser != null && Objects.equals(loginUser.getId(), post.getUserId())) {
            model.addAttribute("isAuthor", Boolean.TRUE);
        }

        model.addAttribute("sameCategoryPosts", sameCategoryPosts);
        model.addAttribute("post", post);
        model.addAttribute("rainbow", commentPage.getRainbow());
        model.addAttribute("comments", commentPage.getCommentListPage());
        model.addAttribute("tagWords", CollUtil.join(tagWords, ","));

        //侧边栏
        model.addAttribute("sidebarType", SidebarTypeEnum.DETAIL.getValue());
        User user = userService.get(post.getUserId());
        if (user != null) {
            //该用户的文章数
            user.setPostCount(postService.countByUserId(user.getId()));
            //该用户的评论数
            user.setCommentCount(commentService.countByUserId(user.getId()));
            //该用户上次登录时间
            model.addAttribute("lastLogin", SensUtils.getRelativeDate(user.getLoginLast()));
            model.addAttribute("author", user);
            model.addAttribute("tagRanking", tagService.getTagRankingByUserId(user.getId(), 100));
            model.addAttribute("postRanking", postService.getPostRankingByUserIdAndPostView(user.getId(), 10));
        }
        return this.render("post");
    }

    /**
     * 点赞
     *
     * @param postId
     * @return
     */
    @PostMapping("/like")
    @ResponseBody
    public JsonResult like(@RequestParam("id") Long postId) {
        Post post = postService.get(postId);
        if (post == null) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), "文章不存在");
        }
        postService.incrPostLikes(postId);
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), "操作成功");
    }


}
