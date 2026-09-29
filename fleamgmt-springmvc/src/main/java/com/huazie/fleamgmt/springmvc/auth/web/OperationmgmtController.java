package com.huazie.fleamgmt.springmvc.auth.web;

import com.huazie.fleaframework.auth.base.function.entity.FleaOperation;
import com.huazie.fleaframework.auth.common.pojo.function.operation.FleaOperationPOJO;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaFunctionModuleSV;
import com.huazie.fleaframework.common.exceptions.CommonException;
import com.huazie.fleaframework.common.pojo.OutputCommonData;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleaframework.common.util.ObjectUtils;
import com.huazie.fleaframework.common.util.POJOUtils;
import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.module.auth.pojo.InputOperationInfo;
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
 * <p> 操作管理 Controller </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class OperationmgmtController extends BusinessController {

    private static final FleaLogger LOGGER = FleaLoggerProxy.getProxyInstance(OperationmgmtController.class);

    /**
     * <p> 操作节点信息提取器，用于把操作数据转换成前端 Fuelux 树的扁平节点 </p>
     */
    private static final AuthFunctionTreeUtil.FunctionNodeExtractor<FleaOperation> OPERATION_NODE_EXTRACTOR
            = new AuthFunctionTreeUtil.FunctionNodeExtractor<FleaOperation>() {

        @Override
        public Long getId(FleaOperation operation) {
            return operation.getOperationId();
        }

        @Override
        public String getCode(FleaOperation operation) {
            return operation.getOperationCode();
        }

        @Override
        public String getName(FleaOperation operation) {
            return operation.getOperationName();
        }
    };

    private IFleaFunctionModuleSV fleaFunctionModuleSV;

    @Resource(name = "fleaFunctionModuleSV")
    public void setFleaFunctionModuleSV(IFleaFunctionModuleSV fleaFunctionModuleSV) {
        this.fleaFunctionModuleSV = fleaFunctionModuleSV;
    }

    /**
     * <p> 展示操作列表（操作没有层级，统一为 Fuelux 树的叶子节点） </p>
     *
     * @return 操作列表信息
     * @since 1.0.0
     */
    @GetMapping("authOperation!list.flea")
    @ResponseBody
    public OutputFunctionInfo list() throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        List<FleaOperation> operationList = fleaFunctionModuleSV.queryValidOperations(null);
        List<Map<String, Object>> operationTreeList = AuthFunctionTreeUtil.toFlatTreeList(operationList, OPERATION_NODE_EXTRACTOR);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Operation Tree List = {}", operationTreeList);
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setTreeList(operationTreeList);

        return output;
    }

    /**
     * <p> 操作明细查询（操作变更页回填用） </p>
     *
     * @param operationId 操作编号
     * @return 操作明细信息
     * @since 1.0.0
     */
    @GetMapping("authOperation!query.flea")
    @ResponseBody
    public OutputFunctionInfo query(@RequestParam("operationId") Long operationId) throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        // 操作查询接口不支持按操作编号过滤，先查有效操作，再按编号匹配
        List<FleaOperation> operationList = fleaFunctionModuleSV.queryValidOperations(null);
        FleaOperation fleaOperation = AuthFunctionTreeUtil.findById(operationList, operationId, OPERATION_NODE_EXTRACTOR);

        if (ObjectUtils.isEmpty(fleaOperation)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，操作【" + operationId + "】不存在或已失效，请刷新操作列表后重试！");
            return output;
        }

        Map<String, Object> operationMap = new HashMap<>();
        operationMap.put("operationId", fleaOperation.getOperationId());
        operationMap.put("operationCode", fleaOperation.getOperationCode());
        operationMap.put("operationName", fleaOperation.getOperationName());
        operationMap.put("operationDesc", fleaOperation.getOperationDesc());
        operationMap.put("remarks", fleaOperation.getRemarks());

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setData(operationMap);

        return output;
    }

    /**
     * <p> 操作新增 </p>
     *
     * @param inputOperationInfo 操作业务入参
     * @return 操作新增结果
     * @since 1.0.0
     */
    @PostMapping("authOperation!add.flea")
    @ResponseBody
    public OutputCommonData operationAdd(InputOperationInfo inputOperationInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        FleaOperationPOJO fleaOperationPOJO = new FleaOperationPOJO();
        POJOUtils.copyAll(inputOperationInfo, fleaOperationPOJO);

        Long operationId = fleaFunctionModuleSV.addFleaOperation(fleaOperationPOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，操作添加成功，操作编号：" + operationId);
        return output;
    }

    /**
     * <p> 操作变更 </p>
     *
     * @param inputOperationInfo 操作业务入参
     * @return 操作变更结果
     * @since 1.0.0
     */
    @PostMapping("authOperation!update.flea")
    @ResponseBody
    public OutputCommonData operationUpdate(InputOperationInfo inputOperationInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        FleaOperationPOJO fleaOperationPOJO = new FleaOperationPOJO();
        POJOUtils.copyAll(inputOperationInfo, fleaOperationPOJO);

        fleaFunctionModuleSV.modifyFleaOperation(inputOperationInfo.getOperationId(), fleaOperationPOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，操作修改成功！");
        return output;
    }

}
