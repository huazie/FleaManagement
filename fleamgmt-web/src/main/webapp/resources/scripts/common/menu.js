/**
 * <p> 菜单相关JS </p>
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

        window.currentMenu = menu;

        Huazie.tpl.loadTpl(TplUrlMap.get("commonLi"), function () {
            Huazie.tpl.loadTemp($("#breadcrumb"), "#tpl_common_li_1", menu);
        });
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
        var height = window.screen.height * 0.76;
        $newIframe.css('height', height);
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
        var height = parent.screen.height * 0.76;
        $newIframe.css('height', height);
        $newIframe.attr('frameborder', '0');
        $newIframe.attr('scrolling', 'yes');
        $("#page-content", parent.document).html($newIframe);
    }

});