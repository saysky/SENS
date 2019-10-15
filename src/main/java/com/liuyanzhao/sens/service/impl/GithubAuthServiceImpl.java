package com.liuyanzhao.sens.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.liuyanzhao.sens.model.dto.BindUserDTO;
import com.liuyanzhao.sens.model.dto.SensConst;
import com.liuyanzhao.sens.model.enums.BlogPropertiesEnum;
import com.liuyanzhao.sens.service.GithubAuthService;
import com.liuyanzhao.sens.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 言曌
 * @date 2018/5/15 下午11:31
 */
@Service
@Slf4j
public class GithubAuthServiceImpl extends DefaultAuthServiceImpl implements GithubAuthService {

    private static final String AUTHORIZE_URL = "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&state=%s";

    private static final String ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token?client_id=%s&client_secret=%s&code=%s&redirect_uri=%s&state=%s";

    private static final String USER_INFO_URL = "https://api.github.com/user?access_token=%s";

    private static final String GITHUB_STATE = "use-login";//state，随便填，会返回原值给你

    //此处是获取key-value类型的参数
    private Map<String, String> getParam(String string) {
        Map<String, String> map = new HashMap();
        String[] kvArray = string.split("&");
        for (int i = 0; i < kvArray.length; i++) {
            String[] kv = kvArray[i].split("=");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            } else if (kv.length == 1) {
                map.put(kv[0], "");
            }
        }
        return map;
    }

    @Override
    public Response<String> getAccessToken(String code) {
        String APP_ID = SensConst.OPTIONS.get(BlogPropertiesEnum.bind_github_app_id.getProp());
        String APP_SECRET = SensConst.OPTIONS.get(BlogPropertiesEnum.bind_github_app_secret.getProp());
        String CALLBACK_URL = SensConst.OPTIONS.get(BlogPropertiesEnum.bind_github_callback.getProp());
        String url = String.format(ACCESS_TOKEN_URL, APP_ID, APP_SECRET, code, CALLBACK_URL, GITHUB_STATE);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();
        String resp;
        try {
            resp = getRestTemplate().getForObject(uri, String.class);
        } catch (Exception e) {
            log.error("Github获得access_token失败, cause:{}", e);
            return Response.no();
        }
        if (resp != null && resp.contains("access_token")) {
            Map<String, String> map = getParam(resp);
            String access_token = map.get("access_token");
            return Response.yes(access_token);
        }
        log.error("GitHub获得access_token失败，resp:{}", resp);
        return Response.no();
    }

    @Override
    public Response<String> getOpenId(String accessToken) {
        String url = String.format(USER_INFO_URL, accessToken);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();
        String resp;
        try {
            resp = getRestTemplate().getForObject(uri, String.class);
        } catch (Exception e) {
            log.error("GitHub获得OpenId失败, cause:{}", e);
            return Response.no("access_token无效！");
        }
        if (resp != null && resp.contains("id")) {
            JSONObject data = JSONObject.parseObject(resp);
            String openid = data.getString("id");
            return Response.yes(openid);
        }
        log.error("GitHub获得openid失败，resp:{}", resp);
        return Response.no();
    }

    @Override
    public Response<String> refreshToken(String code) {
        return null;
    }

    @Override
    public Response<String> getAuthorizationUrl() {
        String APP_ID = SensConst.OPTIONS.get(BlogPropertiesEnum.bind_github_app_id.getProp());
        String CALLBACK_URL = SensConst.OPTIONS.get(BlogPropertiesEnum.bind_github_callback.getProp());

        String url = String.format(AUTHORIZE_URL, APP_ID, CALLBACK_URL, GITHUB_STATE);
        return Response.yes(url);
    }

    @Override
    public Response<BindUserDTO> getUserInfo(String accessToken, String openId) {

        String url = String.format(USER_INFO_URL, accessToken);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();
        String resp;
        try {
            resp = getRestTemplate().getForObject(uri, String.class);
        } catch (Exception e) {
            log.error("GitHub获得用户信息失败，access_token无效, cause:{}", e);
            return Response.no("access_token无效！");
        }
        if (resp != null && resp.contains("id")) {
            JSONObject data = JSONObject.parseObject(resp);
            BindUserDTO result = new BindUserDTO();
            result.setOpenId(data.getString("id"));
            result.setAvatar(data.getString("avatar_url"));
            result.setNickname(data.getString("name"));
            return Response.yes(result);
        }
        log.error("GitHub获得用户信息失败，resp:{}", resp);
        return Response.no();
    }
}
