package com.huazie.fleamgmt.springmvc.auth.web;

import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.module.auth.pojo.OutputMenuInfo;
import com.huazie.fleamgmt.springmvc.base.web.BusinessController;
import com.huazie.frame.auth.base.function.entity.FleaMenu;
import com.huazie.frame.auth.util.FleaMenuTree;
import com.huazie.frame.common.FleaSessionManager;
import com.huazie.frame.common.IFleaUser;
import com.huazie.frame.common.util.CollectionUtils;
import com.huazie.frame.common.util.ObjectUtils;
import com.huazie.frame.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 菜单管理Controller </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class MenumgmtController extends BusinessController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenumgmtController.class);

    /**
     * <p> 展示菜单树 </p>
     *
     * @return 菜单树信息
     * @since 1.0.0
     */
    @RequestMapping("authMenu!tree.flea")
    @ResponseBody
    public OutputMenuInfo tree() {

        OutputMenuInfo output = new OutputMenuInfo();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenumgmtController##tree() start");
        }

        List<Map<String, Object>> menuTreeMapList = null;

        if (LOGGER.isDebugEnabled()) {
             LOGGER.debug("MenumgmtController##tree() Menu List = {}", menuTreeMapList);
            LOGGER.debug("MenumgmtController##tree() end");
        }
        return output;
    }

    /**
     * <p> 菜单搜索 </p>
     *
     * @return 菜单信息
     * @since 1.0.0
     */
    @RequestMapping("authMenu!search.flea")
    @ResponseBody
    public OutputMenuInfo search(@RequestParam("menu.menuName") String menuName) {

        OutputMenuInfo output = new OutputMenuInfo();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenumgmtController##search(String) start");
        }

        List<Map<String, Object>> searchMenuMapList = null;

        try {
            // 获取菜单名
            menuName = new String(menuName.getBytes("ISO-8859-1"), "UTF-8");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("MenumgmtController##search(String) MenuName = {}", menuName);
            }

            IFleaUser fleaUser = FleaSessionManager.getUserInfo();
            if (ObjectUtils.isNotEmpty(fleaUser)) {
                FleaMenuTree fleaMenuTree = fleaUser.get(FleaMenuTree.MENU_TREE, FleaMenuTree.class);
                List<FleaMenu> leafMenuList = fleaMenuTree.getAllTreeLeafElement();
                if (CollectionUtils.isNotEmpty(leafMenuList)) {
                    searchMenuMapList = new ArrayList<>();
                    for (FleaMenu fleaMenu : leafMenuList) {
                        String mCode = fleaMenu.getMenuCode();
                        String mName = fleaMenu.getMenuName();
                        if (StringUtils.isNotBlank(mName) && StringUtils.isFuzzySearch(mName, menuName)) {
                            Map<String, Object> menuMap = new HashMap<>();
                            menuMap.put("MENU_CODE", mCode);
                            menuMap.put("MENU_NAME", mName);
                            searchMenuMapList.add(menuMap);
                        }
                    }
                }
                output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
                output.setRetMess("SUCCESS");
                output.setMenuList(searchMenuMapList);
            } else {
                output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
                output.setRetMess("用户登录异常");
            }

        } catch (Exception e) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess(e.getMessage());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenumgmtController##search(String) Menu List = {}", searchMenuMapList);
            LOGGER.debug("MenumgmtController##search(String) end");
        }
        return output;
    }
}
