package com.liuyanzhao.sens.web.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.config.annotation.SystemLog;
import com.liuyanzhao.sens.entity.Tag;
import com.liuyanzhao.sens.model.dto.JsonResult;
import com.liuyanzhao.sens.model.enums.LogTypeEnum;
import com.liuyanzhao.sens.model.enums.ResultCodeEnum;
import com.liuyanzhao.sens.service.TagService;
import com.liuyanzhao.sens.utils.LocaleMessageUtil;
import com.liuyanzhao.sens.utils.PageUtil;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 *     后台标签管理控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2017/12/10
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/tag")
public class TagController extends BaseController {

    @Autowired
    private TagService tagService;

    @Autowired
    private LocaleMessageUtil localeMessageUtil;

    /**
     * 渲染标签管理页面
     *
     * @return 模板路径admin/admin_tag
     */
    @GetMapping
    public String tags(@RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
                       @RequestParam(value = "size", defaultValue = "50") Integer pageSize,
                       @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                       @RequestParam(value = "order", defaultValue = "desc") String order, Model model) {
        Long userId = getLoginUserId();
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);

        Page<Tag> tagPage = tagService.findByUserIdWithCount(userId, page);
        model.addAttribute("tags", tagPage.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        return "admin/admin_tag";
    }

    /**
     * 新增/修改标签
     *
     * @param tag tag
     */
    @PostMapping(value = "/save")
    @ResponseBody
    @SystemLog(description = "保存标签", type = LogTypeEnum.OPERATION)
    public JsonResult saveTag(@ModelAttribute Tag tag) {
        Long userId = getLoginUserId();
        //1.判断该标签是否为当前用户
        if (tag.getId() != null) {
            Tag checkId = tagService.get(tag.getId());
            if (checkId != null && !checkId.getUserId().equals(userId)) {
                return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.permission-denied"));
            }
        }
        //2.添加或者保存
        tag.setUserId(userId);
        tagService.insertOrUpdate(tag);
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.save-success"));

    }

    /**
     * 删除标签
     *
     * @param tagId 标签Id
     * @return JsonResult
     */
    @GetMapping(value = "/delete")
    @ResponseBody
    @SystemLog(description = "删除标签", type = LogTypeEnum.OPERATION)
    public JsonResult checkDelete(@RequestParam("id") Long tagId) {
        tagService.delete(tagId);
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.delete-success"));
    }

    /**
     * 跳转到修改标签页面
     *
     * @param model model
     * @param tagId 标签编号
     * @return 模板路径admin/admin_tag
     */
    @GetMapping(value = "/edit")
    public String toEditTag(Model model, @RequestParam("id") Long tagId) {
        Long userId = getLoginUserId();
        //当前修改的标签
        Tag tag = tagService.get(tagId);
        if (tag == null) {
            return this.renderNotFound();
        }
        model.addAttribute("updateTag", tag);

        //所有标签
        List<Tag> tags = tagService.findByUserId(userId);
        model.addAttribute("tags", tags);
        return "admin/admin_tag";
    }
}
