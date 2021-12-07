/**
 * <p> comm.js </p>
 *
 * @author huazie
 * @version v1.0.0
 * @date 2017年3月10日
 */

$(function () {

    // 判断是否该菜单已经被收藏了
    Comm.isFavorites();

    // 监听AJAX请求完成，用于实现ajax请求跳转
    Comm.ajaxComplete();

});

Comm = {
    /**
     * 添加收藏
     */
    addMenuFav: function () {

        var $afav = $(".page-header-fav .page-header-fav-a");

        var menuCode = Huazie.browser.getFrameParameter("code");

        seajs.use(SeaJsUrlMap.get("menuFavorites"), function (menuFavorites) {

            // 如果还没有收藏
            if ($afav.find("span").html() === "收藏") {

                menuFavorites.collectMenu(menuCode, function (data) {
                    if (data.retCode === "Y") {
                        $afav.find("i").removeClass("fa-star-o").addClass("fa-star");
                        $afav.find("span").html("已收藏");
                    } else {
                        // 收藏失败

                    }
                });

            } else {//已经收藏
                menuFavorites.unCollectMenu(menuCode, function (data) {
                    if (data.retCode === "Y") {
                        $afav.find("i").removeClass("fa-star").addClass("fa-star-o");
                        $afav.find("span").html("收藏");
                    } else {
                        // 取消收藏失败
                    }
                });

            }
        });

    },
    isHome: function (menuCode) {
        if (menuCode === "index") {
            return true;
        } else {
            return false;
        }
    },
    /**
     * 获取用户某菜单收藏信息，如果已经收藏过了，界面显示已经收藏
     */
    isFavorites: function () {

        var $afav = $(".page-header-fav .page-header-fav-a");

        var menuCode = Huazie.browser.getFrameParameter("code");

        if (Comm.isHome(menuCode)) {
            return;
        }

        // start load menu-favorites.js
        seajs.use(SeaJsUrlMap.get("menuFavorites"), function (menuFavorites) {

            menuFavorites.isFavorites(menuCode, function (data) {
                if (data.retCode === "Y") {
                    $afav.find("i").removeClass("fa-star-o").addClass("fa-star");
                    $afav.find("span").html("已收藏");
                } else if (data.retCode === "N") {
                    $afav.find("i").removeClass("fa-star").addClass("fa-star-o");
                    $afav.find("span").html("收藏");
                } else {
                    Huazie.dialog.tips("warning", data.retMess, 2);
                }
            });

        }); // end load menu-favorites.js

    },
    /**
     * 打开菜单
     */
    openMenu: function (menuCode) {

        // start load auth-menu.js
        seajs.use(SeaJsUrlMap.get("menu"), function (fleaMenu) {
            fleaMenu.open(menuCode);
        });// end load auth-menu.js

    },
    /**
     * <p> ajax请求完成监听 </p>
     */
    ajaxComplete: function () {
        $(document).ajaxComplete(function (event, xhr, options) {
            var redirectUrl = xhr.getResponseHeader("REDIRECT_URL");
            if (redirectUrl && redirectUrl !== "" && redirectUrl !== "null") {
                var win = window;
                if (win !== win.top) {
                    win = win.top;
                }
                win.location.href = redirectUrl;
            }
        });
    }

};
