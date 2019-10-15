package com.liuyanzhao.sens.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuyanzhao.sens.entity.ThirdAppBind;
import com.liuyanzhao.sens.mapper.ThirdAppBindMapper;
import com.liuyanzhao.sens.service.ThirdAppBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <pre>
 *     第三方应用绑定业务逻辑实现类
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/14
 */
@Service
public class ThirdAppBindServiceImpl implements ThirdAppBindService {


    @Autowired
    private ThirdAppBindMapper thirdAppBindMapper;


    @Override
    public ThirdAppBind findByAppTypeAndOpenId(String appType, String openId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("app_type", appType);
        queryWrapper.eq("open_id", openId);
        return thirdAppBindMapper.selectOne(queryWrapper);
    }

    @Override
    public ThirdAppBind findByThirdAppBindId(Long thirdAppBindId) {
        return thirdAppBindMapper.selectById(thirdAppBindId);
    }

    @Override
    public List<ThirdAppBind> findByUserId(Long userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        return thirdAppBindMapper.selectList(queryWrapper);
    }

    @Override
    public BaseMapper<ThirdAppBind> getRepository() {
        return thirdAppBindMapper;
    }

    @Override
    public QueryWrapper<ThirdAppBind> getQueryWrapper(ThirdAppBind thirdAppBind) {
        //对指定字段查询
        QueryWrapper<ThirdAppBind> queryWrapper = new QueryWrapper<>();
        if (thirdAppBind != null) {
            if (StrUtil.isNotBlank(thirdAppBind.getAppType())) {
                queryWrapper.eq("app_type", thirdAppBind.getAppType());
            }
            if (StrUtil.isNotBlank(thirdAppBind.getOpenId())) {
                queryWrapper.eq("open_id", thirdAppBind.getOpenId());
            }
            if (thirdAppBind.getUserId() != null) {
                queryWrapper.eq("user_id", thirdAppBind.getUserId());
            }
            if (thirdAppBind.getStatus() != null) {
                queryWrapper.eq("status", thirdAppBind.getStatus());
            }
        }
        return queryWrapper;
    }

    @Override
    public ThirdAppBind insertOrUpdate(ThirdAppBind entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }
}
