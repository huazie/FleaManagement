/**
 * @Description auth-element.js 元素管理模块脚本
 *              覆盖：元素新增（分步向导）、元素变更（表格 + 编辑面板）。
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

    // 元素列表
    ReqUrlMap.put("authElementList", "authElement!list.flea");

    // 元素明细列表（表格）
    ReqUrlMap.put("authElementPage", "authElement!page.flea");

    // 元素新增
    ReqUrlMap.put("authElementAdd", "authElement!add.flea");

    // 元素变更
    ReqUrlMap.put("authElementUpdate", "authElement!update.flea");

    // 元素明细查询（变更页回填用）
    ReqUrlMap.put("authElementQuery", "authElement!query.flea");

    /* ==================== 分步向导类页面 ==================== */

    /**
     * 向导类页面配置（分步录入 + 提交前摘要）
     */
    var WIZARD_CONF = {
        "elementAdd": {
            wizardId: "element_add_wizard",
            stepContainerId: "element_add_steps",
            formId: "auth_wizard_form",
            summaryId: "element_add_summary",
            submitUrl: "authElementAdd",
            required: [
                ["elementCode", "元素编码"],
                ["elementName", "元素名称"]
            ],
            summaryFields: [
                ["elementCode", "元素编码"],
                ["elementName", "元素名称"],
                ["elementType", "元素类型"],
                ["elementContent", "元素内容"],
                ["elementDesc", "元素描述"],
                ["remarks", "备注"]
            ]
        }
    };

    /* ==================== 表格类页面 ==================== */

    /**
     * 表格类页面配置（jqGrid 明细列表 + 编辑面板）
     */
    var GRID_CONF = {
        "elementModify": {
            gridId: "element_grid",
            pagerId: "element_grid_pager",
            dataUrl: "authElementPage",
            height: 300,
            rowKey: "elementId",
            queryUrl: "authElementQuery",
            queryKey: "elementId",
            formId: "element_change",
            tipId: "element_change_tip",
            submitUrl: "authElementUpdate",
            submitId: "submit",
            resetId: "reset",
            required: [
                ["elementCode", "元素编码"],
                ["elementName", "元素名称"]
            ],
            summaryIds: {total: "element_total", enabled: "element_enabled", disabled: "element_disabled"},
            // mobile:false 的列属次要信息，窄屏隐藏
            columns: [
                // 编号为主键，筛选无实际意义，不生成筛选控件
                {name: "elementId", label: "编号", width: 70, align: "center", search: false, mobile: false},
                {name: "elementCode", label: "元素编码", width: 130},
                {name: "elementName", label: "元素名称", width: 130},
                {name: "elementType", label: "类型", width: 70, align: "center", mobile: false},
                {name: "elementDesc", label: "元素描述", width: 200, mobile: false},
                {
                    name: "elementState", label: "状态", width: 80, align: "center", formatter: "state",
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
