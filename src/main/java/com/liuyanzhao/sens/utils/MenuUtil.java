package com.liuyanzhao.sens.utils;

import com.liuyanzhao.sens.entity.Menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 *     拼装菜单，
 * </pre>
 *
 * @author : saysky
 * @date : 2018/7/12
 */
public class MenuUtil {

    /**
     * 获取组装好的菜单
     * 以树的形式显示
     *
     * @param menusRoot menusRoot
     * @return List
     */
    public static List<Menu> getMenuTree(List<Menu> menusRoot) {
        List<Menu> menusResult = new ArrayList<>();

        for (Menu menu : menusRoot) {
            if (menu.getMenuPid() == 0) {
                menusResult.add(menu);
            }
        }

        for (Menu menu : menusResult) {
            menu.setChildMenus(getChildTree(menu.getId(), menusRoot));
        }
        return menusResult;
    }

    /**
     * 获取菜单的子菜单
     *
     * @param id        菜单编号
     * @param menusRoot menusRoot
     * @return List
     */
    private static List<Menu> getChildTree(Long id, List<Menu> menusRoot) {
        List<Menu> menusChild = new ArrayList<>();
        for (Menu menu : menusRoot) {
            if (menu.getMenuPid() != 0) {
                if (menu.getMenuPid().equals(id)) {
                    menusChild.add(menu);
                }
            }
        }
        for (Menu menu : menusChild) {
            if (menu.getMenuPid() != 0) {
                menu.setChildMenus(getChildTree(menu.getId(), menusRoot));
            }
        }
        if (menusChild.size() == 0) {
            return null;
        }
        return menusChild;
    }


    /**
     * 获取组装好的菜单,
     *
     * @param menusRoot menusRoot
     * @return List
     */
    public static List<Menu> getMenuList(List<Menu> menusRoot) {
        List<Menu> menusResult = new ArrayList<>();

        for (Menu menu : menusRoot) {
            if (menu.getMenuPid() == 0) {
                menu.setLevel(1);
                menusResult.add(menu);
                menusResult.addAll(getChildList(menu, menusRoot));
            }
        }
        return menusResult;
    }

    /**
     * 获取菜单的子菜单
     *
     * @param parentMenu 父级菜单
     * @param menusRoot  menusRoot
     * @return List
     */
    private static List<Menu> getChildList(Menu parentMenu, List<Menu> menusRoot) {
        List<Menu> menusChild = new ArrayList<>();
        for (Menu menu : menusRoot) {
            if (menu.getMenuPid() != 0) {
                if (menu.getMenuPid().equals(parentMenu.getId())) {
                    menu.setLevel(parentMenu.getLevel() + 1);
                    menusChild.add(menu);
                    List<Menu> tempList = getChildList(menu, menusRoot);
                    tempList.sort((a, b) -> b.getMenuSort() - a.getMenuSort());
                    menusChild.addAll(tempList);
                }
            }
        }
        if (menusChild.size() == 0) {
            return Collections.emptyList();
        }
        return menusChild;
    }

}
