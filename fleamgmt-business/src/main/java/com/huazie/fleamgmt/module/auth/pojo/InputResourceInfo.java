package com.huazie.fleamgmt.module.auth.pojo;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * <p> 资源信息 业务入参 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class InputResourceInfo implements Serializable {

    private static final long serialVersionUID = 6158293047615293847L;

    private Long resourceId; // 资源编号

    private String resourceCode; // 资源编码

    private String resourceName; // 资源名称

    private String resourceDesc; // 资源描述

    private String remarks; // 备注

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceDesc() {
        return resourceDesc;
    }

    public void setResourceDesc(String resourceDesc) {
        this.resourceDesc = resourceDesc;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
