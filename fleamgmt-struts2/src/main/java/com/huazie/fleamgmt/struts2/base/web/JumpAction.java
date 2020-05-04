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
public class JumpAction extends ActionSupport {

    private static final long serialVersionUID = -3626646068576958721L;

    private static final Logger LOGGER = LoggerFactory.getLogger(JumpAction.class);

    /**
     * <p> 跳转首页 </p>
     *
     * @return struts2首页跳转字符串
     * @since 1.0.0
     */
    public String goToIndex() {
        LOGGER.debug("JumpAction##goToIndex() start");
        LOGGER.debug("JumpAction##goToIndex() Just For jumping to index.html");
        LOGGER.debug("JumpAction##goToIndex() end");
        return "jumpToIndex";
    }
}
