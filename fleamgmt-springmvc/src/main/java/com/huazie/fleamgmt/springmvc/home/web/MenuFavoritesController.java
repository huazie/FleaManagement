package com.huazie.fleamgmt.springmvc.home.web;

import com.huazie.fleaframework.auth.base.function.entity.FleaMenu;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaFunctionModuleSV;
import com.huazie.fleaframework.common.EntityStateEnum;
import com.huazie.fleaframework.common.FleaSessionManager;
import com.huazie.fleaframework.common.exceptions.CommonException;
import com.huazie.fleaframework.common.pojo.OutputCommonData;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleaframework.common.util.CollectionUtils;
import com.huazie.fleaframework.common.util.DateUtils;
import com.huazie.fleaframework.common.util.ObjectUtils;
import com.huazie.fleaframework.core.base.cfgdata.bean.FleaConfigDataSpringBean;
import com.huazie.fleaframework.core.base.cfgdata.entity.FleaMenuFavorites;
import com.huazie.fleaframework.core.base.cfgdata.service.interfaces.IFleaMenuFavoritesSV;
import com.huazie.fleaframework.core.common.pojo.FleaMenuFavoritesPOJO;
import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.module.home.pojo.OutputMenuFavorites;
import com.huazie.fleamgmt.springmvc.base.web.BusinessController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final String FAVORITES_CACHE = "fleamenufavorites";

    private FleaConfigDataSpringBean springBean;

    private IFleaFunctionModuleSV fleaFunctionModuleSV;

    private IFleaMenuFavoritesSV fleaMenuFavoritesSV;

    private CacheManager cacheManager;

    @Autowired
    public void setSpringBean(FleaConfigDataSpringBean springBean) {
        this.springBean = springBean;
    }

    @Resource(name = "fleaFunctionModuleSV")
    public void setFleaFunctionModuleSV(IFleaFunctionModuleSV fleaFunctionModuleSV) {
        this.fleaFunctionModuleSV = fleaFunctionModuleSV;
    }

    @Resource(name = "fleaMenuFavoritesSV")
    public void setFleaMenuFavoritesSV(IFleaMenuFavoritesSV fleaMenuFavoritesSV) {
        this.fleaMenuFavoritesSV = fleaMenuFavoritesSV;
    }

    @Resource(name = "coreSpringCacheManager")
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
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

    /**
     * <p> 收藏菜单 </p>
     *
     * @param menuCode 菜单编码
     * @return 业务结果返回信息
     * @since 1.0.0
     */
    @PostMapping("menuFavorites!collect.flea")
    @ResponseBody
    public OutputCommonData collect(@RequestParam("menuCode") String menuCode) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        Long accountId = FleaSessionManager.getAccountId();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("AccountId = {}, MenuCode = {}", accountId, menuCode);
        }

        // 已收藏的菜单不能重复收藏
        if (ObjectUtils.isNotEmpty(springBean.queryValidFleaMenuFavorites(accountId, menuCode))) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("该菜单已经被收藏");
            return output;
        }

        FleaMenu fleaMenu = getMenuByCode(menuCode);
        if (ObjectUtils.isEmpty(fleaMenu)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("菜单不存在或已失效");
            return output;
        }

        // 保存菜单收藏夹信息
        FleaMenuFavoritesPOJO fleaMenuFavoritesPOJO = new FleaMenuFavoritesPOJO();
        fleaMenuFavoritesPOJO.setAccountId(accountId);
        fleaMenuFavoritesPOJO.setMenuCode(menuCode);
        fleaMenuFavoritesPOJO.setMenuName(fleaMenu.getMenuName());
        fleaMenuFavoritesPOJO.setMenuIcon(fleaMenu.getMenuIcon());
        fleaMenuFavoritesPOJO.setRemarks("收藏菜单【" + fleaMenu.getMenuName() + "】");
        springBean.saveFleaMenuFavorites(fleaMenuFavoritesPOJO);

        // 收藏数据发生变更,失效菜单收藏夹缓存
        evictFavoritesCache(accountId, menuCode);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("收藏菜单【" + fleaMenu.getMenuName() + "】成功");
        return output;
    }

    /**
     * <p> 取消收藏菜单 </p>
     *
     * @param menuCode 菜单编码
     * @return 业务结果返回信息
     * @since 1.0.0
     */
    @PostMapping("menuFavorites!cancel.flea")
    @ResponseBody
    public OutputCommonData cancel(@RequestParam("menuCode") String menuCode) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        Long accountId = FleaSessionManager.getAccountId();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("AccountId = {}, MenuCode = {}", accountId, menuCode);
        }

        FleaMenuFavorites fleaMenuFavorites = springBean.queryValidFleaMenuFavorites(accountId, menuCode);
        if (ObjectUtils.isEmpty(fleaMenuFavorites)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("该菜单还未被收藏");
            return output;
        }

        // 逻辑删除:置为删除状态,并记录修改时间
        fleaMenuFavorites.setFavoritesState(EntityStateEnum.BE_DELETED.getState());
        fleaMenuFavorites.setDoneDate(DateUtils.getCurrentTime());
        fleaMenuFavoritesSV.update(fleaMenuFavorites);

        // 收藏数据发生变更,失效菜单收藏夹缓存
        evictFavoritesCache(accountId, menuCode);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("已取消收藏菜单【" + fleaMenuFavorites.getMenuName() + "】");
        return output;
    }

    /**
     * <p> 获取登录用户收藏的菜单 </p>
     *
     * @return 菜单收藏信息
     * @since 1.0.0
     */
    @GetMapping("menuFavorites!find.flea")
    @ResponseBody
    public OutputMenuFavorites find() throws CommonException {

        OutputMenuFavorites output = new OutputMenuFavorites();

        Long accountId = FleaSessionManager.getAccountId();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("AccountId = {}", accountId);
        }

        List<FleaMenuFavorites> fleaMenuFavoritesList = springBean.queryValidFleaMenuFavorites(accountId);
        List<Map<String, Object>> menuFavoritesList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(fleaMenuFavoritesList)) {
            for (FleaMenuFavorites fleaMenuFavorites : fleaMenuFavoritesList) {
                Map<String, Object> menuFavoritesMap = new HashMap<>();
                menuFavoritesMap.put("MENU_CODE", fleaMenuFavorites.getMenuCode());
                menuFavoritesMap.put("MENU_NAME", fleaMenuFavorites.getMenuName());
                menuFavoritesMap.put("MENU_ICON", fleaMenuFavorites.getMenuIcon());
                menuFavoritesList.add(menuFavoritesMap);
            }
        }
        output.setMenuFavoritesList(menuFavoritesList);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        return output;
    }

    /**
     * <p> 根据菜单编码获取有效的菜单信息 </p>
     *
     * @param menuCode 菜单编码
     * @return 菜单信息
     * @since 1.0.0
     */
    private FleaMenu getMenuByCode(String menuCode) throws CommonException {
        List<FleaMenu> fleaMenuList = fleaFunctionModuleSV.queryValidMenus(null);
        if (CollectionUtils.isEmpty(fleaMenuList)) {
            return null;
        }
        for (FleaMenu fleaMenu : fleaMenuList) {
            if (fleaMenu.getMenuCode().equals(menuCode)) {
                return fleaMenu;
            }
        }
        return null;
    }

    /**
     * <p> 失效菜单收藏夹缓存（收藏数据变更后调用） </p>
     *
     * @param accountId 操作账户编号
     * @param menuCode  菜单编码
     * @since 1.0.0
     */
    private void evictFavoritesCache(Long accountId, String menuCode) {
        Cache cache = cacheManager.getCache(FAVORITES_CACHE);
        if (ObjectUtils.isEmpty(cache)) {
            return;
        }
        cache.evict(accountId);
        cache.evict(accountId + "_" + menuCode);
    }
}
