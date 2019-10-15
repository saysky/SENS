package com.liuyanzhao.sens.web.controller.admin;

import com.liuyanzhao.sens.config.annotation.SystemLog;
import com.liuyanzhao.sens.entity.*;
import com.liuyanzhao.sens.model.dto.JsonResult;
import com.liuyanzhao.sens.model.enums.BlogPropertiesEnum;
import com.liuyanzhao.sens.model.enums.LogTypeEnum;
import com.liuyanzhao.sens.model.enums.PostTypeEnum;
import com.liuyanzhao.sens.model.enums.ResultCodeEnum;
import com.liuyanzhao.sens.service.LinkService;
import com.liuyanzhao.sens.service.LogService;
import com.liuyanzhao.sens.service.PostService;
import com.liuyanzhao.sens.utils.LocaleMessageUtil;
import cn.hutool.core.util.RandomUtil;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <pre>
 *     后台页面管理控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2017/12/10
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/page")
public class PageController extends BaseController {

    @Autowired
    private LinkService linkService;

    @Autowired
    private PostService postService;

    @Autowired
    private LogService logService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    LocaleMessageUtil localeMessageUtil;

    /**
     * 页面管理页面
     *
     * @param model model
     * @return 模板路径admin/admin_page
     */
    @GetMapping
    public String pages(Model model) {
        List<Post> posts = postService.findAllPosts(PostTypeEnum.POST_TYPE_PAGE.getValue());
        model.addAttribute("pages", posts);
        return "admin/admin_page";
    }

    /**
     * 获取友情链接列表并渲染页面
     *
     * @return 模板路径admin/admin_page_link
     */
    @GetMapping(value = "/links")
    public String links() {
        return "admin/admin_page_link";
    }

    /**
     * 跳转到修改页面
     *
     * @param model  model
     * @param linkId linkId 友情链接编号
     * @return String 模板路径admin/admin_page_link
     */
    @GetMapping(value = "/links/edit")
    public String toEditLink(Model model, @RequestParam("id") Long linkId) {
        Link link = linkService.get(linkId);
        model.addAttribute("updateLink", link);
        return "admin/admin_page_link";
    }

    /**
     * 处理添加/修改友链的请求并渲染页面
     *
     * @param link Link实体
     * @return 重定向到/admin/page/links
     */
    @PostMapping(value = "/links/save")
    @SystemLog(description = "保存友情链接", type = LogTypeEnum.OPERATION)
    public String saveLink(@ModelAttribute Link link) {
        linkService.insert(link);
        return "redirect:/admin/page/links";
    }

    /**
     * 处理删除友情链接的请求并重定向
     *
     * @param linkId 友情链接编号
     * @return 重定向到/admin/page/links
     */
    @GetMapping(value = "/links/delete")
    public String removeLink(@RequestParam("id") Long linkId) {
        linkService.delete(linkId);
        return "redirect:/admin/page/links";
    }



    /**
     * 跳转到新建页面
     *
     * @return 模板路径admin/admin_page_editor
     */
    @GetMapping(value = "/new")
    public String newPage() {
        return "admin/admin_page_editor";
    }

    /**
     * 发表页面
     *
     * @param post post
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public JsonResult pushPage(@ModelAttribute Post post) {
        String msg = localeMessageUtil.getMessage("code.admin.common.save-success");
        //发表用户
        User loginUser = getLoginUser();
        post.setUserId(loginUser.getId());
        post.setPostType(PostTypeEnum.POST_TYPE_PAGE.getValue());
        //当没有选择文章缩略图的时候，自动分配一张内置的缩略图
        if (StringUtils.equals(post.getPostThumbnail(), BlogPropertiesEnum.DEFAULT_THUMBNAIL.getProp())) {
            post.setPostThumbnail("/static/images/thumbnail/img_" + RandomUtil.randomInt(0, 14) + ".jpg");
        }
        postService.insertOrUpdate(post);
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), msg);
    }


    /**
     * 跳转到修改页面
     *
     * @param pageId 页面编号
     * @param model  model
     * @return admin/admin_page_editor
     */
    @GetMapping(value = "/edit")
    public String editPage(@RequestParam("id") Long pageId, Model model) {
        Post post = postService.get(pageId);
        model.addAttribute("post", post);
        return "admin/admin_page_editor";
    }

    /**
     * 检查该路径是否已经存在
     *
     * @param postUrl postUrl
     * @return JsonResult
     */
    @GetMapping(value = "/checkUrl")
    @ResponseBody
    public JsonResult checkUrlExists(@RequestParam("postUrl") String postUrl) {
        Post post = postService.findByPostUrl(postUrl, PostTypeEnum.POST_TYPE_PAGE.getValue());
        if (null != post) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.url-is-exists"));
        }
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), "");
    }
}
