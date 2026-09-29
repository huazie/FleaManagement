package com.huazie.fleamgmt.module.auth.pojo;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * <p> 元素信息 业务入参 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class InputElementInfo implements Serializable {

    private static final long serialVersionUID = -3392761496273845172L;

    private Long elementId; // 元素编号

    private String elementCode; // 元素编码

    private String elementName; // 元素名称

    private String elementDesc; // 元素描述

    private Integer elementType; // 元素类型

    private String elementContent; // 元素内容

    private String remarks; // 备注

    public Long getElementId() {
        return elementId;
    }

    public void setElementId(Long elementId) {
        this.elementId = elementId;
    }

    public String getElementCode() {
        return elementCode;
    }

    public void setElementCode(String elementCode) {
        this.elementCode = elementCode;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getElementDesc() {
        return elementDesc;
    }

    public void setElementDesc(String elementDesc) {
        this.elementDesc = elementDesc;
    }

    public Integer getElementType() {
        return elementType;
    }

    public void setElementType(Integer elementType) {
        this.elementType = elementType;
    }

    public String getElementContent() {
        return elementContent;
    }

    public void setElementContent(String elementContent) {
        this.elementContent = elementContent;
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
