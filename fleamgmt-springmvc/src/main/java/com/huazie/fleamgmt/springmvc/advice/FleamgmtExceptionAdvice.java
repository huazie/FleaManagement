package com.huazie.fleamgmt.springmvc.advice;

import com.huazie.fleaframework.common.exceptions.CommonException;
import com.huazie.fleaframework.common.pojo.OutputCommonData;
import com.huazie.fleaframework.common.slf4j.FleaLogger;
import com.huazie.fleaframework.common.slf4j.impl.FleaLoggerProxy;
import com.huazie.fleamgmt.constant.FleamgmtConstants;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 通用异常处理
 *
 * @author huazie
 * @version 2.0.0
 * @since 2.0.0
 */
@ControllerAdvice
public class FleamgmtExceptionAdvice {

    private static final FleaLogger LOGGER = FleaLoggerProxy.getProxyInstance(FleamgmtExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public OutputCommonData handleException(HttpServletRequest request, Exception e) {
        if (LOGGER.isDebugEnabled()) {
            String requestUrl = request.getRequestURI();
            String queryString = request.getQueryString();
            Map<String, String[]> parameterMap = request.getParameterMap();
            LOGGER.debug("RequestURL={}, QueryString={}, ParameterMap={}", requestUrl, queryString, parameterMap, e);
        }
        OutputCommonData result = new OutputCommonData();
        result.setRetCode(FleamgmtConstants.ReturnCodeConstants.RETURN_CODE_N);
        if (e instanceof CommonException) {
            result.setRetMess(e.getMessage());
        } else if (null != e.getCause() && e.getCause() instanceof CommonException) {
            result.setRetMess(e.getCause().getMessage());
        } else {
            result.setRetMess("服务器开小差了，请您稍后重新尝试！！！");
        }
        return result;
    }
}
