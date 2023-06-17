package com.huazie.fleamgmt.module.auth.pojo;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p> 菜单信息 业务入参 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class InputMenuInfo implements Serializable {

    private static final long serialVersionUID = -5922687659962830797L;

    private Long menuId; // 菜单编号

    private String menuCode; // 菜单编码

    private String menuName; // 菜单名称

    private String menuIcon; // 菜单FontAwesome小图标

    private Integer menuSort; // 菜单展示顺序(同一个父菜单下)

    private String menuView; // 菜单对应页面（非叶子菜单的可以为空）

    private Integer menuLevel; // 菜单层级（1：一级菜单 2；二级菜单 3：三级菜单 4：四级菜单）

    private Long parentId; // 父菜单编号

    private String remarks; // 备注

    private List<SystemInfo> systemsInUse; // 在用的系统列表

    private List<Map<String, String>> menuAttrs; // 菜单属性

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

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

    public Integer getMenuSort() {
        return menuSort;
    }

    public void setMenuSort(Integer menuSort) {
        this.menuSort = menuSort;
    }

    public String getMenuView() {
        return menuView;
    }

    public void setMenuView(String menuView) {
        this.menuView = menuView;
    }

    public Integer getMenuLevel() {
        return menuLevel;
    }

    public void setMenuLevel(Integer menuLevel) {
        this.menuLevel = menuLevel;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<SystemInfo> getSystemsInUse() {
        return systemsInUse;
    }

    public void setSystemsInUse(List<SystemInfo> systemsInUse) {
        this.systemsInUse = systemsInUse;
    }

    public List<Map<String, String>> getMenuAttrs() {
        return menuAttrs;
    }

    public void setMenuAttrs(List<Map<String, String>> menuAttrs) {
        this.menuAttrs = menuAttrs;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
