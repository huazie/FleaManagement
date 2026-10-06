/**
 * @Description auth-resource.js 资源管理模块脚本
 *              覆盖：资源新增（分步向导）、资源变更（表格 + 编辑面板）。
 *              页面逻辑由 auth-common.js 的各形态引擎统一承载，此处只做接口注册与模块配置。
 *
 * @author huazie
 * @version v1.1.0
 * @date 2026年9月29日
 */
define(function (require, exports, module) {

    // 授权管理公共模块
    var AuthCommon = require('../../auth-common');

    /* ==================== 接口注册 ==================== */

    // 资源列表
    ReqUrlMap.put("authResourceList", "authResource!list.flea");

    // 资源明细列表（表格）
    ReqUrlMap.put("authResourcePage", "authResource!page.flea");

    // 资源新增
    ReqUrlMap.put("authResourceAdd", "authResource!add.flea");

    // 资源变更
    ReqUrlMap.put("authResourceUpdate", "authResource!update.flea");

    // 资源明细查询（变更页回填用）
    ReqUrlMap.put("authResourceQuery", "authResource!query.flea");

    /* ==================== 分步向导类页面 ==================== */

    /**
     * 向导类页面配置（分步录入 + 提交前摘要）
     */
    var WIZARD_CONF = {
        "resourceAdd": {
            wizardId: "resource_add_wizard",
            stepContainerId: "resource_add_steps",
            formId: "auth_wizard_form",
            summaryId: "resource_add_summary",
            submitUrl: "authResourceAdd",
            required: [
                ["resourceCode", "资源编码"],
                ["resourceName", "资源名称"]
            ],
            summaryFields: [
                ["resourceCode", "资源编码"],
                ["resourceName", "资源名称"],
                ["resourceDesc", "资源描述"],
                ["remarks", "备注"]
            ]
        }
    };

    /* ==================== 表格类页面 ==================== */

    /**
     * 表格类页面配置（jqGrid 明细列表 + 编辑面板）
     */
    var GRID_CONF = {
        "resourceModify": {
            gridId: "resource_grid",
            pagerId: "resource_grid_pager",
            dataUrl: "authResourcePage",
            height: 300,
            rowKey: "resourceId",
            queryUrl: "authResourceQuery",
            queryKey: "resourceId",
            formId: "resource_change",
            tipId: "resource_change_tip",
            submitUrl: "authResourceUpdate",
            submitId: "submit",
            resetId: "reset",
            required: [
                ["resourceCode", "资源编码"],
                ["resourceName", "资源名称"]
            ],
            summaryIds: {total: "resource_total", enabled: "resource_enabled", disabled: "resource_disabled"},
            // mobile:false 的列属次要信息，窄屏隐藏
            columns: [
                // 编号为主键，筛选无实际意义，不生成筛选控件
                {name: "resourceId", label: "编号", width: 70, align: "center", search: false, mobile: false},
                {name: "resourceCode", label: "资源编码", width: 140},
                {name: "resourceName", label: "资源名称", width: 140},
                {name: "resourceDesc", label: "资源描述", width: 220, mobile: false},
                {
                    name: "resourceState", label: "状态", width: 80, align: "center", formatter: "state",
                    stype: "select", options: "1:正常;2:禁用;3:待审核"
                },
                {name: "op", label: "操作", width: 80, align: "center", formatter: "action", mobile: false}
            ]
        }
    };

    /**
     * 页面初始化
     *
     * @param moduleType 模块类型
     */
    exports.init = function (moduleType) {
        AuthCommon.initModule(moduleType, {
            wizardConf: WIZARD_CONF,
            gridConf: GRID_CONF
        });
    };

});
