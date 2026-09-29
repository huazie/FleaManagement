package com.huazie.fleamgmt.module.auth.pojo;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * <p> 授权关联 业务入参 </p>
 *
 * <p> 授权类页面（用户授权、用户组授权、角色授权、角色组关联、权限关联、权限组关联）
 * 共用的提交入参：ownerId 为授权主体编号，relType 为关联类型（取值见
 * {@code AuthRelTypeEnum}），relIds 为本次新增关联的被授权方编号集合。 </p>
 *
 * <p> 说明：框架仅提供「新增关联」语义的接口，故本入参承载的是**增量**授权数据；
 * 撤销授权需另行处理，不在本页面范围内。 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class InputAuthRelInfo implements Serializable {

    private static final long serialVersionUID = 5678123409876543210L;

    private Long ownerId; // 授权主体编号（用户/用户组/角色/角色组/权限/权限组）

    private String relType; // 关联类型

    private List<Long> relIds; // 被授权方编号集合

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getRelType() {
        return relType;
    }

    public void setRelType(String relType) {
        this.relType = relType;
    }

    public List<Long> getRelIds() {
        return relIds;
    }

    public void setRelIds(List<Long> relIds) {
        this.relIds = relIds;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
