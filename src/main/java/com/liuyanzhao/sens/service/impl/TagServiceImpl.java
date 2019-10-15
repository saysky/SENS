package com.liuyanzhao.sens.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.common.constant.RedisKeyExpire;
import com.liuyanzhao.sens.common.constant.RedisKeys;
import com.liuyanzhao.sens.entity.Tag;
import com.liuyanzhao.sens.mapper.PostTagRefMapper;
import com.liuyanzhao.sens.mapper.TagMapper;
import com.liuyanzhao.sens.service.TagService;
import com.liuyanzhao.sens.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *     标签业务逻辑实现类
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/12
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private PostTagRefMapper postTagRefMapper;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public BaseMapper<Tag> getRepository() {
        return tagMapper;
    }


    @Override
    public QueryWrapper<Tag> getQueryWrapper(Tag tag) {
        //对指定字段查询
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        if (tag != null) {
            if (StrUtil.isNotBlank(tag.getTagName())) {
                queryWrapper.eq("tag_name", tag.getTagName());
            }
            if (tag.getUserId() != null) {
                queryWrapper.eq("user_id", tag.getUserId());
            }
        }
        return queryWrapper;
    }

    @Override
    public List<Tag> findHotTags(Integer limit) {
        return tagMapper.findAllWithCount(limit);
    }

    @Override
    public Tag findTagByTagName(String tagName) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("tag_name", tagName);
        return tagMapper.selectOne(queryWrapper);
    }

    @Override
    public Tag findTagByUserIdAndTagName(Long userId, String tagName) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("tag_name", tagName);
        return tagMapper.selectOne(queryWrapper);
    }


    @Override
    public List<Tag> strListToTagList(Long userId, String tagList) {
        String[] tags = tagList.split(",");
        List<Tag> tagsList = new ArrayList<>();
        for (String tag : tags) {
            Tag t = findTagByUserIdAndTagName(userId, tag);
            Tag nt = null;
            if (null != t) {
                tagsList.add(t);
            } else {
                nt = new Tag();
                nt.setTagName(tag);
                nt.setUserId(userId);
                tagsList.add(insert(nt));
            }
        }
        return tagsList;
    }


    @Override
    public List<Tag> findByPostId(Long postId) {
        String value = redisUtil.get(RedisKeys.POST_TAG+ postId);
        // 先从缓存取，缓存没有从数据库取
        if (StringUtils.isNotEmpty(value)) {
            return JSON.parseArray(value, Tag.class);
        }
        List<Tag> tagList = tagMapper.findByPostId(postId);
        redisUtil.set(RedisKeys.POST_TAG + postId, JSON.toJSONString(tagList), RedisKeyExpire.POST_TAG);
        return tagList;
    }

    @Override
    public List<Tag> findByUserId(Long userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        return tagMapper.selectList(queryWrapper);
    }

    @Override
    public Tag insert(Tag entity) {
        Tag isExist = findTagByUserIdAndTagName(entity.getUserId(), entity.getTagName());
        if (isExist != null) {
            return isExist;
        }
        tagMapper.insert(entity);
        return entity;
    }

    @Override
    public Tag update(Tag entity) {
        Tag isExist = findTagByUserIdAndTagName(entity.getUserId(), entity.getTagName());
        if (isExist != null && !Objects.equals(isExist.getId(), entity.getId())) {
            return isExist;
        }
        tagMapper.updateById(entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Tag tag = this.get(id);
        if (tag != null) {
            //1.删除标签和文章的关联
            postTagRefMapper.deleteByTagId(id);
            //2.删除标签
            tagMapper.deleteById(id);
        }
    }


    @Override
    public Tag insertOrUpdate(Tag entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }

    @Override
    public Integer getTodayCount() {
        return tagMapper.getTodayCount();
    }

    @Override
    public Page<Tag> findByUserIdWithCount(Long userId, Page<Tag> page) {
        return page.setRecords(tagMapper.findByUserIdWithCount(userId, page));
    }

    @Override
    public Integer deleteByUserId(Long userId) {
        return tagMapper.deleteByUserId(userId);
    }

    @Override
    public List<Tag> getTagRankingByUserId(Long userId, Integer limit) {
        String value = redisUtil.get(RedisKeys.USER_TAG_RANKING + userId);
        // 先从缓存取，缓存没有从数据库取
        if (StringUtils.isNotEmpty(value)) {
            return JSON.parseArray(value, Tag.class);
        }
        List<Tag> tagList = tagMapper.getTagRankingByUserId(userId, limit);
        redisUtil.set(RedisKeys.USER_TAG_RANKING + userId, JSON.toJSONString(tagList), RedisKeyExpire.USER_TAG_RANKING);
        return tagList;
    }
}
