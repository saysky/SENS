package com.liuyanzhao.sens.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuyanzhao.sens.common.constant.RedisKeyExpire;
import com.liuyanzhao.sens.common.constant.RedisKeys;
import com.liuyanzhao.sens.entity.Menu;
import com.liuyanzhao.sens.entity.Permission;
import com.liuyanzhao.sens.mapper.MenuMapper;
import com.liuyanzhao.sens.mapper.PermissionMapper;
import com.liuyanzhao.sens.model.enums.LanguageTypeEnum;
import com.liuyanzhao.sens.service.MenuService;
import com.liuyanzhao.sens.utils.MenuUtil;
import com.liuyanzhao.sens.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <pre>
 *     菜单业务逻辑实现类
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/24
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<Menu> findByMenuPid(Long id) {
        Map<String, Object> condition = new HashMap<>(1);
        condition.put("menu_pid", id);
        return menuMapper.selectByMap(condition);
    }

    @Override
    public List<Menu> findMenuTree(Integer menuType) {
        String value = redisUtil.get(RedisKeys.FRONT_MENU + menuType);
        // 先从缓存取，缓存没有从数据库取
        if (StringUtils.isNotEmpty(value)) {
            return JSON.parseArray(value, Menu.class);
        }
        Map<String, Object> condition = new HashMap<>(1);
        condition.put("menu_type", menuType);
        List<Menu> menuList = menuMapper.selectByMap(condition);
        //以层级(树)关系显示
        List<Menu> menuTree = MenuUtil.getMenuTree(menuList);
        redisUtil.set(RedisKeys.FRONT_MENU + menuType, JSON.toJSONString(menuTree), RedisKeyExpire.FRONT_MENU);
        return menuTree;
    }

    @Override
    public List<Menu> findMenuListWithLevel(Integer menuType) {
        Map<String, Object> condition = new HashMap<>(1);
        condition.put("menu_type", menuType);
        List<Menu> menuList = menuMapper.selectByMap(condition);
        return MenuUtil.getMenuList(menuList);
    }


    @Override
    public BaseMapper<Menu> getRepository() {
        return menuMapper;
    }

    @Override
    public Menu insert(Menu menu) {
        menuMapper.insert(menu);
        return menu;
    }

    @Override
    public Menu update(Menu menu) {
        menuMapper.updateById(menu);
        return menu;
    }


    @Override
    public QueryWrapper<Menu> getQueryWrapper(Menu menu) {
        //对指定字段查询
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        if (menu != null) {
            if (StrUtil.isNotBlank(menu.getMenuName())) {
                queryWrapper.like("menu_title", menu.getMenuName());
            }
            if (StrUtil.isNotBlank(menu.getMenuUrl())) {
                queryWrapper.like("menu_content", menu.getMenuUrl());
            }
            if (menu.getMenuType() != null) {
                queryWrapper.eq("menu_type", menu.getMenuType());
            }
            if (menu.getMenuPid() != null) {
                queryWrapper.eq("menu_pid", menu.getMenuPid());
            }
            if (menu.getMenuTarget() != null) {
                queryWrapper.eq("menu_target", menu.getMenuTarget());
            }
        }
        return queryWrapper;
    }

    @Override
    public Menu insertOrUpdate(Menu menu) {
        if (menu.getId() == null) {
            insert(menu);
        } else {
            update(menu);
        }
        //删除缓存
        redisUtil.del(RedisKeys.FRONT_MENU + menu.getMenuType());
        return menu;
    }

    @Override
    public void delete(Long id) {
        Menu menu = menuMapper.selectById(id);
        if (menu != null) {
            menuMapper.deleteById(id);
            //删除缓存
            redisUtil.del(RedisKeys.FRONT_MENU + menu.getMenuType());
        }
    }
}