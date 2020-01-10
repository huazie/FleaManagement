package com.huazie.fleamgmt.struts2.base.web;

import com.huazie.frame.auth.base.user.service.interfaces.IFleaLoginLogSV;
import com.huazie.frame.common.util.HttpUtils;
import org.apache.struts2.ServletActionContext;

/**
 * <p> 记录登录日志的Action </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class LoginLogAction extends BaseAction {

    private static final long serialVersionUID = -7976938991425389205L;

    /**
     * <p> 保存登录日志 </p>
     *
     * @param accountId 登录账户编号
     * @throws Exception
     * @since 1.0.0
     */
    protected void saveLoginLog(long accountId) throws Exception {

        //获取用户登录的ip4地址
        String ip4 = HttpUtils.getIp(ServletActionContext.getRequest());

        //获取用户登录的ip6地址
        String ip6 = "";

        //获取用户登录的地市地址
        String address = HttpUtils.getAddressByTaoBao(ip4);

        // this.getLoginLogSV().saveLoginLog(accountId, ip4, ip6, address);

    }

    /**
     * <p> 记录退出日志 </p>
     *
     * @param accountId 账户编号
     * @throws Exception
     * @since 1.0.0
     */
    protected void saveQuitLog(long accountId) throws Exception {
        // this.getLoginLogSV().saveQuitLog(accountId);
    }

    protected abstract IFleaLoginLogSV getFleaLoginLogSV();
}
