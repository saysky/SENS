package com.liuyanzhao.sens.web.controller.front;

import com.liuyanzhao.sens.entity.ThirdAppBind;
import com.liuyanzhao.sens.entity.User;
import com.liuyanzhao.sens.config.shiro.UserToken;
import com.liuyanzhao.sens.model.enums.BindTypeEnum;
import com.liuyanzhao.sens.model.enums.LoginTypeEnum;
import com.liuyanzhao.sens.service.*;
import com.liuyanzhao.sens.utils.LocaleMessageUtil;
import com.liuyanzhao.sens.utils.Response;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Set;


/**
 * @author 言曌
 * @date 2018/5/9 下午2:59
 */
@Controller
@Slf4j
public class FrontOAuthController extends BaseController {

    @Autowired
    private QQAuthService qqAuthService;

    @Autowired
    private GithubAuthService githubAuthService;

    @Autowired
    private UserService userService;

    @Autowired
    private ThirdAppBindService thirdAppBindService;

    @Autowired
    private LocaleMessageUtil localeMessageUtil;

    @Autowired
    private PermissionService permissionService;
    /**
     * 第三方授权后会回调此方法，并将code传过来
     *
     * @param code    回调code
     * @param request request
     * @param model   model
     * @return
     */
    @GetMapping("/oauth/qq/callback")
    public String oauthByQQ(@RequestParam(value = "code") String code, HttpServletRequest request, RedirectAttributes model) {
        Response<String> tokenResponse = qqAuthService.getAccessToken(code);
        if (tokenResponse.isSuccess()) {
            Response<String> openidResponse = qqAuthService.getOpenId(tokenResponse.getData());
            if (openidResponse.isSuccess()) {
                //根据openId去找关联的用户
                String openId = openidResponse.getData();
                ThirdAppBind bind = thirdAppBindService.findByAppTypeAndOpenId(BindTypeEnum.QQ.getValue(), openId);
                if (bind != null && bind.getUserId() != null) {
                    //执行Login操作
                    User user = userService.get(bind.getUserId());
                    if (user != null) {
                        Subject subject = SecurityUtils.getSubject();
                        UserToken userToken = new UserToken(user.getUserName(), user.getUserPass(), LoginTypeEnum.FREE.getValue());
                        try {
                            subject.login(userToken);

                            Set<String> permissionUrls = permissionService.findPermissionUrlsByUserId(user.getId());
                            subject.getSession().setAttribute("permissionUrls", permissionUrls);
                        } catch (LockedAccountException e) {
                            e.printStackTrace();
                            log.error("第三方登录(QQ)免密码登录失败, 账号被锁定, cause:{}", e.getMessage());
                            model.addAttribute("error", e.getMessage());
                            return "redirect:/admin/login";
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("第三方登录(QQ)免密码登录失败, cause:{}", e.getMessage());
                            model.addAttribute("error", localeMessageUtil.getMessage("code.admin.common.query-failed"));
                            return "redirect:/admin/login";
                        }
                        log.info("用户[{}]登录成功(QQ登录)。", user.getUserDisplayName());
                        return "redirect:/admin";
                    }
                } else {
                    //1.如果登录了，就跳转到绑定
                    User loginUser = getLoginUser();
                    if (loginUser != null) {
                        ThirdAppBind thirdAppBind = new ThirdAppBind();
                        thirdAppBind.setOpenId(openId);
                        thirdAppBind.setAppType(BindTypeEnum.QQ.getValue());
                        thirdAppBind.setCreateTime(new Date());
                        thirdAppBind.setStatus(1);
                        thirdAppBind.setUserId(loginUser.getId());
                        thirdAppBindService.insertOrUpdate(thirdAppBind);
                        return "redirect:/admin/user/profile";
                    }
                    //2.如果没有登录，跳转到注册
                    else {
                        return "redirect:/admin/register";
                    }
                }
            }
        }
        return "redirect:/admin/login";
    }

    /**
     * 第三方授权后会回调此方法，并将code传过来
     *
     * @param code code
     * @return
     */
    @GetMapping("/oauth/github/callback")
    public String oauthByGitHub(@RequestParam(value = "code") String code,
                                HttpServletRequest request) {
        Response<String> tokenResponse = githubAuthService.getAccessToken(code);
        if (tokenResponse.isSuccess()) {
            Response<String> openidResponse = githubAuthService.getOpenId(tokenResponse.getData());
            if (openidResponse.isSuccess()) {
                //根据openId去找关联的用户
                String openId = openidResponse.getData();
                ThirdAppBind bind = thirdAppBindService.findByAppTypeAndOpenId(BindTypeEnum.GITHUB.getValue(), openId);
                if (bind != null && bind.getUserId() != null) {
                    //执行Login操作
                    User user = userService.get(bind.getUserId());
                    if (user != null) {
                        Subject subject = SecurityUtils.getSubject();
                        UserToken userToken = new UserToken(user.getUserName(), user.getUserPass(), LoginTypeEnum.FREE.getValue());
                        try {
                            subject.login(userToken);

                            Set<String> permissionUrls = permissionService.findPermissionUrlsByUserId(user.getId());
                            subject.getSession().setAttribute("permissionUrls", permissionUrls);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error("第三方登录(GitHub)免密码登录失败, cause:{}", e);
                            return "redirect:/admin/login";
                        }
                        log.info("用户[{}]登录成功(登录)。", user.getUserDisplayName());
                        return "redirect:/admin";
                    }
                } else {
                    //1.如果登录了，就跳转到绑定
                    User loginUser = getLoginUser();
                    if (loginUser != null) {
                        ThirdAppBind thirdAppBind = new ThirdAppBind();
                        thirdAppBind.setOpenId(openId);
                        thirdAppBind.setAppType(BindTypeEnum.GITHUB.getValue());
                        thirdAppBind.setCreateTime(new Date());
                        thirdAppBind.setStatus(1);
                        thirdAppBind.setUserId(loginUser.getId());
                        thirdAppBindService.insertOrUpdate(thirdAppBind);
                        return "redirect:/admin/user/profile";
                    }
                    //2.如果没有登录，跳转到注册
                    else {
                        return "redirect:/admin/register";
                    }
                }
            }
        }
        return "redirect:/admin/login";
    }

}