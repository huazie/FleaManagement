package com.huazie.fleamgmt.struts2.home.web;

import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.module.home.pojo.OutputUserInfo;
import com.huazie.frame.auth.common.service.interfaces.IFleaUserLoginSV;
import com.huazie.frame.common.FleaSessionManager;
import com.huazie.frame.common.IFleaUser;
import com.huazie.frame.common.exception.CommonException;
import com.huazie.frame.core.request.FleaRequestUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

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

    private IFleaUserLoginSV fleaUserLoginSV;

    @Resource(name = "fleaUserLoginSV")
    public void setFleaUserLoginSV(IFleaUserLoginSV fleaUserLoginSV) {
        this.fleaUserLoginSV = fleaUserLoginSV;
    }

    private OutputUserInfo userInfo = new OutputUserInfo();

    public OutputUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(OutputUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * <p> 获取用户信息 </p>
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
            userInfo.setRetMess("用户登录成功");


        } else {
            userInfo.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            userInfo.setRetMess("用户信息已失效");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("IndexAction##getUserSession() end");
        }

        return "json";
    }

    /**
     * <p> 用户退出 </p>
     *
     * @return 默认以json格式返回数据
     * @since 1.0.0
     */
    public String quit() {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("IndexAction##quit() start");
        }

        ActionContext aContext = ActionContext.getContext();
        try {
            IFleaUser fleaUser = (IFleaUser) aContext.getSession().get(FleaRequestUtil.getUserSessionKey());
            if (null != fleaUser) {
                aContext.getSession().remove(FleaRequestUtil.getUserSessionKey());
                FleaSessionManager.setUserInfo(null); // 用户信息置空
                // TODO (异步) 保存用户当月最近一次登录的退出日志
                fleaUserLoginSV.saveQuitLog(fleaUser.getAcctId());
                userInfo.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
                userInfo.setRetMess("用户成功退出");
            } else {
                userInfo.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
                userInfo.setRetMess("用户登录异常");
            }
        } catch (CommonException e) {
            userInfo.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            userInfo.setRetMess("用户退出异常：" + e.getMessage());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("IndexAction##quit() end");
        }

        return "json";
    }
}
