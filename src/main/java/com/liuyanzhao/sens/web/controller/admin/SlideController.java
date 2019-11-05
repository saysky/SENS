package com.liuyanzhao.sens.web.controller.admin;

import com.liuyanzhao.sens.config.annotation.SystemLog;
import com.liuyanzhao.sens.entity.Slide;
import com.liuyanzhao.sens.model.dto.JsonResult;
import com.liuyanzhao.sens.model.enums.LogTypeEnum;
import com.liuyanzhao.sens.model.enums.ResultCodeEnum;
import com.liuyanzhao.sens.model.enums.SlideTypeEnum;
import com.liuyanzhao.sens.service.SlideService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 *     后台幻灯片管理控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/30
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/slide")
public class SlideController {

    @Autowired
    private SlideService slideService;


    /**
     * 渲染幻灯片设置页面
     *
     * @return 模板路径/admin/admin_slide
     */
    @GetMapping
    public String slides(Model model) {
        //前台主要幻灯片
        List<Slide> slides = slideService.findBySlideType(SlideTypeEnum.INDEX_SLIDE.getCode());
        model.addAttribute("slides", slides);
        return "/admin/admin_slide";
    }

    /**
     * 新增/修改幻灯片
     *
     * @param slide slide
     * @return 重定向到/admin/slide
     */
    @PostMapping(value = "/save")
    @SystemLog(description = "保存幻灯片", type = LogTypeEnum.OPERATION)
    public String saveSlide(@ModelAttribute Slide slide) {
        slideService.insertOrUpdate(slide);
        return "redirect:/admin/slide";
    }

    /**
     * 跳转到修改页面
     *
     * @param slideId 幻灯片编号
     * @param model   model
     * @return 模板路径/admin/admin_slide
     */
    @GetMapping(value = "/edit")
    public String updateSlide(@RequestParam("id") Long slideId, Model model) {
        Slide slide = slideService.get(slideId);
        model.addAttribute("updateSlide", slide);

        List<Slide> slides = slideService.findBySlideType(SlideTypeEnum.INDEX_SLIDE.getCode());
        model.addAttribute("slides", slides);
        return "/admin/admin_slide";
    }

    /**
     * 删除幻灯片
     *
     * @param slideId 幻灯片编号
     * @return 重定向到/admin/slide
     */
    @PostMapping(value = "/delete")
    @SystemLog(description = "删除幻灯片", type = LogTypeEnum.OPERATION)
    @ResponseBody
    public JsonResult removeSlide(@RequestParam("id") Long slideId) {
        slideService.delete(slideId);
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), "删除成功");

    }

}
