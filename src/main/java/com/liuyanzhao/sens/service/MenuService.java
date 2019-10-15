package com.liuyanzhao.sens.service;

import com.liuyanzhao.sens.common.base.BaseService;
import com.liuyanzhao.sens.entity.Menu;

import java.util.List;

/**
 * <pre>
 *     菜单业务逻辑接口
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/24
 */
public interface MenuService extends BaseService<Menu, Long> {

    /**
     * 根据菜单Pid获得菜单
     *
     * @return List
     */
    List<Menu> findByMenuPid(Long id);


    /**
     * 根据类型查询，以树形展示，用于前台
     *
     * @param menuType 菜单类型
     * @return List
     */
    List<Menu> findMenuTree(Integer menuType);

    /**
     * 根据类型查询，以列表显示展示，用于后台管理
     *
     * @param menuType 菜单类型
     * @return 菜单
     */
    List<Menu> findMenuListWithLevel(Integer menuType);

}
