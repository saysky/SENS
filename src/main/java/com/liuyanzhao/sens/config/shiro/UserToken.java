package com.liuyanzhao.sens.config.shiro;

import lombok.Data;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 *
 * 自定义UsernamePasswordToken
 * 必须传loginType
 *
 * @author 言曌
 * @date 2019/1/24 下午4:20
 */
@Data
public class UserToken extends UsernamePasswordToken {
    private String loginType;

    public UserToken() {
    }

    public UserToken(final String username, final String password,
                     final String loginType) {
        super(username, password);
        this.loginType = loginType;
    }

}
