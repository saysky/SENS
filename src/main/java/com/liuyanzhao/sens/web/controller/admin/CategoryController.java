package com.liuyanzhao.sens.web.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.config.annotation.SystemLog;
import com.liuyanzhao.sens.entity.Category;
import com.liuyanzhao.sens.model.dto.JsonResult;
import com.liuyanzhao.sens.model.enums.LogTypeEnum;
import com.liuyanzhao.sens.model.enums.ResultCodeEnum;
import com.liuyanzhao.sens.service.CategoryService;
import com.liuyanzhao.sens.utils.LocaleMessageUtil;
import com.liuyanzhao.sens.utils.PageUtil;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <pre>
 *     后台分类管理控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2017/12/10
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/category")
public class CategoryController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LocaleMessageUtil localeMessageUtil;

    /**
     * 查询所有分类并渲染category页面
     *
     * @return 模板路径admin/admin_category
     */
    @GetMapping
    public String categories(@RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
                             @RequestParam(value = "size", defaultValue = "50") Integer pageSize,
                             @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                             @RequestParam(value = "order", defaultValue = "desc") String order, Model model) {
        Long userId = getLoginUserId();
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);

        Page<Category> categoryPage = categoryService.findByUserIdWithCountAndLevel(userId, page);
        model.addAttribute("categories", categoryPage.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        return "admin/admin_category";
    }

    /**
     * 新增/修改分类目录
     *
     * @param category category对象
     * @return 重定向到/admin/category
     */
    @PostMapping(value = "/save")
    @ResponseBody
    @SystemLog(description = "保存分类", type = LogTypeEnum.OPERATION)
    public JsonResult saveCategory(@ModelAttribute Category category) {
        Long userId = getLoginUserId();

        //我容易嘛，就怕你们乱搞，o(╥﹏╥)o
        if(category.getId() != null) {
            //1.判断id是否为当前用户
            Category checkId = categoryService.get(category.getId());
            if (checkId != null && !Objects.equals(checkId.getUserId(), userId)) {
                return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.permission-denied"));
            }

            //2.判断pid是否为该用户
            Category checkPid = categoryService.get(category.getCatePid());
            if (checkPid != null && !Objects.equals(checkPid.getUserId(), userId)) {
                return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.permission-denied"));
            }
        }

        //3.do
        category.setUserId(userId);
        categoryService.insertOrUpdate(category);
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.save-success"));
    }

    /**
     * 删除分类
     *
     * @param cateId 分类Id
     * @return JsonResult
     */
    @GetMapping(value = "/delete")
    @ResponseBody
    @SystemLog(description = "删除分类", type = LogTypeEnum.OPERATION)
    public JsonResult checkDelete(@RequestParam("id") Long cateId) {
        //1.判断这个分类是否属于该用户
        Long userId = getLoginUserId();
        Category category = categoryService.get(cateId);
        if(!Objects.equals(category.getUserId(), userId)) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.permission-denied"));
        }
        //2.判断这个分类有没有文章
        Integer postCount = categoryService.countPostByCateId(cateId);
        if (postCount != 0) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.category.first-delete-child"));
        }
        //3.判断这个分类有没有子分类
        Integer childCount = categoryService.selectChildCateId(cateId).size();
        if (childCount != 0) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.category.first-delete-post"));
        }
        //4.do
        categoryService.delete(cateId);
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.delete-success"));
    }


    /**
     * 跳转到修改页面
     *
     * @param cateId cateId
     * @param model  model
     * @return 模板路径admin/admin_category
     */
    @GetMapping(value = "/edit")
    public String toEditCategory(Model model,
                                 @RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
                                 @RequestParam(value = "size", defaultValue = "50") Integer pageSize,
                                 @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                                 @RequestParam(value = "order", defaultValue = "desc") String order,
                                 @RequestParam("id") Long cateId) {
        Long userId = getLoginUserId();
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);

        //更新的分类
        Category category = categoryService.get(cateId);
        if (category == null) {
            return this.renderNotFound();
        }
        model.addAttribute("updateCategory", category);

        // 所有分类
        Page<Category> categoryPage = categoryService.findByUserIdWithCountAndLevel(userId, page);
        model.addAttribute("categories", categoryPage.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        return "admin/admin_category";
    }
}
