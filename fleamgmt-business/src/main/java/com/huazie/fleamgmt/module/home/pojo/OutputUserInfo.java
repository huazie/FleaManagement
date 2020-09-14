package com.huazie.fleamgmt.module.home.pojo;

import com.huazie.frame.common.pojo.OutputCommonData;

import java.util.List;
import java.util.Map;

/**
 * <p> 用户信息 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class OutputUserInfo extends OutputCommonData {

    private static final long serialVersionUID = 6562951839192098570L;

    private Map<String, Object> userInfo; // 用户信息Map集合

    private List<Map<String,Object>> menuList; // 菜单列表

    public Map<String, Object> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Map<String, Object> userInfo) {
        this.userInfo = userInfo;
    }

    public List<Map<String, Object>> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Map<String, Object>> menuList) {
        this.menuList = menuList;
    }
}
