package com.liuyanzhao.sens.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuyanzhao.sens.entity.MailRetrieve;
import com.liuyanzhao.sens.mapper.MailRetrieveMapper;
import com.liuyanzhao.sens.service.MailRetrieveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <pre>
 *     友情链接业务逻辑实现类
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/14
 */
@Service
public class MailRetrieveServiceImpl implements MailRetrieveService {


    @Autowired
    private MailRetrieveMapper mailRetrieveMapper;

    @Override
    public BaseMapper<MailRetrieve> getRepository() {
        return mailRetrieveMapper;
    }

    @Override
    public QueryWrapper<MailRetrieve> getQueryWrapper(MailRetrieve mailRetrieve) {
        //对指定字段查询
        QueryWrapper<MailRetrieve> queryWrapper = new QueryWrapper<>();
        if (mailRetrieve != null) {
            if (StrUtil.isNotBlank(mailRetrieve.getEmail())) {
                queryWrapper.eq("email", mailRetrieve.getEmail());
            }
            if (StrUtil.isNotBlank(mailRetrieve.getCode())) {
                queryWrapper.eq("code", mailRetrieve.getCode());
            }
        }
        return queryWrapper;
    }


    @Override
    public MailRetrieve findLatestByUserId(Long userId) {
        return mailRetrieveMapper.findLatestByUserId(userId);
    }

    @Override
    public MailRetrieve findLatestByEmail(String email) {
        return mailRetrieveMapper.findLatestByEmail(email);
    }

    @Override
    public MailRetrieve insertOrUpdate(MailRetrieve entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }

}
