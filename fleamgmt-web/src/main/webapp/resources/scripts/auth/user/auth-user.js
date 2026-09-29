/**
 * @Description auth-user.js 用户模块管理脚本
 *              覆盖：用户注册（分步向导）、用户变更（表格 + 编辑面板）、用户授权（标签页 + 穿梭）；
 *              用户组新增、用户组变更、用户组授权同构。
 *              页面逻辑由 auth-common.js 的各形态引擎统一承载，此处只做接口注册与模块配置。
 *
 * @author huazie
 * @version v1.1.0
 * @date 2026年9月27日
 */
define(function (require, exports, module) {

    // 授权管理公共模块
    var AuthCommon = require('../auth-common');

    /* ==================== 接口注册 ==================== */

    // 用户列表（Fuelux 树 / 候选）
    ReqUrlMap.put("authUserList", "authUser!list.flea");

    // 用户明细列表（表格）
    ReqUrlMap.put("authUserPage", "authUser!page.flea");

    // 用户明细查询
    ReqUrlMap.put("authUserQuery", "authUser!query.flea");

    // 用户注册
    ReqUrlMap.put("authUserRegister", "authUser!register.flea");

    // 用户变更
    ReqUrlMap.put("authUserUpdate", "authUser!update.flea");

    // 用户授权明细
    ReqUrlMap.put("authUserAuth", "authUser!auth.flea");

    // 用户授权提交
    ReqUrlMap.put("authUserAuthorize", "authUser!authorize.flea");

    // 用户组列表（Fuelux 树 / 候选）
    ReqUrlMap.put("authUserGroupList", "authUserGroup!list.flea");

    // 用户组明细列表（表格）
    ReqUrlMap.put("authUserGroupPage", "authUserGroup!page.flea");

    // 用户组明细查询
    ReqUrlMap.put("authUserGroupQuery", "authUserGroup!query.flea");

    // 用户组新增
    ReqUrlMap.put("authUserGroupAdd", "authUserGroup!add.flea");

    // 用户组变更
    ReqUrlMap.put("authUserGroupModify", "authUserGroup!modify.flea");

    // 用户组授权明细
    ReqUrlMap.put("authUserGroupAuth", "authUserGroup!auth.flea");

    // 用户组授权提交
    ReqUrlMap.put("authUserGroupAuthorize", "authUserGroup!authorize.flea");

    /* ==================== 分步向导类页面 ==================== */

    /**
     * 密码强度提示。
     * <p> 依据「长度 ≥ 6」「字母与数字混合」「含特殊字符」三项累计评分，
     * 以进度条与文案实时反馈，避免用户反复试错。
     */
    function bindPasswordStrength() {

        $("#accountPwd").off("keyup").on("keyup", function () {

            var value = $(this).val() || "";
            var score = 0;

            if (value.length >= 6) {
                score += 1;
            }
            if (/[a-zA-Z]/.test(value) && /\d/.test(value)) {
                score += 1;
            }
            if (/[^a-zA-Z0-9]/.test(value)) {
                score += 1;
            }

            var levels = [
                {cls: "progress-bar-danger", text: "过短", width: "25%"},
                {cls: "progress-bar-danger", text: "弱", width: "40%"},
                {cls: "progress-bar-warning", text: "中", width: "70%"},
                {cls: "progress-bar-success", text: "强", width: "100%"}
            ];

            var level = levels[score];

            $("#pwd_strength").attr("class", "progress-bar " + level.cls).css("width", level.width);
            $("#pwd_strength_text").text("密码强度：" + level.text);

        });

    }

    /**
     * 向导类页面配置（分步录入 + 提交前摘要）
     */
    var WIZARD_CONF = {
        "userRegister": {
            wizardId: "user_register_wizard",
            stepContainerId: "user_register_steps",
            formId: "auth_wizard_form",
            summaryId: "user_register_summary",
            submitUrl: "authUserRegister",
            required: [
                ["accountCode", "登录账号"],
                ["accountPwd", "登录密码"],
                ["accountPwd2", "确认密码"],
                ["userName", "用户昵称"]
            ],
            equalTo: [["accountPwd2", "accountPwd", "亲，两次输入的密码不一致哟！"]],
            selects: [{id: "groupId", url: "authUserGroupList", label: "用户组"}],
            summaryFields: [
                ["accountCode", "登录账号"],
                ["userName", "用户昵称"],
                ["userSex", "性别", {1: "男", 2: "女", 3: "其他"}],
                ["userEmail", "邮箱"],
                ["userPhone", "手机号"],
                ["groupId", "所属用户组"],
                ["state", "账户状态", {1: "正常", 2: "禁用", 3: "待审核"}],
                ["effectiveDate", "生效日期"],
                ["expiryDate", "失效日期"]
            ],
            onReady: function () {
                bindPasswordStrength();
            }
        },
        "userGroupAdd": {
            wizardId: "user_group_add_wizard",
            stepContainerId: "user_group_add_steps",
            formId: "auth_wizard_form",
            summaryId: "user_group_add_summary",
            submitUrl: "authUserGroupAdd",
            required: [["userGroupName", "用户组名称"]],
            summaryFields: [
                ["userGroupName", "用户组名称"],
                ["userGroupDesc", "用户组描述"],
                ["remarks", "备注"]
            ]
        }
    };

    /* ==================== 表格类页面 ==================== */

    /**
     * 表格类页面配置（jqGrid 明细列表 + 编辑面板）
     */
    var GRID_CONF = {
        "userModify": {
            gridId: "user_grid",
            pagerId: "user_grid_pager",
            dataUrl: "authUserPage",
            height: 280,
            rowKey: "accountId",
            queryUrl: "authUserQuery",
            queryKey: "accountId",
            formId: "user_change",
            tipId: "user_change_tip",
            submitUrl: "authUserUpdate",
            submitId: "submit",
            resetId: "reset",
            required: [["userName", "用户昵称"]],
            selects: [{id: "groupId", url: "authUserGroupList", label: "用户组"}],
            summaryIds: {total: "user_total", enabled: "user_enabled", disabled: "user_disabled"},
            // mobile:false 的列属次要信息，窄屏隐藏（实测 375px 仅约 318px 可容纳表格）
            columns: [
                // 编号为主键，筛选无实际意义，不生成筛选控件
                {name: "accountId", label: "编号", width: 70, align: "center", search: false, mobile: false},
                {name: "accountCode", label: "登录账号", width: 130},
                {name: "userName", label: "昵称", width: 120},
                // 用户组在窄屏让位给账号/昵称/状态三列（375px 实测放不下四列），仅桌面端展示
                {name: "groupName", label: "用户组", width: 110, mobile: false},
                {name: "userEmail", label: "邮箱", width: 170, mobile: false},
                {name: "userPhone", label: "手机号", width: 120, mobile: false},
                // 状态用下拉筛选，取值有限，比空白输入框语义清晰
                {
                    name: "accountState", label: "状态", width: 80, align: "center", formatter: "state",
                    stype: "select", options: "1:正常;2:禁用;3:待审核"
                },
                {name: "effectiveDate", label: "生效日期", width: 100, align: "center", mobile: false},
                {name: "expiryDate", label: "失效日期", width: 100, align: "center", mobile: false},
                {name: "op", label: "操作", width: 80, align: "center", formatter: "action", mobile: false}
            ]
        },
        "userGroupModify": {
            gridId: "user_group_grid",
            pagerId: "user_group_grid_pager",
            dataUrl: "authUserGroupPage",
            height: 260,
            rowKey: "userGroupId",
            queryUrl: "authUserGroupQuery",
            queryKey: "userGroupId",
            formId: "user_group_change",
            tipId: "user_group_change_tip",
            submitUrl: "authUserGroupModify",
            submitId: "submit",
            resetId: "reset",
            required: [["userGroupName", "用户组名称"]],
            summaryIds: {total: "user_group_total", enabled: "user_group_enabled", disabled: "user_group_disabled"},
            // mobile:false 的列属次要信息，窄屏隐藏
            columns: [
                {name: "userGroupId", label: "编号", width: 80, align: "center", search: false, mobile: false},
                {name: "userGroupName", label: "用户组名称", width: 180},
                {name: "userGroupDesc", label: "用户组描述", width: 260, mobile: false},
                {name: "memberCount", label: "成员数", width: 90, align: "center"},
                {
                    name: "userGroupState", label: "状态", width: 80, align: "center", formatter: "state",
                    stype: "select", options: "1:正常;2:禁用;3:待审核"
                },
                {name: "op", label: "操作", width: 80, align: "center", formatter: "action", mobile: false}
            ]
        }
    };

    /* ==================== 授权类页面 ==================== */

    /**
     * 授权类页面配置（主体表格 + 标签页分维度 + 左右穿梭）
     */
    var AUTH_CONF = {
        "userAuth": {
            ownerGridId: "auth_owner_grid",
            ownerPagerId: "auth_owner_pager",
            ownerListUrl: "authUserList",
            ownerKey: "accountId",
            ownerLabel: "用户",
            ownerHeight: 170,
            authPanelId: "auth_panel",
            ownerLabelId: "auth_owner_label",
            tabsId: "auth_tabs",
            authUrl: "authUserAuth",
            submitUrl: "authUserAuthorize",
            relTypes: [
                ["USER_REL_ROLE", "角色"],
                ["USER_REL_ROLE_GROUP", "角色组"]
            ]
        },
        "userGroupAuth": {
            ownerGridId: "auth_owner_grid",
            ownerPagerId: "auth_owner_pager",
            ownerListUrl: "authUserGroupList",
            ownerKey: "userGroupId",
            ownerLabel: "用户组",
            ownerHeight: 170,
            authPanelId: "auth_panel",
            ownerLabelId: "auth_owner_label",
            tabsId: "auth_tabs",
            authUrl: "authUserGroupAuth",
            submitUrl: "authUserGroupAuthorize",
            relTypes: [
                ["USER_GROUP_REL_ROLE", "角色"],
                ["USER_GROUP_REL_ROLE_GROUP", "角色组"],
                ["USER_GROUP_REL_USER", "用户"]
            ]
        }
    };

    /**
     * 页面初始化
     *
     * @param moduleType 模块类型
     */
    exports.init = function (moduleType) {
        AuthCommon.initModule(moduleType, {
            wizardConf: WIZARD_CONF,
            gridConf: GRID_CONF,
            authConf: AUTH_CONF
        });
    };

});
