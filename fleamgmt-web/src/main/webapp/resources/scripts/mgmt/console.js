/**
 * <p> 控制台页面展示JS </p>
 *
 * @author huazie
 * @version v1.0.0
 * @date 2017年3月10日
 */
define(function (require, exports, module) {

    exports.init = function () {
        // ConsoleModule.loadMenufavorites();
    };
    /**
     * 控制台模块
     */
    var ConsoleModule = {
        /**
         * 加载收藏夹
         */
        loadMenufavorites: function () {
            require.async("../common/menu-favorites", function (menuFavorites) {
                menuFavorites.findMenufavorites(function (data) {
                    Huazie.tpl.loadTpl(TplUrlMap.get("commonDiv"), function () {
                        Huazie.tpl.loadTemp($("#menuFavorites"), "#tpl_common_div_a", data);
                        BindEvent.bindMenufavoritesEvent();
                    });
                });
            });
        }
    };

    var BindEvent = {
        /**
         * 绑定收藏夹点击事件
         */
        bindMenufavoritesEvent: function () {
            $("a[id^='favorites_']").on("click", function () {
                var menuCode = $(this).attr("name");

                require.async("../common/menu", function (fleaMenu) {
                    parent.window.currentMenu = {};
                    fleaMenu.open1(menuCode); // 打开初始化选中的菜单
                    fleaMenu.clearMenuPath(); // 清空展示菜单的路径
                });

            });
        }
    }

});