package com.liuyanzhao.sens.service.impl;

import com.alibaba.fastjson.JSON;
import com.liuyanzhao.sens.common.constant.RedisKeyExpire;
import com.liuyanzhao.sens.common.constant.RedisKeys;
import com.liuyanzhao.sens.entity.Options;
import com.liuyanzhao.sens.mapper.OptionsMapper;
import com.liuyanzhao.sens.service.OptionsService;
import com.liuyanzhao.sens.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     系统设置业务逻辑实现类
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/14
 */
@Service
public class OptionsServiceImpl implements OptionsService {

    @Autowired
    private OptionsMapper optionsMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 批量保存设置
     *
     * @param options options
     */
    @Override
    public void saveOptions(Map<String, String> options) {
        if (null != options && !options.isEmpty()) {
            options.forEach((k, v) -> saveOption(k, v));
        }
        // 删除缓存
        redisUtil.del(RedisKeys.ALL_OPTIONS);
    }

    /**
     * 保存单个设置选项
     *
     * @param key   key
     * @param value value
     */
    @Override
    public void saveOption(String key, String value) {
        Options options = null;
        if (StringUtils.equals(value, "")) {
            options = new Options();
            options.setOptionName(key);
            this.removeOption(options);
        } else {
            if (StringUtils.isNotEmpty(key)) {
                //如果查询到有该设置选项则做更新操作，反之保存新的设置选项
                if (null == optionsMapper.findOptionsByOptionName(key)) {
                    options = new Options();
                    options.setOptionName(key);
                    options.setOptionValue(value);
                    optionsMapper.insert(options);
                } else {
                    options = optionsMapper.findOptionsByOptionName(key);
                    options.setOptionValue(value);
                    optionsMapper.updateById(options);
                }
            }
        }
    }

    /**
     * 移除设置项
     *
     * @param options options
     */
    @Override
    public void removeOption(Options options) {
        optionsMapper.deleteById(options);
    }

    /**
     * 获取设置选项
     *
     * @return Map
     */
    @Override
    public Map<String, String> findAllOptions() {
        String value = redisUtil.get(RedisKeys.ALL_OPTIONS);
        // 先从缓存取，缓存没有从数据库取
        if (StringUtils.isNotEmpty(value)) {
            return JSON.parseObject(value, Map.class);
        }
        List<Options> optionsList = optionsMapper.selectList(null);
        Map<String, String> options = new HashMap<>(optionsList.size());
        if (null != optionsList) {
            optionsList.forEach(option -> options.put(option.getOptionName(), option.getOptionValue()));
        }
        redisUtil.set(RedisKeys.ALL_OPTIONS, JSON.toJSONString(options), RedisKeyExpire.ALL_OPTIONS);
        return options;
    }

    /**
     * 根据key查询单个设置选项
     *
     * @param key key
     * @return String
     */
    @Override
    public String findOneOption(String key) {
        Options options = optionsMapper.findOptionsByOptionName(key);
        if (null != options) {
            return options.getOptionValue();
        }
        return null;
    }

}
