package com.huazie.fleamgmt.struts2.auth.web;

import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.module.auth.pojo.InputMenuInfo;
import com.huazie.fleamgmt.module.auth.pojo.OutputMenuInfo;
import com.huazie.frame.auth.base.function.entity.FleaMenu;
import com.huazie.frame.auth.base.function.service.interfaces.IFleaMenuSV;
import com.huazie.frame.common.util.ObjectUtils;
import com.opensymphony.xwork2.ActionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * <p> 菜单管理Action </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class MenumgmtAction extends ActionSupport {

    private static final long serialVersionUID = 5773270988602898517L;

    private static final Logger LOGGER = LoggerFactory.getLogger(MenumgmtAction.class);

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

        } else {
            result.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
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


        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("MenuManagementAction##search() end");
        }
        return "json";
    }

}
