/**
 * <p> 主页模块的js </p>
 *
 * @author huazie
 * @version v1.0.0
 * @date 2017年3月1日
 */
define(function (require, exports, module) {

    // 获取用户Session信息
    ReqUrlMap.put("getUserSession", "fleamgmtIndex!getUserSession.flea");

    // 跳主退出
    ReqUrlMap.put("userQuit", "fleamgmtIndex!quit.flea");

    // 菜单搜索
    ReqUrlMap.put("menuSearch", "authMenu!search.flea");

    // 导航栏tpl加载
    TplUrlMap.put("headerNav", "nav/header_nav.tpl");

    // 侧边栏快捷菜单tpl加载
    TplUrlMap.put("shortcuts", "sidebar/sidebar_shortcuts.tpl");

    // 侧边菜单tpl加载
    TplUrlMap.put("sideMenu", "nav/sidebar_nav.tpl");

    /**
     * 初始化
     */
    exports.init = function () {

        FleamgmtIndex.getUserSession(function (data) {
            window.currentMenu = {};
            // 加载顶部功能模块
            FuncModule.loadNavFuncModule(data);
            // 加载侧边快捷菜单
            FuncModule.loadShortcutsModule(data);
            // 加载侧边菜单
            FuncModule.loadSideMenuModule(data);
            // 加载菜单搜索
            FuncModule.loadMenuSearch();
        });

        //关闭IE浏览器事件
        //$(window).bind('beforeunload',function(){
        //待确认？？
//			if(window.opener && !window.opener.closed && window.opener.openWindow){
//				// 当前窗口为子窗口时，不进行注销操作
//			} else {
//				event = event || window.event;
//				if(event){
//					//用户点击浏览器右上角关闭按钮或  按alt+F4关闭  或者 点击任务栏
//
//				}
//			}
//			FuncModule.UserFuncModule().quit();
//			return confirm("确定离开此页面吗？");
        //});

    };

    var FleamgmtIndex = {
        /**
         * 获取用户Session信息
         */
        getUserSession: function (callback) {
            // 读取相应的用户Session信息
            Huazie.ajax.getJson(ReqUrlMap.get("getUserSession"), function (data, status) {
                var result = data;
                if (status) {
                    if (result.retCode === "Y") {
                        callback(result);
                    } else {
                        Huazie.dialog.tips("warning", result.retMess, 2);
                    }
                } else {
                    Huazie.dialog.tips("warning", result.retMess, 2);
                }
            });
        }
    };

    /**
     * 功能模块
     */
    var FuncModule = {
        /**
         * 加载顶部功能模块
         */
        loadNavFuncModule: function (data) {

            var json = {
                "NAV_BG_COLOR": "grey",
                "NAV_ICON": "fa-tasks",
                "NAV_BADGE_COLOR": "badge-grey",
                "NAV_BADGE_NUM": "4",
                "NAV_DROPDOWN_MENU_COLOR": "",
                "NAV_DROPDOWN_HEADER_ICON": "fa-check",
                "NAV_DROPDOWN_HEADER_TITLE": "未任务完成",
                "NAV_CONTENT": [{
                    "PROGRESS_BAR_NAME": "软件更新",
                    "PROGRESS_BAR_PERCENTTAGE": "65%",
                    "PROGRESS_BAR_STYLE": "",
                    "PROGRESS_BAR_STATE": ""
                }, {
                    "PROGRESS_BAR_NAME": "硬件更新",
                    "PROGRESS_BAR_PERCENTTAGE": "35%",
                    "PROGRESS_BAR_STYLE": "",
                    "PROGRESS_BAR_STATE": "progress-bar-danger"
                }, {
                    "PROGRESS_BAR_NAME": "单元测试",
                    "PROGRESS_BAR_PERCENTTAGE": "15%",
                    "PROGRESS_BAR_STYLE": "",
                    "PROGRESS_BAR_STATE": "progress-bar-warning"
                }, {
                    "PROGRESS_BAR_NAME": "错误修复",
                    "PROGRESS_BAR_PERCENTTAGE": "90%",
                    "PROGRESS_BAR_STYLE": "progress-striped active",
                    "PROGRESS_BAR_STATE": "progress-bar-success"
                }],
                "NAV_DROPDOWN_BOTTOM_TITLE": "查看任务详情"
            };

            var json1 = {
                "NAV_BG_COLOR": "purple",
                "NAV_ICON": "fa-bell icon-animated-bell",
                "NAV_BADGE_COLOR": "badge-important",
                "NAV_BADGE_NUM": "8",
                "NAV_DROPDOWN_MENU_COLOR": "navbar-pink",
                "NAV_DROPDOWN_HEADER_ICON": "fa-warning",
                "NAV_DROPDOWN_HEADER_TITLE": "通知",
                "NAV_CONTENT": [{
                    "NOTICE_ICON_COLOR": "btn-inverse",
                    "NOTICE_ICON": "fa-comment",
                    "NOTICE_TITLE": "新闻评论",
                    "NOTICE_BADGE": "badge-info",
                    "NOTICE_NUM": "12"
                }, {
                    "NOTICE_ICON_COLOR": "btn-primary",
                    "NOTICE_ICON": "fa-user",
                    "NOTICE_TITLE": "用户数",
                    "NOTICE_BADGE": "",
                    "NOTICE_NUM": "10"
                }, {
                    "NOTICE_ICON_COLOR": "btn-success",
                    "NOTICE_ICON": "fa-shopping-cart",
                    "NOTICE_TITLE": "新订单",
                    "NOTICE_BADGE": "badge-success",
                    "NOTICE_NUM": "8"
                }, {
                    "NOTICE_ICON_COLOR": "btn-info",
                    "NOTICE_ICON": "fa-twitter",
                    "NOTICE_TITLE": "粉丝",
                    "NOTICE_BADGE": "badge-important",
                    "NOTICE_NUM": "11"
                }],
                "NAV_DROPDOWN_BOTTOM_TITLE": "查看所有通知"
            };

            // var notAuditResInfo = data.notAuditResInfo;
            //
            var json2 = {
                "NAV_BG_COLOR": "green",
                "NAV_ICON": "fa-envelope-o fa-fw icon-animated-vertical",
                "NAV_BADGE_COLOR": "badge-success",
                "NAV_BADGE_NUM": 10,
                "NAV_DROPDOWN_MENU_COLOR": "",
                "NAV_DROPDOWN_HEADER_ICON": "fa-envelope-o fa-fw",
                "NAV_DROPDOWN_HEADER_TITLE": "待审核物品",
                "NAV_CONTENT": "",
                "NAV_HREF": "javascript:Comm.openMenu(\"res_audit\");",
                "NAV_DROPDOWN_BOTTOM_TITLE": "查看所有"
            };

            var user_json = {
                "NAV_BG_COLOR": "light-blue",
                "NAV_USER_PHOTO": ImgUrlMap.get("user") + "defaults/male.png",
                "NAV_USER_NAME": data.userInfo.USER_NAME,
                "NAV_CONTENT": [{
                    "HAS_DIVIDER": false,
                    "USER_FUNCTION_ICON": "cog",
                    "USER_FUNCTION_NAME": "设置",
                    "USER_FUNCTION_EVENT": "setting"
                }, {
                    "HAS_DIVIDER": false,
                    "USER_FUNCTION_ICON": "user",
                    "USER_FUNCTION_NAME": "个人资料",
                    "USER_FUNCTION_EVENT": "personalData"
                }, {
                    "HAS_DIVIDER": true,
                    "USER_FUNCTION_ICON": "power-off",
                    "USER_FUNCTION_NAME": "退出",
                    "USER_FUNCTION_EVENT": "quit"
                }]
            };

            //加载顶部导航栏
            Huazie.tpl.loadTpl(TplUrlMap.get("headerNav"), function () {
                var $navTop = $("#nav-top");
                Huazie.tpl.loadTemp($navTop, "#tpl_header_nav_progress", json);
                Huazie.tpl.appendTemp($navTop, "#tpl_header_nav_notice", json1);
                //Huazie.tpl.appendTemp($navTop, "#tpl_header_nav_msg", json2);
                Huazie.tpl.appendTemp($navTop, "#tpl_header_nav_user", user_json);
                BindEvent.bindUserFuncEvent();
            });
        },
        /**
         * 加载侧边快捷菜单
         */
        loadShortcutsModule: function (data) {
            var shortcuts = [{
                "BTN_CLASS": "btn-success",
                "BTN_ICON": "signal"
            }, {
                "BTN_CLASS": "btn-info",
                "BTN_ICON": "pencil"
            }, {
                "BTN_CLASS": "btn-warning",
                "BTN_ICON": "group"
            }, {
                "BTN_CLASS": "btn-danger",
                "BTN_ICON": "cogs"
            }];

            //加载侧边快捷菜单
            Huazie.tpl.loadTpl(TplUrlMap.get("shortcuts"), function () {
                Huazie.tpl.loadTemp($("#sidebar-shortcuts"), "#tpl_sidebar_shortcuts", shortcuts);
            });
        },
        /**
         * 加载侧边菜单
         */
        loadSideMenuModule: function (data) {

            //加载侧边快捷菜单
            Huazie.tpl.loadTpl(TplUrlMap.get("sideMenu"), function () {
                var menuList = data.menuList;
                if (menuList && menuList.length > 0) {
                    // 第一个被选择
                    menuList[0]['IS_SELECT'] = true;
                }
                Huazie.tpl.loadTemp($("#nav-list"), "#tpl_sidebar_nav", menuList);
                BindEvent.bindSideMenuEvent();

                require.async("../common/menu", function(fleaMenu) {
                    var menu = menuList[0];
                    if(menu) {
                        if(!menu["HAS_SUB_MENU"]) {
                            fleaMenu.open(menu["MENU_CODE"]);//打开初始化选中的菜单
                        }
                        fleaMenu.showMenuPath(menu);
                    }
                });
            });
        },
        /**
         * 用户导航栏功能模块
         */
        UserFuncModule: function () {
            return {
                /**
                 * 设置
                 */
                setting: function () {
                    Huazie.log("setting");
                },
                /**
                 * 个人资料
                 */
                personalData: function () {
                    Huazie.log("personalData");
                },
                /**
                 * 退出
                 */
                quit: function () {
                    // 执行退出的操作
                    Huazie.ajax.getJson(ReqUrlMap.get("userQuit"), function (data, status) {
                        var result = data;
                        if (status) {
                            if (result.retCode === "Y") {
                                window.location.reload();
                            }
                        } else {
                            Huazie.dialog.tips("warning", result.retMess, 2);
                        }
                    });
                }
            }
        },
        /**
         * 用户导航栏功能模块
         */
        loadMenuSearch: function () {
            try {
                $("#nav-search-input").typeahead({
                    source: function (query, process) {

                        var cmd = {
                            "menu.menuName": query
                        };
                        // 菜单搜索
                        Huazie.ajax.getJson(ReqUrlMap.get("menuSearch"), cmd, function (data, status) {
                            var result = data;
                            if (status) {
                                if (result.retCode === "Y") {
                                    var menuList = result.menuList.map(function (menuMap) {
                                        var jsonStr = {
                                            code: menuMap["MENU_CODE"],
                                            name: menuMap["MENU_NAME"]
                                        };
                                        return JSON.stringify(jsonStr);
                                    });
                                    return process(menuList);
                                } else {
                                    Huazie.dialog.tips("warning", result.retMess, 2);
                                }
                            } else {
                                Huazie.dialog.tips("warning", result.retMess, 2);
                            }
                        });
                    },
                    matcher: function (item) {
                        return true;
                    },
                    sorter: function (items) {
                        return items;
                    },
                    highlighter: function (obj) {
                        var item = JSON.parse(obj);
                        return item.name;
                    },
                    updater: function (obj) {
                        var item = JSON.parse(obj);

                        var menuCode = item.code;

                        require.async("../common/menu", function (fleaMenu) {
                            window.currentMenu = {};
                            fleaMenu.open(menuCode); // 打开初始化选中的菜单
                            fleaMenu.clearMenuPath(); // 清空展示菜单的路径
                        });

                        // 在这边设置code，或者直接就打开菜单
                        return item.name;
                    }
                })
            } catch (exception) {
            }
        }
    };

    /**
     * 事件绑定对象
     */
    var BindEvent = {
        /**
         * 绑定用户导航功能栏的事件
         */
        bindUserFuncEvent: function () {
            $("a[id^='user_function_']").on("click", function () {
                var name = $(this).attr("name");
                var func = eval("FuncModule.UserFuncModule()." + name);
                if (name == "quit") {
                    Huazie.dialog.confirm("question", "亲，您确定退出吗？", function () {
                        func(); // 执行相应的功能
                    });
                } else {
                    func(); // 执行相应的功能
                }
            });
        },
        /**
         * 绑定侧边菜单事件
         */
        bindSideMenuEvent: function () {
            $("a[id^='menu_']").on("click", function () {
                var $thiz = $(this);
                var menu = Huazie.form.serialize($thiz);

                if (window.currentMenu && window.currentMenu["MENU_CODE"] === menu["MENU_CODE"]) {
                    return;
                }

                require.async("../common/menu", function (fleaMenu) {
                    // 表示这个是叶子菜单
                    if (!$thiz.hasClass("dropdown-toggle")) {
                        // 打开选中的菜单
                        fleaMenu.open(menu["MENU_CODE"]);
                    }
                    fleaMenu.showMenuPath(menu);
                });
            });
        }
    };

});

