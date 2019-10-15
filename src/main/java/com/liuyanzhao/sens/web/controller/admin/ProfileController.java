package com.liuyanzhao.sens.web.controller.admin;

import com.liuyanzhao.sens.config.annotation.SystemLog;
import com.liuyanzhao.sens.entity.Role;
import com.liuyanzhao.sens.entity.ThirdAppBind;
import com.liuyanzhao.sens.entity.User;
import com.liuyanzhao.sens.model.dto.JsonResult;
import com.liuyanzhao.sens.model.enums.LogTypeEnum;
import com.liuyanzhao.sens.model.enums.ResultCodeEnum;
import com.liuyanzhao.sens.service.RoleService;
import com.liuyanzhao.sens.service.ThirdAppBindService;
import com.liuyanzhao.sens.service.UserService;
import com.liuyanzhao.sens.utils.LocaleMessageUtil;
import com.liuyanzhao.sens.utils.Md5Util;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *     后台用户管理控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2017/12/24
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/user")
public class ProfileController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ThirdAppBindService thirdAppBindService;


    @Autowired
    private LocaleMessageUtil localeMessageUtil;

    /**
     * 获取用户信息并跳转
     *
     * @return 模板路径admin/admin_profile
     */
    @GetMapping("/profile")
    public String profile(Model model) {
        //1.用户信息
        User user = getLoginUser();
        model.addAttribute("user", user);

        //2.第三方信息
        List<ThirdAppBind> thirdAppBinds = thirdAppBindService.findByUserId(user.getId());
        model.addAttribute("thirdAppBinds", thirdAppBinds);

        //3.角色列表
        List<Role> roles = roleService.listRolesByUserId(user.getId());
        model.addAttribute("roles", roles);
        return "admin/admin_profile";
    }


    /**
     * 处理修改用户资料的请求
     *
     * @param user user
     * @return JsonResult
     */
    @PostMapping(value = "/profile/save")
    @ResponseBody
    @SystemLog(description = "修改个人资料", type = LogTypeEnum.OPERATION)
    public JsonResult saveProfile(@ModelAttribute User user) {
        User loginUser = getLoginUser();

        User saveUser = new User();
        saveUser.setId(loginUser.getId());
        saveUser.setUserName(user.getUserName());
        saveUser.setUserDisplayName(user.getUserDisplayName());
        saveUser.setUserSite(user.getUserSite());
        saveUser.setUserAvatar(user.getUserAvatar());
        saveUser.setUserDesc(user.getUserDesc());
        saveUser.setUserEmail(user.getUserEmail());
        userService.insertOrUpdate(saveUser);
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.edit-success-login-again"));
    }


    /**
     * 处理修改密码的请求
     *
     * @param beforePass 旧密码
     * @param newPass    新密码
     * @return JsonResult
     */
    @PostMapping(value = "/changePass")
    @ResponseBody
    @SystemLog(description = "修改密码", type = LogTypeEnum.OPERATION)
    public JsonResult changePass(@ModelAttribute("beforePass") String beforePass,
                                 @ModelAttribute("newPass") String newPass) {
        User loginUser = getLoginUser();
        User user = userService.get(loginUser.getId());
        if (user != null && Objects.equals(user.getUserPass(), Md5Util.toMd5(beforePass, "sens", 10))) {
            userService.updatePassword(user.getId(), newPass);
        } else {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.user.old-password-error"));
        }
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.user.update-password-success"));
    }

    /**
     * 删除第三方关联
     *
     * @param bindId 关联
     * @return JsonResult
     */
    @PostMapping(value = "/deleteBind")
    @ResponseBody
    @SystemLog(description = "取消第三方关联", type = LogTypeEnum.OPERATION)
    public JsonResult checkDelete(@RequestParam("id") Long bindId) {
        //1.判断是否存在
        ThirdAppBind thirdAppBind = thirdAppBindService.findByThirdAppBindId(bindId);
        if (thirdAppBind == null) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.bind-not-exist"));
        }
        //2.判断是不是本人
        User user = getLoginUser();
        if (!Objects.equals(thirdAppBind.getUserId(), user.getId())) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.permission-denied"));
        }
        //3.删除
        thirdAppBindService.delete(bindId);
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.delete-success"));
    }


}
