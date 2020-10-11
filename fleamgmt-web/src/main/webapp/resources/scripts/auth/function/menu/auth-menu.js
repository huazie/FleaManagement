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

        // 加载菜单添加
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
                            var menuCode = $thiz.find("input[name=code]").val();
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
                                        "MENU_CODE": menuCode
                                    }];
                                } else if (moduleType === "change") {
                                    data = [{
                                        "HAS_DIVIDER": false,
                                        "FUNCTION_ICON": "refresh",
                                        "FUNCTION_NAME": "菜单变更",
                                        "FUNCTION_EVENT": "update",
                                        "MENU_CODE": menuCode
                                    },{
                                        "HAS_DIVIDER": false,
                                        "FUNCTION_ICON": "minus-circle",
                                        "FUNCTION_NAME": "菜单下线",
                                        "FUNCTION_EVENT": "remove",
                                        "MENU_CODE": menuCode
                                    }];
                                }

                                if (type === "item") {
                                    if (moduleType === "add") {
                                        data = undefined;
                                    }
                                }

                                if (data) {
                                    Huazie.dialog.rightClickBubble(data, obj, function () {
                                        BindEvent.bindMenuManagementEvent();
                                    });
                                }
                            }
                        });
                    });

                    BindEvent.bindSubmitEvent(moduleType); // 绑定提交事件
                    BindEvent.bindResetEvent(moduleType); // 绑定重置事件

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
                add: function (menuCode) {
                    $("#parent_id").val(menuCode);
                    Huazie.dialog.tips("info", "已经设置新增菜单的父菜单编码为" + menuCode, 3);
                },
                /**
                 * 菜单变更
                 */
                update: function (menuCode) {
                    console.log("remove-->>" + menuCode);
                },
                /**
                 * 菜单下线
                 */
                remove: function (menuCode) {
                    console.log("remove-->>" + menuCode);
                },
                /**
                 * 重置
                 */
                reset: function () {
                    $("#menu_code").val("");
                    $("#menu_name").val("");
                    $("#menu_icon").val("");
                    $("#menu_view").val("");
                    $("#parent_id").val("");
                    $("#description").val("");
                }
            }
        }
    }

    var BindEvent = {
        /**
         * 绑定收藏夹点击事件
         */
        bindMenuManagementEvent: function () {
            $("a[id^='function_']").off("click");
            $("a[id^='function_']").on("click", function () {
                var name = $(this).attr("name");
                var menuCode = $(this).find("input[name=code]").val();
                var func = eval("AuthModule.MenuManagementFuncModule()." + name);
                func(menuCode); // 执行相应的功能
            });
        },
        /**
         * 绑定提交事件
         */
        bindSubmitEvent: function (moduleType) {
            $("#submit").off("click");
            $("#submit").on("click", function () {

                var menu = Huazie.form.serialize($("#menu_add"));

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

                Huazie.log(menu);

            });
        },
        /**
         * 绑定重置事件
         */
        bindResetEvent: function (module) {
            $("#reset").off("click");
            $("#reset").on("click", function () {

                AuthModule.MenuManagementFuncModule().reset();

            });
        }

    }

});