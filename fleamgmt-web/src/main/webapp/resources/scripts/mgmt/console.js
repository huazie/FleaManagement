/**
 * <p> 控制台页面展示JS </p>
 *
 * @author huazie
 * @version v1.0.0
 * @date 2017年3月10日
 */
define(function (require, exports, module) {

    exports.init = function () {
        ConsoleModule.loadMenufavorites();
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
                    Huazie.tpl.loadTpl(TplUrlMap.get("menuFavorites"), function () {
                        Huazie.tpl.loadTemp($("#menuFavorites"), "#tpl_menu_favorites", data);
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
                var $thiz = $(this);
                var menuCode = $thiz.attr("name");
                var menuName = $.trim($thiz.find("span").text());

                require.async("../common/menu", function (fleaMenu) {
                    fleaMenu.open1(menuCode, menuName); // 在父页面标签栏中打开收藏的菜单(激活标签时同步面包屑)
                });

            });
        }
    }

});