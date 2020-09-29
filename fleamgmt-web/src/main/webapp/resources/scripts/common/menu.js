/**
 * @Description
 *
 * @author huazie
 * @version v1.0.0
 * @date 2017年3月8日
 */
define(function (require, exports, module) {

    /**
     * 展示菜单的路径
     */
    exports.showMenuPath = function (menu) {

        var level = menu["MENU_LEVEL"];

        if (level == 1 || level == "1") {
            window.currentMenu = {};
        }

        window.currentMenu[menu["MENU_LEVEL"]] = menu;

        for (var i = 1; i <= level; i++) {
            appendMenuPath(window.currentMenu[i]);
        }

    }

    /**
     * 追加menu的路径
     *
     * @param menu
     *            菜单
     */
    function appendMenuPath(menu) {
        Huazie.tpl.loadTpl(TplUrlMap.get("commonLi"), function () {
            if (menu["MENU_LEVEL"] == 1) {
                Huazie.tpl.loadTemp($("#breadcrumb"), "#tpl_common_li_1", menu);
            } else {
                Huazie.tpl.appendTemp($("#breadcrumb"), "#tpl_common_li_1", menu);//追加
            }
        });
    }

    /**
     * 打开指定的菜单
     */
    exports.open = function (menuCode) {
        openIframe(menuCode);
    };

    /**
     * 从子页面打开菜单
     */
    exports.open1 = function (menuCode) {
        openIframe1(menuCode);
    };

    /**
     * 打开一个Iframe
     */
    function openIframe(menuCode) {
        var $newIframe = $(document.createElement("iframe"));
        var src = ReqUrlMap.get("fleamgmtMenu") + menuCode;
        $newIframe.attr('id', menuCode);
        $newIframe.attr('src', src);
        $newIframe.css('width', '100%');
        $newIframe.css('height', '550px');
        $newIframe.attr('frameborder', '0');
        $newIframe.attr('scrolling', 'yes');
        $("#page-content").html($newIframe);
    }

    /**
     * 从子页面打开菜单
     */
    function openIframe1(menuCode) {
        var $newIframe = $(parent.document.createElement("iframe"));
        var src = ReqUrlMap.get("fleamgmtMenu") + menuCode;
        $newIframe.attr('id', menuCode);
        $newIframe.attr('src', src);
        $newIframe.css('width', '100%');
        $newIframe.css('height', '550px');
        $newIframe.attr('frameborder', '0');
        $newIframe.attr('scrolling', 'yes');
        $("#page-content", parent.document).html($newIframe);
    }

});