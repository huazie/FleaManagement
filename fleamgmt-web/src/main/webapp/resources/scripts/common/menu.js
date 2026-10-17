/**
 * <p> 菜单相关JS </p>
 *
 * <p> 菜单以多标签页形式打开：每个菜单对应一个标签与 iframe 面板，
 * 标签栏位于内容区最顶部（菜单搜索框同行居右），菜单面板紧随其下，
 * 重复打开已打开的菜单仅激活对应标签（iframe 状态保留），
 * 标签右上角提供关闭按钮，全部关闭后隐藏标签栏。 </p>
 *
 * @author huazie
 * @version v1.0.0
 * @date 2026年10月17日
 */
define(function (require, exports, module) {

    watchMenuToggler();
    watchSidebarDisplay();

    /**
     * 菜单数据映射（挂在顶层 window 上，各 iframe 子页面共享同一份数据）
     */
    function getMenuDataMap() {
        var hostWindow = window.top || window;
        if (!hostWindow.__fleaMenuDataMap) {
            hostWindow.__fleaMenuDataMap = {};
        }
        return hostWindow.__fleaMenuDataMap;
    }

    /**
     * 手机端菜单按钮守护：headerNav 模板加载时会整体重写 #nav-top（静态写入的按钮列表项被清掉），
     * 监听其变化，按钮不在列表时自动移回首位并恢复 ace 的侧边栏展开点击事件
     */
    function watchMenuToggler() {
        var hostWindow = window.top || window;
        if (hostWindow.__fleaMenuTogglerWatched) {
            return;
        }
        hostWindow.__fleaMenuTogglerWatched = true;

        alignMenuToggler();
        // 徽章行高度/位置随模板加载与视口变化，需要在变化后重新校准按钮位置
        $(window).on("resize.menuToggler", alignMenuToggler);
        $(window).on("load.menuToggler", alignMenuToggler);

        var $navTop = $("#nav-top");
        if ($navTop.length === 0 || typeof MutationObserver === "undefined") {
            return;
        }

        // 保存按钮列表项的克隆，供模板重写后恢复
        var $item = $navTop.children("li.menu-toggler-item");
        if ($item.length > 0) {
            hostWindow.__fleaMenuTogglerLi = $item[0].cloneNode(true);
        }

        new MutationObserver(function () {
            alignMenuToggler();
            var $navTop = $("#nav-top");
            var savedLi = hostWindow.__fleaMenuTogglerLi;
            if ($navTop.length === 0 || !savedLi) {
                return;
            }
            if ($navTop.children("li.menu-toggler-item").length === 0) {
                var $restored = $(savedLi.cloneNode(true)).prependTo($navTop);
                // 节点被替换后原有点击绑定丢失，恢复侧边栏展开逻辑（同 ace.handle_side_menu）
                $restored.find("#menu-toggler").off("click.menuToggler").on("click.menuToggler", function () {
                    $("#sidebar").toggleClass("display");
                    $(this).toggleClass("display");
                    return false;
                });
            }
        }).observe($navTop[0], { childList: true });
    }

    /**
     * 校准手机端菜单按钮的垂直位置：以徽章行（.navbar-header.pull-right）顶部为基准居中，
     * 避免锚定 navbar-container 底部时被多余空间顶偏（窄屏两个 navbar-header 均为 block 流，
     * 容器底部高度不稳定）
     */
    function alignMenuToggler() {
        var $btn = $("#menu-toggler");
        var $row = $(".navbar-header.pull-right");
        if ($btn.length === 0 || $row.length === 0) {
            return;
        }
        var rowTop = Math.round($row.position().top);
        var rowH = $row.outerHeight() || 0;
        var btnH = $btn.outerHeight() || 45;
        var top = rowTop + Math.max(0, Math.round((rowH - btnH) / 2));
        if ($btn.css("top") !== top + "px") {
            $btn.css("top", top + "px");
        }
    }

    /**
     * 手机端：侧边栏展开时显示全屏遮罩，点击遮罩任意位置（含菜单 iframe 区域上方）关闭侧边栏。
     * 监听 #sidebar 的 class 变化决定遮罩显隐，与触发入口（菜单按钮/ace/守护恢复）完全解耦。
     */
    function watchSidebarDisplay() {
        var hostWindow = window.top || window;
        if (hostWindow.__fleaSidebarMaskWatched) {
            return;
        }
        hostWindow.__fleaSidebarMaskWatched = true;

        var $sidebar = $("#sidebar");
        if ($sidebar.length === 0 || typeof MutationObserver === "undefined") {
            return;
        }

        // 全屏遮罩（挂顶层文档，覆盖内容区与各菜单 iframe；层级低于 navbar 与展开的 sidebar）
        var $mask = $('<div id="menu-mask"></div>').appendTo("body");
        $mask.on("click.menuMask", function () {
            $("#sidebar").removeClass("display");
            $("#menu-toggler").removeClass("display");
        });

        // 记录折叠态（menu-min），用于还原时判断
        var hadMin = $sidebar.hasClass("menu-min");
        new MutationObserver(function () {
            var hasMin = $sidebar.hasClass("menu-min");
            var hasDisplay = $sidebar.hasClass("display");
            // 手机端（≤991）.sidebar 默认 display:none，ace 还原折叠（移除 menu-min）时若展开态 class 已丢，
            // 侧边栏会整个隐藏；此处强制恢复展开，保证还原后是完整菜单列表
            if (hadMin && !hasMin && !hasDisplay) {
                if (hostWindow.__fleaSkipMinRestore) {
                    // 菜单按钮主动关闭折叠态：不强制展开
                    hostWindow.__fleaSkipMinRestore = false;
                } else {
                    $sidebar.addClass("display");
                    hasDisplay = true;
                }
            }
            hadMin = hasMin;
            // 菜单按钮高亮态与侧边栏可见性严格同步：展开态或折叠窄条可见 → 按钮保持展开状态
            $("#menu-toggler").toggleClass("display", hasDisplay || hasMin);
            $mask.toggleClass("display", hasDisplay);
        }).observe($sidebar[0], { attributes: true, attributeFilter: ["class"] });

        // 手机端折叠态（menu-min 窄条）下点菜单按钮：退出折叠并直接关闭整个侧边栏。
        // 窄条有 display:block 保底，若放任 ace 原生 toggle 只移除 display，窄条依然显示（看起来"关不掉"）
        hostWindow.document.addEventListener("click", function (e) {
            var target = e.target;
            var hit = false;
            while (target && target !== hostWindow.document) {
                if (target.id === "menu-toggler") {
                    hit = true;
                    break;
                }
                target = target.parentNode;
            }
            if (!hit || hostWindow.innerWidth > 991 || !$sidebar.hasClass("menu-min")) {
                return;
            }
            hostWindow.__fleaSkipMinRestore = true;
            $sidebar.removeClass("menu-min");
            $sidebar.removeClass("display");
            $("#menu-toggler").removeClass("display");
            e.preventDefault();
            e.stopPropagation();
        }, true);
    }

    
    /**
     * 获取指定上下文的文档 jQuery 包装（子页面打开时取父页面文档）
     */
    function $root(inParent) {
        return inParent ? $(parent.document) : $(document);
    }

    /**
     * 展示菜单的路径（标签页模式下仅同步菜单数据，不再渲染面包屑文本）
     */
    exports.showMenuPath = function (menu) {
        // 同步完整菜单数据，供收藏等模块使用
        if (menu && menu["MENU_CODE"]) {
            getMenuDataMap()[menu["MENU_CODE"]] = $.extend({}, getMenuDataMap()[menu["MENU_CODE"]], menu);
        }
    };

    /**
     * 清空展示菜单的路径
     */
    exports.clearMenuPath = function () {
        $("#breadcrumb").html('');
    };

    /**
     * 打开指定的菜单
     *
     * @param menuCode 菜单编码
     * @param menuName 菜单名称（标签页标题，缺省时沿用已记录名称或以编码展示）
     */
    exports.open = function (menuCode, menuName) {
        openTab(menuCode, menuName, false);
    };

    /**
     * 从子页面打开菜单（在父页面的标签栏中打开）
     */
    exports.open1 = function (menuCode, menuName) {
        openTab(menuCode, menuName, true);
    };

    /**
     * 以标签页方式打开菜单
     */
    function openTab(menuCode, menuName, inParent) {

        var $ctx = $root(inParent);
        var $tabs = $ctx.find("#menu-tabs");

        if ($tabs.length === 0) {
            // 首次打开：标签栏置于内容区最顶部（面包屑条之上），
            // iframe 面板容器置于 #page-content（面包屑条之下），
            // 保证右上角的菜单搜索框不被 iframe 顶出视口
            $ctx.find(".main-content").prepend(
                '<div id="menu-tabs" style="display:none;">'
                + '<ul class="menu-tabs" id="menu-tab-list"></ul>'
                + '</div>');
            $ctx.find("#page-content").append(
                '<div class="menu-tab-content" id="menu-tab-content"></div>');
            bindTabEvents($ctx, inParent);
            $tabs = $ctx.find("#menu-tabs");

            // 将面包屑条右上角的菜单搜索框移入标签栏同一行，面包屑条整行隐藏
            var $search = $ctx.find(".breadcrumbs > .nav-search");
            if ($search.length > 0) {
                $tabs.append($search);
                $ctx.find(".breadcrumbs").hide();
            }
        }
        $tabs.show();

        var menuDataMap = getMenuDataMap();
        var oldMenu = menuDataMap[menuCode];
        if (!menuName) {
            menuName = (oldMenu && oldMenu["MENU_NAME"]) || menuCode;
        }
        menuDataMap[menuCode] = $.extend({}, oldMenu, {
            "MENU_CODE": menuCode,
            "MENU_NAME": menuName
        });

        // 已打开的菜单仅激活对应标签，iframe 状态保留
        if ($tabs.find("#menu-tab-list > li[data-code='" + menuCode + "']").length > 0) {
            activateTab($ctx, menuCode, inParent);
            return;
        }

        $tabs.find("#menu-tab-list").append(
            '<li data-code="' + menuCode + '">'
            + '<a href="javascript:;" title="' + menuName + '">' + menuName + '</a>'
            + '<i class="fa fa-times menu-tab-close" title="关闭"></i>'
            + '</li>');

        var src = ReqUrlMap.get("fleamgmtMenu") + menuCode;
        $ctx.find("#menu-tab-content").append(
            '<div class="menu-tab-pane" data-code="' + menuCode + '">'
            + '<iframe id="' + menuCode + '" name="' + menuCode + '" src="' + src + '" frameborder="0" scrolling="yes"></iframe>'
            + '</div>');

        activateTab($ctx, menuCode, inParent);
    }

    /**
     * 激活指定标签（同步面板显示与当前菜单数据）
     */
    function activateTab($ctx, menuCode, inParent) {

        $ctx.find("#menu-tab-list > li").each(function () {
            $(this).toggleClass("active", $(this).attr("data-code") === menuCode);
        });
        $ctx.find("#menu-tab-content > .menu-tab-pane").each(function () {
            $(this).toggleClass("active", $(this).attr("data-code") === menuCode);
        });

        // 激活标签时仅同步当前菜单数据（供收藏等模块使用），不再渲染面包屑文本
        var menu = getMenuDataMap()[menuCode];
        if (menu) {
            if (inParent) {
                parent.window.currentMenu = menu;
            } else {
                window.currentMenu = menu;
            }
        }
    }

    /**
     * 绑定标签栏事件（仅首次构建时绑定一次）
     */
    function bindTabEvents($ctx, inParent) {

        var $list = $ctx.find("#menu-tab-list");

        // 点击标签标题：激活对应标签
        $list.on("click", "> li > a", function () {
            var menuCode = $(this).parent().attr("data-code");
            activateTab($ctx, menuCode, inParent);
        });

        // 点击关闭按钮：移除标签与面板，激活相邻标签
        $list.on("click", ".menu-tab-close", function () {
            if ($list.children("li").length <= 1) {
                // 只剩最后一个标签，不允许关闭
                return;
            }
            var $li = $(this).parent();
            var menuCode = $li.attr("data-code");
            var wasActive = $li.hasClass("active");

            $li.remove();
            $ctx.find("#menu-tab-content > .menu-tab-pane[data-code='" + menuCode + "']").remove();
            delete getMenuDataMap()[menuCode];

            var $rest = $ctx.find("#menu-tab-list > li");
            if ($rest.length === 0) {
                // 全部关闭：隐藏标签栏，还原空页面
                $ctx.find("#menu-tabs").hide();
                $ctx.find("#breadcrumb").html('');
                if (inParent) {
                    parent.window.currentMenu = {};
                } else {
                    window.currentMenu = null;
                }
            } else if (wasActive) {
                // 关闭的是激活标签：激活剩余的最后一个
                activateTab($ctx, $rest.last().attr("data-code"), inParent);
            }
        });
    }

});
