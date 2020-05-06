package com.huazie.fleamgmt.struts2.login.web;

import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.struts2.base.web.BaseAction;
import com.huazie.frame.auth.base.user.entity.FleaAccount;
import com.huazie.frame.auth.common.pojo.user.login.FleaUserLoginPOJO;
import com.huazie.frame.auth.common.service.interfaces.IFleaAuthSV;
import com.huazie.frame.auth.common.service.interfaces.IFleaUserLoginSV;
import com.huazie.frame.common.FleaSessionManager;
import com.huazie.frame.common.exception.CommonException;
import com.huazie.frame.common.util.ObjectUtils;
import com.huazie.frame.core.request.FleaRequestUtil;
import com.huazie.frame.jersey.client.core.FleaJerseyClientConfig;
import com.huazie.frame.jersey.common.FleaUserImplObjectFactory;
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
public class FleamgmtLoginAction extends BaseAction {

    private static final long serialVersionUID = -8632343740482642538L;

    private static final Logger LOGGER = LoggerFactory.getLogger(FleamgmtLoginAction.class);

    private IFleaUserLoginSV fleaUserLoginSV;

    private IFleaAuthSV fleaAuthSV;

    private FleaUserLoginPOJO fleaUserLoginPOJO;

    @Resource(name = "fleaUserLoginSV")
    public void setFleaUserLoginSV(IFleaUserLoginSV fleaUserLoginSV) {
        this.fleaUserLoginSV = fleaUserLoginSV;
    }

    @Resource(name = "fleaAuthSV")
    public void setFleaAuthSV(IFleaAuthSV fleaAuthSV) {
        this.fleaAuthSV = fleaAuthSV;
    }

    public FleaUserLoginPOJO getFleaUserLoginPOJO() {
        return fleaUserLoginPOJO;
    }

    public void setFleaUserLoginPOJO(FleaUserLoginPOJO fleaUserLoginPOJO) {
        this.fleaUserLoginPOJO = fleaUserLoginPOJO;
    }

    /**
     * <p> 跳主登录 </p>
     *
     * @return 默认以json格式返回数据
     * @since 1.0.0
     */
    public String login() {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("FleamgmtLoginAction##login() start");
        }

        try {
            // 跳主的登录
            FleaAccount fleaAccount = fleaUserLoginSV.login(fleaUserLoginPOJO);

            if (ObjectUtils.isNotEmpty(fleaAccount)) {
                // 初始化用户信息
                fleaAuthSV.initUserInfo(fleaAccount.getUserId(), fleaAccount.getAccountId(),
                        FleaJerseyClientConfig.getSystemAcctId(Long.class), null,
                        new FleaUserImplObjectFactory() {
                            @Override
                            public void initObject() {
                                initFleaUserSession();
                            }
                        });
                // 在这边记录登陆日志
                fleaUserLoginSV.saveLoginLog(fleaAccount.getAccountId(), ServletActionContext.getRequest());
                this.result.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
                this.result.setRetMess("亲，恭喜您登录成功呦");
            }

        } catch (Exception e) {
            this.result.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            this.result.setRetMess(e.getMessage());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("FleamgmtLoginAction##login() end");
        }

        return "json";
    }

    /**
     * <p> 初始化用户Session信息 </p>
     *
     * @since 1.0.0
     */
    private void initFleaUserSession() {
        try {
            ActionContext aContext = ActionContext.getContext();
            // 将用户的信息写入到session中,并在跳转到主界面获取这个用户的信息
            // 这是用户的浏览器与web服务器建立的一次会话,会话结束后,该信息也就消失了
            aContext.getSession().put(FleaRequestUtil.getUserSessionKey(), FleaSessionManager.getUserInfo());
        } catch (CommonException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("FleamgmtLoginAction##initFleaUserSession() Init User Session occurs exception", e);
            }
        }
    }

}
