package com.liuyanzhao.sens.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuyanzhao.sens.common.constant.RedisKeyExpire;
import com.liuyanzhao.sens.common.constant.RedisKeys;
import com.liuyanzhao.sens.entity.Slide;
import com.liuyanzhao.sens.mapper.SlideMapper;
import com.liuyanzhao.sens.service.SlideService;
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
public class SlideServiceImpl implements SlideService {

    @Autowired
    private SlideMapper slideMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<Slide> findBySlideType(Integer slideType) {
        String value = redisUtil.get(RedisKeys.SLIDE + slideType);
        // 先从缓存取，缓存没有从数据库取
        if (StringUtils.isNotEmpty(value)) {
            return JSON.parseArray(value, Slide.class);
        }
        Map<String, Object> map = new HashMap<>(1);
        map.put("slide_type", slideType);
        List<Slide> slideList = slideMapper.selectByMap(map);
        redisUtil.set(RedisKeys.SLIDE + slideType, JSON.toJSONString(slideList), RedisKeyExpire.SLIDE);
        return slideList;
    }

    @Override
    public BaseMapper<Slide> getRepository() {
        return slideMapper;
    }

    @Override
    public QueryWrapper<Slide> getQueryWrapper(Slide slide) {
        //对指定字段查询
        QueryWrapper<Slide> queryWrapper = new QueryWrapper<>();
        if (slide != null) {
            if (StrUtil.isNotBlank(slide.getSlideTitle())) {
                queryWrapper.like("slide_title", slide.getSlideTitle());
            }
            if (slide.getSlideType() != null) {
                queryWrapper.eq("slide_type", slide.getSlideType());
            }
        }
        return queryWrapper;
    }

    @Override
    public Slide insertOrUpdate(Slide slide) {
        if (slide.getId() == null) {
            insert(slide);
        } else {
            update(slide);
        }
        // 删除缓存
        redisUtil.del(RedisKeys.SLIDE + slide.getSlideType());
        return slide;
    }

    @Override
    public void delete(Long id) {
        Slide slide = slideMapper.selectById(id);
        if (slide != null) {
            slideMapper.deleteById(id);
            // 删除缓存
            redisUtil.del(RedisKeys.SLIDE + slide.getSlideType());
        }
    }
}
