package com.liuyanzhao.sens.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuyanzhao.sens.entity.Log;
import com.liuyanzhao.sens.mapper.LogMapper;
import com.liuyanzhao.sens.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * <pre>
 *     日志业务逻辑实现类
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/19
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper logMapper;

    @Override
    public BaseMapper<Log> getRepository() {
        return logMapper;
    }

    @Override
    public QueryWrapper<Log> getQueryWrapper(Log log) {
        //对指定字段查询
        QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
        if (log != null) {
            if (StrUtil.isNotBlank(log.getName())) {
                queryWrapper.eq("name", log.getName());
            }
            if (StrUtil.isNotBlank(log.getIp())) {
                queryWrapper.eq("ip", log.getIp());
            }
            if (StrUtil.isNotBlank(log.getLogType())) {
                queryWrapper.eq("log_type", log.getLogType());
            }
            if (StrUtil.isNotBlank(log.getUsername())) {
                queryWrapper.eq("username", log.getUsername());
            }
            if (StrUtil.isNotBlank(log.getRequestType())) {
                queryWrapper.eq("request_type", log.getRequestType());
            }
        }
        return queryWrapper;
    }
    
    /**
     * 移除所有日志
     */
    @Override
    public void removeAllLog() {
        logMapper.deleteAll();
    }

    /**
     * 查询最新的五条日志
     *
     * @return List
     */
    @Override
    public List<Log> findLatestLog(Integer limit) {
        return logMapper.findLatestLog(limit);
    }

    /**
     * 查询最新的日志
     * @param logTypes
     * @param limit
     * @return List
     */
    @Override
    public List<Log> findLatestLogByLogTypes(List<String> logTypes, Integer limit) {
        return logMapper.findLatestLogByLogTypes(logTypes, limit);
    }

    @Override
    public List<Log> findLatestLogByUsername(String username, Integer limit) {
        return logMapper.findLatestLogByUsername(username, limit);
    }

    @Override
    public Log insertOrUpdate(Log entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }

    @Override
    public Integer getTodayCount() {
        return logMapper.getTodayCount();
    }
}
