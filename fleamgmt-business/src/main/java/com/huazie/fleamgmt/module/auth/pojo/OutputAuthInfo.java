package com.huazie.fleamgmt.module.auth.pojo;

import com.huazie.fleaframework.common.pojo.OutputCommonData;

import java.util.List;
import java.util.Map;

/**
 * <p> 授权信息 业务出参 </p>
 *
 * <p> 授权类页面（用户授权、用户组授权、角色授权、角色组关联、权限关联、权限组关联）
 * 共用的明细出参： </p>
 * <ul>
 *     <li>ownerId / ownerName：授权主体编号与名称，用于页面顶部展示；</li>
 *     <li>relType：当前选中的关联类型；</li>
 *     <li>candidates：可授权方候选列表（Fuelux 树扁平节点结构）；</li>
 *     <li>selected：该关联类型下已授权的编号集合，用于左侧列表打标与右侧已选清单回显；</li>
 *     <li>relTypeCounts：各关联类型下已授权的数量，用于授权页标签页徽章展示。</li>
 * </ul>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public class OutputAuthInfo extends OutputCommonData {

    private static final long serialVersionUID = -7203948561230789451L;

    private Long ownerId; // 授权主体编号

    private String ownerName; // 授权主体名称

    private String relType; // 关联类型

    private List<Map<String, Object>> candidates; // 可授权方候选列表

    private List<Long> selected; // 已授权的编号集合

    private Map<String, Integer> relTypeCounts; // 各关联类型下已授权的数量

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getRelType() {
        return relType;
    }

    public void setRelType(String relType) {
        this.relType = relType;
    }

    public List<Map<String, Object>> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Map<String, Object>> candidates) {
        this.candidates = candidates;
    }

    public List<Long> getSelected() {
        return selected;
    }

    public void setSelected(List<Long> selected) {
        this.selected = selected;
    }

    public Map<String, Integer> getRelTypeCounts() {
        return relTypeCounts;
    }

    public void setRelTypeCounts(Map<String, Integer> relTypeCounts) {
        this.relTypeCounts = relTypeCounts;
    }

}
