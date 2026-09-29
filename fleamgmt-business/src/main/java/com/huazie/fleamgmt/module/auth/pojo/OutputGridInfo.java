package com.huazie.fleamgmt.module.auth.pojo;

import com.huazie.fleaframework.common.pojo.OutputCommonData;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;

/**
 * <p> 授权管理模块「明细列表」业务出参 </p>
 *
 * <p> 供列表类页面（用户变更、用户组变更等）的表格（jqGrid）渲染使用： </p>
 * <ul>
 *     <li>rows：明细行集合，每行以「字段名-值」的形式给出，具体列定义由页面维护；</li>
 *     <li>summary：列表汇总统计（总数、正常数、禁用数等），用于页面顶部统计卡片。</li>
 * </ul>
 *
 * <p> 说明：与 {@code OutputFunctionInfo#treeList} 的区别在于，treeList 服务于 Fuelux 树的
 * 「层级节点」语义（name/type/id/code/level），而本出参服务于表格的「扁平明细」语义，
 * 两者承载的字段不同，故独立定义，避免相互污染。 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class OutputGridInfo extends OutputCommonData {

    private static final long serialVersionUID = 6153487902174635281L;

    private List<Map<String, Object>> rows; // 明细行集合

    private Map<String, Object> summary; // 汇总统计

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
    }

    public Map<String, Object> getSummary() {
        return summary;
    }

    public void setSummary(Map<String, Object> summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
