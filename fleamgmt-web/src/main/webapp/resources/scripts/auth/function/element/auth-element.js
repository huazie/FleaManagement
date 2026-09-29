/**
 * @Description auth-element.js 元素管理模块脚本（元素新增 / 元素变更）
 *
 * @author huazie
 * @version v1.0.0
 * @date 2026年9月25日
 */
define(function (require, exports, module) {

    // 授权管理公共模块
    var AuthCommon = require('../../auth-common');

    // 元素列表
    ReqUrlMap.put("authElementList", "authElement!list.flea");

    // 元素新增
    ReqUrlMap.put("authElementAdd", "authElement!add.flea");

    // 元素变更
    ReqUrlMap.put("authElementUpdate", "authElement!update.flea");

    // 元素明细查询（变更页回填用）
    ReqUrlMap.put("authElementQuery", "authElement!query.flea");

    /**
     * 页面初始化
     *
     * @param moduleType 模块类型（add-元素新增 change-元素变更）
     */
    exports.init = function (moduleType) {

        // 加载元素列表
        ElementModule.loadElementList(moduleType);

    };

    /**
     * 元素管理模块
     */
    var ElementModule = {

        /**
         * 表单容器编号
         *
         * @param moduleType 模块类型
         */
        formId: function (moduleType) {
            return moduleType === "add" ? "element_add" : "element_change";
        },

        /**
         * 加载元素列表
         *
         * @param moduleType 模块类型
         */
        loadElementList: function (moduleType) {

            AuthCommon.loadTree({
                treeId: "tree_" + moduleType,
                url: ReqUrlMap.get("authElementList"),
                buildMenu: function (node) {

                    // 元素无层级，新增页左侧列表仅作参照
                    if (moduleType === "add") {
                        return undefined;
                    }

                    // 元素变更：任意元素均可发起变更
                    return [{
                        "HAS_DIVIDER": false,
                        "FUNCTION_ICON": "refresh",
                        "FUNCTION_NAME": "元素变更",
                        "FUNCTION_EVENT": "change",
                        "MENU_ID": node.id,
                        "MENU_CODE": node.code,
                        "MENU_NAME": node.name,
                        "MENU_LEVEL": node.level
                    }];
                },
                onMenuEvent: function (eventName, node) {
                    var func = ElementModule.ElementManagementFuncModule()[eventName];
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
         * 元素管理功能模块
         */
        ElementManagementFuncModule: function () {
            return {
                /**
                 * 元素变更：加载指定元素信息并回填变更表单
                 */
                change: function (node, moduleType) {

                    Huazie.ajax.getJson(ReqUrlMap.get("authElementQuery"), {elementId: node.id}, function (data, status) {
                        var result = data;
                        if (!status || result.retCode !== "Y") {
                            Huazie.dialog.tips("warning", (result && result.retMess) || "亲，元素信息加载失败！", 2);
                            return;
                        }

                        var element = result.data || {};
                        var formId = ElementModule.formId(moduleType);

                        AuthCommon.fillForm(formId, element);
                        // 表单启用
                        AuthCommon.setFormDisabled(formId, false);

                        Huazie.dialog.tips("info", "亲，元素【" + element.elementName + "】信息已加载，请修改后提交！", 2);
                    });

                },
                /**
                 * 重置
                 */
                reset: function (moduleType) {

                    var formId = ElementModule.formId(moduleType);
                    AuthCommon.resetForm(formId);

                    if (moduleType === "change") {
                        // 清空后重新禁用，等待下一次选择
                        AuthCommon.setFormDisabled(formId, true);
                    }

                },
                /**
                 * 元素新增受理提交
                 */
                addSubmit: function () {

                    var element = Huazie.form.serialize($("#element_add"));

                    // 校验元素编码
                    if (!AuthCommon.checkRequired(element.elementCode, "元素编码")) {
                        return;
                    }

                    // 校验元素名称
                    if (!AuthCommon.checkRequired(element.elementName, "元素名称")) {
                        return;
                    }

                    // 校验元素类型
                    if (!AuthCommon.checkRequired(element.elementType, "元素类型")) {
                        return;
                    }

                    // 新增元素
                    AuthCommon.submitForm({
                        url: ReqUrlMap.get("authElementAdd"),
                        data: element,
                        onSuccess: function () {
                            ElementModule.ElementManagementFuncModule().reset("add");
                            setTimeout(function () {
                                // 重新加载元素列表
                                ElementModule.loadElementList("add");
                            }, 1000);
                        }
                    });

                },
                /**
                 * 元素变更受理提交
                 */
                changeSubmit: function () {

                    var element = Huazie.form.serialize($("#element_change"));

                    // 校验是否已选择待变更的元素
                    if (!element.elementId) {
                        Huazie.dialog.tips("warning", [{"MESSAGE": "亲，请先从左侧元素列表中选择要变更的元素哟！"}, {"MESSAGE": "提示：【右击或长按列表项】"}], 2);
                        return;
                    }

                    // 校验元素编码
                    if (!AuthCommon.checkRequired(element.elementCode, "元素编码")) {
                        return;
                    }

                    // 校验元素名称
                    if (!AuthCommon.checkRequired(element.elementName, "元素名称")) {
                        return;
                    }

                    // 变更元素
                    AuthCommon.submitForm({
                        url: ReqUrlMap.get("authElementUpdate"),
                        data: element,
                        onSuccess: function () {
                            ElementModule.ElementManagementFuncModule().reset("change");
                            setTimeout(function () {
                                ElementModule.loadElementList("change");
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
                if (moduleType === "add") { // 元素新增
                    ElementModule.ElementManagementFuncModule().addSubmit();
                } else { // 元素变更
                    ElementModule.ElementManagementFuncModule().changeSubmit();
                }
            });
        },
        /**
         * 绑定重置事件
         */
        bindResetEvent: function (moduleType) {
            $("#reset").off("click").on("click", function () {
                ElementModule.ElementManagementFuncModule().reset(moduleType);
            });
        }

    }

});
