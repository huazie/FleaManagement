/**
 * @Description auth-role.js 角色模块管理脚本
 *              覆盖：角色新增 / 角色变更 / 角色授权；角色组新增 / 角色组变更 / 角色组关联。
 *              页面逻辑由 auth-common.js 的模块引擎统一承载，此处只做接口注册与模块配置。
 *
 * @author huazie
 * @version v1.0.0
 * @date 2026年9月26日
 */
define(function (require, exports, module) {

    // 授权管理公共模块
    var AuthCommon = require('../auth-common');

    // 角色列表
    ReqUrlMap.put("authRoleList", "authRole!list.flea");

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

    // 角色组列表
    ReqUrlMap.put("authRoleGroupList", "authRoleGroup!list.flea");

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

    /**
     * 增改类页面配置
     */
    var FORM_CONF = {
        "roleAdd": {
            formId: "role_add",
            listUrl: "authRoleList",
            submitUrl: "authRoleAdd",
            required: [["roleName", "角色名称"]],
            selects: [{"id": "groupId", "url": "authRoleGroupList", "label": "角色组"}]
        },
        "roleModify": {
            formId: "role_change",
            listUrl: "authRoleList",
            queryUrl: "authRoleQuery",
            queryKey: "roleId",
            submitUrl: "authRoleModify",
            required: [["roleName", "角色名称"]],
            selects: [{"id": "groupId", "url": "authRoleGroupList", "label": "角色组"}]
        },
        "roleGroupAdd": {
            formId: "role_group_add",
            listUrl: "authRoleGroupList",
            submitUrl: "authRoleGroupAdd",
            required: [["roleGroupName", "角色组名称"]]
        },
        "roleGroupModify": {
            formId: "role_group_change",
            listUrl: "authRoleGroupList",
            queryUrl: "authRoleGroupQuery",
            queryKey: "roleGroupId",
            submitUrl: "authRoleGroupModify",
            required: [["roleGroupName", "角色组名称"]]
        }
    };

    /**
     * 授权类页面配置
     */
    var AUTH_CONF = {
        "roleAuth": {
            ownerListUrl: "authRoleList",
            authUrl: "authRoleAuth",
            submitUrl: "authRoleAuthorize",
            ownerKey: "roleId",
            ownerLabel: "角色",
            relTypes: [
                ["ROLE_REL_PRIVILEGE", "权限"],
                ["ROLE_REL_PRIVILEGE_GROUP", "权限组"],
                ["ROLE_REL_ROLE", "角色"]
            ]
        },
        "roleGroupRel": {
            ownerListUrl: "authRoleGroupList",
            authUrl: "authRoleGroupAuth",
            submitUrl: "authRoleGroupAuthorize",
            ownerKey: "roleGroupId",
            ownerLabel: "角色组",
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
        AuthCommon.initModule(moduleType, {formConf: FORM_CONF, authConf: AUTH_CONF});
    };

});
