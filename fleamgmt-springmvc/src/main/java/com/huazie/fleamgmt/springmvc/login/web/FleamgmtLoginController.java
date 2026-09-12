package com.huazie.fleamgmt.springmvc.login.web;

import com.huazie.fleaframework.auth.base.user.entity.FleaAccount;
import com.huazie.fleaframework.auth.common.pojo.user.login.FleaUserLoginPOJO;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaUserModuleSV;
import com.huazie.fleaframework.auth.util.FleaAuthAsyncTask;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

        // 用户登录验证
        FleaAccount fleaAccount = fleaUserModuleSV.login(fleaUserLoginPOJO);

        // 异步初始化用户信息（含菜单等耗时数据）
        // 通过 FleaUserImplObjectFactory 回调，在异步任务完成后将用户信息写入 Session
        FleaAuthAsyncTask.asyncInitUserInfo(fleaUserModuleSV, fleaAccount.getAccountId(), FleaJerseyConfig.getSystemAccountId(Long.class),
                null, new FleaUserImplObjectFactory() {
                    @Override
                    public void initObject(IFleaUser fleaUser) {
                        // 异步任务完成后，将用户信息写入 Session
                        initFleaUserSession(session, fleaUser);
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
     * <p> 检查用户Session是否初始化完成 </p>
     *
     * <p> 解决首次登录时，异步初始化用户信息未完成导致首页无法获取用户数据的问题 </p>
     *
     * @param session HttpSession对象
     * @return 检查结果
     * @since 1.0.0
     */
    @RequestMapping(value = "fleamgmtLogin!checkSession.flea", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public OutputCommonData checkSession(final HttpSession session) {
        OutputCommonData result = new OutputCommonData();

        try {
            Object userInfo = session.getAttribute(FleaRequestUtil.getUserSessionKey());
            if (userInfo != null) {
                // Session 中已有用户信息，说明异步初始化已完成
                result.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
                result.setRetMess("用户Session初始化完成");
            } else {
                // 尚未完成
                result.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
                result.setRetMess("用户Session初始化中，请稍候...");
            }
        } catch (Exception e) {
            result.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            result.setRetMess("检查用户Session异常：" + e.getMessage());
        }

        return result;
    }

    /**
     * <p> 初始化用户Session信息 </p>
     *
     * @param session   HttpSession对象
     * @param fleaUser Flea用户信息对象
     * @since 1.0.0
     */
    private void initFleaUserSession(HttpSession session, IFleaUser fleaUser) {
        try {
            // 将用户的信息写入到session中,并在跳转到主界面获取这个用户的信息
            // 这是用户的浏览器与web服务器建立的一次会话,会话结束后,该信息也就消失了
            session.setAttribute(FleaRequestUtil.getUserSessionKey(), fleaUser);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Init User Session occurs exception", e);
            }
        }
    }

}
