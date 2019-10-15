package com.liuyanzhao.sens.service;

import com.liuyanzhao.sens.model.dto.BindUserDTO;
import com.liuyanzhao.sens.model.dto.JsonResult;
import com.liuyanzhao.sens.utils.Response;

import java.io.UnsupportedEncodingException;

/**
 * @author 言曌
 * @date 2019/1/20 上午10:24
 */

public interface AuthService {

    /**
     * 根据code获得Token
     *
     * @param code code
     * @return token
     */
    Response<String> getAccessToken(String code);

    /**
     * 根据Token获得OpenId
     *
     * @param accessToken Token
     * @return openId
     */
    Response<String> getOpenId(String accessToken);

    /**
     * 刷新Token
     *
     * @param code code
     * @return 新的token
     */
    Response<String> refreshToken(String code);

    /**
     * 拼接授权URL
     *
     * @return URL
     */
    Response<String> getAuthorizationUrl();

    /**
     * 根据Token和OpenId获得用户信息
     *
     * @param accessToken Token
     * @param openId openId
     * @return 第三方应用给的用户信息
     */
    Response<BindUserDTO> getUserInfo(String accessToken, String openId);
}
