/**
 * @Description auth-menu.js
 *
 * @author huazie
 * @version v1.0.0
 * @date 2017年3月17日
 */
define(function (require, exports, module) {

    // 菜单树展示
    ReqUrlMap.put("authMenuTree", "authMenu!tree.flea");

    // 菜单新增
    ReqUrlMap.put("authMenuAdd", "authMenu!add.flea");

    // 菜单变更
    ReqUrlMap.put("authMenuUpdate", "authMenu!update.flea");

    // 菜单下线
    ReqUrlMap.put("authMenuRemove", "authMenu!remove.flea");

    exports.init = function (moduleType) {

        // 加载菜单树
        AuthModule.loadMenuTree(moduleType);

    };

    /**
     * 控制台模块
     */
    var AuthModule = {

        /**
         * 加载菜单添加
         */
        loadMenuTree: function (moduleType) {

            // 开始获取菜单树
            Huazie.ajax.getJson(ReqUrlMap.get("authMenuTree"), function (data, status) {
                var result = data;
                if (status) {
                    var treeDataSource = new DataSourceTree({data: result.menuList});
                    var tree = $("#tree_" + moduleType);
                    tree.ace_tree({
                        dataSource: treeDataSource,
                        loadingHTML: '<div class="tree-loading"><i class="fa fa-refresh blue"></i></div>',
                        'open-icon': 'fa-folder-open',
                        'close-icon': 'fa-folder',
                        'selectable': false,
                        'selected-icon': null,
                        'unselected-icon': null
                    }, function () {
                        tree.rightClick(function (obj) {
                            var $thiz = $(obj);
                            var menuId = $thiz.find("input[name=id]").val();
                            var menuCode = $thiz.find("input[name=code]").val();
                            var menuName = $thiz.find("input[name=name]").val();
                            var menuLevel = $thiz.find("input[name=level]").val();
                            var subMenuCount = $thiz.find("input[name=count]").val();
                            if (menuCode) {
                                // 获取folder还是item
                                var type = $thiz.find("input[name=type]").val();

                                var data;

                                if (moduleType === "add") {
                                    data = [{
                                        "HAS_DIVIDER": false,
                                        "FUNCTION_ICON": "plus-circle",
                                        "FUNCTION_NAME": "菜单新增",
                                        "FUNCTION_EVENT": "add",
                                        "MENU_ID" : menuId,
                                        "MENU_CODE": menuCode,
                                        "MENU_NAME": menuName,
                                        "MENU_LEVEL" : menuLevel,
                                        "MENU_SORT" : Huazie.data.convertToInt(subMenuCount, 0) + 1
                                    }];
                                } else if (moduleType === "change") {
                                    data = [{
                                        "HAS_DIVIDER": false,
                                        "FUNCTION_ICON": "refresh",
                                        "FUNCTION_NAME": "菜单变更",
                                        "FUNCTION_EVENT": "update",
                                        "MENU_ID" : menuId,
                                        "MENU_CODE": menuCode,
                                        "MENU_NAME": menuName,
                                        "MENU_LEVEL" : menuLevel
                                    },{
                                        "HAS_DIVIDER": true,
                                        "FUNCTION_ICON": "minus-circle",
                                        "FUNCTION_NAME": "菜单下线",
                                        "FUNCTION_EVENT": "remove",
                                        "MENU_ID" : menuId,
                                        "MENU_CODE": menuCode,
                                        "MENU_NAME": menuName,
                                        "MENU_LEVEL" : menuLevel
                                    }];
                                }
                                // 如果是子菜单
                                if (type === "item") {
                                    // 如果是菜单新增
                                    if (moduleType === "add") {
                                        // 数据置undefined，不弹出选择框
                                        data = undefined;
                                    }
                                }

                                if (data) {
                                    Huazie.dialog.rightClickBubble(data, obj, function (dialog) {
                                        BindEvent.bindMenuManagementEvent(dialog);
                                    });
                                }
                            }
                        });
                    });
                    // 绑定提交事件
                    BindEvent.bindSubmitEvent(moduleType);
                    // 绑定重置事件
                    BindEvent.bindResetEvent(moduleType);

                } else {
                    Huazie.dialog.tips("warning", result.retMess, 2);
                }
            });

        },
        /**
         * 右键导航栏功能模块
         */
        MenuManagementFuncModule: function () {
            return {
                /**
                 * 菜单添加
                 */
                add: function (menu, dialog) {
                    dialog.close();
                    var menuCode = menu["MENU_CODE"];
                    var menuName = menu["MENU_NAME"];
                    var parentInfo = menuName + "【" + menuCode + "】";
                    $("#parent").val(parentInfo);
                    $("#parent_id").val(menu["MENU_ID"]);
                    $("#menu_sort").val(menu["MENU_SORT"]);
                    var menuLevel = Huazie.data.convertToInt(menu["MENU_LEVEL"], 0) + 1;
                    $("#menu_level").find("option[value='" + menuLevel + "']").attr("selected", true).siblings().removeAttr("selected");
                    Huazie.dialog.tips("info", [{"MESSAGE" : "亲，您正在添加新菜单！"},{"MESSAGE": "新菜单的父菜单如下所示："},{"MESSAGE": parentInfo}], 3);
                },
                /**
                 * 菜单变更
                 */
                update: function (menu, dialog) {

                },
                /**
                 * 菜单下线
                 */
                remove: function (menu, dialog) {

                },
                /**
                 * 重置
                 */
                reset: function (moduleType) {
                    if (moduleType === "add") { // 菜单新增
                        $("#menu_code").val("");
                        $("#menu_name").val("");
                        $("#menu_icon").val("");
                        $("#menu_view").val("");
                        $("#menu_sort").val("");
                        $("#parent").val("");
                        $("#parent_id").val("");
                        $("#remarks").val("");
                        $("#menu_level").find("option[value='0']").attr("selected", true).siblings().removeAttr("selected");
                    }
                },
                /**
                 * 菜单新增受理提交
                 */
                addSubmit: function() {

                    var menu = Huazie.form.serialize($("#menu_add"));

                    // 校验父菜单信息是否加载
                    if (menu.parentId === "") {
                        Huazie.dialog.tips("warning", [{"MESSAGE" : "亲，请先从菜单树中新增菜单哟！"},{"MESSAGE": "提示：【右击或长按父菜单】"}], 2);
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
                    Huazie.ajax.postJson(ReqUrlMap.get("authMenuAdd"), menu, function (data, status) {
                        var result = data;
                        if (status) {
                            if (result.retCode === "Y") {
                                Huazie.dialog.tips("info", result.retMess, 1);
                                // 重置即清空上一次添加的菜单
                                AuthModule.MenuManagementFuncModule().reset("add");
                                setTimeout(function() {
                                    // 重新加载菜单树
                                    AuthModule.loadMenuTree("add");
                                }, 1000);
                            } else {
                                Huazie.dialog.tips("warning", result.retMess, 2);
                            }
                        } else {
                            Huazie.dialog.tips("warning", result.retMess, 2);
                        }
                    });
                }
            }
        }
    };

    var BindEvent = {
        /**
         * 绑定菜单管理点击事件
         */
        bindMenuManagementEvent: function (dialog) {
            $("a[id^='function_']").off("click").on("click", function () {
                var name = $(this).attr("name");
                var menuId = $(this).find("input[name=id]").val();
                var menuCode = $(this).find("input[name=code]").val();
                var menuName = $(this).find("input[name=name]").val();
                var menuLevel = $(this).find("input[name=level]").val();
                var menuSort = $(this).find("input[name=sort]").val();
                var menu = {
                    "MENU_ID" : menuId,
                    "MENU_CODE": menuCode,
                    "MENU_NAME": menuName,
                    "MENU_LEVEL" : menuLevel,
                    "MENU_SORT" : menuSort
                };
                var func = eval("AuthModule.MenuManagementFuncModule()." + name);
                func(menu, dialog); // 执行相应的功能
            });
        },
        /**
         * 绑定提交事件
         */
        bindSubmitEvent: function (moduleType) {
            $("#submit").off("click").on("click", function () {
                if (moduleType === "add") { // 菜单新增
                    AuthModule.MenuManagementFuncModule().addSubmit();
                } else if (moduleType === "change") { // 菜单变更

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