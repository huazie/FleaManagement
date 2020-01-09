package com.huazie.fleamgmt.struts2.auth.web;

import com.huazie.fleamgmt.constant.FleaMgmtConstants;
import com.huazie.fleamgmt.module.auth.pojo.InputMenuInfo;
import com.huazie.fleamgmt.module.auth.pojo.OutputMenuInfo;
import com.huazie.frame.auth.base.function.entity.FleaMenu;
import com.huazie.frame.auth.base.function.service.interfaces.IFleaMenuSV;
import com.huazie.frame.auth.base.user.entity.FleaAccount;
import com.huazie.frame.common.util.ObjectUtils;
import com.huazie.frame.common.util.StringUtils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p> 菜单管理Action </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class MenuManagementAction extends ActionSupport {

    private static final long serialVersionUID = 5773270988602898517L;

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuManagementAction.class);

    @Resource(name = "fleaMenuSV")
    private IFleaMenuSV fleaMenuSV;

    private InputMenuInfo menu;

    private OutputMenuInfo result = new OutputMenuInfo();

    public void setMenu(InputMenuInfo menu) {
        this.menu = menu;
    }

    public InputMenuInfo getMenu() {
        return menu;
    }

    public OutputMenuInfo getResult() {
        return result;
    }

    public void setResult(OutputMenuInfo result) {
        this.result = result;
    }

    /**
     * <p> 添加菜单 </p>
     *
     * @return 默认以json格式返回数据
     * @throws Exception
     * @since 1.0.0
     */
    public String add() throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenuManagementAction##add() start");
        }

        if (menu != null) {
            String menuCode = menu.getMenuCode();
            String menuName = menu.getMenuName();
            String menuIcon = menu.getMenuIcon();
            int hasSubMenu = menu.getHasSubMenu();
            String menuView = menu.getMenuView();
            String parentId = menu.getParentId();
            int moduleType = menu.getModuleType();
            String description = menu.getDescription();

            FleaMenu parentMenu = fleaMenuSV.query(parentId);

            int menuLevel = 1;
            if (ObjectUtils.isNotEmpty(parentMenu)) {
                menuLevel = parentMenu.getMenuLevel() + 1;
            }

//            List<FleaMenu> subMenuList = fleaMenuSV.getSubLevelMenu(parentId);
//            int menuSort = 1;
//            if (subMenuList != null) {
//                menuSort = subMenuList.size() + 1;// 获取新增菜单在父菜单下的序号
//            }
//
//            FleaMenu newMenu = fleaMenuSV.saveMenu(menuCode, menuName, menuIcon, menuSort, hasSubMenu, parentId, menuView, menuLevel, moduleType, description);
//
//            if (newMenu.getMenuId() > 0) {
//                result.setRetCode(FleaMgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
//                result.setRetMess("保存成功");
//            } else {
//                result.setRetCode(FleaMgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
//                result.setRetMess("保存失败,菜单编码为" + menuCode + "");
//            }

        } else {
            result.setRetCode(FleaMgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            result.setRetMess("请求新增的菜单数据为空");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenuManagementAction##add() end");
        }
        return "json";
    }

    /**
     * <p> 变更菜单 </p>
     *
     * @return 默认以json格式返回数据
     * @throws Exception
     * @since 1.0.0
     */
    public String update() throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenuManagementAction##update() start");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenuManagementAction##update() end");
        }
        return "json";
    }

    /**
     * <p> 菜单下线 </p>
     *
     * @return 默认以json格式返回数据
     * @throws Exception
     * @since 1.0.0
     */
    public String remove() throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenuManagementAction##remove() start");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenuManagementAction##remove() end");
        }
        return "json";
    }

    /**
     * <p> 获取树菜单 </p>
     *
     * @return 默认以json格式返回数据
     * @throws Exception
     * @since 1.0.0
     */
    public String treeMenus() throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenuManagementAction##remove() start");
        }

        ActionContext aContext = ActionContext.getContext();
        FleaAccount account = (FleaAccount) aContext.getSession().get(FleaMgmtConstants.SessionConstants.SESSION_FLEAER_ACCOUNT);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenuManagementAction##getTreeMenus() Fleaer Account={}", account);
        }

        if (account != null) {
//            List<Map<String, Object>> menuMapList = fleaMenuSV.getAllLevelTreeMenu(account.getAccountId());
//
//            result.setMenuList(menuMapList);
//            result.setRetCode(FleaMgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
//            result.setRetMess("菜单树加载成功");

        } else {
            result.setRetCode(FleaMgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            result.setRetMess("系统登录用户信息异常，请刷新重新登录");
        }


        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenuManagementAction##remove() end");
        }
        return "json";
    }

    /**
     * <p> 菜单搜索 </p>
     *
     * @return 默认以json格式返回数据
     * @throws Exception
     * @since 1.0.0
     */
    public String search() throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenuManagementAction##search() start");
        }

        ActionContext aContext = ActionContext.getContext();
        FleaAccount account = (FleaAccount) aContext.getSession().get(FleaMgmtConstants.SessionConstants.SESSION_FLEAER_ACCOUNT);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenuManagementAction##search() Fleaer Account={}", account);
        }

        if (account != null) {
            if (this.menu != null) {
                String menuName = new String(menu.getMenuName().getBytes("ISO-8859-1"), "UTF-8");//获取菜单名

//                List<Map<String, Object>> menuMapList = fleaMenuSV.getAccessLeafMenus(account.getAccountId());
//
//                List<Map<String, Object>> searchMenuMapList = new ArrayList<Map<String, Object>>();
//
//                if (menuMapList != null && !menuMapList.isEmpty()) {
//                    for (Map<String, Object> menuMap : menuMapList) {
//                        Object mName = menuMap.get("MENU_NAME");
//                        Object mCode = menuMap.get("MENU_CODE");
//                        if (mName == null || mCode == null) {
//                            continue;
//                        }
//                        if (StringUtils.isFuzzySearch(mName.toString(), menuName)) {//满足模糊搜索，简拼，全拼搜索
//                            searchMenuMapList.add(menuMap);
//                        }
//                    }
//                }
//
//                result.setMenuList(searchMenuMapList);
//                result.setRetCode(FleaMgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
//                result.setRetMess("已查找到" + searchMenuMapList.size() + "条数据");

            } else {
                result.setRetCode(FleaMgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
                result.setRetMess("请求查询的菜单信息为空");
            }

        } else {
            result.setRetCode(FleaMgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            result.setRetMess("系统登录用户信息异常，请刷新重新登录");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenuManagementAction##search() end");
        }
        return "json";
    }

}
