/**
 * @Description auth-resource.js 资源管理模块脚本（资源新增 / 资源变更）
 *
 * @author huazie
 * @version v1.0.0
 * @date 2026年9月25日
 */
define(function (require, exports, module) {

    // 授权管理公共模块
    var AuthCommon = require('../../auth-common');

    // 资源列表
    ReqUrlMap.put("authResourceList", "authResource!list.flea");

    // 资源新增
    ReqUrlMap.put("authResourceAdd", "authResource!add.flea");

    // 资源变更
    ReqUrlMap.put("authResourceUpdate", "authResource!update.flea");

    // 资源明细查询（变更页回填用）
    ReqUrlMap.put("authResourceQuery", "authResource!query.flea");

    /**
     * 页面初始化
     *
     * @param moduleType 模块类型（add-资源新增 change-资源变更）
     */
    exports.init = function (moduleType) {

        // 加载资源列表
        ResourceModule.loadResourceList(moduleType);

    };

    /**
     * 资源管理模块
     */
    var ResourceModule = {

        /**
         * 表单容器编号
         *
         * @param moduleType 模块类型
         */
        formId: function (moduleType) {
            return moduleType === "add" ? "resource_add" : "resource_change";
        },

        /**
         * 加载资源列表
         *
         * @param moduleType 模块类型
         */
        loadResourceList: function (moduleType) {

            AuthCommon.loadTree({
                treeId: "tree_" + moduleType,
                url: ReqUrlMap.get("authResourceList"),
                buildMenu: function (node) {

                    // 资源无层级，新增页左侧列表仅作参照
                    if (moduleType === "add") {
                        return undefined;
                    }

                    // 资源变更：任意资源均可发起变更
                    return [{
                        "HAS_DIVIDER": false,
                        "FUNCTION_ICON": "refresh",
                        "FUNCTION_NAME": "资源变更",
                        "FUNCTION_EVENT": "change",
                        "MENU_ID": node.id,
                        "MENU_CODE": node.code,
                        "MENU_NAME": node.name,
                        "MENU_LEVEL": node.level
                    }];
                },
                onMenuEvent: function (eventName, node) {
                    var func = ResourceModule.ResourceManagementFuncModule()[eventName];
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
         * 资源管理功能模块
         */
        ResourceManagementFuncModule: function () {
            return {
                /**
                 * 资源变更：加载指定资源信息并回填变更表单
                 */
                change: function (node, moduleType) {

                    Huazie.ajax.getJson(ReqUrlMap.get("authResourceQuery"), {resourceId: node.id}, function (data, status) {
                        var result = data;
                        if (!status || result.retCode !== "Y") {
                            Huazie.dialog.tips("warning", (result && result.retMess) || "亲，资源信息加载失败！", 2);
                            return;
                        }

                        var resource = result.data || {};
                        var formId = ResourceModule.formId(moduleType);

                        AuthCommon.fillForm(formId, resource);
                        // 表单启用
                        AuthCommon.setFormDisabled(formId, false);

                        Huazie.dialog.tips("info", "亲，资源【" + resource.resourceName + "】信息已加载，请修改后提交！", 2);
                    });

                },
                /**
                 * 重置
                 */
                reset: function (moduleType) {

                    var formId = ResourceModule.formId(moduleType);
                    AuthCommon.resetForm(formId);

                    if (moduleType === "change") {
                        // 清空后重新禁用，等待下一次选择
                        AuthCommon.setFormDisabled(formId, true);
                    }

                },
                /**
                 * 资源新增受理提交
                 */
                addSubmit: function () {

                    var resource = Huazie.form.serialize($("#resource_add"));

                    // 校验资源编码
                    if (!AuthCommon.checkRequired(resource.resourceCode, "资源编码")) {
                        return;
                    }

                    // 校验资源名称
                    if (!AuthCommon.checkRequired(resource.resourceName, "资源名称")) {
                        return;
                    }

                    // 新增资源
                    AuthCommon.submitForm({
                        url: ReqUrlMap.get("authResourceAdd"),
                        data: resource,
                        onSuccess: function () {
                            ResourceModule.ResourceManagementFuncModule().reset("add");
                            setTimeout(function () {
                                // 重新加载资源列表
                                ResourceModule.loadResourceList("add");
                            }, 1000);
                        }
                    });

                },
                /**
                 * 资源变更受理提交
                 */
                changeSubmit: function () {

                    var resource = Huazie.form.serialize($("#resource_change"));

                    // 校验是否已选择待变更的资源
                    if (!resource.resourceId) {
                        Huazie.dialog.tips("warning", [{"MESSAGE": "亲，请先从左侧资源列表中选择要变更的资源哟！"}, {"MESSAGE": "提示：【右击或长按列表项】"}], 2);
                        return;
                    }

                    // 校验资源编码
                    if (!AuthCommon.checkRequired(resource.resourceCode, "资源编码")) {
                        return;
                    }

                    // 校验资源名称
                    if (!AuthCommon.checkRequired(resource.resourceName, "资源名称")) {
                        return;
                    }

                    // 变更资源
                    AuthCommon.submitForm({
                        url: ReqUrlMap.get("authResourceUpdate"),
                        data: resource,
                        onSuccess: function () {
                            ResourceModule.ResourceManagementFuncModule().reset("change");
                            setTimeout(function () {
                                ResourceModule.loadResourceList("change");
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
                if (moduleType === "add") { // 资源新增
                    ResourceModule.ResourceManagementFuncModule().addSubmit();
                } else { // 资源变更
                    ResourceModule.ResourceManagementFuncModule().changeSubmit();
                }
            });
        },
        /**
         * 绑定重置事件
         */
        bindResetEvent: function (moduleType) {
            $("#reset").off("click").on("click", function () {
                ResourceModule.ResourceManagementFuncModule().reset(moduleType);
            });
        }

    }

});
