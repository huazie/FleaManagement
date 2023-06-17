package com.huazie.fleamgmt.springmvc.base.web;

import com.huazie.fleaframework.auth.base.function.entity.FleaMenu;
import com.huazie.fleaframework.auth.util.FleaMenuTree;
import com.huazie.fleaframework.common.FleaSessionManager;
import com.huazie.fleaframework.common.IFleaUser;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleaframework.common.util.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p> 页面跳转Controller </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
@RequestMapping("/page")
public class PageController {

    private static final FleaLogger LOGGER = FleaLoggerProxy.getProxyInstance(PageController.class);

    /**
     * 跳转首页
     *
     * @return 首页
     * @since 1.0.0
     */
    @RequestMapping("/home")
    public String index() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Just For jumping to index.html");
        }
        return "index.html";
    }

    /**
     * 根据菜单编码，跳转指定菜单
     * <p> 如果菜单编码对应的菜单不在当前用户所授权的叶子菜单之中，
     * 则默认返回 404的错误页面
     *
     * @return 指定菜单页面
     * @since 1.0.0
     */
    @RequestMapping("/menu")
    public String menu(@RequestParam("code") String menuCode) {

        String menuView = "";

        IFleaUser fleaUser = FleaSessionManager.getUserInfo();
        if (null != fleaUser) {
            FleaMenuTree fleaMenuTree = fleaUser.get(FleaMenuTree.MENU_TREE, FleaMenuTree.class);
            FleaMenu fleaMenu = fleaMenuTree.getTreeLeafMenu(menuCode);
            if (null != fleaMenu) {
                menuView = fleaMenu.getMenuView(); // 获取菜单页面地址
            }
        }

        if (StringUtils.isBlank(menuView)) {
            menuView = "/WEB-INF/error-404.html";
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Just For jumping to menu, MenuCode = {}, MenuView = {}", menuCode, menuView);
        }
        return menuView;
    }
}
