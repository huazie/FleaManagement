/**
 * @Description auth-operation.js 操作管理模块脚本
 *              覆盖：操作新增（分步向导）、操作变更（表格 + 编辑面板）。
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

    // 操作列表
    ReqUrlMap.put("authOperationList", "authOperation!list.flea");

    // 操作明细列表（表格）
    ReqUrlMap.put("authOperationPage", "authOperation!page.flea");

    // 操作新增
    ReqUrlMap.put("authOperationAdd", "authOperation!add.flea");

    // 操作变更
    ReqUrlMap.put("authOperationUpdate", "authOperation!update.flea");

    // 操作明细查询（变更页回填用）
    ReqUrlMap.put("authOperationQuery", "authOperation!query.flea");

    /* ==================== 分步向导类页面 ==================== */

    /**
     * 向导类页面配置（分步录入 + 提交前摘要）
     */
    var WIZARD_CONF = {
        "operationAdd": {
            wizardId: "operation_add_wizard",
            stepContainerId: "operation_add_steps",
            formId: "auth_wizard_form",
            summaryId: "operation_add_summary",
            submitUrl: "authOperationAdd",
            required: [
                ["operationCode", "操作编码"],
                ["operationName", "操作名称"]
            ],
            summaryFields: [
                ["operationCode", "操作编码"],
                ["operationName", "操作名称"],
                ["operationDesc", "操作描述"],
                ["remarks", "备注"]
            ]
        }
    };

    /* ==================== 表格类页面 ==================== */

    /**
     * 表格类页面配置（jqGrid 明细列表 + 编辑面板）
     */
    var GRID_CONF = {
        "operationModify": {
            gridId: "operation_grid",
            pagerId: "operation_grid_pager",
            dataUrl: "authOperationPage",
            height: 300,
            rowKey: "operationId",
            queryUrl: "authOperationQuery",
            queryKey: "operationId",
            formId: "operation_change",
            tipId: "operation_change_tip",
            submitUrl: "authOperationUpdate",
            submitId: "submit",
            resetId: "reset",
            required: [
                ["operationCode", "操作编码"],
                ["operationName", "操作名称"]
            ],
            summaryIds: {total: "operation_total", enabled: "operation_enabled", disabled: "operation_disabled"},
            // mobile:false 的列属次要信息，窄屏隐藏
            columns: [
                // 编号为主键，筛选无实际意义，不生成筛选控件
                {name: "operationId", label: "编号", width: 70, align: "center", search: false, mobile: false},
                {name: "operationCode", label: "操作编码", width: 140},
                {name: "operationName", label: "操作名称", width: 140},
                {name: "operationDesc", label: "操作描述", width: 220, mobile: false},
                {
                    name: "operationState", label: "状态", width: 80, align: "center", formatter: "state",
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
