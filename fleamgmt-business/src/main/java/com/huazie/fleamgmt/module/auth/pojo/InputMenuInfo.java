package com.huazie.fleamgmt.module.auth.pojo;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p> 菜单信息 业务入参 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class InputMenuInfo implements Serializable {

    private static final long serialVersionUID = 3883742212360452776L;

    private String menuCode;    // 菜单编码

    private String menuName;    // 菜单名称

    private String menuIcon;    // 菜单FontAwesome小图标

    private int hasSubMenu;     // 是否有子菜单（0：没有子菜单 1：有子菜单）

    private String parentId;    // 子菜单的父编号

    private String menuView;    // 菜单对应页面（含有子菜单的可以为空）

    private int moduleType;     // 模块类型（1: 跳蚤管家）

    private String description; // 描述信息

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public int getHasSubMenu() {
        return hasSubMenu;
    }

    public void setHasSubMenu(int hasSubMenu) {
        this.hasSubMenu = hasSubMenu;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getMenuView() {
        return menuView;
    }

    public void setMenuView(String menuView) {
        this.menuView = menuView;
    }

    public int getModuleType() {
        return moduleType;
    }

    public void setModuleType(int moduleType) {
        this.moduleType = moduleType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
