package com.liuyanzhao.sens.web.controller.admin;

import com.liuyanzhao.sens.config.annotation.SystemLog;
import com.liuyanzhao.sens.entity.Menu;
import com.liuyanzhao.sens.model.dto.JsonResult;
import com.liuyanzhao.sens.model.enums.LogTypeEnum;
import com.liuyanzhao.sens.model.enums.MenuTypeEnum;
import com.liuyanzhao.sens.model.enums.ResultCodeEnum;
import com.liuyanzhao.sens.service.MenuService;
import com.liuyanzhao.sens.utils.LocaleMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *     后台菜单管理控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/30
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    LocaleMessageUtil localeMessageUtil;

    /**
     * 渲染菜单设置页面
     *
     * @return 模板路径/admin/admin_menu
     */
    @GetMapping
    public String menus(Model model) {
        //前台主要菜单
        List<Menu> frontMainMenus = menuService.findMenuListWithLevel(MenuTypeEnum.FRONT_MAIN_MENU.getCode());
        //前台顶部菜单
        List<Menu> frontTopMenus = menuService.findMenuListWithLevel(MenuTypeEnum.FRONT_TOP_MENU.getCode());
        model.addAttribute("frontMainMenus", frontMainMenus);
        model.addAttribute("frontTopMenus", frontTopMenus);
        return "/admin/admin_menu";
    }

    /**
     * 新增/修改菜单
     *
     * @param menu menu
     * @return 重定向到/admin/menu
     */
    @PostMapping(value = "/save")
    @SystemLog(description = "保存菜单", type = LogTypeEnum.OPERATION)
    public String saveMenu(@ModelAttribute Menu menu) {
        menuService.insertOrUpdate(menu);
        return "redirect:/admin/menu";
    }

    /**
     * 跳转到修改页面
     *
     * @param id    菜单编号
     * @param model model
     * @return 模板路径/admin/admin_menu
     */
    @GetMapping(value = "/edit")
    public String updateMenu(@RequestParam("id") Long id, Model model) {
        Menu menu = menuService.get(id);
        model.addAttribute("updateMenu", menu);

        //前台主要菜单
        List<Menu> frontMainMenus = menuService.findMenuListWithLevel(MenuTypeEnum.FRONT_MAIN_MENU.getCode());
        //前台顶部菜单
        List<Menu> frontTopMenus = menuService.findMenuListWithLevel(MenuTypeEnum.FRONT_TOP_MENU.getCode());
        model.addAttribute("frontMainMenus", frontMainMenus);
        model.addAttribute("frontTopMenus", frontTopMenus);
        return "/admin/admin_menu";
    }

    /**
     * 删除菜单
     *
     * @param id 菜单编号
     * @return 重定向到/admin/menu
     */
    @GetMapping(value = "/delete")
    @SystemLog(description = "删除菜单", type = LogTypeEnum.OPERATION)
    public String removeMenu(@RequestParam("id") Long id) {
        //1.先查看该菜单是否有子节点，如果有不能删除
        List<Menu> childMenus = menuService.findByMenuPid(id);
        if (childMenus == null || childMenus.size() == 0) {
            menuService.delete(id);
        } else {
            String msg = localeMessageUtil.getMessage("code.admin.common.must-delete-parent-node");
            return "redirect:/admin/menu?error=" + msg;
        }
        return "redirect:/admin/menu";
    }

}
