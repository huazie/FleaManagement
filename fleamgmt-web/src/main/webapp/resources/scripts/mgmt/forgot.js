/**
 * 忘记密码模块
 *
 * @author huazie
 * @version 1.0.0
 */
define(function(require, exports, module) {

    /**
     * 忘记密码模块初始化
     */
    exports.init = function () {

        // 注册按钮
        $("#send").on("click", function () {

            Huazie.dialog.tips("warning", [{"MESSAGE" : "亲，该功能已下线哟！"},{"MESSAGE": "如有需要，请联系管理员哈！"}], 2);

        });

    };
});