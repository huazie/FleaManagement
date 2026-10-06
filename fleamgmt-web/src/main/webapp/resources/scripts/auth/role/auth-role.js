/**
 * @Description auth-role.js 角色模块管理脚本
 *              覆盖：角色新增（分步向导）、角色变更（表格 + 编辑面板）、角色授权（主体表格 + 标签页 + 穿梭）；
 *              角色组新增、角色组变更、角色组关联同构。
 *              页面逻辑由 auth-common.js 的各形态引擎统一承载，此处只做接口注册与模块配置。
 *
 * @author huazie
 * @version v1.1.0
 * @date 2026年9月29日
 */
define(function (require, exports, module) {

    // 授权管理公共模块
    var AuthCommon = require('../auth-common');

    /* ==================== 接口注册 ==================== */

    // 角色列表（授权主体表）
    ReqUrlMap.put("authRoleList", "authRole!list.flea");

    // 角色明细列表（表格）
    ReqUrlMap.put("authRolePage", "authRole!page.flea");

    // 角色明细查询
    ReqUrlMap.put("authRoleQuery", "authRole!query.flea");

    // 角色新增
    ReqUrlMap.put("authRoleAdd", "authRole!add.flea");

    // 角色变更
    ReqUrlMap.put("authRoleModify", "authRole!modify.flea");

    // 角色授权明细
    ReqUrlMap.put("authRoleAuth", "authRole!auth.flea");

    // 角色授权提交
    ReqUrlMap.put("authRoleAuthorize", "authRole!authorize.flea");

    // 角色组列表（授权主体表 / 下拉候选）
    ReqUrlMap.put("authRoleGroupList", "authRoleGroup!list.flea");

    // 角色组明细列表（表格）
    ReqUrlMap.put("authRoleGroupPage", "authRoleGroup!page.flea");

    // 角色组明细查询
    ReqUrlMap.put("authRoleGroupQuery", "authRoleGroup!query.flea");

    // 角色组新增
    ReqUrlMap.put("authRoleGroupAdd", "authRoleGroup!add.flea");

    // 角色组变更
    ReqUrlMap.put("authRoleGroupModify", "authRoleGroup!modify.flea");

    // 角色组关联明细
    ReqUrlMap.put("authRoleGroupAuth", "authRoleGroup!auth.flea");

    // 角色组关联提交
    ReqUrlMap.put("authRoleGroupAuthorize", "authRoleGroup!authorize.flea");

    /* ==================== 分步向导类页面 ==================== */

    /**
     * 向导类页面配置（分步录入 + 提交前摘要）
     */
    var WIZARD_CONF = {
        "roleAdd": {
            wizardId: "role_add_wizard",
            stepContainerId: "role_add_steps",
            formId: "auth_wizard_form",
            summaryId: "role_add_summary",
            submitUrl: "authRoleAdd",
            required: [["roleName", "角色名称"]],
            selects: [{id: "groupId", url: "authRoleGroupList", label: "角色组"}],
            summaryFields: [
                ["roleName", "角色名称"],
                ["roleDesc", "角色描述"],
                ["groupId", "所属角色组"],
                ["remarks", "备注"]
            ]
        },
        "roleGroupAdd": {
            wizardId: "role_group_add_wizard",
            stepContainerId: "role_group_add_steps",
            formId: "auth_wizard_form",
            summaryId: "role_group_add_summary",
            submitUrl: "authRoleGroupAdd",
            required: [["roleGroupName", "角色组名称"]],
            summaryFields: [
                ["roleGroupName", "角色组名称"],
                ["roleGroupDesc", "角色组描述"],
                ["remarks", "备注"]
            ]
        }
    };

    /* ==================== 表格类页面 ==================== */

    /**
     * 表格类页面配置（jqGrid 明细列表 + 编辑面板）
     */
    var GRID_CONF = {
        "roleModify": {
            gridId: "role_grid",
            pagerId: "role_grid_pager",
            dataUrl: "authRolePage",
            height: 300,
            rowKey: "roleId",
            queryUrl: "authRoleQuery",
            queryKey: "roleId",
            formId: "role_change",
            tipId: "role_change_tip",
            submitUrl: "authRoleModify",
            submitId: "submit",
            resetId: "reset",
            required: [["roleName", "角色名称"]],
            selects: [{id: "groupId", url: "authRoleGroupList", label: "角色组"}],
            summaryIds: {total: "role_total", enabled: "role_enabled", disabled: "role_disabled"},
            // mobile:false 的列属次要信息，窄屏隐藏
            columns: [
                // 编号为主键，筛选无实际意义，不生成筛选控件
                {name: "roleId", label: "编号", width: 70, align: "center", search: false, mobile: false},
                {name: "roleName", label: "角色名称", width: 150},
                {name: "roleDesc", label: "角色描述", width: 240, mobile: false},
                {name: "groupName", label: "角色组", width: 110},
                {
                    name: "roleState", label: "状态", width: 80, align: "center", formatter: "state",
                    stype: "select", options: "1:正常;2:禁用;3:待审核"
                },
                {name: "op", label: "操作", width: 80, align: "center", formatter: "action", mobile: false}
            ]
        },
        "roleGroupModify": {
            gridId: "role_group_grid",
            pagerId: "role_group_grid_pager",
            dataUrl: "authRoleGroupPage",
            height: 260,
            rowKey: "roleGroupId",
            queryUrl: "authRoleGroupQuery",
            queryKey: "roleGroupId",
            formId: "role_group_change",
            tipId: "role_group_change_tip",
            submitUrl: "authRoleGroupModify",
            submitId: "submit",
            resetId: "reset",
            required: [["roleGroupName", "角色组名称"]],
            summaryIds: {total: "role_group_total", enabled: "role_group_enabled", disabled: "role_group_disabled"},
            // mobile:false 的列属次要信息，窄屏隐藏
            columns: [
                {name: "roleGroupId", label: "编号", width: 80, align: "center", search: false, mobile: false},
                {name: "roleGroupName", label: "角色组名称", width: 180},
                {name: "roleGroupDesc", label: "角色组描述", width: 260, mobile: false},
                {name: "memberCount", label: "成员数", width: 90, align: "center"},
                {
                    name: "roleGroupState", label: "状态", width: 80, align: "center", formatter: "state",
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
        "roleAuth": {
            ownerGridId: "auth_owner_grid",
            ownerPagerId: "auth_owner_pager",
            ownerListUrl: "authRoleList",
            ownerKey: "roleId",
            ownerLabel: "角色",
            ownerHeight: 170,
            authPanelId: "auth_panel",
            ownerLabelId: "auth_owner_label",
            tabsId: "auth_tabs",
            authUrl: "authRoleAuth",
            submitUrl: "authRoleAuthorize",
            relTypes: [
                ["ROLE_REL_PRIVILEGE", "权限"],
                ["ROLE_REL_PRIVILEGE_GROUP", "权限组"],
                ["ROLE_REL_ROLE", "角色"]
            ]
        },
        "roleGroupRel": {
            ownerGridId: "auth_owner_grid",
            ownerPagerId: "auth_owner_pager",
            ownerListUrl: "authRoleGroupList",
            ownerKey: "roleGroupId",
            ownerLabel: "角色组",
            ownerHeight: 170,
            authPanelId: "auth_panel",
            ownerLabelId: "auth_owner_label",
            tabsId: "auth_tabs",
            authUrl: "authRoleGroupAuth",
            submitUrl: "authRoleGroupAuthorize",
            relTypes: [
                ["ROLE_GROUP_REL_ROLE", "角色"]
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
