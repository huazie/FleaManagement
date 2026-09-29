/**
 * @Description auth-operation.js 操作管理模块脚本（操作新增 / 操作变更）
 *
 * @author huazie
 * @version v1.0.0
 * @date 2026年9月25日
 */
define(function (require, exports, module) {

    // 授权管理公共模块
    var AuthCommon = require('../../auth-common');

    // 操作列表
    ReqUrlMap.put("authOperationList", "authOperation!list.flea");

    // 操作新增
    ReqUrlMap.put("authOperationAdd", "authOperation!add.flea");

    // 操作变更
    ReqUrlMap.put("authOperationUpdate", "authOperation!update.flea");

    // 操作明细查询（变更页回填用）
    ReqUrlMap.put("authOperationQuery", "authOperation!query.flea");

    /**
     * 页面初始化
     *
     * @param moduleType 模块类型（add-操作新增 change-操作变更）
     */
    exports.init = function (moduleType) {

        // 加载操作列表
        OperationModule.loadOperationList(moduleType);

    };

    /**
     * 操作管理模块
     */
    var OperationModule = {

        /**
         * 表单容器编号
         *
         * @param moduleType 模块类型
         */
        formId: function (moduleType) {
            return moduleType === "add" ? "operation_add" : "operation_change";
        },

        /**
         * 加载操作列表
         *
         * @param moduleType 模块类型
         */
        loadOperationList: function (moduleType) {

            AuthCommon.loadTree({
                treeId: "tree_" + moduleType,
                url: ReqUrlMap.get("authOperationList"),
                buildMenu: function (node) {

                    // 操作无层级，新增页左侧列表仅作参照
                    if (moduleType === "add") {
                        return undefined;
                    }

                    // 操作变更：任意操作均可发起变更
                    return [{
                        "HAS_DIVIDER": false,
                        "FUNCTION_ICON": "refresh",
                        "FUNCTION_NAME": "操作变更",
                        "FUNCTION_EVENT": "change",
                        "MENU_ID": node.id,
                        "MENU_CODE": node.code,
                        "MENU_NAME": node.name,
                        "MENU_LEVEL": node.level
                    }];
                },
                onMenuEvent: function (eventName, node) {
                    var func = OperationModule.OperationManagementFuncModule()[eventName];
                    if (typeof func === "function") {
                        func(node, moduleType);
                    }
                },
                onLoaded: function () {
                    // 绑定提交事件
                    BindEvent.bindSubmitEvent(moduleType);
                    // 绑定重置事件
                    BindEvent.bindResetEvent(moduleType);
                }
            });

        },

        /**
         * 操作管理功能模块
         */
        OperationManagementFuncModule: function () {
            return {
                /**
                 * 操作变更：加载指定操作信息并回填变更表单
                 */
                change: function (node, moduleType) {

                    Huazie.ajax.getJson(ReqUrlMap.get("authOperationQuery"), {operationId: node.id}, function (data, status) {
                        var result = data;
                        if (!status || result.retCode !== "Y") {
                            Huazie.dialog.tips("warning", (result && result.retMess) || "亲，操作信息加载失败！", 2);
                            return;
                        }

                        var operation = result.data || {};
                        var formId = OperationModule.formId(moduleType);

                        AuthCommon.fillForm(formId, operation);
                        // 表单启用
                        AuthCommon.setFormDisabled(formId, false);

                        Huazie.dialog.tips("info", "亲，操作【" + operation.operationName + "】信息已加载，请修改后提交！", 2);
                    });

                },
                /**
                 * 重置
                 */
                reset: function (moduleType) {

                    var formId = OperationModule.formId(moduleType);
                    AuthCommon.resetForm(formId);

                    if (moduleType === "change") {
                        // 清空后重新禁用，等待下一次选择
                        AuthCommon.setFormDisabled(formId, true);
                    }

                },
                /**
                 * 操作新增受理提交
                 */
                addSubmit: function () {

                    var operation = Huazie.form.serialize($("#operation_add"));

                    // 校验操作编码
                    if (!AuthCommon.checkRequired(operation.operationCode, "操作编码")) {
                        return;
                    }

                    // 校验操作名称
                    if (!AuthCommon.checkRequired(operation.operationName, "操作名称")) {
                        return;
                    }

                    // 新增操作
                    AuthCommon.submitForm({
                        url: ReqUrlMap.get("authOperationAdd"),
                        data: operation,
                        onSuccess: function () {
                            OperationModule.OperationManagementFuncModule().reset("add");
                            setTimeout(function () {
                                // 重新加载操作列表
                                OperationModule.loadOperationList("add");
                            }, 1000);
                        }
                    });

                },
                /**
                 * 操作变更受理提交
                 */
                changeSubmit: function () {

                    var operation = Huazie.form.serialize($("#operation_change"));

                    // 校验是否已选择待变更的操作
                    if (!operation.operationId) {
                        Huazie.dialog.tips("warning", [{"MESSAGE": "亲，请先从左侧操作列表中选择要变更的操作哟！"}, {"MESSAGE": "提示：【右击或长按列表项】"}], 2);
                        return;
                    }

                    // 校验操作编码
                    if (!AuthCommon.checkRequired(operation.operationCode, "操作编码")) {
                        return;
                    }

                    // 校验操作名称
                    if (!AuthCommon.checkRequired(operation.operationName, "操作名称")) {
                        return;
                    }

                    // 变更操作
                    AuthCommon.submitForm({
                        url: ReqUrlMap.get("authOperationUpdate"),
                        data: operation,
                        onSuccess: function () {
                            OperationModule.OperationManagementFuncModule().reset("change");
                            setTimeout(function () {
                                OperationModule.loadOperationList("change");
                            }, 1000);
                        }
                    });

                }
            }
        }
    };

    var BindEvent = {
        /**
         * 绑定提交事件
         */
        bindSubmitEvent: function (moduleType) {
            $("#submit").off("click").on("click", function () {
                if (moduleType === "add") { // 操作新增
                    OperationModule.OperationManagementFuncModule().addSubmit();
                } else { // 操作变更
                    OperationModule.OperationManagementFuncModule().changeSubmit();
                }
            });
        },
        /**
         * 绑定重置事件
         */
        bindResetEvent: function (moduleType) {
            $("#reset").off("click").on("click", function () {
                OperationModule.OperationManagementFuncModule().reset(moduleType);
            });
        }

    }

});
