package com.liuyanzhao.sens.config.shiro;

import cn.hutool.core.lang.Validator;
import com.liuyanzhao.sens.entity.Permission;
import com.liuyanzhao.sens.entity.Role;
import com.liuyanzhao.sens.entity.User;
import com.liuyanzhao.sens.model.enums.UserStatusEnum;
import com.liuyanzhao.sens.service.PermissionService;
import com.liuyanzhao.sens.service.RoleService;
import com.liuyanzhao.sens.service.UserService;
import com.liuyanzhao.sens.utils.LocaleMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Objects;

/**
 * 免密登录，输入的密码和原密码一致
 *
 * @author 言曌
 * @date 2018/9/1 上午10:47
 */
@Slf4j
public class FreeRealm extends AuthorizingRealm {

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
            log.info("第三方登录，用户不存在! 登录名:{}, 密码:{}", account,token.getCredentials());
            return null;
        }

        //2.判断账号是否被封号
        if (!Objects.equals(user.getStatus(), UserStatusEnum.NORMAL.getCode())) {
            throw new LockedAccountException(localeMessageUtil.getMessage("code.admin.login.disabled.forever"));
        }

        //3.封装authenticationInfo，准备验证密码
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user,
                user.getUserPass(),
                null,
                getName()
        );
        return authenticationInfo;
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        User user = (User) principals.getPrimaryPrincipal();

        List<Role> roles = roleService.listRolesByUserId(user.getId());
        for (Role role : roles) {
            authorizationInfo.addRole(role.getRole());
            List<Permission> permissions = permissionService.listPermissionsByRoleId(role.getId());
            for (Permission p : permissions) {
                authorizationInfo.addStringPermission(p.getUrl());
            }
        }
        return authorizationInfo;
    }
}
