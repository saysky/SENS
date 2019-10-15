package com.liuyanzhao.sens.service;

import com.liuyanzhao.sens.common.base.BaseService;
import com.liuyanzhao.sens.entity.ThirdAppBind;

import java.util.List;

/**
 * <pre>
 *     第三方应用业务逻辑接口
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/14
 */
public interface ThirdAppBindService extends BaseService<ThirdAppBind, Long> {

    /**
     * 根据应用类型和OpenId查询
     *
     * @param appType 应用类型
     * @param openId OpenId
     * @return 关联
     */
    ThirdAppBind findByAppTypeAndOpenId(String appType, String openId);


    /**
     * 根据编号查询单个绑定
     *
     * @param bindId bindId
     * @return ThirdAppBind
     */
    ThirdAppBind findByThirdAppBindId(Long bindId);

    /**
     * 根据用户ID查询
     * @param userId
     * @return
     */
    List<ThirdAppBind> findByUserId(Long userId);

}
