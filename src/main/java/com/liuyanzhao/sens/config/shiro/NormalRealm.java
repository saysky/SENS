package com.liuyanzhao.sens.config.shiro;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HtmlUtil;
import com.liuyanzhao.sens.entity.Permission;
import com.liuyanzhao.sens.entity.Role;
import com.liuyanzhao.sens.entity.User;
import com.liuyanzhao.sens.model.dto.JsonResult;
import com.liuyanzhao.sens.model.dto.LogsRecord;
import com.liuyanzhao.sens.model.enums.CommonParamsEnum;
import com.liuyanzhao.sens.model.enums.ResultCodeEnum;
import com.liuyanzhao.sens.model.enums.TrueFalseEnum;
import com.liuyanzhao.sens.model.enums.UserStatusEnum;
import com.liuyanzhao.sens.service.PermissionService;
import com.liuyanzhao.sens.service.RoleService;
import com.liuyanzhao.sens.service.UserService;
import com.liuyanzhao.sens.utils.LocaleMessageUtil;
import com.liuyanzhao.sens.utils.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 默认的realm
 *
 * @author 言曌
 * @date 2018/9/1 上午10:47
 */
@Slf4j
public class NormalRealm extends AuthorizingRealm {

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private RoleService roleService;

    @Autowired
    @Lazy
    private PermissionService permissionService;

    @Autowired
    private LocaleMessageUtil localeMessageUtil;

    /**
     * 认证信息(身份验证) Authentication 是用来验证用户身份
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        log.info("认证-->MyShiroRealm.doGetAuthenticationInfo()");
        //1.验证用户名
        User user = null;
        String account = (String) token.getPrincipal();
        if (Validator.isEmail(account)) {
            user = userService.findByEmail(account);
        } else {
            user = userService.findByUserName(account);
        }
        if (user == null) {
            //用户不存在
            log.info("用户不存在! 登录名:{}, 密码:{}", account, token.getCredentials());
            return null;
        }

        //2.判断账号是否被封号
        if (!Objects.equals(user.getStatus(), UserStatusEnum.NORMAL.getCode())) {
            throw new LockedAccountException(localeMessageUtil.getMessage("code.admin.login.disabled.forever"));
        }

        //3.首先判断是否已经被禁用已经是否已经过了10分钟
        Date loginLast = DateUtil.date();
        if (null != user.getLoginLast()) {
            loginLast = user.getLoginLast();
        }
        Long between = DateUtil.between(loginLast, DateUtil.date(), DateUnit.MINUTE);
        if (StringUtils.equals(user.getLoginEnable(), TrueFalseEnum.FALSE.getValue()) && (between < CommonParamsEnum.TEN.getValue())) {
            log.info("账号已锁定! 登录名:{}, 密码:{}", account, token.getCredentials());
            throw new LockedAccountException(localeMessageUtil.getMessage("code.admin.login.disabled"));
        }
        //4.封装authenticationInfo，准备验证密码
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user, // 用户名
                user.getUserPass(), // 密码
                ByteSource.Util.bytes("sens"), // 盐
                getName() // realm name
        );
        System.out.println("realName:" + getName());
        return authenticationInfo;
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        User user = (User) principals.getPrimaryPrincipal();

        List<Role> roles = roleService.listRolesByUserId(user.getId());
        for (Role role : roles) {
            authorizationInfo.addRole(role.getRole());
            List<Permission> permissions = permissionService.listPermissionsByRoleId(role.getId());
            //把权限的URL全部放到authorizationInfo中去
            Set<String> urls = permissions.stream().map(p -> p.getUrl()).collect(Collectors.toSet());
            authorizationInfo.addStringPermissions(urls);

        }
        return authorizationInfo;
    }
}
