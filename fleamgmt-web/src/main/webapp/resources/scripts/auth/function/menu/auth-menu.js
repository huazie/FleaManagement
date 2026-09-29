/**
 * @Description auth-menu.js 菜单管理模块脚本（菜单新增 / 菜单变更）
 *
 * @author huazie
 * @version v1.0.0
 * @date 2017年3月17日
 */
define(function (require, exports, module) {

    // 授权管理公共模块
    var AuthCommon = require('../../auth-common');

    // 菜单树展示
    ReqUrlMap.put("authMenuTree", "authMenu!tree.flea");

    // 菜单新增
    ReqUrlMap.put("authMenuAdd", "authMenu!add.flea");

    // 菜单变更
    ReqUrlMap.put("authMenuUpdate", "authMenu!update.flea");

    // 菜单明细查询（变更页回填用）
    ReqUrlMap.put("authMenuQuery", "authMenu!query.flea");

    /**
     * 页面初始化
     *
     * @param moduleType 模块类型（add-菜单新增 change-菜单变更）
     */
    exports.init = function (moduleType) {

        // 加载菜单树
        AuthModule.loadMenuTree(moduleType);

    };

    /**
     * 菜单管理模块
     */
    var AuthModule = {

        /**
         * 表单容器编号
         *
         * @param moduleType 模块类型
         */
        formId: function (moduleType) {
            return moduleType === "add" ? "menu_add" : "menu_change";
        },

        /**
         * 加载菜单树
         *
         * @param moduleType 模块类型
         */
        loadMenuTree: function (moduleType) {

            AuthCommon.loadTree({
                treeId: "tree_" + moduleType,
                url: ReqUrlMap.get("authMenuTree"),
                dataKey: "menuList",
                buildMenu: function (node) {

                    // 菜单新增：只能从父菜单上新增子菜单，叶子菜单不再提供入口
                    if (moduleType === "add") {
                        if (node.type === "item") {
                            return undefined;
                        }
                        return [{
                            "HAS_DIVIDER": false,
                            "FUNCTION_ICON": "plus-circle",
                            "FUNCTION_NAME": "菜单新增",
                            "FUNCTION_EVENT": "add",
                            "MENU_ID": node.id,
                            "MENU_CODE": node.code,
                            "MENU_NAME": node.name,
                            "MENU_LEVEL": node.level,
                            "MENU_SORT": Huazie.data.convertToInt(node.count, 0) + 1
                        }];
                    }

                    // 菜单变更：任意菜单节点均可发起变更
                    return [{
                        "HAS_DIVIDER": false,
                        "FUNCTION_ICON": "refresh",
                        "FUNCTION_NAME": "菜单变更",
                        "FUNCTION_EVENT": "change",
                        "MENU_ID": node.id,
                        "MENU_CODE": node.code,
                        "MENU_NAME": node.name,
                        "MENU_LEVEL": node.level
                    }];
                },
                onMenuEvent: function (eventName, node) {
                    var func = AuthModule.MenuManagementFuncModule()[eventName];
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
         * 菜单管理功能模块
         */
        MenuManagementFuncModule: function () {
            return {
                /**
                 * 菜单添加
                 */
                add: function (node) {
                    var parentInfo = node.name + "【" + node.code + "】";
                    $("#parent").val(parentInfo);
                    $("#parent_id").val(node.id);
                    $("#menu_sort").val(Huazie.data.convertToInt(node.count, 0) + 1);
                    var menuLevel = Huazie.data.convertToInt(node.level, 0) + 1;
                    $("#menu_level").find("option[value='" + menuLevel + "']").attr("selected", true).siblings().removeAttr("selected");
                    Huazie.dialog.tips("info", [{"MESSAGE": "亲，您正在添加新菜单！"}, {"MESSAGE": "新菜单的父菜单如下所示："}, {"MESSAGE": parentInfo}], 3);
                },
                /**
                 * 菜单变更：加载指定菜单信息并回填变更表单
                 */
                change: function (node, moduleType) {

                    Huazie.ajax.getJson(ReqUrlMap.get("authMenuQuery"), {menuId: node.id}, function (data, status) {
                        var result = data;
                        if (!status || result.retCode !== "Y") {
                            Huazie.dialog.tips("warning", (result && result.retMess) || "亲，菜单信息加载失败！", 2);
                            return;
                        }

                        var menu = result.data || {};
                        var formId = AuthModule.formId(moduleType);

                        AuthCommon.fillForm(formId, menu);
                        // 父菜单信息只做展示，不允许在变更页调整归属
                        $("#parent").val(menu.parentName ? (menu.parentName + "【" + menu.parentCode + "】") : "");

                        // 表单启用
                        AuthCommon.setFormDisabled(formId, false);

                        Huazie.dialog.tips("info", "亲，菜单【" + menu.menuName + "】信息已加载，请修改后提交！", 2);
                    });

                },
                /**
                 * 重置
                 */
                reset: function (moduleType) {

                    var formId = AuthModule.formId(moduleType);
                    AuthCommon.resetForm(formId);

                    if (moduleType === "add") { // 菜单新增
                        $("#parent").val("");
                        $("#parent_id").val("");
                    } else { // 菜单变更
                        // 清空后重新禁用，等待下一次选择
                        AuthCommon.setFormDisabled(formId, true);
                    }

                },
                /**
                 * 菜单新增受理提交
                 */
                addSubmit: function () {

                    var menu = Huazie.form.serialize($("#menu_add"));

                    // 校验父菜单信息是否加载
                    if (menu.parentId === "") {
                        Huazie.dialog.tips("warning", [{"MESSAGE": "亲，请先从菜单树中新增菜单哟！"}, {"MESSAGE": "提示：【右击或长按父菜单】"}], 2);
                        return;
                    }

                    // 校验菜单编码
                    if (menu.menuCode === "") {
                        Huazie.dialog.tips("warning", "亲，请填写菜单编码哟！", 1.5);
                        return;
                    }

                    // 校验菜单名称
                    if (menu.menuName === "") {
                        Huazie.dialog.tips("warning", "亲，请填写菜单名称哟！", 1.5);
                        return;
                    }

                    // 校验菜单图标
                    if (menu.menuIcon === "") {
                        Huazie.dialog.tips("warning", "亲，请填写菜单图标哟！", 1.5);
                        return;
                    }

                    // 新增菜单
                    AuthCommon.submitForm({
                        url: ReqUrlMap.get("authMenuAdd"),
                        data: menu,
                        onSuccess: function () {
                            // 重置即清空上一次添加的菜单
                            AuthModule.MenuManagementFuncModule().reset("add");
                            setTimeout(function () {
                                // 重新加载菜单树
                                AuthModule.loadMenuTree("add");
                            }, 1000);
                        }
                    });

                },
                /**
                 * 菜单变更受理提交
                 */
                changeSubmit: function () {

                    var menu = Huazie.form.serialize($("#menu_change"));

                    // 校验是否已选择待变更的菜单
                    if (!menu.menuId) {
                        Huazie.dialog.tips("warning", [{"MESSAGE": "亲，请先从菜单树中选择要变更的菜单哟！"}, {"MESSAGE": "提示：【右击或长按菜单】"}], 2);
                        return;
                    }

                    // 校验菜单编码
                    if (!AuthCommon.checkRequired(menu.menuCode, "菜单编码")) {
                        return;
                    }

                    // 校验菜单名称
                    if (!AuthCommon.checkRequired(menu.menuName, "菜单名称")) {
                        return;
                    }

                    // 校验菜单图标
                    if (!AuthCommon.checkRequired(menu.menuIcon, "菜单图标")) {
                        return;
                    }

                    // 变更菜单
                    AuthCommon.submitForm({
                        url: ReqUrlMap.get("authMenuUpdate"),
                        data: menu,
                        onSuccess: function () {
                            AuthModule.MenuManagementFuncModule().reset("change");
                            setTimeout(function () {
                                AuthModule.loadMenuTree("change");
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
                if (moduleType === "add") { // 菜单新增
                    AuthModule.MenuManagementFuncModule().addSubmit();
                } else { // 菜单变更
                    AuthModule.MenuManagementFuncModule().changeSubmit();
                }
            });
        },
        /**
         * 绑定重置事件
         */
        bindResetEvent: function (moduleType) {
            $("#reset").off("click").on("click", function () {
                AuthModule.MenuManagementFuncModule().reset(moduleType);
            });
        }

    }

});
