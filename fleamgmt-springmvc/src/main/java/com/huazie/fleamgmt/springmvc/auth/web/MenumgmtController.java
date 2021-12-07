package com.huazie.fleamgmt.springmvc.auth.web;

import com.huazie.fleaframework.auth.base.function.entity.FleaMenu;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaFunctionModuleSV;
import com.huazie.fleaframework.auth.util.FleaMenuTree;
import com.huazie.fleaframework.auth.util.FueluxMenuTree;
import com.huazie.fleaframework.common.FleaSessionManager;
import com.huazie.fleaframework.common.IFleaUser;
import com.huazie.fleaframework.common.exception.CommonException;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleaframework.common.util.CollectionUtils;
import com.huazie.fleaframework.common.util.ObjectUtils;
import com.huazie.fleaframework.common.util.StringUtils;
import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.module.auth.pojo.OutputMenuInfo;
import com.huazie.fleamgmt.springmvc.base.web.BusinessController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    private static final FleaLogger LOGGER = FleaLoggerProxy.getProxyInstance(MenumgmtController.class);

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
    @GetMapping("authMenu!tree.flea")
    @ResponseBody
    public OutputMenuInfo tree() {

        OutputMenuInfo output = new OutputMenuInfo();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Start");
        }

        List<Map<String, Object>> menuTreeMapList = null;

        try {
            List<FleaMenu> menuList = fleaFunctionModuleSV.queryValidMenus(null);
            Map<String, String> params = new HashMap<>();
            params.put(FueluxMenuTree.FOLDER_ICON_CLASS, "red");
            FueluxMenuTree fueluxMenuTree = new FueluxMenuTree("Flea Menu", params);
            fueluxMenuTree.addAll(menuList);
            menuTreeMapList = fueluxMenuTree.toMapList(true);
        } catch (CommonException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("【Spring】菜单树获取异常：\n", e);
            }
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("菜单树获取异常：" + e.getMessage());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Menu List = {}", menuTreeMapList);
            LOGGER.debug("End");
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
    @GetMapping("authMenu!search.flea")
    @ResponseBody
    public OutputMenuInfo search(@RequestParam("menu.menuName") String menuName) {

        OutputMenuInfo output = new OutputMenuInfo();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Start");
        }

        List<Map<String, Object>> searchMenuMapList = null;

        try {
            // 获取菜单名
            menuName = new String(menuName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("MenuName = {}", menuName);
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
            LOGGER.debug("Menu List = {}", searchMenuMapList);
            LOGGER.debug("End");
        }
        return output;
    }
}
