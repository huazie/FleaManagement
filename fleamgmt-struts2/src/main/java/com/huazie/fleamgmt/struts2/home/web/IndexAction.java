package com.huazie.fleamgmt.struts2.home.web;

import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.module.home.pojo.OutputUserInfo;
import com.huazie.frame.common.FleaSessionManager;
import com.huazie.frame.common.IFleaUser;
import com.opensymphony.xwork2.ActionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

/**
 * <p> 首页Action </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class IndexAction extends ActionSupport {

    private static final long serialVersionUID = 893158281822366218L;

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexAction.class);

    private OutputUserInfo userInfo = new OutputUserInfo();

    public OutputUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(OutputUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * <p> 获取用户Session信息 </p>
     *
     * @return 默认以json格式返回数据
     * @since 1.0.0
     */
    public String getUserSession() {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("IndexAction##getUserSession() start");
        }

        IFleaUser fleaUser = FleaSessionManager.getUserInfo();
        if (null != fleaUser) {
            userInfo.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
            userInfo.setRetMess("Login Success");


        } else {
            userInfo.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            userInfo.setRetMess("用户信息已失效");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("IndexAction##getUserSession() end");
        }

        return "json";
    }
}
