package com.huazie.fleamgmt.module.auth.pojo;

import com.huazie.fleaframework.common.pojo.OutputCommonData;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;

/**
 * <p> 授权功能（菜单、操作、元素、资源）业务出参 </p>
 *
 * <p> treeList 为左侧列表（Fuelux 树数据），data 为单条功能明细（变更页回填用）。</p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class OutputFunctionInfo extends OutputCommonData {

    private static final long serialVersionUID = 3725487190268374616L;

    private List<Map<String, Object>> treeList; // 功能列表（Fuelux 树数据）

    private Map<String, Object> data; // 单条功能明细

    public List<Map<String, Object>> getTreeList() {
        return treeList;
    }

    public void setTreeList(List<Map<String, Object>> treeList) {
        this.treeList = treeList;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
