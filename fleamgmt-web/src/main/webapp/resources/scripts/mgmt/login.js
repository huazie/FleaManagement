/**
 *  登录模块
 *
 *  @author huazie
 *  @date 2017/03/01
 */
define(function (require, exports, module) {

    // 用户登录
    ReqUrlMap.put("fleamgmtLogin", "fleamgmtLogin!login.flea");
    // 检查用户Session是否初始化完成
    ReqUrlMap.put("fleamgmtCheckSession", "fleamgmtLogin!checkSession.flea");

    var signup = require("./signup.js");

    var forgot = require("./forgot.js");

    /**
     * 登录初始化
     */
    exports.init = function () {
        // jquery tabs
        $("#tabs").tabs();

        // 注册模块初始化
        signup.init();

        // 忘记密码模块初始化
        forgot.init();

        // 登录按钮
        $("#login").on("click", function () {
            var name = $("#name").val();
            var password = $("#password").val();

            if (!name || name.trim() === "") {
                Huazie.dialog.tips("warning", "亲，邮箱或手机号不能为空哟！", 1);
                return;
            }

            // 校验邮箱或手机号是否合法
            if (!Huazie.validate.test("email", name) && !Huazie.validate.test("phone", name)) {
                Huazie.dialog.tips("warning", "亲，请输入正确的邮箱或手机号哟！", 1);
                return;
            }

            if (!password || password.trim() === "") {
                Huazie.dialog.tips("warning", "亲，密码不能为空哟！", 1);
                return;
            }

            var $thiz = $(this);

            $thiz.addClass("disabled").html(Huazie.msg.btnText("key fa-lg", "正在登陆中..."));

            var cmd = {
                "accountCode": name.trim(),
                "accountPwd": password.trim()
            };

            // 登录验证
            Huazie.ajax.postJson(ReqUrlMap.get("fleamgmtLogin"), cmd, function (data, status) {
                var result = data;
                if (status) {
                    if (result.retCode === "Y") {
                        Huazie.dialog.tips("info", result.retMess, 1);
                        // 不恢复按钮，保持禁用状态并显示跳转中，直到真正跳转
                        $thiz.addClass("disabled").html(Huazie.msg.btnText("key fa-lg", "正在跳转..."));
                        // 轮询等待 Session 初始化完成后再跳转首页
                        waitForSessionAndRedirect($thiz);
                    } else if (result.retCode === "N") {
                        Huazie.dialog.tips("warning", result.retMess, 2);
                        $("#name").val('');
                        $("#password").val('');
                        $thiz.removeClass("disabled").html(Huazie.msg.btnText("lock fa-lg", "登录"));
                    }
                } else {
                    Huazie.dialog.tips("warning", result.retMess, 2);
                    $thiz.removeClass("disabled").html(Huazie.msg.btnText("lock fa-lg", "登录"));
                }
            });
        });
    };

    // 轮询等待 Session 初始化完成
    function waitForSessionAndRedirect($btn) {
        var maxRetry = 10;  // 最多重试10次
        var retryInterval = 500;  // 每次间隔500ms
        var retryCount = 0;

        function doCheck() {
            retryCount++;
            Huazie.ajax.getJson(ReqUrlMap.get("fleamgmtCheckSession"), function (data, status) {
                if (status && data.retCode === "Y") {
                    // Session 就绪，跳转首页
                    location.href = ReqUrlMap.get("fleamgmtHome");
                } else if (retryCount >= maxRetry) {
                    // 超过最大重试次数，恢复按钮，提示用户
                    $btn.removeClass("disabled").html(Huazie.msg.btnText("lock fa-lg", "登录"));
                    Huazie.dialog.tips("warning", "跳转超时，请重新登录", 2);
                } else {
                    // 继续等待
                    setTimeout(doCheck, retryInterval);
                }
            });
        }

        doCheck();
    }

});
