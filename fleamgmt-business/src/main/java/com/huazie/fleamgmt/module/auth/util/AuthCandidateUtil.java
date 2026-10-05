package com.huazie.fleamgmt.module.auth.util;

import com.huazie.fleaframework.auth.base.function.entity.FleaElement;
import com.huazie.fleaframework.auth.base.function.entity.FleaMenu;
import com.huazie.fleaframework.auth.base.function.entity.FleaOperation;
import com.huazie.fleaframework.auth.base.function.entity.FleaResource;
import com.huazie.fleaframework.auth.base.privilege.entity.FleaPrivilege;
import com.huazie.fleaframework.auth.base.privilege.entity.FleaPrivilegeGroup;
import com.huazie.fleaframework.auth.base.privilege.service.interfaces.IFleaPrivilegeGroupSV;
import com.huazie.fleaframework.auth.base.privilege.service.interfaces.IFleaPrivilegeSV;
import com.huazie.fleaframework.auth.base.role.entity.FleaRole;
import com.huazie.fleaframework.auth.base.role.entity.FleaRoleGroup;
import com.huazie.fleaframework.auth.base.role.service.interfaces.IFleaRoleGroupSV;
import com.huazie.fleaframework.auth.base.role.service.interfaces.IFleaRoleSV;
import com.huazie.fleaframework.auth.base.user.entity.FleaAccount;
import com.huazie.fleaframework.auth.base.user.entity.FleaUser;
import com.huazie.fleaframework.auth.base.user.entity.FleaUserGroup;
import com.huazie.fleaframework.auth.base.user.service.interfaces.IFleaAccountSV;
import com.huazie.fleaframework.auth.base.user.service.interfaces.IFleaUserGroupSV;
import com.huazie.fleaframework.auth.base.user.service.interfaces.IFleaUserSV;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaFunctionModuleSV;
import com.huazie.fleaframework.auth.util.FueluxMenuTree;
import com.huazie.fleaframework.common.CommonConstants;
import com.huazie.fleaframework.common.exceptions.CommonException;
import com.huazie.fleaframework.common.util.CollectionUtils;
import com.huazie.fleaframework.common.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 授权管理模块「可授权方候选列表」构建工具 </p>
 *
 * <p> 授权类页面（用户授权、用户组授权、角色授权、角色组关联、权限关联、权限组关联）
 * 的左侧候选列表来源各异，但产出的都是 Fuelux 树的扁平 / 层级节点结构，
 * 集中在此处构建，避免各 Controller 各写一份。 </p>
 *
 * <p> 说明：本类为静态工具，各服务以入参传入，不依赖 Spring 容器，
 * 以便 business 模块在无 Spring 扫描的前提下被复用。 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public final class AuthCandidateUtil {

    /**
     * <p> 在用状态值（用户/账户/用户组的状态字段：0-删除 1-正常） </p>
     */
    private static final int STATE_IN_USE = 1;

    private AuthCandidateUtil() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * <p> 构建单个 Fuelux 树节点 </p>
     *
     * <p> Fuelux 树要求每个节点都必须有 code，角色、权限、用户等无业务编码的数据以编号占位。 </p>
     *
     * @param id   编号
     * @param code 编码
     * @param name 展示名称
     * @return 节点 Map
     * @since 1.0.0
     */
    private static Map<String, Object> newNode(Long id, String code, String name) {
        Map<String, Object> nodeMap = new HashMap<>();
        nodeMap.put("name", name);
        nodeMap.put("type", "item");
        nodeMap.put("id", id);
        nodeMap.put("code", (code != null && code.length() > 0) ? code : String.valueOf(id));
        nodeMap.put("level", CommonConstants.NumeralConstants.INT_ONE);
        return nodeMap;
    }

    /**
     * <p> 判断数据是否为「在用」状态 </p>
     *
     * @param state 状态值
     * @return true-在用; false-已删除
     * @since 1.0.0
     */
    private static boolean isInUse(Integer state) {
        return state == null || STATE_IN_USE == state;
    }

    /**
     * <p> 用户（账户）候选列表 </p>
     *
     * <p> 用户本身没有独立的登录标识，登录主体为账户，故以账户为主体构建候选列表，
     * 节点编号取账户编号，展示为「昵称（账号）」。 </p>
     *
     * @param fleaAccountSV 账户服务
     * @param fleaUserSV    用户服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> accounts(IFleaAccountSV fleaAccountSV, IFleaUserSV fleaUserSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaAccount> accountList = fleaAccountSV.queryAll("accountId", "asc");
        if (CollectionUtils.isEmpty(accountList)) {
            return candidateList;
        }

        // 用户编号 -> 昵称，避免逐个账户回查用户
        Map<Long, String> userNameMap = new HashMap<>();
        List<FleaUser> userList = fleaUserSV.queryAll();
        if (CollectionUtils.isNotEmpty(userList)) {
            for (FleaUser fleaUser : userList) {
                if (ObjectUtils.isNotEmpty(fleaUser)) {
                    userNameMap.put(fleaUser.getUserId(), fleaUser.getUserName());
                }
            }
        }

        for (FleaAccount fleaAccount : accountList) {
            if (ObjectUtils.isEmpty(fleaAccount) || !isInUse(fleaAccount.getAccountState())) {
                continue;
            }
            String userName = userNameMap.get(fleaAccount.getUserId());
            String displayName = (userName != null && userName.length() > 0)
                    ? (userName + "（" + fleaAccount.getAccountCode() + "）")
                    : fleaAccount.getAccountCode();
            candidateList.add(newNode(fleaAccount.getAccountId(), fleaAccount.getAccountCode(), displayName));
        }

        return candidateList;
    }

    /**
     * <p> 用户候选列表（用户组授权用） </p>
     *
     * @param fleaUserSV 用户服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> users(IFleaUserSV fleaUserSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaUser> userList = fleaUserSV.queryAll("userId", "asc");
        if (CollectionUtils.isEmpty(userList)) {
            return candidateList;
        }

        for (FleaUser fleaUser : userList) {
            if (ObjectUtils.isEmpty(fleaUser) || !isInUse(fleaUser.getUserState())) {
                continue;
            }
            candidateList.add(newNode(fleaUser.getUserId(), null, fleaUser.getUserName()));
        }

        return candidateList;
    }

    /**
     * <p> 用户组候选列表 </p>
     *
     * @param fleaUserGroupSV 用户组服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> userGroups(IFleaUserGroupSV fleaUserGroupSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaUserGroup> userGroupList = fleaUserGroupSV.queryAll("userGroupId", "asc");
        if (CollectionUtils.isEmpty(userGroupList)) {
            return candidateList;
        }

        for (FleaUserGroup fleaUserGroup : userGroupList) {
            if (ObjectUtils.isEmpty(fleaUserGroup) || !isInUse(fleaUserGroup.getUserGroupState())) {
                continue;
            }
            candidateList.add(newNode(fleaUserGroup.getUserGroupId(), null, fleaUserGroup.getUserGroupName()));
        }

        return candidateList;
    }

    /**
     * <p> 角色候选列表 </p>
     *
     * @param fleaRoleSV 角色服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> roles(IFleaRoleSV fleaRoleSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaRole> roleList = fleaRoleSV.queryRolesInUse(null, null);
        if (CollectionUtils.isEmpty(roleList)) {
            return candidateList;
        }

        for (FleaRole fleaRole : roleList) {
            if (ObjectUtils.isEmpty(fleaRole)) {
                continue;
            }
            candidateList.add(newNode(fleaRole.getRoleId(), null, fleaRole.getRoleName()));
        }

        return candidateList;
    }

    /**
     * <p> 角色组候选列表 </p>
     *
     * @param fleaRoleGroupSV 角色组服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> roleGroups(IFleaRoleGroupSV fleaRoleGroupSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaRoleGroup> roleGroupList = fleaRoleGroupSV.queryRoleGroupsInUse(null);
        if (CollectionUtils.isEmpty(roleGroupList)) {
            return candidateList;
        }

        for (FleaRoleGroup fleaRoleGroup : roleGroupList) {
            if (ObjectUtils.isEmpty(fleaRoleGroup)) {
                continue;
            }
            candidateList.add(newNode(fleaRoleGroup.getRoleGroupId(), null, fleaRoleGroup.getRoleGroupName()));
        }

        return candidateList;
    }

    /**
     * <p> 权限候选列表 </p>
     *
     * @param fleaPrivilegeSV 权限服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> privileges(IFleaPrivilegeSV fleaPrivilegeSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaPrivilege> privilegeList = fleaPrivilegeSV.queryPrivilegesInUse(null, null);
        if (CollectionUtils.isEmpty(privilegeList)) {
            return candidateList;
        }

        for (FleaPrivilege fleaPrivilege : privilegeList) {
            if (ObjectUtils.isEmpty(fleaPrivilege)) {
                continue;
            }
            candidateList.add(newNode(fleaPrivilege.getPrivilegeId(), null, fleaPrivilege.getPrivilegeName()));
        }

        return candidateList;
    }

    /**
     * <p> 权限组候选列表 </p>
     *
     * @param fleaPrivilegeGroupSV 权限组服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> privilegeGroups(IFleaPrivilegeGroupSV fleaPrivilegeGroupSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaPrivilegeGroup> privilegeGroupList = fleaPrivilegeGroupSV.queryPrivilegeGroupsInUse(null, null, null);
        if (CollectionUtils.isEmpty(privilegeGroupList)) {
            return candidateList;
        }

        for (FleaPrivilegeGroup fleaPrivilegeGroup : privilegeGroupList) {
            if (ObjectUtils.isEmpty(fleaPrivilegeGroup)) {
                continue;
            }
            candidateList.add(newNode(fleaPrivilegeGroup.getPrivilegeGroupId(), null, fleaPrivilegeGroup.getPrivilegeGroupName()));
        }

        return candidateList;
    }

    /**
     * <p> 菜单候选列表（带层级，权限关联菜单用） </p>
     *
     * @param fleaFunctionModuleSV 功能模块服务
     * @return Fuelux 树层级节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> menus(IFleaFunctionModuleSV fleaFunctionModuleSV) throws CommonException {

        List<FleaMenu> menuList = fleaFunctionModuleSV.queryValidMenus(null);

        Map<String, String> params = new HashMap<>();
        params.put(FueluxMenuTree.FOLDER_ICON_CLASS, "red");

        FueluxMenuTree fueluxMenuTree = new FueluxMenuTree("Flea Menu", params);
        fueluxMenuTree.addAll(menuList);

        return fueluxMenuTree.toMapList(true);
    }

    /**
     * <p> 操作候选列表 </p>
     *
     * @param fleaFunctionModuleSV 功能模块服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> operations(IFleaFunctionModuleSV fleaFunctionModuleSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaOperation> operationList = fleaFunctionModuleSV.queryValidOperations(null);
        if (CollectionUtils.isEmpty(operationList)) {
            return candidateList;
        }

        for (FleaOperation fleaOperation : operationList) {
            if (ObjectUtils.isEmpty(fleaOperation)) {
                continue;
            }
            candidateList.add(newNode(fleaOperation.getOperationId(), fleaOperation.getOperationCode(),
                    fleaOperation.getOperationName() + "【" + fleaOperation.getOperationCode() + "】"));
        }

        return candidateList;
    }

    /**
     * <p> 元素候选列表 </p>
     *
     * @param fleaFunctionModuleSV 功能模块服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> elements(IFleaFunctionModuleSV fleaFunctionModuleSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaElement> elementList = fleaFunctionModuleSV.queryValidElements(null);
        if (CollectionUtils.isEmpty(elementList)) {
            return candidateList;
        }

        for (FleaElement fleaElement : elementList) {
            if (ObjectUtils.isEmpty(fleaElement)) {
                continue;
            }
            candidateList.add(newNode(fleaElement.getElementId(), fleaElement.getElementCode(),
                    fleaElement.getElementName() + "【" + fleaElement.getElementCode() + "】"));
        }

        return candidateList;
    }

    /**
     * <p> 资源候选列表 </p>
     *
     * @param fleaFunctionModuleSV 功能模块服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> resources(IFleaFunctionModuleSV fleaFunctionModuleSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaResource> resourceList = fleaFunctionModuleSV.queryValidResources(null);
        if (CollectionUtils.isEmpty(resourceList)) {
            return candidateList;
        }

        for (FleaResource fleaResource : resourceList) {
            if (ObjectUtils.isEmpty(fleaResource)) {
                continue;
            }
            candidateList.add(newNode(fleaResource.getResourceId(), fleaResource.getResourceCode(),
                    fleaResource.getResourceName() + "【" + fleaResource.getResourceCode() + "】"));
        }

        return candidateList;
    }

    /**
     * <p> 用户明细行列表（用户变更表格用） </p>
     *
     * <p> 与 {@link #accounts(IFleaAccountSV, IFleaUserSV)} 的差异：候选列表只承载
     * 「编号 / 编码 / 展示名」，服务于树与穿梭框；本方法返回账户与用户的完整明细字段，
     * 服务于表格列展示与筛选。账户状态决定账户是否可用，故一并返回。 </p>
     *
     * @param fleaAccountSV   账户服务
     * @param fleaUserSV      用户服务
     * @param fleaUserGroupSV 用户组服务（用于把 group_id 翻译成用户组名称）
     * @return 用户明细行集合
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> userRows(IFleaAccountSV fleaAccountSV, IFleaUserSV fleaUserSV,
                                                     IFleaUserGroupSV fleaUserGroupSV) throws CommonException {

        List<Map<String, Object>> rowList = new ArrayList<>();

        List<FleaAccount> accountList = fleaAccountSV.queryAll("accountId", "asc");
        if (CollectionUtils.isEmpty(accountList)) {
            return rowList;
        }

        // 用户编号 -> 用户信息
        Map<Long, FleaUser> userMap = new HashMap<>();
        List<FleaUser> userList = fleaUserSV.queryAll("userId", "asc");
        if (CollectionUtils.isNotEmpty(userList)) {
            for (FleaUser fleaUser : userList) {
                if (ObjectUtils.isNotEmpty(fleaUser)) {
                    userMap.put(fleaUser.getUserId(), fleaUser);
                }
            }
        }

        // 用户组编号 -> 用户组名称
        Map<Long, String> userGroupNameMap = new HashMap<>();
        List<FleaUserGroup> userGroupList = fleaUserGroupSV.queryAll();
        if (CollectionUtils.isNotEmpty(userGroupList)) {
            for (FleaUserGroup fleaUserGroup : userGroupList) {
                if (ObjectUtils.isNotEmpty(fleaUserGroup)) {
                    userGroupNameMap.put(fleaUserGroup.getUserGroupId(), fleaUserGroup.getUserGroupName());
                }
            }
        }

        for (FleaAccount fleaAccount : accountList) {
            if (ObjectUtils.isEmpty(fleaAccount)) {
                continue;
            }

            FleaUser fleaUser = userMap.get(fleaAccount.getUserId());

            Map<String, Object> rowMap = new HashMap<>();
            rowMap.put("accountId", fleaAccount.getAccountId());
            rowMap.put("accountCode", fleaAccount.getAccountCode());
            rowMap.put("userId", fleaAccount.getUserId());
            rowMap.put("accountState", fleaAccount.getAccountState());
            rowMap.put("effectiveDate", AuthBizUtils.formatDate(fleaAccount.getEffectiveDate()));
            rowMap.put("expiryDate", AuthBizUtils.formatDate(fleaAccount.getExpiryDate()));

            if (ObjectUtils.isNotEmpty(fleaUser)) {
                rowMap.put("userName", fleaUser.getUserName());
                rowMap.put("userSex", fleaUser.getUserSex());
                rowMap.put("userEmail", fleaUser.getUserEmail());
                rowMap.put("userPhone", fleaUser.getUserPhone());
                rowMap.put("userAddress", fleaUser.getUserAddress());
                rowMap.put("userState", fleaUser.getUserState());
                rowMap.put("groupId", fleaUser.getGroupId());
                rowMap.put("groupName", userGroupNameMap.get(fleaUser.getGroupId()));
            } else {
                // 账户存在但用户信息缺失时，以账号兜底展示，避免表格出现空行
                rowMap.put("userName", fleaAccount.getAccountCode());
            }

            rowList.add(rowMap);
        }

        return rowList;
    }

    /**
     * <p> 用户组明细行列表（用户组变更表格用） </p>
     *
     * <p> 在用户组基础信息之外，附带该组下的用户数量（memberCount），
     * 便于页面在表格中直观呈现分组规模。 </p>
     *
     * @param fleaUserGroupSV 用户组服务
     * @param fleaUserSV      用户服务（用于统计各组成员数）
     * @return 用户组明细行集合
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> userGroupRows(IFleaUserGroupSV fleaUserGroupSV,
                                                          IFleaUserSV fleaUserSV) throws CommonException {

        List<Map<String, Object>> rowList = new ArrayList<>();

        List<FleaUserGroup> userGroupList = fleaUserGroupSV.queryAll("userGroupId", "asc");
        if (CollectionUtils.isEmpty(userGroupList)) {
            return rowList;
        }

        // 用户组编号 -> 成员数
        Map<Long, Integer> memberCountMap = new HashMap<>();
        List<FleaUser> userList = fleaUserSV.queryAll("userId", "asc");
        if (CollectionUtils.isNotEmpty(userList)) {
            for (FleaUser fleaUser : userList) {
                if (ObjectUtils.isEmpty(fleaUser) || fleaUser.getGroupId() == null) {
                    continue;
                }
                Integer memberCount = memberCountMap.get(fleaUser.getGroupId());
                memberCountMap.put(fleaUser.getGroupId(), (memberCount == null) ? 1 : memberCount + 1);
            }
        }

        for (FleaUserGroup fleaUserGroup : userGroupList) {
            if (ObjectUtils.isEmpty(fleaUserGroup)) {
                continue;
            }

            Integer memberCount = memberCountMap.get(fleaUserGroup.getUserGroupId());

            Map<String, Object> rowMap = new HashMap<>();
            rowMap.put("userGroupId", fleaUserGroup.getUserGroupId());
            rowMap.put("userGroupName", fleaUserGroup.getUserGroupName());
            rowMap.put("userGroupDesc", fleaUserGroup.getUserGroupDesc());
            rowMap.put("userGroupState", fleaUserGroup.getUserGroupState());
            rowMap.put("remarks", fleaUserGroup.getRemarks());
            rowMap.put("memberCount", (memberCount == null) ? 0 : memberCount);

            rowList.add(rowMap);
        }

        return rowList;
    }

    /**
     * <p> 角色明细行列表（角色变更表格用） </p>
     *
     * <p> 与 {@link #roles(IFleaRoleSV)} 同源（仅返回在用角色），
     * 额外携带角色组名称，便于表格直接展示归属。 </p>
     *
     * @param fleaRoleSV      角色服务
     * @param fleaRoleGroupSV 角色组服务（用于把 group_id 翻译成角色组名称）
     * @return 角色明细行集合
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> roleRows(IFleaRoleSV fleaRoleSV,
                                                     IFleaRoleGroupSV fleaRoleGroupSV) throws CommonException {

        List<Map<String, Object>> rowList = new ArrayList<>();

        List<FleaRole> roleList = fleaRoleSV.queryRolesInUse(null, null);
        if (CollectionUtils.isEmpty(roleList)) {
            return rowList;
        }

        // 角色组编号 -> 角色组名称
        Map<Long, String> roleGroupNameMap = roleGroupNameMap(fleaRoleGroupSV);

        for (FleaRole fleaRole : roleList) {
            if (ObjectUtils.isEmpty(fleaRole)) {
                continue;
            }

            Map<String, Object> rowMap = new HashMap<>();
            rowMap.put("roleId", fleaRole.getRoleId());
            rowMap.put("roleName", fleaRole.getRoleName());
            rowMap.put("roleDesc", fleaRole.getRoleDesc());
            rowMap.put("groupId", fleaRole.getGroupId());
            rowMap.put("groupName", roleGroupNameMap.get(fleaRole.getGroupId()));
            rowMap.put("roleState", fleaRole.getRoleState());
            rowMap.put("remarks", fleaRole.getRemarks());

            rowList.add(rowMap);
        }

        return rowList;
    }

    /**
     * <p> 角色组明细行列表（角色组变更表格用） </p>
     *
     * <p> 在角色组基础信息之外，附带该组下的在用角色数量（memberCount），
     * 便于页面在表格中直观呈现分组规模。 </p>
     *
     * @param fleaRoleGroupSV 角色组服务
     * @param fleaRoleSV      角色服务（用于统计各组角色数）
     * @return 角色组明细行集合
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> roleGroupRows(IFleaRoleGroupSV fleaRoleGroupSV,
                                                          IFleaRoleSV fleaRoleSV) throws CommonException {

        List<Map<String, Object>> rowList = new ArrayList<>();

        List<FleaRoleGroup> roleGroupList = fleaRoleGroupSV.queryRoleGroupsInUse(null);
        if (CollectionUtils.isEmpty(roleGroupList)) {
            return rowList;
        }

        // 角色组编号 -> 成员角色数
        Map<Long, Integer> memberCountMap = roleGroupMemberCount(roleList(fleaRoleSV));

        for (FleaRoleGroup fleaRoleGroup : roleGroupList) {
            if (ObjectUtils.isEmpty(fleaRoleGroup)) {
                continue;
            }

            Integer memberCount = memberCountMap.get(fleaRoleGroup.getRoleGroupId());

            Map<String, Object> rowMap = new HashMap<>();
            rowMap.put("roleGroupId", fleaRoleGroup.getRoleGroupId());
            rowMap.put("roleGroupName", fleaRoleGroup.getRoleGroupName());
            rowMap.put("roleGroupDesc", fleaRoleGroup.getRoleGroupDesc());
            rowMap.put("roleGroupState", fleaRoleGroup.getRoleGroupState());
            rowMap.put("remarks", fleaRoleGroup.getRemarks());
            rowMap.put("memberCount", (memberCount == null) ? 0 : memberCount);

            rowList.add(rowMap);
        }

        return rowList;
    }

    /**
     * <p> 权限明细行列表（权限变更表格用） </p>
     *
     * <p> 与 {@link #privileges(IFleaPrivilegeSV)} 同源（仅返回在用权限），
     * 额外携带权限组名称，便于表格直接展示归属。 </p>
     *
     * @param fleaPrivilegeSV      权限服务
     * @param fleaPrivilegeGroupSV 权限组服务（用于把 group_id 翻译成权限组名称）
     * @return 权限明细行集合
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> privilegeRows(IFleaPrivilegeSV fleaPrivilegeSV,
                                                          IFleaPrivilegeGroupSV fleaPrivilegeGroupSV) throws CommonException {

        List<Map<String, Object>> rowList = new ArrayList<>();

        List<FleaPrivilege> privilegeList = fleaPrivilegeSV.queryPrivilegesInUse(null, null);
        if (CollectionUtils.isEmpty(privilegeList)) {
            return rowList;
        }

        // 权限组编号 -> 权限组名称
        Map<Long, String> privilegeGroupNameMap = privilegeGroupNameMap(fleaPrivilegeGroupSV);

        for (FleaPrivilege fleaPrivilege : privilegeList) {
            if (ObjectUtils.isEmpty(fleaPrivilege)) {
                continue;
            }

            Map<String, Object> rowMap = new HashMap<>();
            rowMap.put("privilegeId", fleaPrivilege.getPrivilegeId());
            rowMap.put("privilegeName", fleaPrivilege.getPrivilegeName());
            rowMap.put("privilegeDesc", fleaPrivilege.getPrivilegeDesc());
            rowMap.put("groupId", fleaPrivilege.getGroupId());
            rowMap.put("groupName", privilegeGroupNameMap.get(fleaPrivilege.getGroupId()));
            rowMap.put("privilegeState", fleaPrivilege.getPrivilegeState());
            rowMap.put("remarks", fleaPrivilege.getRemarks());

            rowList.add(rowMap);
        }

        return rowList;
    }

    /**
     * <p> 权限组明细行列表（权限组变更表格用） </p>
     *
     * <p> 在权限组基础信息之外，附带该组下的在用权限数量（memberCount）。
     * 额外返回 isMain / functionType，供表格区分「四类权限组」语义分类。 </p>
     *
     * @param fleaPrivilegeGroupSV 权限组服务
     * @param fleaPrivilegeSV      权限服务（用于统计各组权限数）
     * @return 权限组明细行集合
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> privilegeGroupRows(IFleaPrivilegeGroupSV fleaPrivilegeGroupSV,
                                                               IFleaPrivilegeSV fleaPrivilegeSV) throws CommonException {

        List<Map<String, Object>> rowList = new ArrayList<>();

        List<FleaPrivilegeGroup> privilegeGroupList = fleaPrivilegeGroupSV.queryPrivilegeGroupsInUse(null, null, null);
        if (CollectionUtils.isEmpty(privilegeGroupList)) {
            return rowList;
        }

        // 权限组编号 -> 成员权限数
        Map<Long, Integer> memberCountMap = privilegeGroupMemberCount(privilegeList(fleaPrivilegeSV));

        for (FleaPrivilegeGroup fleaPrivilegeGroup : privilegeGroupList) {
            if (ObjectUtils.isEmpty(fleaPrivilegeGroup)) {
                continue;
            }

            Integer memberCount = memberCountMap.get(fleaPrivilegeGroup.getPrivilegeGroupId());

            Map<String, Object> rowMap = new HashMap<>();
            rowMap.put("privilegeGroupId", fleaPrivilegeGroup.getPrivilegeGroupId());
            rowMap.put("privilegeGroupName", fleaPrivilegeGroup.getPrivilegeGroupName());
            rowMap.put("privilegeGroupDesc", fleaPrivilegeGroup.getPrivilegeGroupDesc());
            rowMap.put("privilegeGroupState", fleaPrivilegeGroup.getPrivilegeGroupState());
            rowMap.put("isMain", fleaPrivilegeGroup.getIsMain());
            rowMap.put("functionType", fleaPrivilegeGroup.getFunctionType());
            rowMap.put("remarks", fleaPrivilegeGroup.getRemarks());
            rowMap.put("memberCount", (memberCount == null) ? 0 : memberCount);

            rowList.add(rowMap);
        }

        return rowList;
    }

    /**
     * <p> 操作明细行列表（操作变更表格用） </p>
     *
     * @param fleaFunctionModuleSV 功能模块服务
     * @return 操作明细行集合
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> operationRows(IFleaFunctionModuleSV fleaFunctionModuleSV) throws CommonException {

        List<Map<String, Object>> rowList = new ArrayList<>();

        List<FleaOperation> operationList = fleaFunctionModuleSV.queryValidOperations(null);
        if (CollectionUtils.isEmpty(operationList)) {
            return rowList;
        }

        for (FleaOperation fleaOperation : operationList) {
            if (ObjectUtils.isEmpty(fleaOperation)) {
                continue;
            }

            Map<String, Object> rowMap = new HashMap<>();
            rowMap.put("operationId", fleaOperation.getOperationId());
            rowMap.put("operationCode", fleaOperation.getOperationCode());
            rowMap.put("operationName", fleaOperation.getOperationName());
            rowMap.put("operationDesc", fleaOperation.getOperationDesc());
            rowMap.put("operationState", fleaOperation.getOperationState());

            rowList.add(rowMap);
        }

        return rowList;
    }

    /**
     * <p> 元素明细行列表（元素变更表格用） </p>
     *
     * @param fleaFunctionModuleSV 功能模块服务
     * @return 元素明细行集合
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> elementRows(IFleaFunctionModuleSV fleaFunctionModuleSV) throws CommonException {

        List<Map<String, Object>> rowList = new ArrayList<>();

        List<FleaElement> elementList = fleaFunctionModuleSV.queryValidElements(null);
        if (CollectionUtils.isEmpty(elementList)) {
            return rowList;
        }

        for (FleaElement fleaElement : elementList) {
            if (ObjectUtils.isEmpty(fleaElement)) {
                continue;
            }

            Map<String, Object> rowMap = new HashMap<>();
            rowMap.put("elementId", fleaElement.getElementId());
            rowMap.put("elementCode", fleaElement.getElementCode());
            rowMap.put("elementName", fleaElement.getElementName());
            rowMap.put("elementType", fleaElement.getElementType());
            rowMap.put("elementDesc", fleaElement.getElementDesc());
            rowMap.put("elementState", fleaElement.getElementState());

            rowList.add(rowMap);
        }

        return rowList;
    }

    /**
     * <p> 资源明细行列表（资源变更表格用） </p>
     *
     * @param fleaFunctionModuleSV 功能模块服务
     * @return 资源明细行集合
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> resourceRows(IFleaFunctionModuleSV fleaFunctionModuleSV) throws CommonException {

        List<Map<String, Object>> rowList = new ArrayList<>();

        List<FleaResource> resourceList = fleaFunctionModuleSV.queryValidResources(null);
        if (CollectionUtils.isEmpty(resourceList)) {
            return rowList;
        }

        for (FleaResource fleaResource : resourceList) {
            if (ObjectUtils.isEmpty(fleaResource)) {
                continue;
            }

            Map<String, Object> rowMap = new HashMap<>();
            rowMap.put("resourceId", fleaResource.getResourceId());
            rowMap.put("resourceCode", fleaResource.getResourceCode());
            rowMap.put("resourceName", fleaResource.getResourceName());
            rowMap.put("resourceDesc", fleaResource.getResourceDesc());
            rowMap.put("resourceState", fleaResource.getResourceState());

            rowList.add(rowMap);
        }

        return rowList;
    }

    /**
     * <p> 角色组编号 -> 名称 映射 </p>
     *
     * @param fleaRoleGroupSV 角色组服务
     * @return 映射 Map
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    private static Map<Long, String> roleGroupNameMap(IFleaRoleGroupSV fleaRoleGroupSV) throws CommonException {

        Map<Long, String> nameMap = new HashMap<>();

        List<FleaRoleGroup> roleGroupList = fleaRoleGroupSV.queryRoleGroupsInUse(null);
        if (CollectionUtils.isNotEmpty(roleGroupList)) {
            for (FleaRoleGroup fleaRoleGroup : roleGroupList) {
                if (ObjectUtils.isNotEmpty(fleaRoleGroup)) {
                    nameMap.put(fleaRoleGroup.getRoleGroupId(), fleaRoleGroup.getRoleGroupName());
                }
            }
        }

        return nameMap;
    }

    /**
     * <p> 权限组编号 -> 名称 映射 </p>
     *
     * @param fleaPrivilegeGroupSV 权限组服务
     * @return 映射 Map
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    private static Map<Long, String> privilegeGroupNameMap(IFleaPrivilegeGroupSV fleaPrivilegeGroupSV) throws CommonException {

        Map<Long, String> nameMap = new HashMap<>();

        List<FleaPrivilegeGroup> privilegeGroupList = fleaPrivilegeGroupSV.queryPrivilegeGroupsInUse(null, null, null);
        if (CollectionUtils.isNotEmpty(privilegeGroupList)) {
            for (FleaPrivilegeGroup fleaPrivilegeGroup : privilegeGroupList) {
                if (ObjectUtils.isNotEmpty(fleaPrivilegeGroup)) {
                    nameMap.put(fleaPrivilegeGroup.getPrivilegeGroupId(), fleaPrivilegeGroup.getPrivilegeGroupName());
                }
            }
        }

        return nameMap;
    }

    /**
     * <p> 统计角色组下的在用角色数量 </p>
     *
     * @param roleList 角色列表（在用）
     * @return 角色组编号 -> 角色数
     * @since 1.0.0
     */
    private static Map<Long, Integer> roleGroupMemberCount(List<FleaRole> roleList) {

        Map<Long, Integer> countMap = new HashMap<>();
        if (CollectionUtils.isEmpty(roleList)) {
            return countMap;
        }

        for (FleaRole fleaRole : roleList) {
            if (ObjectUtils.isEmpty(fleaRole) || fleaRole.getGroupId() == null) {
                continue;
            }

            Integer count = countMap.get(fleaRole.getGroupId());
            countMap.put(fleaRole.getGroupId(), (count == null) ? 1 : count + 1);
        }

        return countMap;
    }

    /**
     * <p> 统计权限组下的在用权限数量 </p>
     *
     * @param privilegeList 权限列表（在用）
     * @return 权限组编号 -> 权限数
     * @since 1.0.0
     */
    private static Map<Long, Integer> privilegeGroupMemberCount(List<FleaPrivilege> privilegeList) {

        Map<Long, Integer> countMap = new HashMap<>();
        if (CollectionUtils.isEmpty(privilegeList)) {
            return countMap;
        }

        for (FleaPrivilege fleaPrivilege : privilegeList) {
            if (ObjectUtils.isEmpty(fleaPrivilege) || fleaPrivilege.getGroupId() == null) {
                continue;
            }

            Integer count = countMap.get(fleaPrivilege.getGroupId());
            countMap.put(fleaPrivilege.getGroupId(), (count == null) ? 1 : count + 1);
        }

        return countMap;
    }

    private static List<FleaRole> roleList(IFleaRoleSV fleaRoleSV) throws CommonException {
        return fleaRoleSV.queryRolesInUse(null, null);
    }

    private static List<FleaPrivilege> privilegeList(IFleaPrivilegeSV fleaPrivilegeSV) throws CommonException {
        return fleaPrivilegeSV.queryPrivilegesInUse(null, null);
    }

}
