package com.liuyanzhao.sens.web.controller.admin;

import com.liuyanzhao.sens.entity.Role;
import com.liuyanzhao.sens.model.dto.SensConst;
import com.liuyanzhao.sens.model.dto.JsonResult;
import com.liuyanzhao.sens.model.enums.ResultCodeEnum;
import com.liuyanzhao.sens.service.OptionsService;
import com.liuyanzhao.sens.service.RoleService;
import com.liuyanzhao.sens.utils.LocaleMessageUtil;
import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     后台设置选项控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2017/12/13
 */
@Slf4j
@Controller
@RequestMapping("/admin/option")
public class OptionController {

    @Autowired
    private OptionsService optionsService;

    @Autowired
    private Configuration configuration;

    @Autowired
    private LocaleMessageUtil localeMessageUtil;

    @Autowired
    private RoleService roleService;

    /**
     * 请求跳转到option页面并完成渲染
     *
     * @return 模板路径admin/admin_option
     */
    @GetMapping
    public String options(Model model) {
        List<Role> roles = roleService.findAll();
        model.addAttribute("roles", roles);
        return "admin/admin_option";
    }

    /**
     * 保存设置选项
     *
     * @param options options
     * @return JsonResult
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public JsonResult saveOptions(@RequestParam Map<String, String> options) throws TemplateModelException {
        optionsService.saveOptions(options);
        //刷新options
        configuration.setSharedVariable("options", optionsService.findAllOptions());
        SensConst.OPTIONS.clear();
        SensConst.OPTIONS = optionsService.findAllOptions();
        log.info("所保存的设置选项列表：" + options);
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.save-success"));
    }
}
