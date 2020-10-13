package com.huazie.fleamgmt.springmvc.auth.web;

import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.module.auth.pojo.OutputMenuInfo;
import com.huazie.fleamgmt.springmvc.base.web.BusinessController;
import com.huazie.frame.auth.base.function.entity.FleaMenu;
import com.huazie.frame.auth.common.service.interfaces.IFleaFunctionModuleSV;
import com.huazie.frame.auth.util.FleaMenuTree;
import com.huazie.frame.auth.util.FueluxMenuTree;
import com.huazie.frame.common.FleaSessionManager;
import com.huazie.frame.common.IFleaUser;
import com.huazie.frame.common.exception.CommonException;
import com.huazie.frame.common.util.CollectionUtils;
import com.huazie.frame.common.util.ObjectUtils;
import com.huazie.frame.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
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

    private IFleaFunctionModuleSV fleaFunctionModuleSV;

    @Resource(name = "fleaFunctionModuleSV")
    public void setFleaFunctionModuleSV(IFleaFunctionModuleSV fleaFunctionModuleSV) {
        this.fleaFunctionModuleSV = fleaFunctionModuleSV;
    }

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

        try {
            List<FleaMenu> menuList = fleaFunctionModuleSV.queryValidMenus(null);
            Map<String, String> params = new HashMap<>();
            params.put(FueluxMenuTree.FOLDER_ICON_CLASS, "red");
            FueluxMenuTree fueluxMenuTree = new FueluxMenuTree("FleaFrameAuth", params);
            fueluxMenuTree.addAll(menuList);
            menuTreeMapList = fueluxMenuTree.toMapList();
        } catch (CommonException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("【Spring】菜单树获取异常：\n", e);
            }
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("菜单树获取异常：" + e.getMessage());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenumgmtController##tree() Menu List = {}", menuTreeMapList);
            LOGGER.debug("MenumgmtController##tree() end");
        }

        output.setMenuList(menuTreeMapList);

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
            menuName = new String(menuName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
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
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("【Spring】菜单搜索出现异常：\n", e);
            }
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("菜单搜索出现异常：" + e.getMessage());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenumgmtController##search(String) Menu List = {}", searchMenuMapList);
            LOGGER.debug("MenumgmtController##search(String) end");
        }
        return output;
    }
}
