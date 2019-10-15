package com.liuyanzhao.sens.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.liuyanzhao.sens.model.dto.BindUserDTO;
import com.liuyanzhao.sens.model.dto.SensConst;
import com.liuyanzhao.sens.model.enums.BlogPropertiesEnum;
import com.liuyanzhao.sens.service.QQAuthService;
import com.liuyanzhao.sens.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 言曌
 * @date 2018/5/9 下午3:15
 */

@Service
@Slf4j
public class QQAuthServiceImpl extends DefaultAuthServiceImpl implements QQAuthService {

    //QQ 登陆页面的URL
    private final static String AUTHORIZATION_URL =
            "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=%s";
    //获取token的URL
    private final static String ACCESS_TOKEN_URL = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s&redirect_uri=%s";

    // 获取用户 openid 的 URL
    private static final String OPEN_ID_URL = "https://graph.qq.com/oauth2.0/me?access_token=%s";

    // 获取用户信息的 URL，oauth_consumer_key 为 apiKey
    private static final String USER_INFO_URL = "https://graph.qq.com/user/get_user_info?access_token=%s&oauth_consumer_key=%s&openid=%s";

    // QQ 互联的 API 接口，访问用户资料
    private String SCOPE = "get_user_info";


    @Override
    public Response<String> getAuthorizationUrl() {
        String APP_ID = SensConst.OPTIONS.get(BlogPropertiesEnum.bind_qq_app_id.getProp());
        String CALLBACK_URL = SensConst.OPTIONS.get(BlogPropertiesEnum.bind_qq_callback.getProp());
        String url = String.format(AUTHORIZATION_URL, APP_ID, CALLBACK_URL, SCOPE);
        return Response.yes(url);
    }

    @Override
    public Response<String> getAccessToken(String code) {
        String APP_ID = SensConst.OPTIONS.get(BlogPropertiesEnum.bind_qq_app_id.getProp());
        String APP_SECRET = SensConst.OPTIONS.get(BlogPropertiesEnum.bind_qq_app_secret.getProp());
        String CALLBACK_URL = SensConst.OPTIONS.get(BlogPropertiesEnum.bind_qq_callback.getProp());
        String url = String.format(ACCESS_TOKEN_URL, APP_ID, APP_SECRET, code, CALLBACK_URL);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();

        String resp = getRestTemplate().getForObject(uri, String.class);
        if (resp != null && resp.contains("access_token")) {
            Map<String, String> map = getParam(resp);
            String access_token = map.get("access_token");
            return Response.yes(access_token);
        }
        log.error("QQ获得access_token失败，resp:{}", resp);
        return Response.no();
    }

    //由于QQ的几个接口返回类型不一样，此处是获取key-value类型的参数
    private Map<String, String> getParam(String string) {
        Map<String, String> map = new HashMap();
        String[] kvArray = string.split("&");
        for (int i = 0; i < kvArray.length; i++) {
            String[] kv = kvArray[i].split("=");
            map.put(kv[0], kv[1]);
        }
        return map;
    }

    //QQ接口返回类型是text/plain，此处将其转为json
    private JSONObject ConvertToJson(String string) {
        string = string.substring(string.indexOf("(") + 1, string.length());
        string = string.substring(0, string.indexOf(")"));
        JSONObject jsonObject = JSONObject.parseObject(string);
        return jsonObject;
    }

    @Override
    public Response<String> getOpenId(String accessToken) {
        String url = String.format(OPEN_ID_URL, accessToken);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();

        String resp = getRestTemplate().getForObject(uri, String.class);
        if (resp != null && resp.contains("openid")) {
            JSONObject jsonObject = ConvertToJson(resp);
            String openid = jsonObject.getString("openid");
            return Response.yes(openid);
        }
        log.error("QQ获得openid失败，resp:{}", resp);
        return Response.no(resp);
    }

    @Override
    public Response<BindUserDTO> getUserInfo(String accessToken, String openId) {
        String APP_ID = SensConst.OPTIONS.get(BlogPropertiesEnum.bind_qq_app_id.getProp());
        String url = String.format(USER_INFO_URL, accessToken, APP_ID, openId);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();

        String resp = getRestTemplate().getForObject(uri, String.class);
        JSONObject data = JSONObject.parseObject(resp);
        BindUserDTO result = new BindUserDTO();
        result.setOpenId(openId);
        result.setGender(data.getString("gender"));
        result.setAvatar(data.getString("figureurl_qq_2"));
        result.setNickname(data.getString("nickname"));
        return Response.yes(result);
    }

    @Override
    public Response<String> refreshToken(String code) {
        return null;
    }
}
