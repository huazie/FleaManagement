package com.huazie.fleamgmt.module.auth.util;

import com.huazie.fleaframework.common.CommonConstants;
import com.huazie.fleaframework.common.util.CollectionUtils;
import com.huazie.fleaframework.common.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 授权功能（操作、元素、资源）前端列表工具 </p>
 *
 * <p> 这三类功能数据没有层级关系，统一渲染成 Fuelux 树的叶子节点（{@code type = item}），
 * 节点字段（name/type/id/code/level）是前端 fuelux.tree.js 的数据契约，
 * 集中在此处拼装，避免各 Controller 各写一份。 </p>
 *
 * <p> 说明：菜单存在层级，仍由框架的 {@code FueluxMenuTree} 负责构建。 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public final class AuthFunctionTreeUtil {

    private AuthFunctionTreeUtil() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * <p> 功能节点信息提取接口 </p>
     *
     * <p> 把操作、元素、资源三类功能数据，统一转换成左侧列表所需的最小信息。 </p>
     *
     * @param <T> 功能数据类型
     */
    public interface FunctionNodeExtractor<T> {

        /**
         * 功能编号
         */
        Long getId(T function);

        /**
         * 功能编码
         */
        String getCode(T function);

        /**
         * 功能名称
         */
        String getName(T function);
    }

    /**
     * <p> 把功能数据集转换为前端 Fuelux 树的扁平节点列表 </p>
     *
     * <p> 节点展示为：{@code 功能名称【功能编码】}，便于在列表中区分同名的功能数据。 </p>
     *
     * @param functionList 功能数据集
     * @param extractor    功能节点信息提取器
     * @return Fuelux 树的扁平节点列表
     * @since 1.0.0
     */
    public static <T> List<Map<String, Object>> toFlatTreeList(List<T> functionList, FunctionNodeExtractor<T> extractor) {
        return toFlatTreeList(functionList, extractor, true);
    }

    /**
     * <p> 把数据集转换为前端 Fuelux 树的扁平节点列表 </p>
     *
     * <p> appendCode = true 时节点展示为 {@code 名称【编码】}，适用于既有编码又有名称的功能数据
     * （操作、元素、资源、菜单）；角色、权限、用户、用户组等只有名称、没有业务编码的数据，
     * 传 false 时节点仅展示名称，编码位以编号占位（Fuelux 树的数据契约要求 code 非空）。 </p>
     *
     * @param functionList 数据集
     * @param extractor    节点信息提取器
     * @param appendCode   是否在节点名称后追加编码
     * @param <T>          数据类型
     * @return Fuelux 树的扁平节点列表
     * @since 1.0.0
     */
    public static <T> List<Map<String, Object>> toFlatTreeList(List<T> functionList, FunctionNodeExtractor<T> extractor, boolean appendCode) {

        List<Map<String, Object>> treeList = new ArrayList<>();

        if (CollectionUtils.isEmpty(functionList)) {
            return treeList;
        }

        for (T function : functionList) {
            if (ObjectUtils.isEmpty(function)) {
                continue;
            }

            Long id = extractor.getId(function);
            String code = extractor.getCode(function);
            String name = extractor.getName(function);

            Map<String, Object> nodeMap = new HashMap<>();
            nodeMap.put("name", (appendCode && code != null) ? (name + "【" + code + "】") : name);
            nodeMap.put("type", "item");
            nodeMap.put("id", id);
            // Fuelux 树要求每个节点都有 code，无业务编码的数据以编号占位，仅用于前端标识
            nodeMap.put("code", (code != null) ? code : String.valueOf(id));
            nodeMap.put("level", CommonConstants.NumeralConstants.INT_ONE);

            treeList.add(nodeMap);
        }

        return treeList;
    }

    /**
     * <p> 从功能数据集中按功能编号查询功能数据 </p>
     *
     * <p> 说明：框架的 {@code queryValidXxx} 接口不支持按功能编号过滤，
     * 故先查有效数据集，再按编号匹配。 </p>
     *
     * @param functionList 功能数据集
     * @param functionId   功能编号
     * @param extractor    功能节点信息提取器
     * @return 匹配到的功能数据，未匹配到返回 {@code null}
     * @since 1.0.0
     */
    public static <T> T findById(List<T> functionList, Long functionId, FunctionNodeExtractor<T> extractor) {

        if (ObjectUtils.isEmpty(functionId) || CollectionUtils.isEmpty(functionList)) {
            return null;
        }

        for (T function : functionList) {
            if (ObjectUtils.isNotEmpty(function) && functionId.equals(extractor.getId(function))) {
                return function;
            }
        }

        return null;
    }

}
