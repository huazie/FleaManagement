package com.huazie.fleamgmt.springmvc.auth.web;

import com.huazie.fleaframework.auth.base.function.entity.FleaElement;
import com.huazie.fleaframework.auth.base.function.entity.FleaMenu;
import com.huazie.fleaframework.auth.base.function.entity.FleaOperation;
import com.huazie.fleaframework.auth.base.function.entity.FleaResource;
import com.huazie.fleaframework.auth.base.privilege.entity.FleaPrivilege;
import com.huazie.fleaframework.auth.base.privilege.entity.FleaPrivilegeGroup;
import com.huazie.fleaframework.auth.base.privilege.entity.FleaPrivilegeGroupRel;
import com.huazie.fleaframework.auth.base.privilege.entity.FleaPrivilegeRel;
import com.huazie.fleaframework.auth.base.privilege.service.interfaces.IFleaPrivilegeGroupRelSV;
import com.huazie.fleaframework.auth.base.privilege.service.interfaces.IFleaPrivilegeGroupSV;
import com.huazie.fleaframework.auth.base.privilege.service.interfaces.IFleaPrivilegeRelSV;
import com.huazie.fleaframework.auth.base.privilege.service.interfaces.IFleaPrivilegeSV;
import com.huazie.fleaframework.auth.common.AuthRelTypeEnum;
import com.huazie.fleaframework.auth.common.pojo.FleaAuthRelExtPOJO;
import com.huazie.fleaframework.auth.common.pojo.privilege.FleaPrivilegeGroupPOJO;
import com.huazie.fleaframework.auth.common.pojo.privilege.FleaPrivilegePOJO;
import com.huazie.fleaframework.auth.common.pojo.privilege.FleaPrivilegeRelPOJO;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaPrivilegeModuleSV;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaFunctionModuleSV;
import com.huazie.fleaframework.auth.util.FleaAuthPOJOUtils;
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
import com.huazie.fleamgmt.module.auth.pojo.InputPrivilegeGroupInfo;
import com.huazie.fleamgmt.module.auth.pojo.InputPrivilegeInfo;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 权限模块管理 Controller </p>
 *
 * <p> 覆盖「权限模块管理」下的两个子模块： </p>
 * <ul>
 *     <li>权限管理：权限新增、权限变更、权限关联（关联菜单/操作/元素/资源）；</li>
 *     <li>权限组管理：权限组新增、权限组变更、权限组关联（关联权限）。</li>
 * </ul>
 *
 * <p> 说明：框架的权限模块服务未提供「权限关联功能」的方法，故权限关联走 base 层
 * {@code IFleaPrivilegeRelSV.savePrivilegeRel}，关联 POJO 由
 * {@code FleaAuthPOJOUtils.newFleaPrivilegeRelXxxPOJO} 工厂方法构建；
 * 权限组关联权限则使用框架的 {@code privilegeGroupRelPrivilege}。 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class PrivilegemgmtController extends BusinessController {

    private static final FleaLogger LOGGER = FleaLoggerProxy.getProxyInstance(PrivilegemgmtController.class);

    private IFleaPrivilegeModuleSV fleaPrivilegeModuleSV;

    private IFleaPrivilegeSV fleaPrivilegeSV;

    private IFleaPrivilegeGroupSV fleaPrivilegeGroupSV;

    private IFleaPrivilegeRelSV fleaPrivilegeRelSV;

    private IFleaPrivilegeGroupRelSV fleaPrivilegeGroupRelSV;

    private IFleaFunctionModuleSV fleaFunctionModuleSV;

    @Resource(name = "fleaPrivilegeModuleSV")
    public void setFleaPrivilegeModuleSV(IFleaPrivilegeModuleSV fleaPrivilegeModuleSV) {
        this.fleaPrivilegeModuleSV = fleaPrivilegeModuleSV;
    }

    @Resource(name = "fleaPrivilegeSV")
    public void setFleaPrivilegeSV(IFleaPrivilegeSV fleaPrivilegeSV) {
        this.fleaPrivilegeSV = fleaPrivilegeSV;
    }

    @Resource(name = "fleaPrivilegeGroupSV")
    public void setFleaPrivilegeGroupSV(IFleaPrivilegeGroupSV fleaPrivilegeGroupSV) {
        this.fleaPrivilegeGroupSV = fleaPrivilegeGroupSV;
    }

    @Resource(name = "fleaPrivilegeRelSV")
    public void setFleaPrivilegeRelSV(IFleaPrivilegeRelSV fleaPrivilegeRelSV) {
        this.fleaPrivilegeRelSV = fleaPrivilegeRelSV;
    }

    @Resource(name = "fleaPrivilegeGroupRelSV")
    public void setFleaPrivilegeGroupRelSV(IFleaPrivilegeGroupRelSV fleaPrivilegeGroupRelSV) {
        this.fleaPrivilegeGroupRelSV = fleaPrivilegeGroupRelSV;
    }

    @Resource(name = "fleaFunctionModuleSV")
    public void setFleaFunctionModuleSV(IFleaFunctionModuleSV fleaFunctionModuleSV) {
        this.fleaFunctionModuleSV = fleaFunctionModuleSV;
    }

    /* ==================== 权限管理 ==================== */

    /**
     * <p> 权限列表（权限新增 / 变更 / 关联 左侧列表共用） </p>
     *
     * @return 权限列表信息
     * @since 1.0.0
     */
    @GetMapping("authPrivilege!list.flea")
    @ResponseBody
    public OutputFunctionInfo listPrivileges() throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        List<Map<String, Object>> privilegeTreeList = AuthCandidateUtil.privileges(fleaPrivilegeSV);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setTreeList(privilegeTreeList);

        return output;
    }

    /**
     * <p> 权限明细列表（权限变更页表格用） </p>
     *
     * <p> 与 {@link #listPrivileges()} 的区别：列表接口只返回树节点（编号/展示名），
     * 服务于授权主体表；本接口返回权限的完整明细字段及状态汇总统计，
     * 服务于变更页表格（jqGrid）的列展示与筛选。 </p>
     *
     * @return 权限明细行集合与汇总统计
     * @since 1.0.0
     */
    @GetMapping("authPrivilege!page.flea")
    @ResponseBody
    public OutputGridInfo pagePrivileges() throws CommonException {

        OutputGridInfo output = new OutputGridInfo();

        List<Map<String, Object>> rowList = AuthCandidateUtil.privilegeRows(fleaPrivilegeSV, fleaPrivilegeGroupSV);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setRows(rowList);
        output.setSummary(AuthBizUtils.summarize(rowList, "privilegeState"));

        return output;
    }

    /**
     * <p> 权限明细查询（权限变更页回填用） </p>
     *
     * @param privilegeId 权限编号
     * @return 权限明细信息
     * @since 1.0.0
     */
    @GetMapping("authPrivilege!query.flea")
    @ResponseBody
    public OutputFunctionInfo queryPrivilege(@RequestParam("privilegeId") Long privilegeId) throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        FleaPrivilege fleaPrivilege = fleaPrivilegeSV.queryPrivilegeInUse(privilegeId);
        if (ObjectUtils.isEmpty(fleaPrivilege)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，权限【" + privilegeId + "】不存在或已失效，请刷新权限列表后重试！");
            return output;
        }

        Map<String, Object> privilegeMap = new HashMap<>();
        privilegeMap.put("privilegeId", fleaPrivilege.getPrivilegeId());
        privilegeMap.put("privilegeName", fleaPrivilege.getPrivilegeName());
        privilegeMap.put("privilegeDesc", fleaPrivilege.getPrivilegeDesc());
        privilegeMap.put("groupId", fleaPrivilege.getGroupId());
        privilegeMap.put("remarks", fleaPrivilege.getRemarks());

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setData(privilegeMap);

        return output;
    }

    /**
     * <p> 权限新增 </p>
     *
     * @param inputPrivilegeInfo 权限业务入参
     * @return 权限新增结果
     * @since 1.0.0
     */
    @PostMapping("authPrivilege!add.flea")
    @ResponseBody
    public OutputCommonData addPrivilege(InputPrivilegeInfo inputPrivilegeInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        FleaPrivilegePOJO fleaPrivilegePOJO = new FleaPrivilegePOJO();
        POJOUtils.copyAll(inputPrivilegeInfo, fleaPrivilegePOJO);
        // 库中 group_id 为非空列，未指定分组时落默认值
        fleaPrivilegePOJO.setGroupId(AuthBizUtils.getGroupIdOrDefault(inputPrivilegeInfo.getGroupId()));

        Long privilegeId = fleaPrivilegeModuleSV.addFleaPrivilege(fleaPrivilegePOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，权限添加成功，权限编号：" + privilegeId);
        return output;
    }

    /**
     * <p> 权限变更 </p>
     *
     * @param inputPrivilegeInfo 权限业务入参
     * @return 权限变更结果
     * @since 1.0.0
     */
    @PostMapping("authPrivilege!modify.flea")
    @ResponseBody
    public OutputCommonData modifyPrivilege(InputPrivilegeInfo inputPrivilegeInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        if (!AuthBizUtils.isValidId(inputPrivilegeInfo.getPrivilegeId())) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧权限列表中选择要变更的权限哟！");
            return output;
        }

        FleaPrivilegePOJO fleaPrivilegePOJO = new FleaPrivilegePOJO();
        POJOUtils.copyAll(inputPrivilegeInfo, fleaPrivilegePOJO);

        fleaPrivilegeModuleSV.modifyFleaPrivilege(inputPrivilegeInfo.getPrivilegeId(), fleaPrivilegePOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，权限修改成功！");
        return output;
    }

    /**
     * <p> 权限关联明细（可关联菜单 / 操作 / 元素 / 资源 + 已关联编号） </p>
     *
     * @param privilegeId 权限编号
     * @param relType     关联类型（PRIVILEGE_REL_MENU / OPERATION / ELEMENT / RESOURCE）
     * @return 关联明细信息
     * @since 1.0.0
     */
    @GetMapping("authPrivilege!auth.flea")
    @ResponseBody
    public OutputAuthInfo authPrivilege(@RequestParam("privilegeId") Long privilegeId,
                                        @RequestParam("relType") String relType) throws CommonException {

        OutputAuthInfo output = new OutputAuthInfo();

        FleaPrivilege fleaPrivilege = fleaPrivilegeSV.queryPrivilegeInUse(privilegeId);
        if (ObjectUtils.isEmpty(fleaPrivilege)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，权限【" + privilegeId + "】不存在或已失效，请刷新权限列表后重试！");
            return output;
        }

        List<Map<String, Object>> candidates = buildPrivilegeCandidates(relType);
        if (ObjectUtils.isEmpty(candidates)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，不支持的权限关联类型【" + relType + "】！");
            return output;
        }

        List<Long> selectedList = new ArrayList<>();
        List<FleaPrivilegeRel> privilegeRelList = fleaPrivilegeRelSV.getPrivilegeRelList(privilegeId, relType);
        if (CollectionUtils.isNotEmpty(privilegeRelList)) {
            for (FleaPrivilegeRel fleaPrivilegeRel : privilegeRelList) {
                if (ObjectUtils.isNotEmpty(fleaPrivilegeRel)) {
                    selectedList.add(fleaPrivilegeRel.getRelId());
                }
            }
        }

        output.setOwnerId(privilegeId);
        output.setOwnerName(fleaPrivilege.getPrivilegeName());
        output.setRelType(relType);
        output.setCandidates(candidates);
        output.setSelected(selectedList);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");

        return output;
    }

    /**
     * <p> 权限关联提交（逐条新增「权限关联功能」） </p>
     *
     * @param inputAuthRelInfo 授权业务入参
     * @return 关联结果
     * @since 1.0.0
     */
    @PostMapping("authPrivilege!authorize.flea")
    @ResponseBody
    public OutputCommonData authorizePrivilege(InputAuthRelInfo inputAuthRelInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        Long ownerId = inputAuthRelInfo.getOwnerId();
        String relType = inputAuthRelInfo.getRelType();
        List<Long> relIds = inputAuthRelInfo.getRelIds();

        if (!AuthBizUtils.isValidId(ownerId)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧权限列表中选择要关联的权限哟！");
            return output;
        }

        // 关联类型 -> 功能编号 -> 功能名称，用于构建关联 POJO
        Map<Long, String> relNameMap = buildPrivilegeRelNameMap(relType);
        if (ObjectUtils.isEmpty(relNameMap)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，不支持的权限关联类型【" + relType + "】！");
            return output;
        }

        if (CollectionUtils.isEmpty(relIds)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧列表中选择要关联的功能哟！");
            return output;
        }

        for (Long relId : relIds) {
            if (!AuthBizUtils.isValidId(relId)) {
                continue;
            }
            String relName = relNameMap.get(relId);
            FleaPrivilegeRelPOJO fleaPrivilegeRelPOJO = newFleaPrivilegeRelPOJO(ownerId, relType, relId, relName);
            if (ObjectUtils.isEmpty(fleaPrivilegeRelPOJO)) {
                output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
                output.setRetMess("亲，不支持的权限关联类型【" + relType + "】！");
                return output;
            }
            fleaPrivilegeRelSV.savePrivilegeRel(fleaPrivilegeRelPOJO);
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，权限关联成功，本次新增关联 " + relIds.size() + " 项！");
        return output;
    }

    /* ==================== 权限组管理 ==================== */

    /**
     * <p> 权限组列表（权限组新增 / 变更 / 关联 左侧列表共用） </p>
     *
     * @return 权限组列表信息
     * @since 1.0.0
     */
    @GetMapping("authPrivilegeGroup!list.flea")
    @ResponseBody
    public OutputFunctionInfo listPrivilegeGroups() throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        List<Map<String, Object>> privilegeGroupTreeList = AuthCandidateUtil.privilegeGroups(fleaPrivilegeGroupSV);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setTreeList(privilegeGroupTreeList);

        return output;
    }

    /**
     * <p> 权限组明细列表（权限组变更页表格用） </p>
     *
     * <p> 与 {@link #listPrivilegeGroups()} 的区别：列表接口只返回树节点（编号/展示名），
     * 服务于关联主体表；本接口返回权限组的完整明细字段、组内权限数及状态汇总统计，
     * 服务于变更页表格（jqGrid）的列展示与筛选。 </p>
     *
     * @return 权限组明细行集合与汇总统计
     * @since 1.0.0
     */
    @GetMapping("authPrivilegeGroup!page.flea")
    @ResponseBody
    public OutputGridInfo pagePrivilegeGroups() throws CommonException {

        OutputGridInfo output = new OutputGridInfo();

        List<Map<String, Object>> rowList = AuthCandidateUtil.privilegeGroupRows(fleaPrivilegeGroupSV, fleaPrivilegeSV);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setRows(rowList);
        output.setSummary(AuthBizUtils.summarize(rowList, "privilegeGroupState"));

        return output;
    }

    /**
     * <p> 权限组明细查询（权限组变更页回填用） </p>
     *
     * @param privilegeGroupId 权限组编号
     * @return 权限组明细信息
     * @since 1.0.0
     */
    @GetMapping("authPrivilegeGroup!query.flea")
    @ResponseBody
    public OutputFunctionInfo queryPrivilegeGroup(@RequestParam("privilegeGroupId") Long privilegeGroupId) throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        FleaPrivilegeGroup fleaPrivilegeGroup = fleaPrivilegeGroupSV.queryPrivilegeGroupInUse(privilegeGroupId);
        if (ObjectUtils.isEmpty(fleaPrivilegeGroup)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，权限组【" + privilegeGroupId + "】不存在或已失效，请刷新权限组列表后重试！");
            return output;
        }

        Map<String, Object> privilegeGroupMap = new HashMap<>();
        privilegeGroupMap.put("privilegeGroupId", fleaPrivilegeGroup.getPrivilegeGroupId());
        privilegeGroupMap.put("privilegeGroupName", fleaPrivilegeGroup.getPrivilegeGroupName());
        privilegeGroupMap.put("privilegeGroupDesc", fleaPrivilegeGroup.getPrivilegeGroupDesc());
        privilegeGroupMap.put("isMain", fleaPrivilegeGroup.getIsMain());
        privilegeGroupMap.put("functionType", fleaPrivilegeGroup.getFunctionType());
        privilegeGroupMap.put("remarks", fleaPrivilegeGroup.getRemarks());

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setData(privilegeGroupMap);

        return output;
    }

    /**
     * <p> 权限组新增 </p>
     *
     * @param inputPrivilegeGroupInfo 权限组业务入参
     * @return 权限组新增结果
     * @since 1.0.0
     */
    @PostMapping("authPrivilegeGroup!add.flea")
    @ResponseBody
    public OutputCommonData addPrivilegeGroup(InputPrivilegeGroupInfo inputPrivilegeGroupInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        FleaPrivilegeGroupPOJO fleaPrivilegeGroupPOJO = new FleaPrivilegeGroupPOJO();
        POJOUtils.copyAll(inputPrivilegeGroupInfo, fleaPrivilegeGroupPOJO);
        // 库中 is_main 为非空列，未指定时默认非主权限组
        fleaPrivilegeGroupPOJO.setIsMain(AuthBizUtils.getStateOrDefault(inputPrivilegeGroupInfo.getIsMain(),
                CommonConstants.NumeralConstants.INT_ZERO));

        Long privilegeGroupId = fleaPrivilegeModuleSV.addFleaPrivilegeGroup(fleaPrivilegeGroupPOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，权限组添加成功，权限组编号：" + privilegeGroupId);
        return output;
    }

    /**
     * <p> 权限组变更 </p>
     *
     * @param inputPrivilegeGroupInfo 权限组业务入参
     * @return 权限组变更结果
     * @since 1.0.0
     */
    @PostMapping("authPrivilegeGroup!modify.flea")
    @ResponseBody
    public OutputCommonData modifyPrivilegeGroup(InputPrivilegeGroupInfo inputPrivilegeGroupInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        if (!AuthBizUtils.isValidId(inputPrivilegeGroupInfo.getPrivilegeGroupId())) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧权限组列表中选择要变更的权限组哟！");
            return output;
        }

        FleaPrivilegeGroupPOJO fleaPrivilegeGroupPOJO = new FleaPrivilegeGroupPOJO();
        POJOUtils.copyAll(inputPrivilegeGroupInfo, fleaPrivilegeGroupPOJO);

        fleaPrivilegeModuleSV.modifyFleaPrivilegeGroup(inputPrivilegeGroupInfo.getPrivilegeGroupId(), fleaPrivilegeGroupPOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，权限组修改成功！");
        return output;
    }

    /**
     * <p> 权限组关联明细（可关联权限 + 已关联编号） </p>
     *
     * @param privilegeGroupId 权限组编号
     * @param relType          关联类型（PRIVILEGE_GROUP_REL_PRIVILEGE）
     * @return 关联明细信息
     * @since 1.0.0
     */
    @GetMapping("authPrivilegeGroup!auth.flea")
    @ResponseBody
    public OutputAuthInfo authPrivilegeGroup(@RequestParam("privilegeGroupId") Long privilegeGroupId,
                                             @RequestParam("relType") String relType) throws CommonException {

        OutputAuthInfo output = new OutputAuthInfo();

        FleaPrivilegeGroup fleaPrivilegeGroup = fleaPrivilegeGroupSV.queryPrivilegeGroupInUse(privilegeGroupId);
        if (ObjectUtils.isEmpty(fleaPrivilegeGroup)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，权限组【" + privilegeGroupId + "】不存在或已失效，请刷新权限组列表后重试！");
            return output;
        }

        if (!AuthRelTypeEnum.PRIVILEGE_GROUP_REL_PRIVILEGE.getRelType().equals(relType)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，不支持的权限组关联类型【" + relType + "】！");
            return output;
        }

        List<Long> selectedList = new ArrayList<>();
        List<FleaPrivilegeGroupRel> privilegeGroupRelList = fleaPrivilegeGroupRelSV.getPrivilegeGroupRelList(privilegeGroupId, relType);
        if (CollectionUtils.isNotEmpty(privilegeGroupRelList)) {
            for (FleaPrivilegeGroupRel fleaPrivilegeGroupRel : privilegeGroupRelList) {
                if (ObjectUtils.isNotEmpty(fleaPrivilegeGroupRel)) {
                    selectedList.add(fleaPrivilegeGroupRel.getRelId());
                }
            }
        }

        output.setOwnerId(privilegeGroupId);
        output.setOwnerName(fleaPrivilegeGroup.getPrivilegeGroupName());
        output.setRelType(relType);
        output.setCandidates(AuthCandidateUtil.privileges(fleaPrivilegeSV));
        output.setSelected(selectedList);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");

        return output;
    }

    /**
     * <p> 权限组关联提交（逐条新增「权限组关联权限」） </p>
     *
     * @param inputAuthRelInfo 授权业务入参
     * @return 关联结果
     * @since 1.0.0
     */
    @PostMapping("authPrivilegeGroup!authorize.flea")
    @ResponseBody
    public OutputCommonData authorizePrivilegeGroup(InputAuthRelInfo inputAuthRelInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        Long ownerId = inputAuthRelInfo.getOwnerId();
        String relType = inputAuthRelInfo.getRelType();
        List<Long> relIds = inputAuthRelInfo.getRelIds();

        if (!AuthBizUtils.isValidId(ownerId)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧权限组列表中选择要关联的权限组哟！");
            return output;
        }

        if (!AuthRelTypeEnum.PRIVILEGE_GROUP_REL_PRIVILEGE.getRelType().equals(relType)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，不支持的权限组关联类型【" + relType + "】！");
            return output;
        }

        if (CollectionUtils.isEmpty(relIds)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧列表中选择要关联的权限哟！");
            return output;
        }

        FleaAuthRelExtPOJO fleaAuthRelExtPOJO = new FleaAuthRelExtPOJO();
        for (Long relId : relIds) {
            if (AuthBizUtils.isValidId(relId)) {
                fleaPrivilegeModuleSV.privilegeGroupRelPrivilege(ownerId, relId, fleaAuthRelExtPOJO);
            }
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，权限组关联成功，本次新增关联 " + relIds.size() + " 项！");
        return output;
    }

    /* ==================== 私有辅助 ==================== */

    /**
     * <p> 按关联类型构建权限的可关联功能候选列表 </p>
     *
     * @param relType 关联类型
     * @return 候选列表；关联类型不支持时返回 {@code null}
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    private List<Map<String, Object>> buildPrivilegeCandidates(String relType) throws CommonException {

        if (AuthRelTypeEnum.PRIVILEGE_REL_MENU.getRelType().equals(relType)) {
            return AuthCandidateUtil.menus(fleaFunctionModuleSV);
        } else if (AuthRelTypeEnum.PRIVILEGE_REL_OPERATION.getRelType().equals(relType)) {
            return AuthCandidateUtil.operations(fleaFunctionModuleSV);
        } else if (AuthRelTypeEnum.PRIVILEGE_REL_ELEMENT.getRelType().equals(relType)) {
            return AuthCandidateUtil.elements(fleaFunctionModuleSV);
        } else if (AuthRelTypeEnum.PRIVILEGE_REL_RESOURCE.getRelType().equals(relType)) {
            return AuthCandidateUtil.resources(fleaFunctionModuleSV);
        }

        return null;
    }

    /**
     * <p> 按关联类型构建「功能编号 -> 功能名称」映射 </p>
     *
     * <p> 框架构建权限关联 POJO 时要求传入功能名称，此处按关联类型一次性取出，
     * 避免在循环中反复全表查询。 </p>
     *
     * @param relType 关联类型
     * @return 功能编号与名称的映射；关联类型不支持时返回 {@code null}
     * @throws CommonException 通用异常
     * @since 1.0.0
     */
    private Map<Long, String> buildPrivilegeRelNameMap(String relType) throws CommonException {

        Map<Long, String> relNameMap = new HashMap<>();

        if (AuthRelTypeEnum.PRIVILEGE_REL_MENU.getRelType().equals(relType)) {
            List<FleaMenu> menuList = fleaFunctionModuleSV.queryValidMenus(null);
            if (CollectionUtils.isNotEmpty(menuList)) {
                for (FleaMenu fleaMenu : menuList) {
                    if (ObjectUtils.isNotEmpty(fleaMenu)) {
                        relNameMap.put(fleaMenu.getMenuId(), fleaMenu.getMenuName());
                    }
                }
            }
            return relNameMap;
        } else if (AuthRelTypeEnum.PRIVILEGE_REL_OPERATION.getRelType().equals(relType)) {
            List<FleaOperation> operationList = fleaFunctionModuleSV.queryValidOperations(null);
            if (CollectionUtils.isNotEmpty(operationList)) {
                for (FleaOperation fleaOperation : operationList) {
                    if (ObjectUtils.isNotEmpty(fleaOperation)) {
                        relNameMap.put(fleaOperation.getOperationId(), fleaOperation.getOperationName());
                    }
                }
            }
            return relNameMap;
        } else if (AuthRelTypeEnum.PRIVILEGE_REL_ELEMENT.getRelType().equals(relType)) {
            List<FleaElement> elementList = fleaFunctionModuleSV.queryValidElements(null);
            if (CollectionUtils.isNotEmpty(elementList)) {
                for (FleaElement fleaElement : elementList) {
                    if (ObjectUtils.isNotEmpty(fleaElement)) {
                        relNameMap.put(fleaElement.getElementId(), fleaElement.getElementName());
                    }
                }
            }
            return relNameMap;
        } else if (AuthRelTypeEnum.PRIVILEGE_REL_RESOURCE.getRelType().equals(relType)) {
            List<FleaResource> resourceList = fleaFunctionModuleSV.queryValidResources(null);
            if (CollectionUtils.isNotEmpty(resourceList)) {
                for (FleaResource fleaResource : resourceList) {
                    if (ObjectUtils.isNotEmpty(fleaResource)) {
                        relNameMap.put(fleaResource.getResourceId(), fleaResource.getResourceName());
                    }
                }
            }
            return relNameMap;
        }

        return null;
    }

    /**
     * <p> 按关联类型构建权限关联 POJO </p>
     *
     * @param privilegeId 权限编号
     * @param relType     关联类型
     * @param relId       功能编号
     * @param relName     功能名称
     * @return 权限关联 POJO；关联类型不支持时返回 {@code null}
     * @since 1.0.0
     */
    private FleaPrivilegeRelPOJO newFleaPrivilegeRelPOJO(Long privilegeId, String relType, Long relId, String relName) {

        if (AuthRelTypeEnum.PRIVILEGE_REL_MENU.getRelType().equals(relType)) {
            return FleaAuthPOJOUtils.newFleaPrivilegeRelMenuPOJO(privilegeId, relId, relName);
        } else if (AuthRelTypeEnum.PRIVILEGE_REL_OPERATION.getRelType().equals(relType)) {
            return FleaAuthPOJOUtils.newFleaPrivilegeRelOperationPOJO(privilegeId, relId, relName);
        } else if (AuthRelTypeEnum.PRIVILEGE_REL_ELEMENT.getRelType().equals(relType)) {
            return FleaAuthPOJOUtils.newFleaPrivilegeRelElementPOJO(privilegeId, relId, relName);
        } else if (AuthRelTypeEnum.PRIVILEGE_REL_RESOURCE.getRelType().equals(relType)) {
            return FleaAuthPOJOUtils.newFleaPrivilegeRelResourcePOJO(privilegeId, relId, relName);
        }

        return null;
    }

}
