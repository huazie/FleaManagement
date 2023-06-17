package com.huazie.fleamgmt.struts2.home.web;

import com.huazie.fleaframework.core.base.cfgdata.service.interfaces.IFleaMenuFavoritesSV;
import com.huazie.fleamgmt.module.home.pojo.OutputMenuFavorites;
import com.opensymphony.xwork2.ActionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * <p> 菜单收藏Action </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class MenuFavoritesAction extends ActionSupport {

    private static final long serialVersionUID = 4383067849995484060L;

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuFavoritesAction.class);

    @Resource(name = "fleaMenuFavoritesSV")
    private IFleaMenuFavoritesSV fleaMenuFavoritesSV;

    private String menuCode;    // 菜单编码

    private OutputMenuFavorites menuFavorite = new OutputMenuFavorites(); // 菜单收藏夹

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public OutputMenuFavorites getMenuFavorite() {
        return menuFavorite;
    }

    public void setMenuFavorite(OutputMenuFavorites menuFavorite) {
        this.menuFavorite = menuFavorite;
    }

    /**
     * <p> 收藏菜单 </p>
     *
     * @return
     * @throws Exception
     */
    public String collect() throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Start");
        }


        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("End");
        }
        return "json";
    }

    /**
     * <p> 取消收藏菜单 </p>
     *
     * @return
     * @throws Exception
     */
    public String cancel() throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Start");
        }


        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("End");
        }
        return "json";
    }

    /**
     * <p> 获取登录用户收藏的菜单 </p>
     *
     * @return
     * @throws Exception
     */
    public String find() throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Start");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("End");
        }
        return "json";
    }

    /**
     * <p> 当前菜单是否已收藏 </p>
     *
     * @return
     * @throws Exception
     */
    public String isFavorite() throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Start");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("End");
        }
        return "json";
    }

}
