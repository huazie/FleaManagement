/**
 * @Description auth-privilege.js 权限模块管理脚本
 *              覆盖：权限新增（分步向导）、权限变更（表格 + 编辑面板）、权限关联（主体表格 + 标签页 + 穿梭）；
 *              权限组新增、权限组变更、权限组关联同构。
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

    // 权限列表（授权主体表）
    ReqUrlMap.put("authPrivilegeList", "authPrivilege!list.flea");

    // 权限明细列表（表格）
    ReqUrlMap.put("authPrivilegePage", "authPrivilege!page.flea");

    // 权限明细查询
    ReqUrlMap.put("authPrivilegeQuery", "authPrivilege!query.flea");

    // 权限新增
    ReqUrlMap.put("authPrivilegeAdd", "authPrivilege!add.flea");

    // 权限变更
    ReqUrlMap.put("authPrivilegeModify", "authPrivilege!modify.flea");

    // 权限关联明细
    ReqUrlMap.put("authPrivilegeAuth", "authPrivilege!auth.flea");

    // 权限关联提交
    ReqUrlMap.put("authPrivilegeAuthorize", "authPrivilege!authorize.flea");

    // 权限组列表（授权主体表 / 下拉候选）
    ReqUrlMap.put("authPrivilegeGroupList", "authPrivilegeGroup!list.flea");

    // 权限组明细列表（表格）
    ReqUrlMap.put("authPrivilegeGroupPage", "authPrivilegeGroup!page.flea");

    // 权限组明细查询
    ReqUrlMap.put("authPrivilegeGroupQuery", "authPrivilegeGroup!query.flea");

    // 权限组新增
    ReqUrlMap.put("authPrivilegeGroupAdd", "authPrivilegeGroup!add.flea");

    // 权限组变更
    ReqUrlMap.put("authPrivilegeGroupModify", "authPrivilegeGroup!modify.flea");

    // 权限组关联明细
    ReqUrlMap.put("authPrivilegeGroupAuth", "authPrivilegeGroup!auth.flea");

    // 权限组关联提交
    ReqUrlMap.put("authPrivilegeGroupAuthorize", "authPrivilegeGroup!authorize.flea");

    /* ==================== 分步向导类页面 ==================== */

    /**
     * 向导类页面配置（分步录入 + 提交前摘要）
     */
    var WIZARD_CONF = {
        "privilegeAdd": {
            wizardId: "privilege_add_wizard",
            stepContainerId: "privilege_add_steps",
            formId: "auth_wizard_form",
            summaryId: "privilege_add_summary",
            submitUrl: "authPrivilegeAdd",
            required: [["privilegeName", "权限名称"]],
            selects: [{id: "groupId", url: "authPrivilegeGroupList", label: "权限组"}],
            summaryFields: [
                ["privilegeName", "权限名称"],
                ["privilegeDesc", "权限描述"],
                ["groupId", "所属权限组"],
                ["remarks", "备注"]
            ]
        },
        "privilegeGroupAdd": {
            wizardId: "privilege_group_add_wizard",
            stepContainerId: "privilege_group_add_steps",
            formId: "auth_wizard_form",
            summaryId: "privilege_group_add_summary",
            submitUrl: "authPrivilegeGroupAdd",
            required: [["privilegeGroupName", "权限组名称"]],
            summaryFields: [
                ["privilegeGroupName", "权限组名称"],
                ["privilegeGroupDesc", "权限组描述"],
                ["remarks", "备注"]
            ]
        }
    };

    /* ==================== 表格类页面 ==================== */

    /**
     * 表格类页面配置（jqGrid 明细列表 + 编辑面板）
     */
    var GRID_CONF = {
        "privilegeModify": {
            gridId: "privilege_grid",
            pagerId: "privilege_grid_pager",
            dataUrl: "authPrivilegePage",
            height: 300,
            rowKey: "privilegeId",
            queryUrl: "authPrivilegeQuery",
            queryKey: "privilegeId",
            formId: "privilege_change",
            tipId: "privilege_change_tip",
            submitUrl: "authPrivilegeModify",
            submitId: "submit",
            resetId: "reset",
            required: [["privilegeName", "权限名称"]],
            selects: [{id: "groupId", url: "authPrivilegeGroupList", label: "权限组"}],
            summaryIds: {total: "privilege_total", enabled: "privilege_enabled", disabled: "privilege_disabled"},
            // mobile:false 的列属次要信息，窄屏隐藏
            columns: [
                // 编号为主键，筛选无实际意义，不生成筛选控件
                {name: "privilegeId", label: "编号", width: 70, align: "center", search: false, mobile: false},
                {name: "privilegeName", label: "权限名称", width: 160},
                {name: "privilegeDesc", label: "权限描述", width: 240, mobile: false},
                {name: "groupName", label: "权限组", width: 110},
                {
                    name: "privilegeState", label: "状态", width: 80, align: "center", formatter: "state",
                    stype: "select", options: "1:正常;2:禁用;3:待审核"
                },
                {name: "op", label: "操作", width: 80, align: "center", formatter: "action", mobile: false}
            ]
        },
        "privilegeGroupModify": {
            gridId: "privilege_group_grid",
            pagerId: "privilege_group_grid_pager",
            dataUrl: "authPrivilegeGroupPage",
            height: 260,
            rowKey: "privilegeGroupId",
            queryUrl: "authPrivilegeGroupQuery",
            queryKey: "privilegeGroupId",
            formId: "privilege_group_change",
            tipId: "privilege_group_change_tip",
            submitUrl: "authPrivilegeGroupModify",
            submitId: "submit",
            resetId: "reset",
            required: [["privilegeGroupName", "权限组名称"]],
            summaryIds: {total: "privilege_group_total", enabled: "privilege_group_enabled", disabled: "privilege_group_disabled"},
            // mobile:false 的列属次要信息，窄屏隐藏
            columns: [
                {name: "privilegeGroupId", label: "编号", width: 80, align: "center", search: false, mobile: false},
                {name: "privilegeGroupName", label: "权限组名称", width: 170},
                {name: "privilegeGroupDesc", label: "权限组描述", width: 220, mobile: false},
                {name: "memberCount", label: "成员数", width: 90, align: "center"},
                {
                    name: "privilegeGroupState", label: "状态", width: 80, align: "center", formatter: "state",
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
        "privilegeRel": {
            ownerGridId: "auth_owner_grid",
            ownerPagerId: "auth_owner_pager",
            ownerListUrl: "authPrivilegeList",
            ownerKey: "privilegeId",
            ownerLabel: "权限",
            ownerHeight: 170,
            authPanelId: "auth_panel",
            ownerLabelId: "auth_owner_label",
            tabsId: "auth_tabs",
            authUrl: "authPrivilegeAuth",
            submitUrl: "authPrivilegeAuthorize",
            relTypes: [
                ["PRIVILEGE_REL_MENU", "菜单"],
                ["PRIVILEGE_REL_OPERATION", "操作"],
                ["PRIVILEGE_REL_ELEMENT", "元素"],
                ["PRIVILEGE_REL_RESOURCE", "资源"]
            ]
        },
        "privilegeGroupRel": {
            ownerGridId: "auth_owner_grid",
            ownerPagerId: "auth_owner_pager",
            ownerListUrl: "authPrivilegeGroupList",
            ownerKey: "privilegeGroupId",
            ownerLabel: "权限组",
            ownerHeight: 170,
            authPanelId: "auth_panel",
            ownerLabelId: "auth_owner_label",
            tabsId: "auth_tabs",
            authUrl: "authPrivilegeGroupAuth",
            submitUrl: "authPrivilegeGroupAuthorize",
            relTypes: [
                ["PRIVILEGE_GROUP_REL_PRIVILEGE", "权限"]
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
