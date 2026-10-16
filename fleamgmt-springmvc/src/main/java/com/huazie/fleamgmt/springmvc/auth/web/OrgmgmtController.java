package com.huazie.fleamgmt.springmvc.auth.web;

import com.huazie.fleaframework.auth.base.audit.service.impl.FleaAuthAuditLogger;
import com.huazie.fleaframework.auth.base.audit.util.FleaAuthAuditDiffUtils;
import com.huazie.fleaframework.auth.base.organization.entity.FleaOrganization;
import com.huazie.fleaframework.auth.common.pojo.FleaAuthRelExtPOJO;
import com.huazie.fleaframework.auth.common.pojo.organization.FleaOrganizationPOJO;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaOrgModuleSV;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaUserModuleSV;
import com.huazie.fleaframework.common.exceptions.CommonException;
import com.huazie.fleaframework.common.pojo.OutputCommonData;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleaframework.common.util.ObjectUtils;
import com.huazie.fleaframework.common.util.StringUtils;
import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.module.auth.pojo.InputAuthRelInfo;
import com.huazie.fleamgmt.module.auth.pojo.InputOrgInfo;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 组织管理 Controller </p>
 *
 * <p> 覆盖：组织新增、组织变更、用户关联组织。
 * 组织是用户的归属维度，不做权限授权主体，用户关联组织走 flea_user_org_rel 关系表。
 * 全部数据操作统一通过框架的 {@code IFleaOrgModuleSV} 完成。 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class OrgmgmtController extends BusinessController {

    private static final FleaLogger LOGGER = FleaLoggerProxy.getProxyInstance(OrgmgmtController.class);

    /**
     * <p> 用户关联组织的关联类型（唯一的授权维度） </p>
     */
    private static final String REL_TYPE_ORG_REL_USER = "ORG_REL_USER";

    /**
     * <p> 组织变更审计字段标签（key：属性名，value：展示名） </p>
     */
    private static final Map<String, String> ORG_AUDIT_LABELS = new LinkedHashMap<String, String>();

    static {
        ORG_AUDIT_LABELS.put("orgCode", "组织编码");
        ORG_AUDIT_LABELS.put("orgName", "组织名称");
        ORG_AUDIT_LABELS.put("orgDesc", "组织描述");
        ORG_AUDIT_LABELS.put("orgType", "组织类型");
        ORG_AUDIT_LABELS.put("parentId", "父组织编号");
        ORG_AUDIT_LABELS.put("remarks", "备注");
    }

    private IFleaOrgModuleSV fleaOrgModuleSV;

    private IFleaUserModuleSV fleaUserModuleSV;

    @Resource(name = "fleaOrgModuleSV")
    public void setFleaOrgModuleSV(IFleaOrgModuleSV fleaOrgModuleSV) {
        this.fleaOrgModuleSV = fleaOrgModuleSV;
    }

    @Resource(name = "fleaUserModuleSV")
    public void setFleaUserModuleSV(IFleaUserModuleSV fleaUserModuleSV) {
        this.fleaUserModuleSV = fleaUserModuleSV;
    }

    @Resource(name = "fleaAuthAuditLogger")
    private FleaAuthAuditLogger authAuditLogger;

    /**
     * <p> 组织明细列表（组织变更页表格用） </p>
     *
     * @return 组织明细行集合与汇总统计
     * @since 1.0.0
     */
    @GetMapping("authOrg!page.flea")
    @ResponseBody
    public OutputGridInfo page() throws CommonException {

        OutputGridInfo output = new OutputGridInfo();

        List<Map<String, Object>> rowList = AuthCandidateUtil.orgRows(fleaOrgModuleSV);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setRows(rowList);
        output.setSummary(AuthBizUtils.summarize(rowList, "orgState"));

        return output;
    }

    /**
     * <p> 组织候选列表（新增组织的父组织下拉用） </p>
     *
     * @return 组织候选列表
     * @since 1.0.0
     */
    @GetMapping("authOrg!list.flea")
    @ResponseBody
    public OutputFunctionInfo list() throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setTreeList(AuthCandidateUtil.organizations(fleaOrgModuleSV));

        return output;
    }

    /**
     * <p> 组织明细查询（组织变更页回填用） </p>
     *
     * @param orgId 组织编号
     * @return 组织明细信息
     * @since 1.0.0
     */
    @GetMapping("authOrg!query.flea")
    @ResponseBody
    public OutputFunctionInfo query(@RequestParam("orgId") Long orgId) throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        FleaOrganization fleaOrganization = fleaOrgModuleSV.queryValidOrganization(orgId);

        if (ObjectUtils.isEmpty(fleaOrganization)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，组织【" + orgId + "】不存在或已失效，请刷新组织列表后重试！");
            return output;
        }

        List<Map<String, Object>> rowList = AuthCandidateUtil.orgRows(fleaOrgModuleSV);
        Map<String, Object> orgMap = new HashMap<>();
        for (Map<String, Object> row : rowList) {
            if (row != null && orgId.equals(row.get("orgId"))) {
                orgMap = row;
                break;
            }
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setData(orgMap);

        return output;
    }

    /**
     * <p> 组织新增 </p>
     *
     * @param inputOrgInfo 组织业务入参
     * @return 组织新增结果
     * @since 1.0.0
     */
    @PostMapping("authOrg!add.flea")
    @ResponseBody
    public OutputCommonData addOrg(InputOrgInfo inputOrgInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        String checkMess = checkOrgBasic(inputOrgInfo);
        if (checkMess != null) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess(checkMess);
            return output;
        }

        Long orgId = fleaOrgModuleSV.addOrganization(toOrgPOJO(inputOrgInfo));

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，组织添加成功，组织编号：" + (orgId == null ? "" : orgId));

        if (orgId != null) {
            authAuditLogger.recordCreate("org_" + orgId, output.getRetMess(), WebUtils.getRequest());
        }

        return output;
    }

    /**
     * <p> 组织变更（父组织支持变更，框架层做防成环校验并重算子树层级） </p>
     *
     * @param inputOrgInfo 组织业务入参
     * @return 组织变更结果
     * @since 1.0.0
     */
    @PostMapping("authOrg!update.flea")
    @ResponseBody
    public OutputCommonData updateOrg(InputOrgInfo inputOrgInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        if (!AuthBizUtils.isValidId(inputOrgInfo.getOrgId())) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧列表中选择要变更的组织哟！");
            return output;
        }

        if (ObjectUtils.isEmpty(inputOrgInfo.getOrgName())) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请填写组织名称哟！");
            return output;
        }

        if (ObjectUtils.isEmpty(inputOrgInfo.getOrgType())) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请选择组织类型哟！");
            return output;
        }

        // 变更前快照，用于审计备注记录具体修改内容
        FleaOrganization oldOrganization = fleaOrgModuleSV.queryValidOrganization(inputOrgInfo.getOrgId());

        FleaOrganizationPOJO orgPOJO = toOrgPOJO(inputOrgInfo);

        fleaOrgModuleSV.modifyOrganization(inputOrgInfo.getOrgId(), orgPOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，组织修改成功！");

        String changeDesc = FleaAuthAuditDiffUtils.diff(oldOrganization, orgPOJO, ORG_AUDIT_LABELS);
        authAuditLogger.recordUpdate("org_" + inputOrgInfo.getOrgId(), output.getRetMess(), changeDesc, WebUtils.getRequest());

        return output;
    }

    /**
     * <p> 用户关联组织明细（可关联用户 + 已关联用户编号） </p>
     *
     * @param orgId   组织编号
     * @param relType 关联类型（ORG_REL_USER）
     * @return 关联明细信息
     * @since 1.0.0
     */
    @GetMapping("authOrgUserRel!auth.flea")
    @ResponseBody
    public OutputAuthInfo authOrgUserRel(@RequestParam("orgId") Long orgId,
                                         @RequestParam("relType") String relType) throws CommonException {

        OutputAuthInfo output = new OutputAuthInfo();

        if (!REL_TYPE_ORG_REL_USER.equals(relType)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，不支持的组织关联类型【" + relType + "】！");
            return output;
        }

        FleaOrganization fleaOrganization = fleaOrgModuleSV.queryValidOrganization(orgId);
        if (ObjectUtils.isEmpty(fleaOrganization)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，组织【" + orgId + "】不存在或已失效，请刷新组织列表后重试！");
            return output;
        }

        List<Long> selectedList = fleaOrgModuleSV.getRelUserIds(orgId);

        Map<String, Integer> relTypeCounts = new HashMap<>();
        relTypeCounts.put(REL_TYPE_ORG_REL_USER, selectedList.size());

        output.setOwnerId(orgId);
        output.setOwnerName(fleaOrganization.getOrgName());
        output.setRelType(relType);
        output.setCandidates(AuthCandidateUtil.users(fleaUserModuleSV));
        output.setSelected(selectedList);
        output.setRelTypeCounts(relTypeCounts);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");

        return output;
    }

    /**
     * <p> 用户关联组织提交（逐条新增用户组织关联） </p>
     *
     * @param inputAuthRelInfo 授权业务入参
     * @return 关联结果
     * @since 1.0.0
     */
    @PostMapping("authOrgUserRel!authorize.flea")
    @ResponseBody
    public OutputCommonData authorizeOrgUserRel(InputAuthRelInfo inputAuthRelInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        Long ownerId = inputAuthRelInfo.getOwnerId();
        String relType = inputAuthRelInfo.getRelType();
        List<Long> relIds = inputAuthRelInfo.getRelIds();

        if (!AuthBizUtils.isValidId(ownerId)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧组织列表中选择要关联的组织哟！");
            return output;
        }

        if (!REL_TYPE_ORG_REL_USER.equals(relType)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，不支持的组织关联类型【" + relType + "】！");
            return output;
        }

        if (relIds == null || relIds.isEmpty()) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧列表中选择要关联的用户哟！");
            return output;
        }

        int count = 0;
        for (Long userId : relIds) {
            if (!AuthBizUtils.isValidId(userId)) {
                continue;
            }
            // 框架侧幂等：已存在有效关联时跳过
            if (fleaOrgModuleSV.userRelOrganization(userId, ownerId, new FleaAuthRelExtPOJO())) {
                count++;
            }
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，用户关联组织成功，本次新增关联 " + count + " 项！");

        authAuditLogger.recordGrant("org_" + ownerId, output.getRetMess() + "（关联类型：" + relType + "）", WebUtils.getRequest());

        return output;
    }

    /**
     * <p> 用户取消关联组织（逻辑删除用户组织关联） </p>
     *
     * @param inputAuthRelInfo 授权业务入参
     * @return 取消关联结果
     * @since 1.0.0
     */
    @PostMapping("authOrgUserRel!cancel.flea")
    @ResponseBody
    public OutputCommonData cancelOrgUserRel(InputAuthRelInfo inputAuthRelInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        Long ownerId = inputAuthRelInfo.getOwnerId();
        String relType = inputAuthRelInfo.getRelType();
        List<Long> relIds = inputAuthRelInfo.getRelIds();

        if (!AuthBizUtils.isValidId(ownerId)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从左侧组织列表中选择要取消关联的组织哟！");
            return output;
        }

        if (!REL_TYPE_ORG_REL_USER.equals(relType)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，不支持的组织关联类型【" + relType + "】！");
            return output;
        }

        if (relIds == null || relIds.isEmpty()) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，请先从授权清单中选择要取消关联的数据哟！");
            return output;
        }

        int count = 0;
        for (Long userId : relIds) {
            if (!AuthBizUtils.isValidId(userId)) {
                continue;
            }
            count += fleaOrgModuleSV.cancelUserOrganization(userId, ownerId);
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，用户取消关联组织成功，本次取消关联 " + count + " 项！");

        authAuditLogger.recordRevoke("org_" + ownerId, output.getRetMess() + "（关联类型：" + relType + "）", WebUtils.getRequest());

        return output;
    }

    /* ==================== 内部方法 ==================== */

    /**
     * <p> 校验组织基础信息（编码 / 名称 / 类型） </p>
     *
     * @param inputOrgInfo 组织业务入参
     * @return 校验不通过时返回提示信息，通过返回 {@code null}
     * @since 1.0.0
     */
    private String checkOrgBasic(InputOrgInfo inputOrgInfo) {

        if (ObjectUtils.isEmpty(inputOrgInfo) || StringUtils.isBlank(inputOrgInfo.getOrgCode())) {
            return "亲，请填写组织编码哟！";
        }

        if (StringUtils.isBlank(inputOrgInfo.getOrgName())) {
            return "亲，请填写组织名称哟！";
        }

        if (ObjectUtils.isEmpty(inputOrgInfo.getOrgType())) {
            return "亲，请选择组织类型哟！";
        }

        return null;
    }

    /**
     * <p> 组织业务入参转 Flea组织POJO对象 </p>
     *
     * @param inputOrgInfo 组织业务入参
     * @return Flea组织POJO对象
     * @since 1.0.0
     */
    private FleaOrganizationPOJO toOrgPOJO(InputOrgInfo inputOrgInfo) {

        FleaOrganizationPOJO orgPOJO = new FleaOrganizationPOJO();
        orgPOJO.setOrgCode(inputOrgInfo.getOrgCode());
        orgPOJO.setOrgName(inputOrgInfo.getOrgName());
        orgPOJO.setOrgDesc(inputOrgInfo.getOrgDesc());
        orgPOJO.setOrgType(inputOrgInfo.getOrgType());
        orgPOJO.setParentId(inputOrgInfo.getParentId());
        orgPOJO.setRemarks(inputOrgInfo.getRemarks());

        return orgPOJO;
    }

}
