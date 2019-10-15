package com.liuyanzhao.sens.web.controller.common;

import com.liuyanzhao.sens.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.servlet.http.Cookie;

/**
 * <pre>
 *     Controller抽象类
 * </pre>
 *
 * @author : saysky
 * @date : 2017/12/15
 */
public abstract class BaseController {

    /**
     * 定义默认主题
     */
    public static String THEME = "begin";

    /**
     * 管理员
     */
    public static Integer IS_ADMIN = 1;

    /**
     * 根据主题名称渲染页面
     *
     * @param pageName pageName
     * @return 返回拼接好的模板路径
     */
    public String render(String pageName) {
        StringBuffer themeStr = new StringBuffer("themes/");
        themeStr.append(THEME);
        themeStr.append("/");
        return themeStr.append(pageName).toString();
    }

    /**
     * 渲染404页面
     *
     * @return redirect:/404
     */
    public String renderNotFound() {
        return "forward:/404";
    }

    /**
     * 渲染404页面
     *
     * @return redirect:/404
     */
    public String renderNotAllowAccess() {
        return "redirect:/403";
    }

    /**
     * 当前登录用户
     *
     * @return
     */
    public User getLoginUser() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return (User) subject.getPrincipal();
        }
        return null;
    }

    /**
     * 当前用户ID
     *
     * @return
     */
    public Long getLoginUserId() {
        return getLoginUser().getId();
    }

    /**
     * 当前用户是管理员
     *
     * @return
     */
    public Boolean loginUserIsAdmin() {
        User loginUser = getLoginUser();
        if (loginUser != null) {
            return IS_ADMIN.equals(loginUser.getIsAdmin());
        }

        return false;
    }


}
