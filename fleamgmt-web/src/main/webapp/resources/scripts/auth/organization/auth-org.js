/**
 * @Description auth-org.js 组织模块管理脚本
 *              覆盖：组织新增（分步向导）、组织变更（表格 + 编辑面板）、用户关联组织（主体表格 + 标签页 + 穿梭）。
 *              组织是用户的归属维度，不做权限授权主体，唯一的关联维度为 ORG_REL_USER。
 *              页面逻辑由 auth-common.js 的各形态引擎统一承载，此处只做接口注册与模块配置。
 *
 * @author huazie
 * @version v1.0.0
 * @date 2026年10月11日
 */
define(function (require, exports, module) {

    // 授权管理公共模块
    var AuthCommon = require('../auth-common');

    /* ==================== 接口注册 ==================== */

    // 组织候选列表（父组织下拉 / 关联主体表）
    ReqUrlMap.put("authOrgList", "authOrg!list.flea");

    // 组织明细列表（表格）
    ReqUrlMap.put("authOrgPage", "authOrg!page.flea");

    // 组织明细查询（变更页回填用）
    ReqUrlMap.put("authOrgQuery", "authOrg!query.flea");

    // 组织新增
    ReqUrlMap.put("authOrgAdd", "authOrg!add.flea");

    // 组织变更
    ReqUrlMap.put("authOrgUpdate", "authOrg!update.flea");

    // 用户关联组织明细
    ReqUrlMap.put("authOrgUserRelAuth", "authOrgUserRel!auth.flea");

    // 用户关联组织提交
    ReqUrlMap.put("authOrgUserRelAuthorize", "authOrgUserRel!authorize.flea");

    // 用户取消关联组织
    ReqUrlMap.put("authOrgUserRelCancel", "authOrgUserRel!cancel.flea");

    /* ==================== 分步向导类页面 ==================== */

    /**
     * 加载父组织下拉候选（保留首项「根组织（顶级）」，值为 -1 表示挂到根下）
     */
    function loadParentOrgOptions() {

        Huazie.ajax.getJson(ReqUrlMap.get("authOrgList"), {}, function (data, status) {

            var result = data || {};
            if (!status || (result.retCode && result.retCode !== "Y")) {
                return;
            }

            var list = result.treeList || result.rows || [];
            if (list.length === 0) {
                return;
            }

            var $select = $("#parentId");
            var current = $select.val();

            $select.empty();
            $select.append($("<option/>").val("-1").text("根组织（顶级）"));

            for (var i = 0; i < list.length; i++) {
                // 候选名形如「名称【编码】」，仅去标签，转义交给 text() 完成
                var text = String(list[i].name || "").replace(/<[^>]*>/g, "");
                $select.append($("<option/>").val(list[i].id).text(text));
            }

            if (current) {
                $select.val(current);
            }
        });
    }

    /**
     * 加载变更页父组织下拉候选（按树形先序排序并缩进，父子组织相邻展示，首项「根组织（顶级）」值为 -1）
     *
     * @param excludeOrgId 需排除的组织编号：自身及其子树不可作为父组织，防止成环
     */
    function loadModifyParentOptions(excludeOrgId) {

        Huazie.ajax.getJson(ReqUrlMap.get("authOrgPage"), {}, function (data, status) {

            var result = data || {};
            if (!status || (result.retCode && result.retCode !== "Y")) {
                return;
            }

            var rows = result.rows || [];
            if (rows.length === 0) {
                return;
            }

            // 待排除集：自身 + 沿 parent 链找到的自身子树
            var excluded = {};
            if (excludeOrgId) {
                excluded[excludeOrgId] = true;
                var changed = true;
                while (changed) {
                    changed = false;
                    for (var i = 0; i < rows.length; i++) {
                        var row = rows[i];
                        if (!excluded[row.orgId] && excluded[row.parentId]) {
                            excluded[row.orgId] = true;
                            changed = true;
                        }
                    }
                }
            }

            // 构建父子映射，树形先序（DFS）展开：父组织紧跟其子组织，同级按编号升序
            var rowMap = {};
            for (var i = 0; i < rows.length; i++) {
                rowMap[rows[i].orgId] = rows[i];
            }
            var childrenMap = {};
            var rootRows = [];
            for (var i = 0; i < rows.length; i++) {
                var parent = rowMap[rows[i].parentId];
                if (parent) {
                    (childrenMap[parent.orgId] = childrenMap[parent.orgId] || []).push(rows[i]);
                } else {
                    rootRows.push(rows[i]);
                }
            }
            var byOrgId = function (a, b) {
                return a.orgId - b.orgId;
            };
            var ordered = [];
            (function walk(list) {
                list.sort(byOrgId);
                for (var c = 0; c < list.length; c++) {
                    ordered.push(list[c]);
                    var kids = childrenMap[list[c].orgId];
                    if (kids) {
                        walk(kids);
                    }
                }
            })(rootRows);
            rows = ordered;

            var $select = $("#parentId");
            var current = $select.val();

            $select.empty();
            $select.append($("<option/>").val("-1").text("根组织（顶级）"));

            for (var k = 0; k < rows.length; k++) {
                if (excluded[rows[k].orgId]) {
                    continue;
                }
                // 层级缩进：第 N 层缩进 N-1 个全角空格，非顶级组织前加树形符号
                var indent = "";
                for (var j = 1; j < rows[k].orgLevel; j++) {
                    indent += "\u3000";
                }
                var prefix = rows[k].orgLevel > 1 ? indent + "\u2514 " : "";
                var text = prefix + rows[k].orgName + "\u3010" + rows[k].orgCode + "\u3011";
                $select.append($("<option/>").val(rows[k].orgId).text(text));
            }

            if (current && !$select.find("option[value='" + current + "']").length) {
                // 当前值被排除（如刷新前的脏值），回落到首项
                current = null;
            }
            if (current) {
                $select.val(current);
            }
        });
    }

    /**
     * 向导类页面配置（分步录入 + 提交前摘要）
     */
    var WIZARD_CONF = {
        "orgAdd": {
            wizardId: "org_add_wizard",
            stepContainerId: "org_add_steps",
            formId: "auth_wizard_form",
            summaryId: "org_add_summary",
            submitUrl: "authOrgAdd",
            required: [
                ["orgCode", "组织编码"],
                ["orgName", "组织名称"]
            ],
            summaryFields: [
                ["orgCode", "组织编码"],
                ["orgName", "组织名称"],
                ["orgType", "组织类型"],
                ["parentId", "父组织"],
                ["orgDesc", "组织描述"],
                ["remarks", "备注"]
            ],
            onReady: function () {
                loadParentOrgOptions();
            }
        }
    };

    /* ==================== 表格类页面 ==================== */

    /**
     * 表格类页面配置（jqGrid 明细列表 + 编辑面板）
     */
    var GRID_CONF = {
        "orgModify": {
            gridId: "org_grid",
            pagerId: "org_grid_pager",
            dataUrl: "authOrgPage",
            height: 300,
            rowKey: "orgId",
            queryUrl: "authOrgQuery",
            queryKey: "orgId",
            formId: "org_change",
            tipId: "org_change_tip",
            submitUrl: "authOrgUpdate",
            submitId: "submit",
            resetId: "reset",
            required: [["orgName", "组织名称"]],
            // 保持后端树形先序（父子相邻），禁用引擎默认的第一列排序
            sortname: "",
            summaryIds: {total: "org_total", enabled: "org_enabled", disabled: "org_disabled"},
            // mobile:false 的列属次要信息，窄屏隐藏
            columns: [
                // 编号为主键，筛选无实际意义，不生成筛选控件
                {name: "orgId", label: "编号", width: 70, align: "center", search: false, mobile: false},
                {name: "orgCode", label: "组织编码", width: 140},
                // orgNameTree 带树形缩进（与用户关联组织页展示一致）；表单回填仍取纯名称 orgName
                {name: "orgNameTree", label: "组织名称", width: 140},
                {name: "orgTypeName", label: "类型", width: 70, align: "center", mobile: false},
                {name: "parentName", label: "父组织", width: 120, mobile: false},
                {name: "orgLevel", label: "层级", width: 60, align: "center", sorttype: "int", mobile: false},
                {
                    name: "orgState", label: "状态", width: 80, align: "center", formatter: "state",
                    stype: "select", options: "1:正常;2:禁用;3:待审核"
                },
                {name: "op", label: "操作", width: 80, align: "center", formatter: "action", mobile: false}
            ]
        }
    };

    /* ==================== 授权类页面 ==================== */

    /**
     * 授权类页面配置（主体表格 + 标签页分维度 + 左右穿梭）
     */
    var AUTH_CONF = {
        "orgUserRel": {
            ownerGridId: "auth_owner_grid",
            ownerPagerId: "auth_owner_pager",
            ownerListUrl: "authOrgList",
            ownerKey: "orgId",
            ownerLabel: "组织",
            ownerHeight: 170,
            authPanelId: "auth_panel",
            ownerLabelId: "auth_owner_label",
            tabsId: "auth_tabs",
            authUrl: "authOrgUserRelAuth",
            submitUrl: "authOrgUserRelAuthorize",
            cancelUrl: "authOrgUserRelCancel",
            relTypes: [
                ["ORG_REL_USER", "用户"]
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
            gridConf: GRID_CONF,
            authConf: AUTH_CONF
        });

        // 变更页：父组织下拉候选按树形先序排序缩进（父子相邻），支持变更父组织
        if (moduleType === "orgModify") {

            // 页面就绪先填充全量候选；表格行选中后重建，排除自身及子树
            loadModifyParentOptions();
            $("#org_grid").on("jqGridSelectRow", function (e, rowId) {
                loadModifyParentOptions(rowId);
            });
        }
    };

});
