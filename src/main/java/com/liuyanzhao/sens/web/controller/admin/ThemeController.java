package com.liuyanzhao.sens.web.controller.admin;

import com.liuyanzhao.sens.config.annotation.SystemLog;
import com.liuyanzhao.sens.model.enums.TrueFalseEnum;
import com.liuyanzhao.sens.service.OptionsService;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * <pre>
 *     后台主题管理控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2017/12/16
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/theme")
public class ThemeController extends BaseController {

    @Autowired
    private OptionsService optionsService;

    @Autowired
    private Configuration configuration;


    /**
     * 跳转到主题设置
     *
     * @param theme theme名称
     */
    @GetMapping(value = "/options")
    public String setting(Model model,
                          @RequestParam("theme") String theme,
                          @RequestParam("hasUpdate") String hasUpdate) {
        model.addAttribute("themeDir", theme);
        if (StringUtils.equals(hasUpdate, TrueFalseEnum.TRUE.getValue())) {
            model.addAttribute("hasUpdate", true);
        } else {
            model.addAttribute("hasUpdate", false);
        }
        return "themes/" + theme + "/module/options";
    }


}
