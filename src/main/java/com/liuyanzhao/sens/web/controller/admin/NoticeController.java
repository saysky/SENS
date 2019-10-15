package com.liuyanzhao.sens.web.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.config.annotation.SystemLog;
import com.liuyanzhao.sens.entity.*;
import com.liuyanzhao.sens.model.dto.JsonResult;
import com.liuyanzhao.sens.model.dto.QueryCondition;
import com.liuyanzhao.sens.model.enums.*;
import com.liuyanzhao.sens.model.vo.SearchVo;
import com.liuyanzhao.sens.service.PostService;
import com.liuyanzhao.sens.utils.LocaleMessageUtil;
import com.liuyanzhao.sens.utils.PageUtil;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * <pre>
 *     后台公告管理控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2017/12/10
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/notice")
public class NoticeController extends BaseController {


    @Autowired
    private PostService postService;

    @Autowired
    LocaleMessageUtil localeMessageUtil;

    /**
     * 处理后台获取公告列表的请求
     *
     * @param model model
     * @return 模板路径admin/admin_post
     */
    @GetMapping
    public String posts(Model model,
                        @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                        @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order,
                        @RequestParam(value = "status", defaultValue = "0") Integer status,
                        @ModelAttribute SearchVo searchVo) {
        Post condition = new Post();
        condition.setPostStatus(status);
        condition.setPostType(PostTypeEnum.POST_TYPE_NOTICE.getValue());
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Post> postPage = postService.findAll(
                page,
                new QueryCondition<>(condition, searchVo)
        );
        model.addAttribute("posts", postPage.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("publishCount", postService.countByPostTypeAndStatus(PostTypeEnum.POST_TYPE_NOTICE.getValue(), PostStatusEnum.PUBLISHED.getCode()));
        model.addAttribute("draftCount", postService.countByPostTypeAndStatus(PostTypeEnum.POST_TYPE_NOTICE.getValue(), PostStatusEnum.DRAFT.getCode()));
        model.addAttribute("trashCount", postService.countByPostTypeAndStatus(PostTypeEnum.POST_TYPE_NOTICE.getValue(), PostStatusEnum.RECYCLE.getCode()));
        model.addAttribute("status", status);
        return "admin/admin_notice";
    }

    /**
     * 跳转到新建公告
     *
     * @return 模板路径admin/admin_notice_editor
     */
    @GetMapping(value = "/new")
    public String newPage() {
        return "admin/admin_notice_editor";
    }

    /**
     * 发表公告
     *
     * @param post post
     */
    @PostMapping(value = "/save")
    @ResponseBody
    @SystemLog(description = "保存公告", type = LogTypeEnum.OPERATION)
    public JsonResult pushPage(@ModelAttribute Post post) {

        String msg = localeMessageUtil.getMessage("code.admin.common.save-success");
        //发表用户
        User loginUser = getLoginUser();
        post.setUserId(loginUser.getId());
        post.setPostType(PostTypeEnum.POST_TYPE_NOTICE.getValue());
        if (null != post.getId()) {
            post.setPostViews(postService.get(post.getId()).getPostViews());
            msg = localeMessageUtil.getMessage("code.admin.common.update-success");
        }
        postService.insertOrUpdate(post);
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), msg);
    }

    /**
     * 跳转到修改公告
     *
     * @param postId 公告编号
     * @param model  model
     * @return admin/admin_page_editor
     */
    @GetMapping(value = "/edit")
    public String editPage(@RequestParam("id") Long postId, Model model) {
        Post post = postService.get(postId);
        model.addAttribute("post", post);
        return "admin/admin_notice_editor";
    }

    /**
     * 处理移至回收站的请求
     *
     * @param postId 公告编号
     * @return 重定向到/admin/post
     */
    @PostMapping(value = "/throw")
    @ResponseBody
    @SystemLog(description = "将公告移到回收站", type = LogTypeEnum.OPERATION)
    public JsonResult moveToTrash(@RequestParam("id") Long postId) {
        try {
            Post post = postService.get(postId);
            if (post == null) {
                return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.post-not-exist"));
            }
            postService.updatePostStatus(postId, PostStatusEnum.RECYCLE.getCode());
        } catch (Exception e) {
            log.error("删除公告到回收站失败：{}", e.getMessage());
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.operation-failed"));
        }
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.operation-success"));
    }

    /**
     * 处理公告为发布的状态
     *
     * @param postId 公告编号
     * @return 重定向到/admin/post
     */
    @PostMapping(value = "/revert")
    @ResponseBody
    @SystemLog(description = "将公告改为发布的状态", type = LogTypeEnum.OPERATION)
    public JsonResult moveToPublish(@RequestParam("id") Long postId) {
        try {
            Post post = postService.get(postId);
            if (post == null) {
                return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.post-not-exist"));
            }

            postService.updatePostStatus(postId, PostStatusEnum.PUBLISHED.getCode());
        } catch (Exception e) {
            log.error("发布公告失败：{}", e.getMessage());
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.operation-failed"));
        }
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.operation-success"));
    }

    /**
     * 处理删除公告的请求
     *
     * @param postId 公告编号
     * @return 重定向到/admin/post
     */
    @PostMapping(value = "/delete")
    @ResponseBody
    @SystemLog(description = "删除公告", type = LogTypeEnum.OPERATION)
    public JsonResult removePost(@RequestParam("id") Long postId) {
        Post post = postService.get(postId);
        if (post == null) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.post-not-exist"));
        }
        postService.delete(postId);
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.delete-success"));
    }


}
