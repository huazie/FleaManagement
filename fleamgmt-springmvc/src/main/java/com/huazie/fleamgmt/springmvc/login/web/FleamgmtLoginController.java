package com.huazie.fleamgmt.springmvc.login.web;

import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.springmvc.base.web.BusinessController;
import com.huazie.frame.auth.base.user.entity.FleaAccount;
import com.huazie.frame.auth.common.pojo.user.login.FleaUserLoginPOJO;
import com.huazie.frame.auth.common.service.interfaces.IFleaAuthSV;
import com.huazie.frame.auth.common.service.interfaces.IFleaUserLoginSV;
import com.huazie.frame.common.FleaSessionManager;
import com.huazie.frame.common.exception.CommonException;
import com.huazie.frame.common.pojo.OutputCommonData;
import com.huazie.frame.common.util.ObjectUtils;
import com.huazie.frame.core.request.FleaRequestUtil;
import com.huazie.frame.jersey.client.core.FleaJerseyClientConfig;
import com.huazie.frame.jersey.common.FleaUserImplObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(FleamgmtLoginController.class);

    private IFleaUserLoginSV fleaUserLoginSV;

    private IFleaAuthSV fleaAuthSV;

    @Resource(name = "fleaUserLoginSV")
    public void setFleaUserLoginSV(IFleaUserLoginSV fleaUserLoginSV) {
        this.fleaUserLoginSV = fleaUserLoginSV;
    }

    @Resource(name = "fleaAuthSV")
    public void setFleaAuthSV(IFleaAuthSV fleaAuthSV) {
        this.fleaAuthSV = fleaAuthSV;
    }

    @RequestMapping("fleamgmtLogin!login.flea")
    @ResponseBody
    public OutputCommonData login(@RequestParam("fleaUserLoginPOJO.accountCode") String accountCode,
                                  @RequestParam("fleaUserLoginPOJO.accountPwd") String accountPwd,
                                  HttpServletRequest request,
                                  final HttpSession session) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("FleamgmtLoginController##login(String, String, HttpServletRequest, HttpSession) start");
        }

        OutputCommonData result = new OutputCommonData();

        try {
            // 跳主的登录
            FleaUserLoginPOJO fleaUserLoginPOJO = new FleaUserLoginPOJO();
            fleaUserLoginPOJO.setAccountCode(accountCode);
            fleaUserLoginPOJO.setAccountPwd(accountPwd);
            FleaAccount fleaAccount = fleaUserLoginSV.login(fleaUserLoginPOJO);

            if (ObjectUtils.isNotEmpty(fleaAccount)) {
                // 初始化用户信息
                fleaAuthSV.initUserInfo(fleaAccount.getUserId(), fleaAccount.getAccountId(),
                        FleaJerseyClientConfig.getSystemAcctId(Long.class), null,
                        new FleaUserImplObjectFactory() {
                            @Override
                            public void initObject() {
                                // 初始化用户Session信息
                                initFleaUserSession(session);
                            }
                        });

                // 在这边记录登陆日志
                fleaUserLoginSV.saveLoginLog(fleaAccount.getAccountId(), request);
                result.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
                result.setRetMess("亲，恭喜您登录成功呦");
            }

        } catch (CommonException e) {
            result.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            result.setRetMess(e.getMessage());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("FleamgmtLoginController##login(String, String, HttpServletRequest, HttpSession) end");
        }

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
                LOGGER.error("FleamgmtLoginController##initFleaUserSession() Init User Session occurs exception", e);
            }
        }
    }

}
