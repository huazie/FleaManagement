package com.huazie.fleamgmt.springmvc.auth.web;

import com.huazie.fleaframework.auth.base.function.entity.FleaMenu;
import com.huazie.fleaframework.auth.common.FleaAuthConstants;
import com.huazie.fleaframework.auth.common.pojo.function.attr.FleaFunctionAttrPOJO;
import com.huazie.fleaframework.auth.common.pojo.function.menu.FleaMenuPOJO;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaFunctionModuleSV;
import com.huazie.fleaframework.auth.util.FleaAuthPOJOUtils;
import com.huazie.fleaframework.auth.util.FleaMenuTree;
import com.huazie.fleaframework.auth.util.FueluxMenuTree;
import com.huazie.fleaframework.common.FleaSessionManager;
import com.huazie.fleaframework.common.IFleaUser;
import com.huazie.fleaframework.common.exceptions.CommonException;
import com.huazie.fleaframework.common.pojo.OutputCommonData;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleaframework.common.util.CollectionUtils;
import com.huazie.fleaframework.common.util.ObjectUtils;
import com.huazie.fleaframework.common.util.POJOUtils;
import com.huazie.fleaframework.common.util.StringUtils;
import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.module.auth.pojo.InputMenuInfo;
import com.huazie.fleamgmt.module.auth.pojo.OutputFunctionInfo;
import com.huazie.fleamgmt.module.auth.pojo.OutputMenuInfo;
import com.huazie.fleamgmt.module.auth.pojo.SystemInfo;
import com.huazie.fleamgmt.springmvc.base.web.BusinessController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 菜单管理 Controller </p>
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
    public OutputMenuInfo tree() throws CommonException {

        OutputMenuInfo output = new OutputMenuInfo();

        List<FleaMenu> menuList = fleaFunctionModuleSV.queryValidMenus(null);
        Map<String, String> params = new HashMap<>();
        params.put(FueluxMenuTree.FOLDER_ICON_CLASS, "red");
        FueluxMenuTree fueluxMenuTree = new FueluxMenuTree("Flea Menu", params);
        fueluxMenuTree.addAll(menuList);
        List<Map<String, Object>> menuTreeMapList = fueluxMenuTree.toMapList(true);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Menu List = {}", menuTreeMapList);
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setMenuList(menuTreeMapList);

        return output;
    }

    /**
     * <p> 菜单明细查询（菜单变更页回填用） </p>
     *
     * @param menuId 菜单编号
     * @return 菜单明细信息
     * @since 1.0.0
     */
    @GetMapping("authMenu!query.flea")
    @ResponseBody
    public OutputFunctionInfo query(@RequestParam("menuId") Long menuId) throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        // 菜单树查询接口不支持按菜单编号过滤，先查有效菜单，再按编号匹配
        List<FleaMenu> menuList = fleaFunctionModuleSV.queryValidMenus(null);
        FleaMenu currentMenu = getMenuById(menuList, menuId);

        if (ObjectUtils.isEmpty(currentMenu)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，菜单【" + menuId + "】不存在或已失效，请刷新菜单树后重试！");
            return output;
        }

        Map<String, Object> menuMap = new HashMap<>();
        menuMap.put("menuId", currentMenu.getMenuId());
        menuMap.put("menuCode", currentMenu.getMenuCode());
        menuMap.put("menuName", currentMenu.getMenuName());
        menuMap.put("menuIcon", currentMenu.getMenuIcon());
        menuMap.put("menuView", currentMenu.getMenuView());
        menuMap.put("menuLevel", currentMenu.getMenuLevel());
        menuMap.put("menuSort", currentMenu.getMenuSort());
        menuMap.put("remarks", currentMenu.getRemarks());

        // 父菜单信息仅用于变更页展示，不允许调整菜单归属
        FleaMenu parentMenu = getMenuById(menuList, currentMenu.getParentId());
        if (ObjectUtils.isNotEmpty(parentMenu)) {
            menuMap.put("parentName", parentMenu.getMenuName());
            menuMap.put("parentCode", parentMenu.getMenuCode());
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setData(menuMap);

        return output;
    }

    /**
     * <p> 菜单变更 </p>
     *
     * @param inputMenuInfo 菜单业务入参
     * @return 菜单变更结果
     * @since 1.0.0
     */
    @PostMapping("authMenu!update.flea")
    @ResponseBody
    public OutputCommonData menuUpdate(InputMenuInfo inputMenuInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        FleaMenuPOJO fleaMenuPOJO = new FleaMenuPOJO();
        POJOUtils.copyAll(inputMenuInfo, fleaMenuPOJO);

        fleaFunctionModuleSV.modifyFleaMenu(inputMenuInfo.getMenuId(), fleaMenuPOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，菜单修改成功！");
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

        List<Map<String, Object>> searchMenuMapList = null;

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
                    String mIcon = fleaMenu.getMenuIcon();
                    if (StringUtils.isNotBlank(mName) && StringUtils.isFuzzySearch(mName, menuName)) {
                        Map<String, Object> menuMap = new HashMap<>();
                        menuMap.put("MENU_CODE", mCode);
                        menuMap.put("MENU_NAME", mName);
                        menuMap.put("MENU_ICON", mIcon);
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

        return output;
    }

    @PostMapping("authMenu!add.flea")
    @ResponseBody
    public OutputCommonData menuAdd(InputMenuInfo inputMenuInfo) throws CommonException {
        OutputCommonData output = new OutputCommonData();

        FleaMenuPOJO fleaMenuPOJO = new FleaMenuPOJO();
        POJOUtils.copyAll(inputMenuInfo, fleaMenuPOJO);

        List<FleaFunctionAttrPOJO> functionAttrPOJOList = new ArrayList<>();
        // 添加在用的系统菜单属性
        List<SystemInfo> systemsInUse = inputMenuInfo.getSystemsInUse();
        // 前端传了在用的系统菜单属性，则按前端的新增
        if (CollectionUtils.isNotEmpty(systemsInUse)) {
            for (SystemInfo systemInfo : systemsInUse) {
                if (ObjectUtils.isEmpty(systemInfo)) continue;
                functionAttrPOJOList.add(FleaAuthPOJOUtils.newSystemInUseAttr(systemInfo.getSystemId(), systemInfo.getSystemName()));
            }
        } else {
            // 默认当前系统
            IFleaUser fleaUser = FleaSessionManager.getUserInfo();
            if (ObjectUtils.isNotEmpty(fleaUser)) {
                String systemId = StringUtils.valueOf(fleaUser.getSystemAccountId());
                String systemName = fleaUser.get(FleaAuthConstants.UserModuleConstants.SYSTEM_USER_NAME, String.class);
                functionAttrPOJOList.add(FleaAuthPOJOUtils.newSystemInUseAttr(systemId, systemName));
            }
        }

        fleaMenuPOJO.setFunctionAttrPOJOList(functionAttrPOJOList);

        Long menuId = fleaFunctionModuleSV.addFleaMenu(fleaMenuPOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，菜单添加成功，菜单编号：" + menuId);
        return output;
    }

    /**
     * <p> 从菜单数据集中按菜单编号查询菜单 </p>
     *
     * @param menuList 菜单数据集
     * @param menuId   菜单编号
     * @return 匹配到的菜单，未匹配到返回 {@code null}
     * @since 1.0.0
     */
    private FleaMenu getMenuById(List<FleaMenu> menuList, Long menuId) {

        if (ObjectUtils.isEmpty(menuId) || CollectionUtils.isEmpty(menuList)) {
            return null;
        }

        for (FleaMenu fleaMenu : menuList) {
            if (ObjectUtils.isNotEmpty(fleaMenu) && menuId.equals(fleaMenu.getMenuId())) {
                return fleaMenu;
            }
        }

        return null;
    }
}
