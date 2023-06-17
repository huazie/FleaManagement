package com.huazie.fleamgmt.struts2.auth.web;

import com.huazie.fleaframework.auth.base.function.service.interfaces.IFleaMenuSV;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleamgmt.module.auth.pojo.InputMenuInfo;
import com.huazie.fleamgmt.module.auth.pojo.OutputMenuInfo;
import com.opensymphony.xwork2.ActionSupport;
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

    private static final FleaLogger LOGGER = FleaLoggerProxy.getProxyInstance(MenumgmtAction.class);

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
            LOGGER.debug("Start");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("End");
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
            LOGGER.debug("Start");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("End");
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
            LOGGER.debug("Start");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("End");
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
            LOGGER.debug("Start");
        }


        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("End");
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
            LOGGER.debug("Start");
        }


        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("End");
        }
        return "json";
    }

}
