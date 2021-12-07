package com.huazie.fleamgmt.struts2.base.web;

import com.huazie.fleaframework.common.pojo.OutputCommonData;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <p> 基础的Action </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class BaseAction extends ActionSupport {

    private static final long serialVersionUID = -4028544941597359557L;

    protected OutputCommonData result = new OutputCommonData(); // 返回的结果

    public OutputCommonData getResult() {
        return result;
    }

    public void setResult(OutputCommonData result) {
        this.result = result;
    }

}
