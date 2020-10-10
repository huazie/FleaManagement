package com.huazie.fleamgmt.util;

import com.huazie.frame.auth.base.function.entity.FleaMenu;
import com.huazie.frame.auth.util.FleaMenuTree;
import com.huazie.frame.common.util.StringUtils;

import java.util.Comparator;
import java.util.Map;

/**
 * <p> Fuelux菜单树 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class FueluxMenuTree extends FleaMenuTree {

    public FueluxMenuTree(String systemName) {
        super(systemName);
    }

    public FueluxMenuTree(String systemName, Comparator<? super FleaMenu> comparator) {
        super(systemName, comparator);
    }

    @Override
    protected String getMapKeyForSubNotes() {
        return "additionalParameters";
    }

    @Override
    protected void reHandleTreeNodeMap(Map<String, Object> treeNodeMap) {
        String name = StringUtils.valueOf(treeNodeMap.get("MENU_NAME"));
        String code = StringUtils.valueOf(treeNodeMap.get("MENU_CODE"));

    }
}
