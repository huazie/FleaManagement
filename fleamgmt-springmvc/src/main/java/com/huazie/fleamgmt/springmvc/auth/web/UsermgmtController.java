package com.huazie.fleamgmt.springmvc.auth.web;

import com.huazie.fleaframework.auth.base.user.entity.FleaAccount;
import com.huazie.fleaframework.auth.base.user.entity.FleaUser;
import com.huazie.fleaframework.auth.base.user.entity.FleaUserGroup;
import com.huazie.fleaframework.auth.base.user.entity.FleaUserGroupRel;
import com.huazie.fleaframework.auth.base.user.entity.FleaUserRel;
import com.huazie.fleaframework.auth.base.user.service.interfaces.IFleaAccountSV;
import com.huazie.fleaframework.auth.base.user.service.interfaces.IFleaUserGroupRelSV;
import com.huazie.fleaframework.auth.base.user.service.interfaces.IFleaUserGroupSV;
import com.huazie.fleaframework.auth.base.user.service.interfaces.IFleaUserRelSV;
import com.huazie.fleaframework.auth.base.user.service.interfaces.IFleaUserSV;
import com.huazie.fleaframework.auth.base.role.service.interfaces.IFleaRoleGroupSV;
import com.huazie.fleaframework.auth.base.role.service.interfaces.IFleaRoleSV;
import com.huazie.fleaframework.auth.common.AuthRelTypeEnum;
import com.huazie.fleaframework.auth.common.pojo.FleaAuthRelExtPOJO;
import com.huazie.fleaframework.auth.common.pojo.user.FleaUserGroupPOJO;
import com.huazie.fleaframework.auth.common.pojo.user.register.FleaUserRegisterPOJO;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaUserModuleSV;
import com.huazie.fleaframework.common.CommonConstants;
import com.huazie.fleaframework.common.exceptions.CommonException;
import com.huazie.fleaframework.common.pojo.OutputCommonData;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleaframework.common.util.CollectionUtils;
import com.huazie.fleaframework.common.util.ObjectUtils;
import com.huazie.fleaframework.common.util.POJOUtils;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
 * <p> 说明：框架未提供「用户变更」的模块级服务，故用户变更走 base 层
 * {@code IFleaUserSV.update} / {@code IFleaAccountSV.update}；账户密码变更时
 * 由 {@code IFleaAccountSV.encrypt} 统一加密，与注册链路保持一致。 </p>
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

    private IFleaUserModuleSV fleaUserModuleSV;

    private IFleaUserSV fleaUserSV;

    private IFleaAccountSV fleaAccountSV;

    private IFleaUserGroupSV fleaUserGroupSV;

    private IFleaUserRelSV fleaUserRelSV;

    private IFleaUserGroupRelSV fleaUserGroupRelSV;

    private IFleaRoleSV fleaRoleSV;

    private IFleaRoleGroupSV fleaRoleGroupSV;

    @Resource(name = "fleaUserModuleSV")
    public void setFleaUserModuleSV(IFleaUserModuleSV fleaUserModuleSV) {
        this.fleaUserModuleSV = fleaUserModuleSV;
    }

    @Resource(name = "fleaUserSV")
    public void setFleaUserSV(IFleaUserSV fleaUserSV) {
        this.fleaUserSV = fleaUserSV;
    }

    @Resource(name = "fleaAccountSV")
    public void setFleaAccountSV(IFleaAccountSV fleaAccountSV) {
        this.fleaAccountSV = fleaAccountSV;
    }

    @Resource(name = "fleaUserGroupSV")
    public void setFleaUserGroupSV(IFleaUserGroupSV fleaUserGroupSV) {
        this.fleaUserGroupSV = fleaUserGroupSV;
    }

    @Resource(name = "fleaUserRelSV")
    public void setFleaUserRelSV(IFleaUserRelSV fleaUserRelSV) {
        this.fleaUserRelSV = fleaUserRelSV;
    }

    @Resource(name = "fleaUserGroupRelSV")
    public void setFleaUserGroupRelSV(IFleaUserGroupRelSV fleaUserGroupRelSV) {
        this.fleaUserGroupRelSV = fleaUserGroupRelSV;
    }

    @Resource(name = "fleaRoleSV")
    public void setFleaRoleSV(IFleaRoleSV fleaRoleSV) {
        this.fleaRoleSV = fleaRoleSV;
    }

    @Resource(name = "fleaRoleGroupSV")
    public void setFleaRoleGroupSV(IFleaRoleGroupSV fleaRoleGroupSV) {
        this.fleaRoleGroupSV = fleaRoleGroupSV;
    }

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

        List<Map<String, Object>> userTreeList = AuthCandidateUtil.accounts(fleaAccountSV, fleaUserSV);

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

        List<Map<String, Object>> rowList = AuthCandidateUtil.userRows(fleaAccountSV, fleaUserSV, fleaUserGroupSV);

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

        FleaAccount fleaAccount = fleaAccountSV.queryValidAccount(accountId);
        if (ObjectUtils.isEmpty(fleaAccount)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，账户【" + accountId + "】不存在或已失效，请刷新用户列表后重试！");
            return output;
        }

        FleaUser fleaUser = fleaUserSV.queryValidUser(fleaAccount.getUserId());

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

        FleaAccount fleaAccount = fleaAccountSV.queryValidAccount(accountId);
        if (ObjectUtils.isEmpty(fleaAccount)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，账户【" + accountId + "】不存在或已失效，请刷新用户列表后重试！");
            return output;
        }

        Date effectiveDate = AuthBizUtils.parseDate(inputUserInfo.getEffectiveDate());
        Date expiryDate = AuthBizUtils.parseDate(inputUserInfo.getExpiryDate());

        // 账户状态、生效失效日期、备注（未提交的项保持原值，避免把库中非空列置空）
        if (inputUserInfo.getState() != null) {
            fleaAccount.setAccountState(inputUserInfo.getState());
        }
        if (effectiveDate != null) {
            fleaAccount.setEffectiveDate(effectiveDate);
        }
        if (expiryDate != null) {
            fleaAccount.setExpiryDate(expiryDate);
        }
        if (inputUserInfo.getRemarks() != null) {
            fleaAccount.setRemarks(inputUserInfo.getRemarks());
        }
        // 密码留空表示不修改；填写时统一走加密
        if (ObjectUtils.isNotEmpty(inputUserInfo.getAccountPwd())) {
            fleaAccount.setAccountPwd(fleaAccountSV.encrypt(inputUserInfo.getAccountPwd()));
        }
        fleaAccountSV.update(fleaAccount);

        // 用户信息变更（昵称、性别、邮箱、手机、住址、用户组、状态）
        FleaUser fleaUser = fleaUserSV.queryValidUser(fleaAccount.getUserId());
        if (ObjectUtils.isNotEmpty(fleaUser)) {
            if (ObjectUtils.isNotEmpty(inputUserInfo.getUserName())) {
                fleaUser.setUserName(inputUserInfo.getUserName());
            }
            if (inputUserInfo.getUserSex() != null) {
                fleaUser.setUserSex(inputUserInfo.getUserSex());
            }
            if (inputUserInfo.getUserEmail() != null) {
                fleaUser.setUserEmail(inputUserInfo.getUserEmail());
            }
            if (inputUserInfo.getUserPhone() != null) {
                fleaUser.setUserPhone(inputUserInfo.getUserPhone());
            }
            if (inputUserInfo.getUserAddress() != null) {
                fleaUser.setUserAddress(inputUserInfo.getUserAddress());
            }
            if (inputUserInfo.getGroupId() != null) {
                fleaUser.setGroupId(inputUserInfo.getGroupId());
            }
            if (inputUserInfo.getState() != null) {
                fleaUser.setUserState(inputUserInfo.getState());
            }
            if (inputUserInfo.getRemarks() != null) {
                fleaUser.setRemarks(inputUserInfo.getRemarks());
            }
            fleaUserSV.update(fleaUser);
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，用户修改成功！");
        return output;
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

        FleaAccount fleaAccount = fleaAccountSV.queryValidAccount(accountId);
        if (ObjectUtils.isEmpty(fleaAccount)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，账户【" + accountId + "】不存在或已失效，请刷新用户列表后重试！");
            return output;
        }

        Long userId = fleaAccount.getUserId();
        List<Map<String, Object>> candidates;
        if (AuthRelTypeEnum.USER_REL_ROLE.getRelType().equals(relType)) {
            candidates = AuthCandidateUtil.roles(fleaRoleSV);
        } else if (AuthRelTypeEnum.USER_REL_ROLE_GROUP.getRelType().equals(relType)) {
            candidates = AuthCandidateUtil.roleGroups(fleaRoleGroupSV);
        } else {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，不支持的用户授权类型【" + relType + "】！");
            return output;
        }

        // 已授权的编号集合
        List<Long> selectedList = new ArrayList<>();
        List<FleaUserRel> userRelList = fleaUserRelSV.getUserRelList(userId, relType);
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

        List<Map<String, Object>> userGroupTreeList = AuthCandidateUtil.userGroups(fleaUserGroupSV);

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

        List<Map<String, Object>> rowList = AuthCandidateUtil.userGroupRows(fleaUserGroupSV, fleaUserSV);

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

        FleaUserGroup fleaUserGroup = fleaUserGroupSV.queryUserGroupInUse(userGroupId);
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

        FleaUserGroupPOJO fleaUserGroupPOJO = new FleaUserGroupPOJO();
        POJOUtils.copyAll(inputUserGroupInfo, fleaUserGroupPOJO);

        fleaUserModuleSV.modifyFleaUserGroup(inputUserGroupInfo.getUserGroupId(), fleaUserGroupPOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，用户组修改成功！");
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

        FleaUserGroup fleaUserGroup = fleaUserGroupSV.queryUserGroupInUse(userGroupId);
        if (ObjectUtils.isEmpty(fleaUserGroup)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，用户组【" + userGroupId + "】不存在或已失效，请刷新用户组列表后重试！");
            return output;
        }

        List<Map<String, Object>> candidates;
        if (AuthRelTypeEnum.USER_GROUP_REL_ROLE.getRelType().equals(relType)) {
            candidates = AuthCandidateUtil.roles(fleaRoleSV);
        } else if (AuthRelTypeEnum.USER_GROUP_REL_ROLE_GROUP.getRelType().equals(relType)) {
            candidates = AuthCandidateUtil.roleGroups(fleaRoleGroupSV);
        } else if (AuthRelTypeEnum.USER_GROUP_REL_USER.getRelType().equals(relType)) {
            candidates = AuthCandidateUtil.users(fleaUserSV);
        } else {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，不支持的用户组授权类型【" + relType + "】！");
            return output;
        }

        List<Long> selectedList = new ArrayList<>();
        List<FleaUserGroupRel> userGroupRelList = fleaUserGroupRelSV.getUserGroupRelList(userGroupId, null, relType);
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
            List<FleaUserRel> relList = fleaUserRelSV.getUserRelList(userId, relType);
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
            List<FleaUserGroupRel> relList = fleaUserGroupRelSV.getUserGroupRelList(userGroupId, null, relType);
            relTypeCounts.put(relType, CollectionUtils.isNotEmpty(relList) ? relList.size() : 0);
        }

        return relTypeCounts;
    }

}
