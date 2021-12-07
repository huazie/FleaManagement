package com.huazie.fleamgmt.module.home.pojo;

import com.huazie.fleaframework.common.pojo.OutputCommonData;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;

/**
 * <p> 菜单收藏信息 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class OutputMenuFavorites extends OutputCommonData {

    private static final long serialVersionUID = -155295652934357520L;

    private List<Map<String, Object>> menuFavoritesList;    // 菜单收藏夹信息集合

    public List<Map<String, Object>> getMenuFavoritesList() {
        return menuFavoritesList;
    }

    public void setMenuFavoritesList(List<Map<String, Object>> menuFavoritesList) {
        this.menuFavoritesList = menuFavoritesList;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
