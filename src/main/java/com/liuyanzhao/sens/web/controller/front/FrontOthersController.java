package com.liuyanzhao.sens.web.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.entity.Post;
import com.liuyanzhao.sens.model.dto.SensConst;
import com.liuyanzhao.sens.model.enums.BlogPropertiesEnum;
import com.liuyanzhao.sens.model.enums.PostStatusEnum;
import com.liuyanzhao.sens.model.enums.PostTypeEnum;
import com.liuyanzhao.sens.service.PostService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <pre>
 *     sitemap，rss页面控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2018/4/26
 */
@Controller
public class FrontOthersController {

    @Autowired
    private PostService postService;

    /**
     * 获取文章rss
     *
     * @return rss
     */
    @GetMapping(value = {"feed", "feed.xml", "atom", "atom.xml"}, produces = "application/xml;charset=UTF-8")
    @ResponseBody
    public String feed() {
        String rssPosts = SensConst.OPTIONS.get(BlogPropertiesEnum.RSS_POSTS.getProp());
        if (StringUtils.isBlank(rssPosts)) {
            rssPosts = "20";
        }
        //获取文章列表并根据时间排序
        Page pageable = new Page(0, Integer.parseInt(rssPosts));
        Page<Post> postsPage = postService.pagingByPostTypeAndStatus(PostTypeEnum.POST_TYPE_POST.getValue(), PostStatusEnum.PUBLISHED.getCode(), pageable);
        List<Post> posts = postsPage.getRecords();
        return postService.buildRss(posts);
    }

    /**
     * 获取sitemap
     *
     * @return sitemap
     */
    @GetMapping(value = {"sitemap", "sitemap.xml"}, produces = "application/xml;charset=UTF-8")
    @ResponseBody
    public String siteMap() {
        //获取文章列表并根据时间排序
        return postService.buildArticleSiteMap();
    }
}
