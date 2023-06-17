/**
 * 注册模块
 *
 * @author huazie
 * @version 1.0.0
 */
define(function(require, exports, module) {

    // 用户注册
    ReqUrlMap.put("fleamgmtSignUp", "fleamgmtSignUp!signup.flea");

    /**
     * 注册模块初始化
     */
    exports.init = function () {

        // 注册按钮
        $("#signup").on("click", function () {
            var name = $("#signup_name").val();
            var pwd = $("#signup_pwd").val();
            var confirmPwd = $("#signup_confirm_pwd").val();

            if (!name || name.trim() === "") {
                Huazie.dialog.tips("warning", "亲，邮箱或手机号不能为空哟！", 1);
                return;
            }

            // 校验邮箱或手机号是否合法
            if (!Huazie.validate.test("email", name) && !Huazie.validate.test("phone", name)) {
                Huazie.dialog.tips("warning", "亲，请输入正确的邮箱或手机号哟！", 2);
                return;
            }

            if (!pwd || pwd.trim() === "") {
                Huazie.dialog.tips("warning", "亲，密码不能为空哟！", 1);
                return;
            }

            // 校验密码是否满足 “8~16位的字母、数字的组合，且必须有数字和字母，可含特殊字符”
            if (!Huazie.validate.test("password1", pwd)) {
                Huazie.dialog.tips("warning", "亲，密码需要8~16位的字母和数字的组合，且必须有数字和字母，可含特殊字符", 3);
                return;
            }

            if (!confirmPwd || confirmPwd.trim() === "") {
                Huazie.dialog.tips("warning", "亲，确认密码不能为空哟！", 1);
                return;
            }

            if (!(pwd === confirmPwd)) {
                Huazie.dialog.tips("warning", "亲，请确认密码填写一致哟！", 2);
                return;
            }

            if (!$("#agreement").prop("checked")) {
                Huazie.dialog.tips("warning", "亲，请确认接受《用户协议》哟！", 2);
                return;
            }

            var $thiz = $(this);

            $thiz.addClass("disabled").html(Huazie.msg.btnText("user", "正在注册中..."));

            var cmd = {
                "accountCode": name.trim(),
                "accountPwd": pwd.trim()
            };

            // 用户注册
            Huazie.ajax.postJson(ReqUrlMap.get("fleamgmtSignUp"), cmd, function (data, status) {
                var result = data;
                if (status && result.retCode === "Y") {
                    // 触发“重置”按钮，清空注册内容
                    $("#reset").click();
                    Huazie.dialog.tips("info", result.retMess, 2);
                    // 设置当前登录用户名
                    $("#name").val(name.trim());
                    // 触发“返回登录”按钮，跳转到登录界面
                    $(".back-to-login-link").click();
                } else {
                    Huazie.dialog.tips("warning", result.retMess, 2);
                }
                $thiz.removeClass("disabled").html(Huazie.msg.btnText1("arrow-right", "注册 "));
            });
        });

    };
});