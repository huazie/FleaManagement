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

                    $('#tree').ace_tree({
                        dataSource: treeDataSource,
                        loadingHTML: '<div class="tree-loading"><i class="fa fa-refresh blue"></i></div>',
                        'open-icon': 'fa-folder-open',
                        'close-icon': 'fa-folder',
                        'selectable': false,
                        'selected-icon': null,
                        'unselected-icon': null
                    }, function () {
                        $("#tree").rightClick(function (obj) {
                            var $thiz = $(obj);
                            var menuId = $thiz.find("input[name=id]").val();
                            var menuCode = $thiz.find("input[name=code]").val();
                            var menuName = $thiz.find("input[name=name]").val();
                            var menuLevel = $thiz.find("input[name=level]").val();
                            if (menuCode) {
                                Huazie.log(menuCode);
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
                                        "MENU_LEVEL" : menuLevel
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

                                if (type === "item") {
                                    if (moduleType === "add") {
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
                    var menuCode = menu["MENU_CODE"];
                    var menuName = menu["MENU_NAME"];
                    var parentInfo = menuName + "【" + menuCode + "】";
                    $("#parent").val(parentInfo);
                    $("#parent_id").val(menu["MENU_ID"]);
                    var menuLevel = Huazie.data.convertToInt(menu["MENU_LEVEL"], 0) + 1;
                    $("#menu_level").find("option[value='" + menuLevel + "']").attr("selected", true).siblings().removeAttr("selected");
                    Huazie.dialog.tips("info", "正在添加新菜单，其父菜单为" + parentInfo, 3);
                    dialog.close();
                },
                /**
                 * 菜单变更
                 */
                update: function (menu, dialog) {
                    Huazie.log("remove-->>" + menu);
                },
                /**
                 * 菜单下线
                 */
                remove: function (menu, dialog) {
                    Huazie.log("remove-->>" + menu);
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

                    Huazie.log(menu);

                    // 校验入参数据


                    //开始调新增菜单的操作
                    Huazie.ajax.postJson(ReqUrlMap.get("authMenuAdd"), menu, function (data, status) {
                        var result = data;
                        if (status) {
                            if (result.retCode === "Y") {
                                Huazie.dialog.tips("info", result.retMess, 1);
                                AuthModule.MenuManagementFuncModule().reset(); // 重置即清空上一次添加的菜单
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
                var menu = {
                    "MENU_ID" : menuId,
                    "MENU_CODE": menuCode,
                    "MENU_NAME": menuName,
                    "MENU_LEVEL" : menuLevel
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