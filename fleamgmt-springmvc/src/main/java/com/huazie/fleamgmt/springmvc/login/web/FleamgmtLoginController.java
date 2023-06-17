package com.huazie.fleamgmt.springmvc.login.web;

import com.huazie.fleaframework.auth.base.user.entity.FleaAccount;
import com.huazie.fleaframework.auth.common.pojo.user.login.FleaUserLoginPOJO;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaUserModuleSV;
import com.huazie.fleaframework.auth.util.FleaAuthLogger;
import com.huazie.fleaframework.common.FleaSessionManager;
import com.huazie.fleaframework.common.IFleaUser;
import com.huazie.fleaframework.common.exceptions.CommonException;
import com.huazie.fleaframework.common.pojo.OutputCommonData;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleaframework.core.request.FleaRequestUtil;
import com.huazie.fleaframework.jersey.common.FleaJerseyConfig;
import com.huazie.fleaframework.jersey.common.FleaUserImplObjectFactory;
import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.springmvc.base.web.BusinessController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * <p> 登录Controller </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class FleamgmtLoginController extends BusinessController {

    private static final FleaLogger LOGGER = FleaLoggerProxy.getProxyInstance(FleamgmtLoginController.class);

    private IFleaUserModuleSV fleaUserModuleSV;

    @Resource(name = "fleaUserModuleSV")
    public void setFleaUserModuleSV(IFleaUserModuleSV fleaUserModuleSV) {
        this.fleaUserModuleSV = fleaUserModuleSV;
    }

    @PostMapping("fleamgmtLogin!login.flea")
    @ResponseBody
    public OutputCommonData login(FleaUserLoginPOJO fleaUserLoginPOJO, HttpServletRequest request, final HttpSession session) throws CommonException {

        // 跳主的登录
        FleaAccount fleaAccount = fleaUserModuleSV.login(fleaUserLoginPOJO);

        // 初始化用户信息
        fleaUserModuleSV.initUserInfo(fleaAccount.getAccountId(), FleaJerseyConfig.getSystemAccountId(Long.class),
                null, new FleaUserImplObjectFactory() {
                    @Override
                    public void initObject(IFleaUser fleaUser) {
                        // 初始化用户Session信息
                        initFleaUserSession(session);
                    }
                });

        // 记录登陆日志 (异步)
        FleaAuthLogger.asyncSaveLoginLog(fleaUserModuleSV, fleaAccount.getAccountId(), request);

        OutputCommonData result = new OutputCommonData();
        result.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        result.setRetMess("亲，恭喜您登录成功呦");

        return result;
    }

    /**
     * <p> 初始化用户Session信息 </p>
     *
     * @param session HttpSession对象
     * @since 1.0.0
     */
    private void initFleaUserSession(HttpSession session) {
        try {
            // 将用户的信息写入到session中,并在跳转到主界面获取这个用户的信息
            // 这是用户的浏览器与web服务器建立的一次会话,会话结束后,该信息也就消失了
            session.setAttribute(FleaRequestUtil.getUserSessionKey(), FleaSessionManager.getUserInfo());
        } catch (CommonException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Init User Session occurs exception", e);
            }
        }
    }

}
