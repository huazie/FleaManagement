package com.huazie.fleamgmt.struts2.base.web;

import com.opensymphony.xwork2.ActionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

/**
 * <p> 页面跳转Action </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class PageAction extends ActionSupport {

    private static final long serialVersionUID = -3626646068576958721L;

    private static final Logger LOGGER = LoggerFactory.getLogger(PageAction.class);

    /**
     * <p> 跳转首页 </p>
     *
     * @return struts2首页跳转字符串
     * @since 1.0.0
     */
    public String home() {
        LOGGER.debug("JumpAction##index() start");
        LOGGER.debug("JumpAction##index() Just For jumping to index.html");
        LOGGER.debug("JumpAction##index() end");
        return "jumpToIndex";
    }
}
