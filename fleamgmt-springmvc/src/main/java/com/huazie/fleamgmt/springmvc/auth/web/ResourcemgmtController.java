package com.huazie.fleamgmt.springmvc.auth.web;

import com.huazie.fleaframework.auth.base.function.entity.FleaResource;
import com.huazie.fleaframework.auth.common.pojo.function.resource.FleaResourcePOJO;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaFunctionModuleSV;
import com.huazie.fleaframework.common.exceptions.CommonException;
import com.huazie.fleaframework.common.pojo.OutputCommonData;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleaframework.common.util.ObjectUtils;
import com.huazie.fleaframework.common.util.POJOUtils;
import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.module.auth.pojo.InputResourceInfo;
import com.huazie.fleamgmt.module.auth.pojo.OutputFunctionInfo;
import com.huazie.fleamgmt.module.auth.util.AuthFunctionTreeUtil;
import com.huazie.fleamgmt.springmvc.base.web.BusinessController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 资源管理 Controller </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class ResourcemgmtController extends BusinessController {

    private static final FleaLogger LOGGER = FleaLoggerProxy.getProxyInstance(ResourcemgmtController.class);

    /**
     * <p> 资源节点信息提取器，用于把资源数据转换成前端 Fuelux 树的扁平节点 </p>
     */
    private static final AuthFunctionTreeUtil.FunctionNodeExtractor<FleaResource> RESOURCE_NODE_EXTRACTOR
            = new AuthFunctionTreeUtil.FunctionNodeExtractor<FleaResource>() {

        @Override
        public Long getId(FleaResource resource) {
            return resource.getResourceId();
        }

        @Override
        public String getCode(FleaResource resource) {
            return resource.getResourceCode();
        }

        @Override
        public String getName(FleaResource resource) {
            return resource.getResourceName();
        }
    };

    private IFleaFunctionModuleSV fleaFunctionModuleSV;

    @Resource(name = "fleaFunctionModuleSV")
    public void setFleaFunctionModuleSV(IFleaFunctionModuleSV fleaFunctionModuleSV) {
        this.fleaFunctionModuleSV = fleaFunctionModuleSV;
    }

    /**
     * <p> 展示资源列表（资源没有层级，统一为 Fuelux 树的叶子节点） </p>
     *
     * @return 资源列表信息
     * @since 1.0.0
     */
    @GetMapping("authResource!list.flea")
    @ResponseBody
    public OutputFunctionInfo list() throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        List<FleaResource> resourceList = fleaFunctionModuleSV.queryValidResources(null);
        List<Map<String, Object>> resourceTreeList = AuthFunctionTreeUtil.toFlatTreeList(resourceList, RESOURCE_NODE_EXTRACTOR);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Resource Tree List = {}", resourceTreeList);
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setTreeList(resourceTreeList);

        return output;
    }

    /**
     * <p> 资源明细查询（资源变更页回填用） </p>
     *
     * @param resourceId 资源编号
     * @return 资源明细信息
     * @since 1.0.0
     */
    @GetMapping("authResource!query.flea")
    @ResponseBody
    public OutputFunctionInfo query(@RequestParam("resourceId") Long resourceId) throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        // 资源查询接口不支持按资源编号过滤，先查有效资源，再按编号匹配
        List<FleaResource> resourceList = fleaFunctionModuleSV.queryValidResources(null);
        FleaResource fleaResource = AuthFunctionTreeUtil.findById(resourceList, resourceId, RESOURCE_NODE_EXTRACTOR);

        if (ObjectUtils.isEmpty(fleaResource)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，资源【" + resourceId + "】不存在或已失效，请刷新资源列表后重试！");
            return output;
        }

        Map<String, Object> resourceMap = new HashMap<>();
        resourceMap.put("resourceId", fleaResource.getResourceId());
        resourceMap.put("resourceCode", fleaResource.getResourceCode());
        resourceMap.put("resourceName", fleaResource.getResourceName());
        resourceMap.put("resourceDesc", fleaResource.getResourceDesc());
        resourceMap.put("remarks", fleaResource.getRemarks());

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setData(resourceMap);

        return output;
    }

    /**
     * <p> 资源新增 </p>
     *
     * @param inputResourceInfo 资源业务入参
     * @return 资源新增结果
     * @since 1.0.0
     */
    @PostMapping("authResource!add.flea")
    @ResponseBody
    public OutputCommonData resourceAdd(InputResourceInfo inputResourceInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        FleaResourcePOJO fleaResourcePOJO = new FleaResourcePOJO();
        POJOUtils.copyAll(inputResourceInfo, fleaResourcePOJO);

        Long resourceId = fleaFunctionModuleSV.addFleaResource(fleaResourcePOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，资源添加成功，资源编号：" + resourceId);
        return output;
    }

    /**
     * <p> 资源变更 </p>
     *
     * @param inputResourceInfo 资源业务入参
     * @return 资源变更结果
     * @since 1.0.0
     */
    @PostMapping("authResource!update.flea")
    @ResponseBody
    public OutputCommonData resourceUpdate(InputResourceInfo inputResourceInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        FleaResourcePOJO fleaResourcePOJO = new FleaResourcePOJO();
        POJOUtils.copyAll(inputResourceInfo, fleaResourcePOJO);

        fleaFunctionModuleSV.modifyFleaResource(inputResourceInfo.getResourceId(), fleaResourcePOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，资源修改成功！");
        return output;
    }

}
