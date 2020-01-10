package com.huazie.fleamgmt.struts2.login.web;

import com.huazie.fleamgmt.constant.FleaMgmtConstants;
import com.huazie.fleamgmt.module.login.pojo.FleaerLogin;
import com.huazie.fleamgmt.struts2.base.web.LoginLogAction;
import com.huazie.frame.auth.base.user.entity.FleaAccount;
import com.huazie.frame.auth.base.user.service.interfaces.IFleaAccountSV;
import com.huazie.frame.auth.base.user.service.interfaces.IFleaLoginLogSV;
import com.opensymphony.xwork2.ActionContext;
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
public class FleaerLoginAction extends LoginLogAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(FleaerLoginAction.class);
    private static final long serialVersionUID = -8632343740482642538L;

    @Resource(name = "accountSVImpl")
    private IFleaAccountSV accountService;

    @Resource(name = "fleaLoginLogSV")
    private IFleaLoginLogSV loginLogService;

    private FleaerLogin fleaerLogin;

    public FleaerLogin getFleaerLogin() {
        return fleaerLogin;
    }

    public void setFleaerLogin(FleaerLogin fleaerLogin) {
        this.fleaerLogin = fleaerLogin;
    }

    /**
     * <p> 跳主登录 </p>
     *
     * @return 默认以json格式返回数据
     * @throws Exception
     * @since 1.0.0
     */
    public String login() throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("FleaerLoginAction##login() start");
        }

        String name = fleaerLogin.getName();
        String password = fleaerLogin.getPassword();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("FleaerLoginAction##login() NAME={}", name);
            LOGGER.debug("FleaerLoginAction##login() PASSWORD={}", password);
        }

        ActionContext aContext = ActionContext.getContext();

//        FleaAccount account = accountService.login(name, password, IAccountValue.ACCOUNT_FLAG_FLEAER); // 跳主的登录
//
//        if (account != null) {
//            // 将用户的信息写入到session中,并在跳转到主界面获取这个用户的信息
//            // 这是用户的浏览器与web服务器建立的一次会话,会话结束后,该信息也就消失了
//            aContext.getSession().put(FleaMgmtConstants.SessionConstants.SESSION_FLEAER_ACCOUNT, account);
//            // 在这边记录登陆日志
//            this.saveLoginLog(account.getAccountId());
//            this.result.setRetCode(FleaMgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
//            this.result.setRetMess("亲，恭喜您登录成功呦");
//        } else {
//            this.result.setRetCode(FleaMgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
//            this.result.setRetMess("亲,您输入的账号或密码有误呦");
//        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("FleaerLoginAction##login() end");
        }

        return "json";
    }

    @Override
    protected IFleaLoginLogSV getFleaLoginLogSV() {
        return loginLogService;
    }

}
