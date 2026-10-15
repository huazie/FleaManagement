package com.huazie.fleamgmt.springmvc.auth.web;

import com.huazie.fleaframework.auth.base.audit.service.impl.FleaAuthAuditLogger;
import com.huazie.fleaframework.auth.base.audit.util.FleaAuthAuditDiffUtils;
import com.huazie.fleaframework.auth.base.user.entity.FleaAccount;
import com.huazie.fleaframework.auth.base.user.entity.FleaUser;
import com.huazie.fleaframework.auth.base.user.entity.FleaUserGroup;
import com.huazie.fleaframework.auth.base.user.entity.FleaUserGroupRel;
import com.huazie.fleaframework.auth.base.user.entity.FleaUserRel;
import com.huazie.fleaframework.auth.common.AuthRelTypeEnum;
import com.huazie.fleaframework.auth.common.pojo.FleaAuthRelExtPOJO;
import com.huazie.fleaframework.auth.common.pojo.user.FleaAccountPOJO;
import com.huazie.fleaframework.auth.common.pojo.user.FleaUserGroupPOJO;
import com.huazie.fleaframework.auth.common.pojo.user.FleaUserPOJO;
import com.huazie.fleaframework.auth.common.pojo.user.register.FleaUserRegisterPOJO;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaRoleModuleSV;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaUserModuleSV;
import com.huazie.fleaframework.common.CommonConstants;
import com.huazie.fleaframework.common.exceptions.CommonException;
import com.huazie.fleaframework.common.pojo.OutputCommonData;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleaframework.common.util.CollectionUtils;
import com.huazie.fleaframework.common.util.ObjectUtils;
import com.huazie.fleaframework.common.util.POJOUtils;
import com.huazie.fleaframework.common.util.SecurityUtils;
import com.huazie.fleaframework.common.util.StringUtils;
import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.module.auth.pojo.InputAuthRelInfo;
import com.huazie.fleamgmt.module.auth.pojo.InputUserGroupInfo;
import com.huazie.fleamgmt.module.auth.pojo.InputUserInfo;
import com.huazie.fleamgmt.module.auth.pojo.OutputAuthInfo;
import com.huazie.fleamgmt.module.auth.pojo.OutputFunctionInfo;
import com.huazie.fleamgmt.module.auth.pojo.OutputGridInfo;
import com.huazie.fleamgmt.module.auth.util.AuthBizUtils;
import com.huazie.fleamgmt.module.auth.util.AuthCandidateUtil;
import com.huazie.fleamgmt.springmvc.base.web.BusinessController;
import com.huazie.fleamgmt.springmvc.common.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 用户模块管理 Controller </p>
 *
 * <p> 覆盖「用户模块管理」下的两个子模块： </p>
 * <ul>
 *     <li>用户管理：用户注册、用户变更、用户授权；</li>
 *     <li>用户组管理：用户组新增、用户组变更、用户组授权。</li>
 * </ul>
 *
 * <p> 说明：全部数据操作统一通过框架的 {@code IFleaUserModuleSV} 完成，
 * 用户变更走模块级 {@code modifyFleaAccount} / {@code modifyFleaUser}；
 * 账户密码变更时由模块服务内部统一加密，与注册链路保持一致。 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class UsermgmtController extends BusinessController {

    private static final FleaLogger LOGGER = FleaLoggerProxy.getProxyInstance(UsermgmtController.class);

    /**
     * <p> 用户授权支持的关联类型（与授权页标签页一一对应） </p>
     */
    private static final String[] USER_REL_TYPES = new String[]{
            AuthRelTypeEnum.USER_REL_ROLE.getRelType(),
            AuthRelTypeEnum.USER_REL_ROLE_GROUP.getRelType()
    };

    /**
     * <p> 用户组授权支持的关联类型（与授权页标签页一一对应） </p>
     */
    private static final String[] USER_GROUP_REL_TYPES = new String[]{
            AuthRelTypeEnum.USER_GROUP_REL_ROLE.getRelType(),
            AuthRelTypeEnum.USER_GROUP_REL_ROLE_GROUP.getRelType(),
            AuthRelTypeEnum.USER_GROUP_REL_USER.getRelType()
    };

    /**
     * <p> 账户变更审计字段标签（key：属性名，value：展示名） </p>
     */
    private static final Map<String, String> ACCOUNT_AUDIT_LABELS = new LinkedHashMap<String, String>();

    /**
     * <p> 用户变更审计字段标签（备注字段在账户侧体现，避免重复） </p>
     */
    private static final Map<String, String> USER_AUDIT_LABELS = new LinkedHashMap<String, String>();

    /**
     * <p> 用户组变更审计字段标签 </p>
     */
    private static final Map<String, String> USER_GROUP_AUDIT_LABELS = new LinkedHashMap<String, String>();

    static {
        ACCOUNT_AUDIT_LABELS.put("accountState", "账户状态");
        ACCOUNT_AUDIT_LABELS.put("effectiveDate", "生效日期");
        ACCOUNT_AUDIT_LABELS.put("expiryDate", "失效日期");
        ACCOUNT_AUDIT_LABELS.put("remarks", "备注");

        USER_AUDIT_LABELS.put("userName", "昵称");
        USER_AUDIT_LABELS.put("userSex", "性别");
        USER_AUDIT_LABELS.put("userEmail", "邮箱");
        USER_AUDIT_LABELS.put("userPhone", "手机");
        USER_AUDIT_LABELS.put("userAddress", "住址");
        USER_AUDIT_LABELS.put("groupId", "用户组编号");
        USER_AUDIT_LABELS.put("userState", "用户状态");

        USER_GROUP_AUDIT_LABELS.put("userGroupName", "用户组名称");
        USER_GROUP_AUDIT_LABELS.put("userGroupDesc", "用户组描述");
        USER_GROUP_AUDIT_LABELS.put("remarks", "备注");
    }

    private IFleaUserModuleSV fleaUserModuleSV;

    private IFleaRoleModuleSV fleaRoleModuleSV;

    @Resource(name = "fleaUserModuleSV")
    public void setFleaUserModuleSV(IFleaUserModuleSV fleaUserModuleSV) {
        this.fleaUserModuleSV = fleaUserModuleSV;
    }

    @Resource(name = "fleaRoleModuleSV")
    public void setFleaRoleModuleSV(IFleaRoleModuleSV fleaRoleModuleSV) {
        this.fleaRoleModuleSV = fleaRoleModuleSV;
    }

    @Resource(name = "fleaAuthAuditLogger")
    private FleaAuthAuditLogger authAuditLogger;

    /* ==================== 用户管理 ==================== */

    /**
     * <p> 用户列表（用户注册 / 用户变更 / 用户授权 左侧列表共用） </p>
     *
     * @return 用户列表信息
     * @since 1.0.0
     */
    @GetMapping("authUser!list.flea")
    @ResponseBody
    public OutputFunctionInfo listUsers() throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        List<Map<String, Object>> userTreeList = AuthCandidateUtil.accounts(fleaUserModuleSV);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("User Tree List = {}", userTreeList);
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setTreeList(userTreeList);

        return output;
    }

    /**
     * <p> 用户明细列表（用户变更页表格用） </p>
     *
     * <p> 与 {@link #listUsers()} 的区别：列表接口只返回树节点（编号/编码/展示名），
     * 服务于 Fuelux 树与穿梭框；本接口返回账户与用户的完整明细字段及汇总统计，
     * 服务于表格（jqGrid）的列展示与筛选。 </p>
     *
     * @return 用户明细行集合与汇总统计
     * @since 1.0.0
     */
    @GetMapping("authUser!page.flea")
    @ResponseBody
    public OutputGridInfo pageUsers() throws CommonException {

        OutputGridInfo output = new OutputGridInfo();

        List<Map<String, Object>> rowList = AuthCandidateUtil.userRows(fleaUserModuleSV);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setRows(rowList);
        output.setSummary(AuthBizUtils.summarize(rowList, "accountState"));

        return output;
    }

    /**
     * <p> 用户明细查询（用户变更页回填用） </p>
     *
     * @param accountId 账户编号
     * @return 用户明细信息
     * @since 1.0.0
     */
    @GetMapping("authUser!query.flea")
    @ResponseBody
    public OutputFunctionInfo queryUser(@RequestParam("accountId") Long accountId) throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        FleaAccount fleaAccount = fleaUserModuleSV.queryValidAccount(accountId);
        if (ObjectUtils.isEmpty(fleaAccount)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，账户【" + accountId + "】不存在或已失效，请刷新用户列表后重试！");
            return output;
        }

        FleaUser fleaUser = fleaUserModuleSV.queryValidUser(fleaAccount.getUserId());

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("accountId", fleaAccount.getAccountId());
        userMap.put("accountCode", fleaAccount.getAccountCode());
        userMap.put("userId", fleaAccount.getUserId());
        userMap.put("state", fleaAccount.getAccountState());
        // 页面日期控件要求 yyyy-MM-dd，此处统一格式化后返回
        userMap.put("effectiveDate", AuthBizUtils.formatDate(fleaAccount.getEffectiveDate()));
        userMap.put("expiryDate", AuthBizUtils.formatDate(fleaAccount.getExpiryDate()));
        userMap.put("remarks", fleaAccount.getRemarks());

        if (ObjectUtils.isNotEmpty(fleaUser)) {
            userMap.put("userName", fleaUser.getUserName());
            userMap.put("userSex", fleaUser.getUserSex());
            userMap.put("userEmail", fleaUser.getUserEmail());
            userMap.put("userPhone", fleaUser.getUserPhone());
            userMap.put("userAddress", fleaUser.getUserAddress());
            userMap.put("groupId", fleaUser.getGroupId());
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setData(userMap);

        return output;
    }

    /**
     * <p> 用户注册 </p>
     *
     * @param inputUserInfo 用户业务入参
     * @return 用户注册结果
     * @since 1.0.0
     */
    @PostMapping("authUser!register.flea")
    @ResponseBody
    public OutputCommonData registerUser(InputUserInfo inputUserInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        FleaUserRegisterPOJO fleaUserRegisterPOJO = new FleaUserRegisterPOJO();
        fleaUserRegisterPOJO.setAccountCode(inputUserInfo.getAccountCode());
        fleaUserRegisterPOJO.setAccountPwd(inputUserInfo.getAccountPwd());
        fleaUserRegisterPOJO.setUserName(inputUserInfo.getUserName());
        // 库中 group_id / user_state / effective_date / expiry_date 均为非空列，未填写时给出默认值
        fleaUserRegisterPOJO.setGroupId(AuthBizUtils.getGroupIdOrDefault(inputUserInfo.getGroupId()));
        fleaUserRegisterPOJO.setState(AuthBizUtils.getStateOrDefault(inputUserInfo.getState(), CommonConstants.NumeralConstants.INT_ONE));
        fleaUserRegisterPOJO.setEffectiveDate(AuthBizUtils.parseEffectiveDate(inputUserInfo.getEffectiveDate()));
        fleaUserRegisterPOJO.setExpiryDate(AuthBizUtils.parseExpiryDate(inputUserInfo.getExpiryDate()));
        fleaUserRegisterPOJO.setRemarks(inputUserInfo.getRemarks());

        FleaAccount fleaAccount = fleaUserModuleSV.register(fleaUserRegisterPOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，用户注册成功，账户编号：" + fleaAccount.getAccountId());

        authAuditLogger.recordCreate("account_" + fleaAccount.getAccountId(), output.getRetMess(), WebUtils.getRequest());

        return output;
    }

    /**
     * <p> 用户变更 </p>
     *
     * @param inputUserInfo 用户业务入参
     * @return 用户变更结果
     * @since 1.0.0
     */
    @PostMapping("authUser!update.flea")
    @ResponseBody
    public OutputCommonData updateUser(InputUserInfo inputUserInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        Long accountId = inputUserInfo.getAccountId();
        if (!AuthBizUtils.isValidId(accountId)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧用户列表中选择要变更的用户哟！");
            return output;
        }

        FleaAccount fleaAccount = fleaUserModuleSV.queryValidAccount(accountId);
        if (ObjectUtils.isEmpty(fleaAccount)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，账户【" + accountId + "】不存在或已失效，请刷新用户列表后重试！");
            return output;
        }

        // 变更前快照，用于审计备注记录具体修改内容
        FleaUser oldFleaUser = fleaUserModuleSV.queryValidUser(fleaAccount.getUserId());

        Date effectiveDate = AuthBizUtils.parseDate(inputUserInfo.getEffectiveDate());
        Date expiryDate = AuthBizUtils.parseDate(inputUserInfo.getExpiryDate());

        // 账户变更：模块服务内部合并非 null 字段（空串支持显式清空），密码不为空时统一加密
        FleaAccountPOJO fleaAccountPOJO = new FleaAccountPOJO();
        fleaAccountPOJO.setAccountState(inputUserInfo.getState());
        fleaAccountPOJO.setEffectiveDate(effectiveDate);
        fleaAccountPOJO.setExpiryDate(expiryDate);
        fleaAccountPOJO.setRemarks(inputUserInfo.getRemarks());
        fleaAccountPOJO.setAccountPwd(inputUserInfo.getAccountPwd());
        fleaUserModuleSV.modifyFleaAccount(accountId, fleaAccountPOJO);

        // 用户信息变更（昵称、性别、邮箱、手机、住址、用户组、状态）
        FleaUserPOJO fleaUserPOJO = new FleaUserPOJO();
        fleaUserPOJO.setUserName(inputUserInfo.getUserName());
        fleaUserPOJO.setUserSex(inputUserInfo.getUserSex());
        fleaUserPOJO.setUserEmail(inputUserInfo.getUserEmail());
        fleaUserPOJO.setUserPhone(inputUserInfo.getUserPhone());
        fleaUserPOJO.setUserAddress(inputUserInfo.getUserAddress());
        fleaUserPOJO.setGroupId(inputUserInfo.getGroupId());
        fleaUserPOJO.setUserState(inputUserInfo.getState());
        fleaUserPOJO.setRemarks(inputUserInfo.getRemarks());
        fleaUserModuleSV.modifyFleaUser(fleaAccount.getUserId(), fleaUserPOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，用户修改成功！");

        String changeDesc = buildUserInfoChangeDesc(inputUserInfo, fleaAccount, oldFleaUser, fleaAccountPOJO, fleaUserPOJO);
        authAuditLogger.recordUpdate("account_" + accountId, output.getRetMess(), changeDesc, WebUtils.getRequest());

        return output;
    }

    /**
     * <p> 构建用户变更的审计备注（具体修改内容） </p>
     *
     * <p> 账户与用户两部分差异合并输出；密码只体现「已修改」，不记录具体值。 </p>
     *
     * @param inputUserInfo   用户业务入参
     * @param oldFleaAccount  变更前账户
     * @param oldFleaUser     变更前用户（可为空）
     * @param fleaAccountPOJO 账户变更入参
     * @param fleaUserPOJO    用户变更入参
     * @return 变更描述，如「昵称：张三→李四；密码：已修改」；无具体修改内容时返回 {@code null}
     * @since 1.0.0
     */
    private String buildUserInfoChangeDesc(InputUserInfo inputUserInfo, FleaAccount oldFleaAccount, FleaUser oldFleaUser,
                                           FleaAccountPOJO fleaAccountPOJO, FleaUserPOJO fleaUserPOJO) {

        StringBuilder changeDesc = new StringBuilder();

        String accountChange = FleaAuthAuditDiffUtils.diff(oldFleaAccount, fleaAccountPOJO, ACCOUNT_AUDIT_LABELS);
        if (StringUtils.isNotBlank(accountChange)) {
            changeDesc.append(accountChange);
        }

        // 密码与库中密文比对相同（如浏览器自动填充原密码）不算修改，不记录；不记录具体值
        if (StringUtils.isNotBlank(inputUserInfo.getAccountPwd())
                && !SecurityUtils.matchesPassword(inputUserInfo.getAccountPwd(), oldFleaAccount.getAccountPwd())) {
            if (changeDesc.length() > 0) {
                changeDesc.append("；");
            }
            changeDesc.append("密码：已修改");
        }

        String userChange = FleaAuthAuditDiffUtils.diff(oldFleaUser, fleaUserPOJO, USER_AUDIT_LABELS);
        if (StringUtils.isNotBlank(userChange)) {
            if (changeDesc.length() > 0) {
                changeDesc.append("；");
            }
            changeDesc.append(userChange);
        }

        return changeDesc.length() > 0 ? changeDesc.toString() : null;
    }

    /**
     * <p> 用户授权明细（可授权角色 / 角色组 + 已授权编号） </p>
     *
     * @param accountId 账户编号
     * @param relType   关联类型（USER_REL_ROLE / USER_REL_ROLE_GROUP）
     * @return 授权明细信息
     * @since 1.0.0
     */
    @GetMapping("authUser!auth.flea")
    @ResponseBody
    public OutputAuthInfo authUser(@RequestParam("accountId") Long accountId,
                                   @RequestParam("relType") String relType) throws CommonException {

        OutputAuthInfo output = new OutputAuthInfo();

        FleaAccount fleaAccount = fleaUserModuleSV.queryValidAccount(accountId);
        if (ObjectUtils.isEmpty(fleaAccount)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，账户【" + accountId + "】不存在或已失效，请刷新用户列表后重试！");
            return output;
        }

        Long userId = fleaAccount.getUserId();
        List<Map<String, Object>> candidates;
        if (AuthRelTypeEnum.USER_REL_ROLE.getRelType().equals(relType)) {
            candidates = AuthCandidateUtil.roles(fleaRoleModuleSV);
        } else if (AuthRelTypeEnum.USER_REL_ROLE_GROUP.getRelType().equals(relType)) {
            candidates = AuthCandidateUtil.roleGroups(fleaRoleModuleSV);
        } else {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，不支持的用户授权类型【" + relType + "】！");
            return output;
        }

        // 已授权的编号集合
        List<Long> selectedList = new ArrayList<>();
        List<FleaUserRel> userRelList = fleaUserModuleSV.getUserRelList(userId, relType);
        if (CollectionUtils.isNotEmpty(userRelList)) {
            for (FleaUserRel fleaUserRel : userRelList) {
                if (ObjectUtils.isNotEmpty(fleaUserRel)) {
                    selectedList.add(fleaUserRel.getRelId());
                }
            }
        }

        output.setOwnerId(userId);
        output.setOwnerName(fleaAccount.getAccountCode());
        output.setRelType(relType);
        output.setCandidates(candidates);
        output.setSelected(selectedList);
        output.setRelTypeCounts(countUserRels(userId));

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");

        return output;
    }

    /**
     * <p> 用户授权提交（按关联类型逐条新增关联） </p>
     *
     * @param inputAuthRelInfo 授权业务入参
     * @return 授权结果
     * @since 1.0.0
     */
    @PostMapping("authUser!authorize.flea")
    @ResponseBody
    public OutputCommonData authorizeUser(InputAuthRelInfo inputAuthRelInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        Long ownerId = inputAuthRelInfo.getOwnerId();
        String relType = inputAuthRelInfo.getRelType();
        List<Long> relIds = inputAuthRelInfo.getRelIds();

        if (!AuthBizUtils.isValidId(ownerId)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧用户列表中选择要授权的用户哟！");
            return output;
        }

        if (CollectionUtils.isEmpty(relIds)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧列表中选择要授权的数据哟！");
            return output;
        }

        FleaAuthRelExtPOJO fleaAuthRelExtPOJO = new FleaAuthRelExtPOJO();
        for (Long relId : relIds) {
            if (!AuthBizUtils.isValidId(relId)) {
                continue;
            }
            if (AuthRelTypeEnum.USER_REL_ROLE.getRelType().equals(relType)) {
                fleaUserModuleSV.userRelRole(ownerId, relId, fleaAuthRelExtPOJO);
            } else if (AuthRelTypeEnum.USER_REL_ROLE_GROUP.getRelType().equals(relType)) {
                fleaUserModuleSV.userRelRoleGroup(ownerId, relId, fleaAuthRelExtPOJO);
            } else {
                output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
                output.setRetMess("亲，不支持的用户授权类型【" + relType + "】！");
                return output;
            }
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，用户授权成功，本次新增授权 " + relIds.size() + " 项！");

        authAuditLogger.recordGrant("account_" + ownerId, output.getRetMess() + "（关联类型：" + relType + "）", WebUtils.getRequest());

        return output;
    }

    /**
     * <p> 用户取消授权（按关联类型逐条取消关联） </p>
     *
     * @param inputAuthRelInfo 授权业务入参
     * @return 取消授权结果
     * @since 1.0.0
     */
    @PostMapping("authUser!cancel.flea")
    @ResponseBody
    public OutputCommonData cancelAuthorizeUser(InputAuthRelInfo inputAuthRelInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        Long ownerId = inputAuthRelInfo.getOwnerId();
        String relType = inputAuthRelInfo.getRelType();
        List<Long> relIds = inputAuthRelInfo.getRelIds();

        if (!AuthBizUtils.isValidId(ownerId)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧用户列表中选择要取消授权的用户哟！");
            return output;
        }

        if (CollectionUtils.isEmpty(relIds)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从授权清单中选择要取消授权的数据哟！");
            return output;
        }

        int count = 0;
        for (Long relId : relIds) {
            if (!AuthBizUtils.isValidId(relId)) {
                continue;
            }
            if (AuthRelTypeEnum.USER_REL_ROLE.getRelType().equals(relType)
                    || AuthRelTypeEnum.USER_REL_ROLE_GROUP.getRelType().equals(relType)) {
                fleaUserModuleSV.removeUserRel(ownerId, relId, relType);
                count++;
            } else {
                output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
                output.setRetMess("亲，不支持的用户授权类型【" + relType + "】！");
                return output;
            }
        }

        if (count == 0) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，不支持的用户授权类型【" + relType + "】！");
            return output;
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，用户取消授权成功，本次取消授权 " + count + " 项！");

        authAuditLogger.recordRevoke("account_" + ownerId, output.getRetMess() + "（关联类型：" + relType + "）", WebUtils.getRequest());

        return output;
    }

    /* ==================== 用户组管理 ==================== */

    /**
     * <p> 用户组列表（用户组新增 / 变更 / 授权 左侧列表共用） </p>
     *
     * @return 用户组列表信息
     * @since 1.0.0
     */
    @GetMapping("authUserGroup!list.flea")
    @ResponseBody
    public OutputFunctionInfo listUserGroups() throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        List<Map<String, Object>> userGroupTreeList = AuthCandidateUtil.userGroups(fleaUserModuleSV);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setTreeList(userGroupTreeList);

        return output;
    }

    /**
     * <p> 用户组明细列表（用户组变更页表格用） </p>
     *
     * <p> 在用户组基础信息之外附带成员数（memberCount），
     * 便于页面直观呈现各分组的规模。 </p>
     *
     * @return 用户组明细行集合与汇总统计
     * @since 1.0.0
     */
    @GetMapping("authUserGroup!page.flea")
    @ResponseBody
    public OutputGridInfo pageUserGroups() throws CommonException {

        OutputGridInfo output = new OutputGridInfo();

        List<Map<String, Object>> rowList = AuthCandidateUtil.userGroupRows(fleaUserModuleSV);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setRows(rowList);
        output.setSummary(AuthBizUtils.summarize(rowList, "userGroupState"));

        return output;
    }

    /**
     * <p> 用户组明细查询（用户组变更页回填用） </p>
     *
     * @param userGroupId 用户组编号
     * @return 用户组明细信息
     * @since 1.0.0
     */
    @GetMapping("authUserGroup!query.flea")
    @ResponseBody
    public OutputFunctionInfo queryUserGroup(@RequestParam("userGroupId") Long userGroupId) throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        FleaUserGroup fleaUserGroup = fleaUserModuleSV.queryUserGroupInUse(userGroupId);
        if (ObjectUtils.isEmpty(fleaUserGroup)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，用户组【" + userGroupId + "】不存在或已失效，请刷新用户组列表后重试！");
            return output;
        }

        Map<String, Object> userGroupMap = new HashMap<>();
        userGroupMap.put("userGroupId", fleaUserGroup.getUserGroupId());
        userGroupMap.put("userGroupName", fleaUserGroup.getUserGroupName());
        userGroupMap.put("userGroupDesc", fleaUserGroup.getUserGroupDesc());
        userGroupMap.put("remarks", fleaUserGroup.getRemarks());

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setData(userGroupMap);

        return output;
    }

    /**
     * <p> 用户组新增 </p>
     *
     * @param inputUserGroupInfo 用户组业务入参
     * @return 用户组新增结果
     * @since 1.0.0
     */
    @PostMapping("authUserGroup!add.flea")
    @ResponseBody
    public OutputCommonData addUserGroup(InputUserGroupInfo inputUserGroupInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        FleaUserGroupPOJO fleaUserGroupPOJO = new FleaUserGroupPOJO();
        POJOUtils.copyAll(inputUserGroupInfo, fleaUserGroupPOJO);

        Long userGroupId = fleaUserModuleSV.addUserGroup(fleaUserGroupPOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，用户组添加成功，用户组编号：" + userGroupId);

        authAuditLogger.recordCreate("userGroup_" + userGroupId, output.getRetMess(), WebUtils.getRequest());

        return output;
    }

    /**
     * <p> 用户组变更 </p>
     *
     * @param inputUserGroupInfo 用户组业务入参
     * @return 用户组变更结果
     * @since 1.0.0
     */
    @PostMapping("authUserGroup!modify.flea")
    @ResponseBody
    public OutputCommonData modifyUserGroup(InputUserGroupInfo inputUserGroupInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        if (!AuthBizUtils.isValidId(inputUserGroupInfo.getUserGroupId())) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧用户组列表中选择要变更的用户组哟！");
            return output;
        }

        // 变更前快照，用于审计备注记录具体修改内容
        FleaUserGroup oldFleaUserGroup = fleaUserModuleSV.queryUserGroupInUse(inputUserGroupInfo.getUserGroupId());

        FleaUserGroupPOJO fleaUserGroupPOJO = new FleaUserGroupPOJO();
        POJOUtils.copyAll(inputUserGroupInfo, fleaUserGroupPOJO);

        fleaUserModuleSV.modifyFleaUserGroup(inputUserGroupInfo.getUserGroupId(), fleaUserGroupPOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，用户组修改成功！");

        String changeDesc = FleaAuthAuditDiffUtils.diff(oldFleaUserGroup, fleaUserGroupPOJO, USER_GROUP_AUDIT_LABELS);
        authAuditLogger.recordUpdate("userGroup_" + inputUserGroupInfo.getUserGroupId(), output.getRetMess(), changeDesc, WebUtils.getRequest());

        return output;
    }

    /**
     * <p> 用户组授权明细（可授权角色 / 角色组 / 用户 + 已授权编号） </p>
     *
     * @param userGroupId 用户组编号
     * @param relType     关联类型（USER_GROUP_REL_ROLE / USER_GROUP_REL_ROLE_GROUP / USER_GROUP_REL_USER）
     * @return 授权明细信息
     * @since 1.0.0
     */
    @GetMapping("authUserGroup!auth.flea")
    @ResponseBody
    public OutputAuthInfo authUserGroup(@RequestParam("userGroupId") Long userGroupId,
                                        @RequestParam("relType") String relType) throws CommonException {

        OutputAuthInfo output = new OutputAuthInfo();

        FleaUserGroup fleaUserGroup = fleaUserModuleSV.queryUserGroupInUse(userGroupId);
        if (ObjectUtils.isEmpty(fleaUserGroup)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，用户组【" + userGroupId + "】不存在或已失效，请刷新用户组列表后重试！");
            return output;
        }

        List<Map<String, Object>> candidates;
        if (AuthRelTypeEnum.USER_GROUP_REL_ROLE.getRelType().equals(relType)) {
            candidates = AuthCandidateUtil.roles(fleaRoleModuleSV);
        } else if (AuthRelTypeEnum.USER_GROUP_REL_ROLE_GROUP.getRelType().equals(relType)) {
            candidates = AuthCandidateUtil.roleGroups(fleaRoleModuleSV);
        } else if (AuthRelTypeEnum.USER_GROUP_REL_USER.getRelType().equals(relType)) {
            candidates = AuthCandidateUtil.users(fleaUserModuleSV);
        } else {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，不支持的用户组授权类型【" + relType + "】！");
            return output;
        }

        List<Long> selectedList = new ArrayList<>();
        List<FleaUserGroupRel> userGroupRelList = fleaUserModuleSV.getUserGroupRelList(userGroupId, relType);
        if (CollectionUtils.isNotEmpty(userGroupRelList)) {
            for (FleaUserGroupRel fleaUserGroupRel : userGroupRelList) {
                if (ObjectUtils.isNotEmpty(fleaUserGroupRel)) {
                    selectedList.add(fleaUserGroupRel.getRelId());
                }
            }
        }

        output.setOwnerId(userGroupId);
        output.setOwnerName(fleaUserGroup.getUserGroupName());
        output.setRelType(relType);
        output.setCandidates(candidates);
        output.setSelected(selectedList);
        output.setRelTypeCounts(countUserGroupRels(userGroupId));

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");

        return output;
    }

    /**
     * <p> 用户组授权提交（按关联类型逐条新增关联） </p>
     *
     * @param inputAuthRelInfo 授权业务入参
     * @return 授权结果
     * @since 1.0.0
     */
    @PostMapping("authUserGroup!authorize.flea")
    @ResponseBody
    public OutputCommonData authorizeUserGroup(InputAuthRelInfo inputAuthRelInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        Long ownerId = inputAuthRelInfo.getOwnerId();
        String relType = inputAuthRelInfo.getRelType();
        List<Long> relIds = inputAuthRelInfo.getRelIds();

        if (!AuthBizUtils.isValidId(ownerId)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧用户组列表中选择要授权的用户组哟！");
            return output;
        }

        if (CollectionUtils.isEmpty(relIds)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧列表中选择要授权的数据哟！");
            return output;
        }

        FleaAuthRelExtPOJO fleaAuthRelExtPOJO = new FleaAuthRelExtPOJO();
        for (Long relId : relIds) {
            if (!AuthBizUtils.isValidId(relId)) {
                continue;
            }
            if (AuthRelTypeEnum.USER_GROUP_REL_ROLE.getRelType().equals(relType)) {
                fleaUserModuleSV.userGroupRelRole(ownerId, relId, fleaAuthRelExtPOJO);
            } else if (AuthRelTypeEnum.USER_GROUP_REL_ROLE_GROUP.getRelType().equals(relType)) {
                fleaUserModuleSV.userGroupRelRoleGroup(ownerId, relId, fleaAuthRelExtPOJO);
            } else if (AuthRelTypeEnum.USER_GROUP_REL_USER.getRelType().equals(relType)) {
                fleaUserModuleSV.userGroupRelUser(ownerId, relId, fleaAuthRelExtPOJO);
            } else {
                output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
                output.setRetMess("亲，不支持的用户组授权类型【" + relType + "】！");
                return output;
            }
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，用户组授权成功，本次新增授权 " + relIds.size() + " 项！");

        authAuditLogger.recordGrant("userGroup_" + ownerId, output.getRetMess() + "（关联类型：" + relType + "）", WebUtils.getRequest());

        return output;
    }

    /**
     * <p> 用户组取消授权（按关联类型逐条取消关联） </p>
     *
     * @param inputAuthRelInfo 授权业务入参
     * @return 取消授权结果
     * @since 1.0.0
     */
    @PostMapping("authUserGroup!cancel.flea")
    @ResponseBody
    public OutputCommonData cancelAuthorizeUserGroup(InputAuthRelInfo inputAuthRelInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        Long ownerId = inputAuthRelInfo.getOwnerId();
        String relType = inputAuthRelInfo.getRelType();
        List<Long> relIds = inputAuthRelInfo.getRelIds();

        if (!AuthBizUtils.isValidId(ownerId)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧用户组列表中选择要取消授权的用户组哟！");
            return output;
        }

        if (CollectionUtils.isEmpty(relIds)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从授权清单中选择要取消授权的数据哟！");
            return output;
        }

        int count = 0;
        for (Long relId : relIds) {
            if (!AuthBizUtils.isValidId(relId)) {
                continue;
            }
            if (AuthRelTypeEnum.USER_GROUP_REL_ROLE.getRelType().equals(relType)
                    || AuthRelTypeEnum.USER_GROUP_REL_ROLE_GROUP.getRelType().equals(relType)
                    || AuthRelTypeEnum.USER_GROUP_REL_USER.getRelType().equals(relType)) {
                fleaUserModuleSV.removeUserGroupRel(ownerId, relId, relType);
                count++;
            } else {
                output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
                output.setRetMess("亲，不支持的用户组授权类型【" + relType + "】！");
                return output;
            }
        }

        if (count == 0) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，不支持的用户组授权类型【" + relType + "】！");
            return output;
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，用户组取消授权成功，本次取消授权 " + count + " 项！");

        authAuditLogger.recordRevoke("userGroup_" + ownerId, output.getRetMess() + "（关联类型：" + relType + "）", WebUtils.getRequest());

        return output;
    }

    /* ==================== 内部方法 ==================== */

    /**
     * <p> 统计指定用户在各关联类型下已授权的数量 </p>
     *
     * <p> 授权页按关联类型分标签页展示，标签页上的徽章需要在打开页面时即呈现各类授权数量，
     * 而前端默认只加载当前标签页的数据，故由后端一次性给出全部维度的计数。 </p>
     *
     * @param userId 用户编号
     * @return 关联类型 -> 已授权数量
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    private Map<String, Integer> countUserRels(Long userId) throws CommonException {

        Map<String, Integer> relTypeCounts = new HashMap<>();

        for (String relType : USER_REL_TYPES) {
            List<FleaUserRel> relList = fleaUserModuleSV.getUserRelList(userId, relType);
            relTypeCounts.put(relType, CollectionUtils.isNotEmpty(relList) ? relList.size() : 0);
        }

        return relTypeCounts;
    }

    /**
     * <p> 统计指定用户组在各关联类型下已授权的数量 </p>
     *
     * @param userGroupId 用户组编号
     * @return 关联类型 -> 已授权数量
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    private Map<String, Integer> countUserGroupRels(Long userGroupId) throws CommonException {

        Map<String, Integer> relTypeCounts = new HashMap<>();

        for (String relType : USER_GROUP_REL_TYPES) {
            List<FleaUserGroupRel> relList = fleaUserModuleSV.getUserGroupRelList(userGroupId, relType);
            relTypeCounts.put(relType, CollectionUtils.isNotEmpty(relList) ? relList.size() : 0);
        }

        return relTypeCounts;
    }

}
