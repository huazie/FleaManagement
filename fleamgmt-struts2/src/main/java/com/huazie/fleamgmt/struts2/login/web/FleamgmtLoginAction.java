package com.huazie.fleamgmt.struts2.login.web;

import com.huazie.fleaframework.auth.base.user.entity.FleaAccount;
import com.huazie.fleaframework.auth.common.pojo.user.login.FleaUserLoginPOJO;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaUserModuleSV;
import com.huazie.fleaframework.auth.util.FleaAuthLogger;
import com.huazie.fleaframework.common.FleaSessionManager;
import com.huazie.fleaframework.common.IFleaUser;
import com.huazie.fleaframework.common.exceptions.CommonException;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleaframework.common.util.ObjectUtils;
import com.huazie.fleaframework.core.request.FleaRequestUtil;
import com.huazie.fleaframework.jersey.common.FleaJerseyConfig;
import com.huazie.fleaframework.jersey.common.FleaUserImplObjectFactory;
import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.struts2.base.web.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.ServletActionContext;
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

    private static final FleaLogger LOGGER = FleaLoggerProxy.getProxyInstance(FleamgmtLoginAction.class);

    private IFleaUserModuleSV fleaUserModuleSV;

    private FleaUserLoginPOJO fleaUserLoginPOJO;

    @Resource(name = "fleaUserModuleSV")
    public void setFleaUserModuleSV(IFleaUserModuleSV fleaUserModuleSV) {
        this.fleaUserModuleSV = fleaUserModuleSV;
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
            LOGGER.debug("Start");
        }

        try {
            // 跳主的登录
            FleaAccount fleaAccount = fleaUserModuleSV.login(fleaUserLoginPOJO);

            if (ObjectUtils.isNotEmpty(fleaAccount)) {
                // 初始化用户信息
                fleaUserModuleSV.initUserInfo(fleaAccount.getAccountId(), FleaJerseyConfig.getSystemAccountId(Long.class),
                        null, new FleaUserImplObjectFactory() {
                            @Override
                            public void initObject(IFleaUser fleaUser) {
                                initFleaUserSession();
                            }
                        });
                // 记录登陆日志 (异步)
                FleaAuthLogger.asyncSaveLoginLog(fleaUserModuleSV, fleaAccount.getAccountId(), ServletActionContext.getRequest());
                result.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
                result.setRetMess("亲，恭喜您登录成功呦");

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(result.getRetMess());
                }
            }

        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("【Struts2】用户登录异常：\n", e);
            }
            result.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            result.setRetMess("用户登录异常：" + e.getMessage());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("End");
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
                LOGGER.error("Init User Session occurs exception", e);
            }
        }
    }

}
