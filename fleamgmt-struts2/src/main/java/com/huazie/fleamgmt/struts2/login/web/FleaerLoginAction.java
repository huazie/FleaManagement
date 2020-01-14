package com.huazie.fleamgmt.struts2.login.web;

import com.huazie.fleamgmt.constant.FleaMgmtConstants;
import com.huazie.fleamgmt.struts2.base.web.BaseAction;
import com.huazie.frame.auth.base.user.entity.FleaAccount;
import com.huazie.frame.auth.common.pojo.login.FleaUserLoginInfo;
import com.huazie.frame.auth.common.service.interfaces.IFleaUserLoginSV;
import com.huazie.frame.common.exception.CommonException;
import com.huazie.frame.common.util.ObjectUtils;
import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * <p> 跳主登录Action </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class FleaerLoginAction extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(FleaerLoginAction.class);
    private static final long serialVersionUID = -8632343740482642538L;

    private transient IFleaUserLoginSV fleaUserLoginSV;

    private FleaUserLoginInfo fleaUserLoginInfo;

    @Resource(name = "fleaUserLoginSV")
    public void setFleaUserLoginSV(IFleaUserLoginSV fleaUserLoginSV) {
        this.fleaUserLoginSV = fleaUserLoginSV;
    }

    public FleaUserLoginInfo getFleaUserLoginInfo() {
        return fleaUserLoginInfo;
    }

    public void setFleaUserLoginInfo(FleaUserLoginInfo fleaUserLoginInfo) {
        this.fleaUserLoginInfo = fleaUserLoginInfo;
    }

    /**
     * <p> 跳主登录 </p>
     *
     * @return 默认以json格式返回数据
     * @since 1.0.0
     */
    public String login() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("FleaerLoginAction##login() start");
        }

        try {
            ActionContext aContext = ActionContext.getContext();

            // 跳主的登录
            FleaAccount fleaAccount = fleaUserLoginSV.login(fleaUserLoginInfo);

            if (ObjectUtils.isNotEmpty(fleaAccount)) {
                // 将用户的信息写入到session中,并在跳转到主界面获取这个用户的信息
                // 这是用户的浏览器与web服务器建立的一次会话,会话结束后,该信息也就消失了
                aContext.getSession().put(FleaMgmtConstants.SessionConstants.SESSION_FLEAER_ACCOUNT, fleaAccount);
                // 在这边记录登陆日志
                fleaUserLoginSV.saveLoginLog(fleaAccount.getAccountId(), ServletActionContext.getRequest());
                this.result.setRetCode(FleaMgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
                this.result.setRetMess("亲，恭喜您登录成功呦");
            }

        } catch (CommonException e) {
            this.result.setRetCode(FleaMgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            this.result.setRetMess(e.getMessage());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("FleaerLoginAction##login() end");
        }

        return "json";
    }

}
