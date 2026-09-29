package com.huazie.fleamgmt.springmvc.auth.web;

import com.huazie.fleaframework.auth.base.function.entity.FleaElement;
import com.huazie.fleaframework.auth.common.pojo.function.element.FleaElementPOJO;
import com.huazie.fleaframework.auth.common.service.interfaces.IFleaFunctionModuleSV;
import com.huazie.fleaframework.common.exceptions.CommonException;
import com.huazie.fleaframework.common.pojo.OutputCommonData;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleaframework.common.util.ObjectUtils;
import com.huazie.fleaframework.common.util.POJOUtils;
import com.huazie.fleamgmt.constant.FleamgmtConstants;
import com.huazie.fleamgmt.module.auth.pojo.InputElementInfo;
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
 * <p> 元素管理 Controller </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
@Controller
public class ElementmgmtController extends BusinessController {

    private static final FleaLogger LOGGER = FleaLoggerProxy.getProxyInstance(ElementmgmtController.class);

    /**
     * <p> 元素节点信息提取器，用于把元素数据转换成前端 Fuelux 树的扁平节点 </p>
     */
    private static final AuthFunctionTreeUtil.FunctionNodeExtractor<FleaElement> ELEMENT_NODE_EXTRACTOR
            = new AuthFunctionTreeUtil.FunctionNodeExtractor<FleaElement>() {

        @Override
        public Long getId(FleaElement element) {
            return element.getElementId();
        }

        @Override
        public String getCode(FleaElement element) {
            return element.getElementCode();
        }

        @Override
        public String getName(FleaElement element) {
            return element.getElementName();
        }
    };

    private IFleaFunctionModuleSV fleaFunctionModuleSV;

    @Resource(name = "fleaFunctionModuleSV")
    public void setFleaFunctionModuleSV(IFleaFunctionModuleSV fleaFunctionModuleSV) {
        this.fleaFunctionModuleSV = fleaFunctionModuleSV;
    }

    /**
     * <p> 展示元素列表（元素没有层级，统一为 Fuelux 树的叶子节点） </p>
     *
     * @return 元素列表信息
     * @since 1.0.0
     */
    @GetMapping("authElement!list.flea")
    @ResponseBody
    public OutputFunctionInfo list() throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        List<FleaElement> elementList = fleaFunctionModuleSV.queryValidElements(null);
        List<Map<String, Object>> elementTreeList = AuthFunctionTreeUtil.toFlatTreeList(elementList, ELEMENT_NODE_EXTRACTOR);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Element Tree List = {}", elementTreeList);
        }

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setTreeList(elementTreeList);

        return output;
    }

    /**
     * <p> 元素明细查询（元素变更页回填用） </p>
     *
     * @param elementId 元素编号
     * @return 元素明细信息
     * @since 1.0.0
     */
    @GetMapping("authElement!query.flea")
    @ResponseBody
    public OutputFunctionInfo query(@RequestParam("elementId") Long elementId) throws CommonException {

        OutputFunctionInfo output = new OutputFunctionInfo();

        // 元素查询接口不支持按元素编号过滤，先查有效元素，再按编号匹配
        List<FleaElement> elementList = fleaFunctionModuleSV.queryValidElements(null);
        FleaElement fleaElement = AuthFunctionTreeUtil.findById(elementList, elementId, ELEMENT_NODE_EXTRACTOR);

        if (ObjectUtils.isEmpty(fleaElement)) {
            output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
            output.setRetMess("亲，元素【" + elementId + "】不存在或已失效，请刷新元素列表后重试！");
            return output;
        }

        Map<String, Object> elementMap = new HashMap<>();
        elementMap.put("elementId", fleaElement.getElementId());
        elementMap.put("elementCode", fleaElement.getElementCode());
        elementMap.put("elementName", fleaElement.getElementName());
        elementMap.put("elementType", fleaElement.getElementType());
        elementMap.put("elementContent", fleaElement.getElementContent());
        elementMap.put("elementDesc", fleaElement.getElementDesc());
        elementMap.put("remarks", fleaElement.getRemarks());

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("SUCCESS");
        output.setData(elementMap);

        return output;
    }

    /**
     * <p> 元素新增 </p>
     *
     * @param inputElementInfo 元素业务入参
     * @return 元素新增结果
     * @since 1.0.0
     */
    @PostMapping("authElement!add.flea")
    @ResponseBody
    public OutputCommonData elementAdd(InputElementInfo inputElementInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        FleaElementPOJO fleaElementPOJO = new FleaElementPOJO();
        POJOUtils.copyAll(inputElementInfo, fleaElementPOJO);

        Long elementId = fleaFunctionModuleSV.addFleaElement(fleaElementPOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，元素添加成功，元素编号：" + elementId);
        return output;
    }

    /**
     * <p> 元素变更 </p>
     *
     * @param inputElementInfo 元素业务入参
     * @return 元素变更结果
     * @since 1.0.0
     */
    @PostMapping("authElement!update.flea")
    @ResponseBody
    public OutputCommonData elementUpdate(InputElementInfo inputElementInfo) throws CommonException {

        OutputCommonData output = new OutputCommonData();

        FleaElementPOJO fleaElementPOJO = new FleaElementPOJO();
        POJOUtils.copyAll(inputElementInfo, fleaElementPOJO);

        fleaFunctionModuleSV.modifyFleaElement(inputElementInfo.getElementId(), fleaElementPOJO);

        output.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_Y);
        output.setRetMess("亲，元素修改成功！");
        return output;
    }

}
