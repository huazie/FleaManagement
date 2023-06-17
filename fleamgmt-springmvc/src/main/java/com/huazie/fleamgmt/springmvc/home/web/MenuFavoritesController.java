package com.huazie.fleamgmt.springmvc.home.web;

import com.huazie.fleaframework.common.FleaSessionManager;
import com.huazie.fleaframework.common.exceptions.CommonException;
import com.huazie.fleaframework.common.pojo.OutputCommonData;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleaframework.common.util.ObjectUtils;
import com.huazie.fleaframework.core.base.cfgdata.bean.FleaConfigDataSpringBean;
import com.huazie.fleaframework.core.base.cfgdata.entity.FleaMenuFavorites;
import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.springmvc.base.web.BusinessController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p> 菜单收藏夹Controller </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class MenuFavoritesController extends BusinessController {

    private static final FleaLogger LOGGER = FleaLoggerProxy.getProxyInstance(MenuFavoritesController.class);

    private FleaConfigDataSpringBean springBean;

    @Autowired
    public void setSpringBean(FleaConfigDataSpringBean springBean) {
        this.springBean = springBean;
    }

    /**
     * <p> 判断是否收藏了某个菜单 </p>
     *
     * @param menuCode 菜单编码
     * @return 业务结果返回信息
     * @since 1.0.0
     */
    @GetMapping("menuFavorites!isFavorites.flea")
    @ResponseBody
    public OutputCommonData isFavorites(@RequestParam("menuCode") String menuCode) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        Long accountId = FleaSessionManager.getAccountId();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("AccountId = {}", accountId);
        }
        FleaMenuFavorites fleaMenuFavorites = springBean.queryValidFleaMenuFavorites(accountId, menuCode);
        if (ObjectUtils.isNotEmpty(fleaMenuFavorites)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
            output.setRetMess(fleaMenuFavorites.getMenuName() + "已经被收藏");
        } else {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess(menuCode + "还未被收藏");
        }

        return output;
    }
}
