package com.huazie.fleamgmt.springmvc.base.web;

import com.huazie.frame.auth.base.function.entity.FleaMenu;
import com.huazie.frame.auth.util.FleaMenuTree;
import com.huazie.frame.common.FleaSessionManager;
import com.huazie.frame.common.IFleaUser;
import com.huazie.frame.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(PageController.class);

    /**
     * <p> 跳转首页 </p>
     *
     * @return 首页字符串
     * @since 1.0.0
     */
    @RequestMapping("/home")
    public String index() {
        LOGGER.debug("Start");
        LOGGER.debug("Just For jumping to index.html");
        LOGGER.debug("End");
        return "index.html";
    }

    /**
     * <p> 跳转菜单 </p>
     *
     * @return 指定菜单页面
     * @since 1.0.0
     */
    @RequestMapping("/menu")
    public String menu(@RequestParam("code") String menuCode) {
        LOGGER.debug("Start");
        LOGGER.debug("Just For jumping to menu, MenuCode = {}", menuCode);

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

        LOGGER.debug("Just For jumping to menu, MenuView = {} ", menuView);
        LOGGER.debug("End");
        return menuView;
    }
}
