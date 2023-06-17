/**
 * <p> 菜单收藏夹 </p>
 *
 * @author huazie
 * @version v1.0.0
 * @date 2017年3月10日
 */
define(function (require, exports, module) {

    //收藏菜单URL
    ReqUrlMap.put("collectMenu", "menuFavorites!collect.flea");
    //取消收藏菜单URL
    ReqUrlMap.put("uncollectMenu", "menuFavorites!cancel.flea");
    //获取用户收藏菜单
    ReqUrlMap.put("findMenufavorites", "menuFavorites!find.flea");

    // 用户是否已经收藏某菜单
    ReqUrlMap.put("isFavorites", "menuFavorites!isFavorites.flea");

    /**
     * 收藏菜单
     */
    exports.collectMenu = function (menuCode, callback) {
        MenuFavorites.collect(menuCode, callback);
    };

    /**
     * 取消菜单收藏
     */
    exports.unCollectMenu = function (menuCode, callback) {
        MenuFavorites.cancel(menuCode, callback);
    };

    /**
     * 获取用户收藏菜单
     */
    exports.findMenufavorites = function (callback) {
        MenuFavorites.find(callback);
    };

    /**
     * 获取当前菜单编码是否已经被收藏
     *
     * @param menuCode 菜单编码
     * @param callback 回调函数
     */
    exports.isFavorites = function (menuCode, callback) {
        MenuFavorites.isFavorites(menuCode, callback);
    };

    /**
     * 菜单收藏夹
     */
    var MenuFavorites = {
        /**
         * 收藏菜单
         */
        collect: function (menuCode, callback) {
            // 收藏菜单
            Huazie.ajax.postJson(ReqUrlMap.get("collectMenu"), {"menuCode": menuCode}, function (data, status) {

                if (status) {
                    if (callback) {
                        callback(data);
                    }
                } else {
                    Huazie.dialog.tips("warning", data.retMess, 2);
                }

            });
        },
        /**
         * 取消菜单
         */
        cancel: function (menuCode, callback) {
            // 取消收藏菜单
            Huazie.ajax.postJson(ReqUrlMap.get("uncollectMenu"), {"menuCode": menuCode}, function (data, status) {

                if (status) {
                    if (callback) {
                        callback(data);
                    }
                } else {
                    Huazie.dialog.tips("warning", data.retMess, 2);
                }

            });
        },
        /**
         * 获取用户收藏菜单
         */
        find: function (callback) {
            // 获取用户收藏菜单
            Huazie.ajax.getJson(ReqUrlMap.get("findMenufavorites"), function (data, status) {

                if (status) {
                    if (callback) {
                        callback(data);
                    }
                } else {
                    Huazie.dialog.tips("warning", data.retMess, 2);
                }

            });
        },
        /**
         * 判断该菜单是否已经被收藏了
         *
         * @param menuCode 菜单编码
         * @param callback 回调方法
         */
        isFavorites: function (menuCode, callback) {
            // 判断用户是否已经收藏该菜单
            Huazie.ajax.getJson(ReqUrlMap.get("isFavorites"), {"menuCode": menuCode}, function (data, status) {
                if (status) {
                    if (callback) {
                        callback(data);
                    }
                } else {
                    Huazie.dialog.tips("warning", data.retMess, 2);
                }
            });
        }
    }

});