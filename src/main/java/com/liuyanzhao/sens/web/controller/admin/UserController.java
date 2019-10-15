package com.liuyanzhao.sens.web.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.config.annotation.SystemLog;
import com.liuyanzhao.sens.entity.Role;
import com.liuyanzhao.sens.entity.User;
import com.liuyanzhao.sens.entity.UserRoleRef;
import com.liuyanzhao.sens.model.dto.JsonResult;
import com.liuyanzhao.sens.config.shiro.UserToken;
import com.liuyanzhao.sens.model.enums.LogTypeEnum;
import com.liuyanzhao.sens.model.enums.LoginTypeEnum;
import com.liuyanzhao.sens.model.enums.ResultCodeEnum;
import com.liuyanzhao.sens.service.*;
import com.liuyanzhao.sens.utils.LocaleMessageUtil;
import com.liuyanzhao.sens.utils.Md5Util;
import com.liuyanzhao.sens.utils.PageUtil;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleRefService userRoleRefService;

    @Autowired
    private PostService postService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private LocaleMessageUtil localeMessageUtil;

    public static final String USER_NAME = "userName";
    public static final String USER_DISPLAY_NAME = "userDisplayName";
    public static final String EMAIL = "email";
    public static final String URL = "url";

    /**
     * 查询所有分类并渲染user页面
     *
     * @return 模板路径admin/admin_user
     */
    @GetMapping
    public String users(
            @RequestParam(value = "status", defaultValue = "0") Integer status,
            @RequestParam(value = "keywords", defaultValue = "") String keywords,
            @RequestParam(value = "searchType", defaultValue = "") String searchType,
            @RequestParam(value = "role", defaultValue = "admin") String role,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sort", defaultValue = "createTime") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order, Model model) {
        //用户列表
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        User condition = new User();
        condition.setStatus(status);
        if (!StringUtils.isBlank(keywords)) {
            if (USER_NAME.equals(searchType)) {
                condition.setUserName(keywords);
            } else if (USER_DISPLAY_NAME.equals(searchType)) {
                condition.setUserDisplayName(keywords);
            } else if (EMAIL.equals(searchType)) {
                condition.setUserEmail(keywords);
            } else if (URL.equals(searchType)) {
                condition.setUserSite(keywords);
            }
        }
        Page<User> users = userService.findByRoleAndCondition(role, condition, page);

        //角色列表
        List<Role> roles = roleService.findAllWithCount();
        model.addAttribute("users", users.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("roles", roles);
        model.addAttribute("currentRole", role);
        model.addAttribute("status", status);
        model.addAttribute("keywords", keywords);
        model.addAttribute("searchType", searchType);
        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        return "admin/admin_user";
    }


    /**
     * 删除用户
     *
     * @param userId 用户Id
     * @return JsonResult
     */
    @PostMapping(value = "/delete")
    @ResponseBody
    public JsonResult removeUser(@RequestParam("id") Long userId) {
        //1.检查用户有没有文章
        Integer postCount = postService.countByUserId(userId);
        if (postCount > 0) {
            Object[] args = {postCount};
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.user.delete-failed", args));
        }
        userService.delete(userId);
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.delete-success"));
    }

    /**
     * 添加用户页面
     *
     * @return 模板路径admin/admin_edit
     */
    @GetMapping("/new")
    public String addUser(Model model) {
        List<Role> roles = roleService.findAll();
        model.addAttribute("roles", roles);
        return "admin/admin_user_add";
    }

    /**
     * 编辑用户页面
     *
     * @return 模板路径admin/admin_edit
     */
    @GetMapping("/edit")
    public String edit(@RequestParam("id") Long userId, Model model) {
        User user = userService.get(userId);
        if (user != null) {
            model.addAttribute("user", user);
            //当前用户的角色
            List<Role> currentRoles = roleService.listRolesByUserId(userId);
            model.addAttribute("currentRoles", currentRoles);
            //角色列表
            List<Role> roles = roleService.findAll();
            model.addAttribute("roles", roles);
            return "admin/admin_user_edit";
        }
        return this.renderNotFound();
    }

    /**
     * 批量删除
     *
     * @param ids 用户ID列表
     * @return
     */
    @DeleteMapping(value = "/batchDelete")
    @ResponseBody
    @SystemLog(description = "批量删除用户", type = LogTypeEnum.OPERATION)
    public JsonResult batchDelete(@RequestParam("ids") List<Long> ids) {
        //批量操作
        if (ids == null || ids.size() == 0 || ids.size() >= 100) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), "参数不合法!");
        }
        List<User> userList = userService.findByBatchIds(ids);
        for (User user : userList) {
            userService.delete(user.getId());
        }
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.delete-success"));
    }

    /**
     * 新增/修改用户
     *
     * @param user user
     * @return 重定向到/admin/user
     */
    @PostMapping(value = "/save")
    @ResponseBody
    @SystemLog(description = "保存用户", type = LogTypeEnum.OPERATION)
    public JsonResult saveUser(@ModelAttribute User user,
                               @RequestParam("roleList") List<Long> roleList) {
        if (roleList == null || roleList.size() == 0) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.user.need-choose-role"));
        }

        //1.添加用户
        userService.insertOrUpdate(user);
        //2.先删除该用户的角色关联
        userRoleRefService.deleteByUserId(user.getId());
        //3.关联角色
        Long userId = user.getId();
        for (Long roleId : roleList) {
            userRoleRefService.insert(new UserRoleRef(userId, roleId));
        }
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.save-success"));
    }

    /**
     * 登录该账号
     *
     * @return 模板路径admin/admin_edit
     */
    @GetMapping("/login")
    @SystemLog(description = "管理员登录其他用户", type = LogTypeEnum.OPERATION)
    public String loginIt(@RequestParam("id") Long userId) {
        User user = userService.get(userId);
        Subject subject = SecurityUtils.getSubject();
        // freeRealm 无需验证账号密码
        UserToken userToken = new UserToken(user.getUserName(), null, LoginTypeEnum.FREE.getValue());
        subject.login(userToken);

        Set<String> permissionUrls = permissionService.findPermissionUrlsByUserId(user.getId());
        subject.getSession().setAttribute("permissionUrls", permissionUrls);
        return "redirect:/admin";
    }


    /**
     * 管理员修改用户资料
     *
     * @param user user
     * @return JsonResult
     */
    @PostMapping(value = "/profile/save/proxy")
    @ResponseBody
    @SystemLog(description = "管理员修改其他用户信息", type = LogTypeEnum.OPERATION)
    public JsonResult adminSaveProfile(@ModelAttribute User user) {
        userService.insertOrUpdate(user);
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.edit-success"));
    }

    /**
     * 管理员给用户修改密码
     *
     * @param newPass 新密码
     * @return JsonResult
     */
    @PostMapping(value = "/proxy/changePass")
    @ResponseBody
    @SystemLog(description = "管理员修改其他用户密码", type = LogTypeEnum.OPERATION)
    public JsonResult adminChangePass(@ModelAttribute("id") Long userId,
                                      @ModelAttribute("newPass") String newPass) {
        User user = userService.get(userId);
        if (null != user) {
            userService.updatePassword(user.getId(), newPass);
        }
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.user.update-password-success"));
    }
}
