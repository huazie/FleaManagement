package com.huazie.fleamgmt.util;

import com.huazie.fleaframework.auth.util.FleaMenuTree;
import com.huazie.fleaframework.common.FleaSessionManager;
import com.huazie.fleaframework.common.IFleaUser;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.module.home.pojo.OutputUserInfo;

/**
 * <p> 用户信息工具类 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserInfoUtil {

    private static final FleaLogger LOGGER = FleaLoggerProxy.getProxyInstance(UserInfoUtil.class);

    private UserInfoUtil() {
    }

    /**
     * <p> 获取用户信息 </p>
     *
     * @return 用户信息
     * @since 1.0.0
     */
    public static OutputUserInfo getUserInfo() {

        OutputUserInfo userInfo = new OutputUserInfo();
        try {
            IFleaUser fleaUser = FleaSessionManager.getUserInfo();
            if (null != fleaUser) {
                userInfo.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
                userInfo.setRetMess("用户登录成功");
                userInfo.setUserInfo(fleaUser.toMap());
                FleaMenuTree fleaMenuTree = fleaUser.get(FleaMenuTree.MENU_TREE, FleaMenuTree.class);
                userInfo.setMenuList(fleaMenuTree.toMapList(false));
            } else {
                userInfo.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
                userInfo.setRetMess("用户信息已失效");
            }
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("获取用户Session异常：\n", e);
            }
            userInfo.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            userInfo.setRetMess("获取用户Session异常：" + e.getMessage());
        }

        return userInfo;
    }
}
