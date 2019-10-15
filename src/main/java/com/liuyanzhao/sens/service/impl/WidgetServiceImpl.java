package com.liuyanzhao.sens.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuyanzhao.sens.common.constant.RedisKeyExpire;
import com.liuyanzhao.sens.common.constant.RedisKeys;
import com.liuyanzhao.sens.entity.Tag;
import com.liuyanzhao.sens.entity.Widget;
import com.liuyanzhao.sens.mapper.WidgetMapper;
import com.liuyanzhao.sens.service.WidgetService;
import com.liuyanzhao.sens.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     幻灯片业务逻辑实现类
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/24
 */
@Service
public class WidgetServiceImpl implements WidgetService {

    @Autowired
    private WidgetMapper widgetMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<Widget> findByWidgetType(Integer widgetType) {
        String value = redisUtil.get(RedisKeys.WIDGET + widgetType);
        // 先从缓存取，缓存没有从数据库取
        if (StringUtils.isNotEmpty(value)) {
            return JSON.parseArray(value, Widget.class);
        }
        Map<String, Object> map = new HashMap<>(1);
        map.put("widget_type", widgetType);
        List<Widget> widgetList = widgetMapper.selectByMap(map);
        redisUtil.set(RedisKeys.WIDGET + widgetType, JSON.toJSONString(widgetList), RedisKeyExpire.WIDGET);
        return widgetList;
    }

    @Override
    public BaseMapper<Widget> getRepository() {
        return widgetMapper;
    }

    @Override
    public QueryWrapper<Widget> getQueryWrapper(Widget widget) {
        //对指定字段查询
        QueryWrapper<Widget> queryWrapper = new QueryWrapper<>();
        if (widget != null) {
            if (StrUtil.isNotBlank(widget.getWidgetTitle())) {
                queryWrapper.eq("widget_title", widget.getWidgetTitle());
            }
            if (widget.getWidgetType() != null) {
                queryWrapper.like("widget_type", widget.getWidgetType());
            }
        }
        return queryWrapper;
    }

    @Override
    public Widget insertOrUpdate(Widget widget) {
        if (widget.getId() == null) {
            insert(widget);
        } else {
            update(widget);
        }
        //删除缓存
        redisUtil.del(RedisKeys.WIDGET + widget.getWidgetType());
        return widget;
    }

    @Override
    public void delete(Long id) {
        Widget widget = widgetMapper.selectById(id);
        if(widget != null) {
            widgetMapper.deleteById(widget.getId());
            //删除缓存
            redisUtil.del(RedisKeys.WIDGET + widget.getWidgetType());
        }
    }
}
