package com.huazie.fleamgmt.module.auth.util;

import com.huazie.fleaframework.auth.base.function.entity.FleaElement;
import com.huazie.fleaframework.auth.base.function.entity.FleaMenu;
import com.huazie.fleaframework.auth.base.function.entity.FleaOperation;
import com.huazie.fleaframework.auth.base.function.entity.FleaResource;
import com.huazie.fleaframework.auth.base.organization.entity.FleaOrganization;
import com.huazie.fleaframework.auth.base.privilege.entity.FleaPrivilege;
import com.huazie.fleaframework.auth.base.privilege.entity.FleaPrivilegeGroup;
import com.huazie.fleaframework.auth.base.role.entity.FleaRole;
import com.huazie.fleaframework.auth.base.role.entity.FleaRoleGroup;
import com.huazie.fleaframework.auth.base.user.entity.FleaAccount;
import com.huazie.fleaframework.auth.base.user.entity.FleaUser;
import com.huazie.fleaframework.auth.base.user.entity.FleaUserGroup;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaFunctionModuleSV;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaOrgModuleSV;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaPrivilegeModuleSV;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaRoleModuleSV;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaUserModuleSV;
import com.huazie.fleaframework.common.CommonConstants;
import com.huazie.fleaframework.common.exceptions.CommonException;
import com.huazie.fleaframework.common.util.CollectionUtils;
import com.huazie.fleaframework.common.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
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

    /**
     * <p> 组织类型：公司 </p>
     */
    private static final int ORG_TYPE_COMPANY = 1;

    /**
     * <p> 组织类型：部门 </p>
     */
    private static final int ORG_TYPE_DEPARTMENT = 2;

    /**
     * <p> 组织类型：小组 </p>
     */
    private static final int ORG_TYPE_GROUP = 3;

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
     * @param fleaUserModuleSV 用户模块服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> accounts(IFleaUserModuleSV fleaUserModuleSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaAccount> accountList = fleaUserModuleSV.queryAllAccounts("accountId", "asc");
        if (CollectionUtils.isEmpty(accountList)) {
            return candidateList;
        }

        // 用户编号 -> 昵称，避免逐个账户回查用户
        Map<Long, String> userNameMap = new HashMap<>();
        List<FleaUser> userList = fleaUserModuleSV.queryAllUsers(null, null);
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
     * <p> 组织候选列表（新增组织的父组织下拉、用户关联组织页的授权主体列表用） </p>
     *
     * <p> 按树形先序（DFS）展开：父组织紧跟其子组织，同级按编号升序，
     * 非顶级组织展示名带层级缩进（全角空格 + 「└」），与变更页父组织下拉样式一致。 </p>
     *
     * @param fleaOrgModuleSV 组织模块服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> organizations(IFleaOrgModuleSV fleaOrgModuleSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaOrganization> orgList = fleaOrgModuleSV.queryAllValidOrganizations();
        if (CollectionUtils.isEmpty(orgList)) {
            return candidateList;
        }

        // 组织列表按树形先序（DFS）排列：父组织紧跟其子组织，同级按编号升序
        List<FleaOrganization> orderedList = orderOrganizationsByTree(orgList);

        // 缩进层级直接取实体维护的 org_level（父组织变更时已联动重算）
        for (FleaOrganization fleaOrganization : orderedList) {
            Integer orgLevel = fleaOrganization.getOrgLevel();
            candidateList.add(newNode(fleaOrganization.getOrgId(), fleaOrganization.getOrgCode(),
                    orgIndent((orgLevel == null) ? 1 : orgLevel) + fleaOrganization.getOrgName()
                            + "【" + fleaOrganization.getOrgCode() + "】"));
        }

        return candidateList;
    }

    /**
     * <p> 组织列表按树形先序（DFS）排列 </p>
     *
     * <p> 父组织紧跟其子组织，同级按编号升序；父组织不在列表内的视作顶级。
     * 脏数据父子成环时环上组织不会被先序遍历输出，末尾按编号升序兜底追加，保证不丢数据。 </p>
     *
     * @param orgList 组织列表
     * @return 树形先序排列后的新列表（不改变入参列表）
     * @since 1.0.0
     */
    private static List<FleaOrganization> orderOrganizationsByTree(List<FleaOrganization> orgList) {

        List<FleaOrganization> orderedList = new ArrayList<>();
        if (CollectionUtils.isEmpty(orgList)) {
            return orderedList;
        }

        // 组织编号 -> 组织，用于识别父组织是否在列表内
        Map<Long, FleaOrganization> orgMap = new HashMap<>();
        for (FleaOrganization fleaOrganization : orgList) {
            if (ObjectUtils.isNotEmpty(fleaOrganization)) {
                orgMap.put(fleaOrganization.getOrgId(), fleaOrganization);
            }
        }

        // 父组织编号 -> 子组织列表；父组织不在列表内的视作顶级
        Map<Long, List<FleaOrganization>> childrenMap = new HashMap<>();
        List<FleaOrganization> rootList = new ArrayList<>();
        for (FleaOrganization fleaOrganization : orgList) {
            if (ObjectUtils.isEmpty(fleaOrganization)) {
                continue;
            }
            FleaOrganization parentOrg = orgMap.get(fleaOrganization.getParentId());
            if (parentOrg != null) {
                List<FleaOrganization> children = childrenMap.get(parentOrg.getOrgId());
                if (children == null) {
                    children = new ArrayList<>();
                    childrenMap.put(parentOrg.getOrgId(), children);
                }
                children.add(fleaOrganization);
            } else {
                rootList.add(fleaOrganization);
            }
        }

        sortOrganizations(rootList);
        for (FleaOrganization rootOrg : rootList) {
            collectOrganizations(rootOrg, childrenMap, orderedList);
        }

        // 防御脏数据父子成环导致的遗漏：未输出的组织按编号升序兜底追加
        if (orderedList.size() < orgMap.size()) {
            List<FleaOrganization> missedList = new ArrayList<>();
            for (FleaOrganization fleaOrganization : orgList) {
                if (ObjectUtils.isNotEmpty(fleaOrganization) && !orderedList.contains(fleaOrganization)) {
                    missedList.add(fleaOrganization);
                }
            }
            sortOrganizations(missedList);
            orderedList.addAll(missedList);
        }

        return orderedList;
    }

    /**
     * <p> 以树形先序收集单个组织及其子树 </p>
     *
     * @param fleaOrganization 当前组织
     * @param childrenMap      父组织编号 -> 子组织列表
     * @param orderedList      有序列表（输出）
     * @since 1.0.0
     */
    private static void collectOrganizations(FleaOrganization fleaOrganization,
            Map<Long, List<FleaOrganization>> childrenMap, List<FleaOrganization> orderedList) {

        orderedList.add(fleaOrganization);

        List<FleaOrganization> children = childrenMap.get(fleaOrganization.getOrgId());
        if (CollectionUtils.isNotEmpty(children)) {
            sortOrganizations(children);
            for (FleaOrganization childOrg : children) {
                collectOrganizations(childOrg, childrenMap, orderedList);
            }
        }
    }

    /**
     * <p> 组织列表按编号升序排序 </p>
     *
     * @param orgList 组织列表
     * @since 1.0.0
     */
    private static void sortOrganizations(List<FleaOrganization> orgList) {
        Collections.sort(orgList, new Comparator<FleaOrganization>() {
            @Override
            public int compare(FleaOrganization o1, FleaOrganization o2) {
                return o1.getOrgId().compareTo(o2.getOrgId());
            }
        });
    }

    /**
     * <p> 按层级生成展示缩进：第 N 层缩进 N-1 个全角空格，非顶级组织前加树形符号 </p>
     *
     * @param level 层级
     * @return 缩进前缀
     * @since 1.0.0
     */
    private static String orgIndent(int level) {
        StringBuilder indentBuilder = new StringBuilder();
        for (int i = 1; i < level; i++) {
            indentBuilder.append("\u3000");
        }
        return indentBuilder.length() > 0 ? indentBuilder + "└ " : "";
    }

    /**
     * <p> 用户候选列表（用户组授权用） </p>
     *
     * @param fleaUserModuleSV 用户模块服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> users(IFleaUserModuleSV fleaUserModuleSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaUser> userList = fleaUserModuleSV.queryAllUsers("userId", "asc");
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
     * @param fleaUserModuleSV 用户模块服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> userGroups(IFleaUserModuleSV fleaUserModuleSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaUserGroup> userGroupList = fleaUserModuleSV.queryAllUserGroups("userGroupId", "asc");
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
     * @param fleaRoleModuleSV 角色模块服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> roles(IFleaRoleModuleSV fleaRoleModuleSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaRole> roleList = fleaRoleModuleSV.queryRolesInUse(null, null);
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
     * @param fleaRoleModuleSV 角色模块服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> roleGroups(IFleaRoleModuleSV fleaRoleModuleSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaRoleGroup> roleGroupList = fleaRoleModuleSV.queryRoleGroupsInUse(null);
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
     * @param fleaPrivilegeModuleSV 权限模块服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> privileges(IFleaPrivilegeModuleSV fleaPrivilegeModuleSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaPrivilege> privilegeList = fleaPrivilegeModuleSV.queryPrivilegesInUse(null, null);
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
     * @param fleaPrivilegeModuleSV 权限模块服务
     * @return Fuelux 树扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> privilegeGroups(IFleaPrivilegeModuleSV fleaPrivilegeModuleSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaPrivilegeGroup> privilegeGroupList = fleaPrivilegeModuleSV.queryPrivilegeGroupsInUse(null, null, null);
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
     * <p> 菜单候选列表（权限关联菜单用） </p>
     *
     * <p> 授权页候选列表为扁平结构，这里将菜单展开为扁平节点，
     * 展示名带上「顶级菜单 / ... / 当前菜单」的层级路径，便于区分同名菜单。 </p>
     *
     * @param fleaFunctionModuleSV 功能模块服务
     * @return 候选扁平节点列表
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> menus(IFleaFunctionModuleSV fleaFunctionModuleSV) throws CommonException {

        List<Map<String, Object>> candidateList = new ArrayList<>();

        List<FleaMenu> menuList = fleaFunctionModuleSV.queryValidMenus(null);
        if (CollectionUtils.isEmpty(menuList)) {
            return candidateList;
        }

        // 菜单编号 -> 菜单，用于向上回溯父菜单，拼接层级路径
        Map<Long, FleaMenu> menuMap = new HashMap<>();
        for (FleaMenu fleaMenu : menuList) {
            if (ObjectUtils.isNotEmpty(fleaMenu)) {
                menuMap.put(fleaMenu.getMenuId(), fleaMenu);
            }
        }

        for (FleaMenu fleaMenu : menuList) {
            if (ObjectUtils.isEmpty(fleaMenu)) {
                continue;
            }
            candidateList.add(newNode(fleaMenu.getMenuId(), fleaMenu.getMenuCode(), menuPath(menuMap, fleaMenu)));
        }

        return candidateList;
    }

    /**
     * <p> 拼接菜单的层级展示路径（顶级菜单 / ... / 当前菜单） </p>
     *
     * @param menuMap  菜单编号 -> 菜单 映射
     * @param fleaMenu 当前菜单
     * @return 层级展示路径
     * @since 1.0.0
     */
    private static String menuPath(Map<Long, FleaMenu> menuMap, FleaMenu fleaMenu) {

        LinkedList<String> pathList = new LinkedList<>();
        pathList.addFirst(fleaMenu.getMenuName());

        // 向上回溯父菜单；以映射大小为上限，防御脏数据导致的父子成环
        FleaMenu parentMenu = menuMap.get(fleaMenu.getParentId());
        while (ObjectUtils.isNotEmpty(parentMenu) && pathList.size() <= menuMap.size()) {
            pathList.addFirst(parentMenu.getMenuName());
            parentMenu = menuMap.get(parentMenu.getParentId());
        }

        StringBuilder pathBuilder = new StringBuilder();
        for (String menuName : pathList) {
            if (pathBuilder.length() > 0) {
                pathBuilder.append(" / ");
            }
            pathBuilder.append(menuName);
        }
        return pathBuilder.toString();
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
     * <p> 与 {@link #accounts(IFleaUserModuleSV)} 的差异：候选列表只承载
     * 「编号 / 编码 / 展示名」，服务于树与穿梭框；本方法返回账户与用户的完整明细字段，
     * 服务于表格列展示与筛选。账户状态决定账户是否可用，故一并返回。 </p>
     *
     * @param fleaUserModuleSV 用户模块服务
     * @return 用户明细行集合
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> userRows(IFleaUserModuleSV fleaUserModuleSV) throws CommonException {

        List<Map<String, Object>> rowList = new ArrayList<>();

        List<FleaAccount> accountList = fleaUserModuleSV.queryAllAccounts("accountId", "asc");
        if (CollectionUtils.isEmpty(accountList)) {
            return rowList;
        }

        // 用户编号 -> 用户信息
        Map<Long, FleaUser> userMap = new HashMap<>();
        List<FleaUser> userList = fleaUserModuleSV.queryAllUsers("userId", "asc");
        if (CollectionUtils.isNotEmpty(userList)) {
            for (FleaUser fleaUser : userList) {
                if (ObjectUtils.isNotEmpty(fleaUser)) {
                    userMap.put(fleaUser.getUserId(), fleaUser);
                }
            }
        }

        // 用户组编号 -> 用户组名称
        Map<Long, String> userGroupNameMap = new HashMap<>();
        List<FleaUserGroup> userGroupList = fleaUserModuleSV.queryAllUserGroups(null, null);
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
     * @param fleaUserModuleSV 用户模块服务
     * @return 用户组明细行集合
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> userGroupRows(IFleaUserModuleSV fleaUserModuleSV) throws CommonException {

        List<Map<String, Object>> rowList = new ArrayList<>();

        List<FleaUserGroup> userGroupList = fleaUserModuleSV.queryAllUserGroups("userGroupId", "asc");
        if (CollectionUtils.isEmpty(userGroupList)) {
            return rowList;
        }

        // 用户组编号 -> 成员数
        Map<Long, Integer> memberCountMap = new HashMap<>();
        List<FleaUser> userList = fleaUserModuleSV.queryAllUsers("userId", "asc");
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
     * <p> 与 {@link #roles(IFleaRoleModuleSV)} 同源（仅返回在用角色），
     * 额外携带角色组名称，便于表格直接展示归属。 </p>
     *
     * @param fleaRoleModuleSV 角色模块服务
     * @return 角色明细行集合
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> roleRows(IFleaRoleModuleSV fleaRoleModuleSV) throws CommonException {

        List<Map<String, Object>> rowList = new ArrayList<>();

        List<FleaRole> roleList = fleaRoleModuleSV.queryRolesInUse(null, null);
        if (CollectionUtils.isEmpty(roleList)) {
            return rowList;
        }

        // 角色组编号 -> 角色组名称
        Map<Long, String> roleGroupNameMap = roleGroupNameMap(fleaRoleModuleSV);

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
     * @param fleaRoleModuleSV 角色模块服务
     * @return 角色组明细行集合
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> roleGroupRows(IFleaRoleModuleSV fleaRoleModuleSV) throws CommonException {

        List<Map<String, Object>> rowList = new ArrayList<>();

        List<FleaRoleGroup> roleGroupList = fleaRoleModuleSV.queryRoleGroupsInUse(null);
        if (CollectionUtils.isEmpty(roleGroupList)) {
            return rowList;
        }

        // 角色组编号 -> 成员角色数
        Map<Long, Integer> memberCountMap = roleGroupMemberCount(roleList(fleaRoleModuleSV));

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
     * <p> 与 {@link #privileges(IFleaPrivilegeModuleSV)} 同源（仅返回在用权限），
     * 额外携带权限组名称，便于表格直接展示归属。 </p>
     *
     * @param fleaPrivilegeModuleSV 权限模块服务
     * @return 权限明细行集合
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> privilegeRows(IFleaPrivilegeModuleSV fleaPrivilegeModuleSV) throws CommonException {

        List<Map<String, Object>> rowList = new ArrayList<>();

        List<FleaPrivilege> privilegeList = fleaPrivilegeModuleSV.queryPrivilegesInUse(null, null);
        if (CollectionUtils.isEmpty(privilegeList)) {
            return rowList;
        }

        // 权限组编号 -> 权限组名称
        Map<Long, String> privilegeGroupNameMap = privilegeGroupNameMap(fleaPrivilegeModuleSV);

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
     * @param fleaPrivilegeModuleSV 权限模块服务
     * @return 权限组明细行集合
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> privilegeGroupRows(IFleaPrivilegeModuleSV fleaPrivilegeModuleSV) throws CommonException {

        List<Map<String, Object>> rowList = new ArrayList<>();

        List<FleaPrivilegeGroup> privilegeGroupList = fleaPrivilegeModuleSV.queryPrivilegeGroupsInUse(null, null, null);
        if (CollectionUtils.isEmpty(privilegeGroupList)) {
            return rowList;
        }

        // 权限组编号 -> 成员权限数
        Map<Long, Integer> memberCountMap = privilegeGroupMemberCount(privilegeList(fleaPrivilegeModuleSV));

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
     * <p> 组织明细行列表（组织变更表格用） </p>
     *
     * <p> 在组织基础信息之外，附带父组织名称，便于表格直接展示归属；
     * 另附 orgTypeName（公司/部门/小组），供表格直接展示类型。
     * 行顺序与候选列表一致，按树形先序（DFS）排列：父组织行紧跟其子组织行。 </p>
     *
     * @param fleaOrgModuleSV 组织模块服务
     * @return 组织明细行集合
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    public static List<Map<String, Object>> orgRows(IFleaOrgModuleSV fleaOrgModuleSV) throws CommonException {

        List<Map<String, Object>> rowList = new ArrayList<>();

        List<FleaOrganization> orgList = fleaOrgModuleSV.queryAllValidOrganizations();
        if (CollectionUtils.isEmpty(orgList)) {
            return rowList;
        }

        // 组织编号 -> 组织，用于按编号取父组织名称
        Map<Long, FleaOrganization> orgMap = new HashMap<>();
        for (FleaOrganization fleaOrganization : orgList) {
            if (ObjectUtils.isNotEmpty(fleaOrganization)) {
                orgMap.put(fleaOrganization.getOrgId(), fleaOrganization);
            }
        }

        // 行顺序按树形先序（DFS）：父组织行紧跟其子组织行
        List<FleaOrganization> orderedList = orderOrganizationsByTree(orgList);

        for (FleaOrganization fleaOrganization : orderedList) {
            if (ObjectUtils.isEmpty(fleaOrganization) || !isInUse(fleaOrganization.getOrgState())) {
                continue;
            }

            FleaOrganization parentOrg = orgMap.get(fleaOrganization.getParentId());

            // 缩进层级直接取实体维护的 org_level（父组织变更时已联动重算）；
            // orgNameTree 供表格树形展示，orgName 保持纯名称供表单回填，避免缩进字符入库
            Integer orgLevel = fleaOrganization.getOrgLevel();
            String orgNameTree = orgIndent((orgLevel == null) ? 1 : orgLevel) + fleaOrganization.getOrgName();

            Map<String, Object> rowMap = new HashMap<>();
            rowMap.put("orgId", fleaOrganization.getOrgId());
            rowMap.put("orgCode", fleaOrganization.getOrgCode());
            rowMap.put("orgName", fleaOrganization.getOrgName());
            rowMap.put("orgNameTree", orgNameTree);
            rowMap.put("orgDesc", fleaOrganization.getOrgDesc());
            rowMap.put("orgType", fleaOrganization.getOrgType());
            rowMap.put("orgTypeName", orgTypeName(fleaOrganization.getOrgType()));
            rowMap.put("orgLevel", fleaOrganization.getOrgLevel());
            rowMap.put("parentId", fleaOrganization.getParentId());
            rowMap.put("parentName", (parentOrg == null) ? "-" : parentOrg.getOrgName());
            rowMap.put("orgState", fleaOrganization.getOrgState());
            rowMap.put("remarks", fleaOrganization.getRemarks());

            rowList.add(rowMap);
        }

        return rowList;
    }

    /**
     * <p> 组织类型展示名 </p>
     *
     * @param orgType 组织类型
     * @return 类型展示名，未知类型返回「未知」
     * @since 1.0.0
     */
    private static String orgTypeName(Integer orgType) {
        if (orgType == null) {
            return "未知";
        }
        if (ORG_TYPE_COMPANY == orgType) {
            return "公司";
        }
        if (ORG_TYPE_DEPARTMENT == orgType) {
            return "部门";
        }
        if (ORG_TYPE_GROUP == orgType) {
            return "小组";
        }
        return "未知";
    }

    /**
     * <p> 角色组编号 -> 名称 映射 </p>
     *
     * @param fleaRoleModuleSV 角色模块服务
     * @return 映射 Map
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    private static Map<Long, String> roleGroupNameMap(IFleaRoleModuleSV fleaRoleModuleSV) throws CommonException {

        Map<Long, String> nameMap = new HashMap<>();

        List<FleaRoleGroup> roleGroupList = fleaRoleModuleSV.queryRoleGroupsInUse(null);
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
     * @param fleaPrivilegeModuleSV 权限模块服务
     * @return 映射 Map
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    private static Map<Long, String> privilegeGroupNameMap(IFleaPrivilegeModuleSV fleaPrivilegeModuleSV) throws CommonException {

        Map<Long, String> nameMap = new HashMap<>();

        List<FleaPrivilegeGroup> privilegeGroupList = fleaPrivilegeModuleSV.queryPrivilegeGroupsInUse(null, null, null);
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

    private static List<FleaRole> roleList(IFleaRoleModuleSV fleaRoleModuleSV) throws CommonException {
        return fleaRoleModuleSV.queryRolesInUse(null, null);
    }

    private static List<FleaPrivilege> privilegeList(IFleaPrivilegeModuleSV fleaPrivilegeModuleSV) throws CommonException {
        return fleaPrivilegeModuleSV.queryPrivilegesInUse(null, null);
    }

}
