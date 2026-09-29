package com.huazie.fleamgmt.module.auth.pojo;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * <p> 权限组信息 业务入参 </p>
 *
 * <p> isMain、functionType 仅在新增权限组时使用：
 * isMain=1 表示主权限组（同一功能类型下框架只认一个主权限组），
 * functionType 取值见框架 {@code FunctionTypeEnum}（MENU/OPERATION/ELEMENT/RESOURCE）。 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class InputPrivilegeGroupInfo implements Serializable {

    private static final long serialVersionUID = 3098712345678901234L;

    private Long privilegeGroupId; // 权限组编号

    private String privilegeGroupName; // 权限组名称

    private String privilegeGroupDesc; // 权限组描述

    private Integer isMain; // 是否为主权限组（0：不是 1：是）

    private String functionType; // 功能类型(菜单、操作、元素、资源)

    private String remarks; // 备注

    public Long getPrivilegeGroupId() {
        return privilegeGroupId;
    }

    public void setPrivilegeGroupId(Long privilegeGroupId) {
        this.privilegeGroupId = privilegeGroupId;
    }

    public String getPrivilegeGroupName() {
        return privilegeGroupName;
    }

    public void setPrivilegeGroupName(String privilegeGroupName) {
        this.privilegeGroupName = privilegeGroupName;
    }

    public String getPrivilegeGroupDesc() {
        return privilegeGroupDesc;
    }

    public void setPrivilegeGroupDesc(String privilegeGroupDesc) {
        this.privilegeGroupDesc = privilegeGroupDesc;
    }

    public Integer getIsMain() {
        return isMain;
    }

    public void setIsMain(Integer isMain) {
        this.isMain = isMain;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
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
