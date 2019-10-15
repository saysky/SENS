package com.liuyanzhao.sens.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.liuyanzhao.sens.common.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <pre>
 *     菜单
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/24
 */
@Data
@TableName("sens_menu")
public class Menu  extends BaseEntity {

    /**
     * 菜单Pid
     */
    private Long menuPid = 0L;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单路径
     */
    private String menuUrl;

    /**
     * 排序编号
     */
    private Integer menuSort = 1;

    /**
     * 图标，可选，部分主题可显示
     */
    private String menuIcon;

    /**
     * 打开方式
     */
    private String menuTarget;

    /**
     * 菜单类型(0前台主要菜单，1前台顶部菜单)
     */
    private Integer menuType;

    /**
     * 菜单层级
     */
    @TableField(exist = false)
    private Integer level;

    /**
     * 子菜单列表
     */
    @TableField(exist = false)
    private List<Menu> childMenus;
}
