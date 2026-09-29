package com.huazie.fleamgmt.springmvc.auth.web;

import com.huazie.fleaframework.auth.base.privilege.service.interfaces.IFleaPrivilegeGroupSV;
import com.huazie.fleaframework.auth.base.privilege.service.interfaces.IFleaPrivilegeSV;
import com.huazie.fleaframework.auth.base.role.entity.FleaRole;
import com.huazie.fleaframework.auth.base.role.entity.FleaRoleGroup;
import com.huazie.fleaframework.auth.base.role.entity.FleaRoleGroupRel;
import com.huazie.fleaframework.auth.base.role.entity.FleaRoleRel;
import com.huazie.fleaframework.auth.base.role.service.interfaces.IFleaRoleGroupRelSV;
import com.huazie.fleaframework.auth.base.role.service.interfaces.IFleaRoleGroupSV;
import com.huazie.fleaframework.auth.base.role.service.interfaces.IFleaRoleRelSV;
import com.huazie.fleaframework.auth.base.role.service.interfaces.IFleaRoleSV;
import com.huazie.fleaframework.auth.common.AuthRelTypeEnum;
import com.huazie.fleaframework.auth.common.pojo.FleaAuthRelExtPOJO;
import com.huazie.fleaframework.auth.common.pojo.role.FleaRoleGroupPOJO;
import com.huazie.fleaframework.auth.common.pojo.role.FleaRolePOJO;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaRoleModuleSV;
import com.huazie.fleaframework.common.exceptions.CommonException;
import com.huazie.fleaframework.common.pojo.OutputCommonData;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleaframework.common.util.CollectionUtils;
import com.huazie.fleaframework.common.util.ObjectUtils;
import com.huazie.fleaframework.common.util.POJOUtils;
import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.module.auth.pojo.InputAuthRelInfo;
import com.huazie.fleamgmt.module.auth.pojo.InputRoleGroupInfo;
import com.huazie.fleamgmt.module.auth.pojo.InputRoleInfo;
import com.huazie.fleamgmt.module.auth.pojo.OutputAuthInfo;
import com.huazie.fleamgmt.module.auth.pojo.OutputFunctionInfo;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 角色模块管理 Controller </p>
 *
 * <p> 覆盖「角色模块管理」下的两个子模块： </p>
 * <ul>
 *     <li>角色管理：角色新增、角色变更、角色授权；</li>
 *     <li>角色组管理：角色组新增、角色组变更、角色组关联。</li>
 * </ul>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class RolemgmtController extends BusinessController {

    private static final FleaLogger LOGGER = FleaLoggerProxy.getProxyInstance(RolemgmtController.class);

    private IFleaRoleModuleSV fleaRoleModuleSV;

    private IFleaRoleSV fleaRoleSV;

    private IFleaRoleGroupSV fleaRoleGroupSV;

    private IFleaRoleRelSV fleaRoleRelSV;

    private IFleaRoleGroupRelSV fleaRoleGroupRelSV;

    private IFleaPrivilegeSV fleaPrivilegeSV;

    private IFleaPrivilegeGroupSV fleaPrivilegeGroupSV;

    @Resource(name = "fleaRoleModuleSV")
    public void setFleaRoleModuleSV(IFleaRoleModuleSV fleaRoleModuleSV) {
        this.fleaRoleModuleSV = fleaRoleModuleSV;
    }

    @Resource(name = "fleaRoleSV")
    public void setFleaRoleSV(IFleaRoleSV fleaRoleSV) {
        this.fleaRoleSV = fleaRoleSV;
    }

    @Resource(name = "fleaRoleGroupSV")
    public void setFleaRoleGroupSV(IFleaRoleGroupSV fleaRoleGroupSV) {
        this.fleaRoleGroupSV = fleaRoleGroupSV;
    }

    @Resource(name = "fleaRoleRelSV")
    public void setFleaRoleRelSV(IFleaRoleRelSV fleaRoleRelSV) {
        this.fleaRoleRelSV = fleaRoleRelSV;
    }

    @Resource(name = "fleaRoleGroupRelSV")
    public void setFleaRoleGroupRelSV(IFleaRoleGroupRelSV fleaRoleGroupRelSV) {
        this.fleaRoleGroupRelSV = fleaRoleGroupRelSV;
    }

    @Resource(name = "fleaPrivilegeSV")
    public void setFleaPrivilegeSV(IFleaPrivilegeSV fleaPrivilegeSV) {
        this.fleaPrivilegeSV = fleaPrivilegeSV;
    }

    @Resource(name = "fleaPrivilegeGroupSV")
    public void setFleaPrivilegeGroupSV(IFleaPrivilegeGroupSV fleaPrivilegeGroupSV) {
        this.fleaPrivilegeGroupSV = fleaPrivilegeGroupSV;
    }

    /* ==================== 角色管理 ==================== */

    /**
     * <p> 角色列表（角色新增 / 变更 / 授权 左侧列表共用） </p>
     *
     * @return 角色列表信息
     * @since 1.0.0
     */
    @GetMapping("authRole!list.flea")
    @ResponseBody
    public OutputFunctionInfo listRoles() throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        List<Map<String, Object>> roleTreeList = AuthCandidateUtil.roles(fleaRoleSV);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setTreeList(roleTreeList);

        return output;
    }

    /**
     * <p> 角色明细查询（角色变更页回填用） </p>
     *
     * @param roleId 角色编号
     * @return 角色明细信息
     * @since 1.0.0
     */
    @GetMapping("authRole!query.flea")
    @ResponseBody
    public OutputFunctionInfo queryRole(@RequestParam("roleId") Long roleId) throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        FleaRole fleaRole = fleaRoleSV.queryRoleInUse(roleId);
        if (ObjectUtils.isEmpty(fleaRole)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，角色【" + roleId + "】不存在或已失效，请刷新角色列表后重试！");
            return output;
        }

        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("roleId", fleaRole.getRoleId());
        roleMap.put("roleName", fleaRole.getRoleName());
        roleMap.put("roleDesc", fleaRole.getRoleDesc());
        roleMap.put("groupId", fleaRole.getGroupId());
        roleMap.put("remarks", fleaRole.getRemarks());

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setData(roleMap);

        return output;
    }

    /**
     * <p> 角色新增 </p>
     *
     * @param inputRoleInfo 角色业务入参
     * @return 角色新增结果
     * @since 1.0.0
     */
    @PostMapping("authRole!add.flea")
    @ResponseBody
    public OutputCommonData addRole(InputRoleInfo inputRoleInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        FleaRolePOJO fleaRolePOJO = new FleaRolePOJO();
        POJOUtils.copyAll(inputRoleInfo, fleaRolePOJO);
        // 库中 group_id 为非空列，未指定分组时落默认值
        fleaRolePOJO.setGroupId(AuthBizUtils.getGroupIdOrDefault(inputRoleInfo.getGroupId()));

        Long roleId = fleaRoleModuleSV.addFleaRole(fleaRolePOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，角色添加成功，角色编号：" + roleId);
        return output;
    }

    /**
     * <p> 角色变更 </p>
     *
     * @param inputRoleInfo 角色业务入参
     * @return 角色变更结果
     * @since 1.0.0
     */
    @PostMapping("authRole!modify.flea")
    @ResponseBody
    public OutputCommonData modifyRole(InputRoleInfo inputRoleInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        if (!AuthBizUtils.isValidId(inputRoleInfo.getRoleId())) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧角色列表中选择要变更的角色哟！");
            return output;
        }

        FleaRolePOJO fleaRolePOJO = new FleaRolePOJO();
        POJOUtils.copyAll(inputRoleInfo, fleaRolePOJO);

        fleaRoleModuleSV.modifyFleaRole(inputRoleInfo.getRoleId(), fleaRolePOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，角色修改成功！");
        return output;
    }

    /**
     * <p> 角色授权明细（可授权权限 / 权限组 / 角色 + 已授权编号） </p>
     *
     * @param roleId  角色编号
     * @param relType 关联类型（ROLE_REL_PRIVILEGE / ROLE_REL_PRIVILEGE_GROUP / ROLE_REL_ROLE）
     * @return 授权明细信息
     * @since 1.0.0
     */
    @GetMapping("authRole!auth.flea")
    @ResponseBody
    public OutputAuthInfo authRole(@RequestParam("roleId") Long roleId,
                                   @RequestParam("relType") String relType) throws CommonException {

        OutputAuthInfo output = new OutputAuthInfo();

        FleaRole fleaRole = fleaRoleSV.queryRoleInUse(roleId);
        if (ObjectUtils.isEmpty(fleaRole)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，角色【" + roleId + "】不存在或已失效，请刷新角色列表后重试！");
            return output;
        }

        List<Map<String, Object>> candidates;
        if (AuthRelTypeEnum.ROLE_REL_PRIVILEGE.getRelType().equals(relType)) {
            candidates = AuthCandidateUtil.privileges(fleaPrivilegeSV);
        } else if (AuthRelTypeEnum.ROLE_REL_PRIVILEGE_GROUP.getRelType().equals(relType)) {
            candidates = AuthCandidateUtil.privilegeGroups(fleaPrivilegeGroupSV);
        } else if (AuthRelTypeEnum.ROLE_REL_ROLE.getRelType().equals(relType)) {
            candidates = AuthCandidateUtil.roles(fleaRoleSV);
        } else {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，不支持的角色授权类型【" + relType + "】！");
            return output;
        }

        List<Long> selectedList = new ArrayList<>();
        List<FleaRoleRel> roleRelList = fleaRoleRelSV.getRoleRelList(roleId, relType);
        if (CollectionUtils.isNotEmpty(roleRelList)) {
            for (FleaRoleRel fleaRoleRel : roleRelList) {
                if (ObjectUtils.isNotEmpty(fleaRoleRel)) {
                    selectedList.add(fleaRoleRel.getRelId());
                }
            }
        }

        output.setOwnerId(roleId);
        output.setOwnerName(fleaRole.getRoleName());
        output.setRelType(relType);
        output.setCandidates(candidates);
        output.setSelected(selectedList);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");

        return output;
    }

    /**
     * <p> 角色授权提交（按关联类型逐条新增关联） </p>
     *
     * @param inputAuthRelInfo 授权业务入参
     * @return 授权结果
     * @since 1.0.0
     */
    @PostMapping("authRole!authorize.flea")
    @ResponseBody
    public OutputCommonData authorizeRole(InputAuthRelInfo inputAuthRelInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        Long ownerId = inputAuthRelInfo.getOwnerId();
        String relType = inputAuthRelInfo.getRelType();
        List<Long> relIds = inputAuthRelInfo.getRelIds();

        if (!AuthBizUtils.isValidId(ownerId)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧角色列表中选择要授权的角色哟！");
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
            if (AuthRelTypeEnum.ROLE_REL_PRIVILEGE.getRelType().equals(relType)) {
                fleaRoleModuleSV.roleRelPrivilege(ownerId, relId, fleaAuthRelExtPOJO);
            } else if (AuthRelTypeEnum.ROLE_REL_PRIVILEGE_GROUP.getRelType().equals(relType)) {
                fleaRoleModuleSV.roleRelPrivilegeGroup(ownerId, relId, fleaAuthRelExtPOJO);
            } else if (AuthRelTypeEnum.ROLE_REL_ROLE.getRelType().equals(relType)) {
                fleaRoleModuleSV.roleRelRole(ownerId, relId, fleaAuthRelExtPOJO);
            } else {
                output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
                output.setRetMess("亲，不支持的角色授权类型【" + relType + "】！");
                return output;
            }
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，角色授权成功，本次新增授权 " + relIds.size() + " 项！");
        return output;
    }

    /* ==================== 角色组管理 ==================== */

    /**
     * <p> 角色组列表（角色组新增 / 变更 / 关联 左侧列表共用） </p>
     *
     * @return 角色组列表信息
     * @since 1.0.0
     */
    @GetMapping("authRoleGroup!list.flea")
    @ResponseBody
    public OutputFunctionInfo listRoleGroups() throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        List<Map<String, Object>> roleGroupTreeList = AuthCandidateUtil.roleGroups(fleaRoleGroupSV);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setTreeList(roleGroupTreeList);

        return output;
    }

    /**
     * <p> 角色组明细查询（角色组变更页回填用） </p>
     *
     * @param roleGroupId 角色组编号
     * @return 角色组明细信息
     * @since 1.0.0
     */
    @GetMapping("authRoleGroup!query.flea")
    @ResponseBody
    public OutputFunctionInfo queryRoleGroup(@RequestParam("roleGroupId") Long roleGroupId) throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        FleaRoleGroup fleaRoleGroup = fleaRoleGroupSV.queryRoleGroupInUse(roleGroupId);
        if (ObjectUtils.isEmpty(fleaRoleGroup)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，角色组【" + roleGroupId + "】不存在或已失效，请刷新角色组列表后重试！");
            return output;
        }

        Map<String, Object> roleGroupMap = new HashMap<>();
        roleGroupMap.put("roleGroupId", fleaRoleGroup.getRoleGroupId());
        roleGroupMap.put("roleGroupName", fleaRoleGroup.getRoleGroupName());
        roleGroupMap.put("roleGroupDesc", fleaRoleGroup.getRoleGroupDesc());
        roleGroupMap.put("remarks", fleaRoleGroup.getRemarks());

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setData(roleGroupMap);

        return output;
    }

    /**
     * <p> 角色组新增 </p>
     *
     * @param inputRoleGroupInfo 角色组业务入参
     * @return 角色组新增结果
     * @since 1.0.0
     */
    @PostMapping("authRoleGroup!add.flea")
    @ResponseBody
    public OutputCommonData addRoleGroup(InputRoleGroupInfo inputRoleGroupInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        FleaRoleGroupPOJO fleaRoleGroupPOJO = new FleaRoleGroupPOJO();
        POJOUtils.copyAll(inputRoleGroupInfo, fleaRoleGroupPOJO);

        Long roleGroupId = fleaRoleModuleSV.addFleaRoleGroup(fleaRoleGroupPOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，角色组添加成功，角色组编号：" + roleGroupId);
        return output;
    }

    /**
     * <p> 角色组变更 </p>
     *
     * @param inputRoleGroupInfo 角色组业务入参
     * @return 角色组变更结果
     * @since 1.0.0
     */
    @PostMapping("authRoleGroup!modify.flea")
    @ResponseBody
    public OutputCommonData modifyRoleGroup(InputRoleGroupInfo inputRoleGroupInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        if (!AuthBizUtils.isValidId(inputRoleGroupInfo.getRoleGroupId())) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧角色组列表中选择要变更的角色组哟！");
            return output;
        }

        FleaRoleGroupPOJO fleaRoleGroupPOJO = new FleaRoleGroupPOJO();
        POJOUtils.copyAll(inputRoleGroupInfo, fleaRoleGroupPOJO);

        fleaRoleModuleSV.modifyFleaRoleGroup(inputRoleGroupInfo.getRoleGroupId(), fleaRoleGroupPOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，角色组修改成功！");
        return output;
    }

    /**
     * <p> 角色组关联明细（可关联角色 + 已关联编号） </p>
     *
     * @param roleGroupId 角色组编号
     * @param relType     关联类型（ROLE_GROUP_REL_ROLE）
     * @return 关联明细信息
     * @since 1.0.0
     */
    @GetMapping("authRoleGroup!auth.flea")
    @ResponseBody
    public OutputAuthInfo authRoleGroup(@RequestParam("roleGroupId") Long roleGroupId,
                                        @RequestParam("relType") String relType) throws CommonException {

        OutputAuthInfo output = new OutputAuthInfo();

        FleaRoleGroup fleaRoleGroup = fleaRoleGroupSV.queryRoleGroupInUse(roleGroupId);
        if (ObjectUtils.isEmpty(fleaRoleGroup)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，角色组【" + roleGroupId + "】不存在或已失效，请刷新角色组列表后重试！");
            return output;
        }

        if (!AuthRelTypeEnum.ROLE_GROUP_REL_ROLE.getRelType().equals(relType)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，不支持的角色组关联类型【" + relType + "】！");
            return output;
        }

        List<Long> selectedList = new ArrayList<>();
        List<FleaRoleGroupRel> roleGroupRelList = fleaRoleGroupRelSV.getRoleGroupRelList(roleGroupId, relType);
        if (CollectionUtils.isNotEmpty(roleGroupRelList)) {
            for (FleaRoleGroupRel fleaRoleGroupRel : roleGroupRelList) {
                if (ObjectUtils.isNotEmpty(fleaRoleGroupRel)) {
                    selectedList.add(fleaRoleGroupRel.getRelId());
                }
            }
        }

        output.setOwnerId(roleGroupId);
        output.setOwnerName(fleaRoleGroup.getRoleGroupName());
        output.setRelType(relType);
        output.setCandidates(AuthCandidateUtil.roles(fleaRoleSV));
        output.setSelected(selectedList);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");

        return output;
    }

    /**
     * <p> 角色组关联提交（逐条新增「角色组关联角色」） </p>
     *
     * @param inputAuthRelInfo 授权业务入参
     * @return 关联结果
     * @since 1.0.0
     */
    @PostMapping("authRoleGroup!authorize.flea")
    @ResponseBody
    public OutputCommonData authorizeRoleGroup(InputAuthRelInfo inputAuthRelInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        Long ownerId = inputAuthRelInfo.getOwnerId();
        String relType = inputAuthRelInfo.getRelType();
        List<Long> relIds = inputAuthRelInfo.getRelIds();

        if (!AuthBizUtils.isValidId(ownerId)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧角色组列表中选择要关联的角色组哟！");
            return output;
        }

        if (!AuthRelTypeEnum.ROLE_GROUP_REL_ROLE.getRelType().equals(relType)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，不支持的角色组关联类型【" + relType + "】！");
            return output;
        }

        if (CollectionUtils.isEmpty(relIds)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧列表中选择要关联的角色哟！");
            return output;
        }

        FleaAuthRelExtPOJO fleaAuthRelExtPOJO = new FleaAuthRelExtPOJO();
        for (Long relId : relIds) {
            if (AuthBizUtils.isValidId(relId)) {
                fleaRoleModuleSV.roleGroupRelRole(ownerId, relId, fleaAuthRelExtPOJO);
            }
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，角色组关联成功，本次新增关联 " + relIds.size() + " 项！");
        return output;
    }

}
