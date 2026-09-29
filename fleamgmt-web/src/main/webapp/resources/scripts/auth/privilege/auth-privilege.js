/**
 * @Description auth-privilege.js 权限模块管理脚本
 *              覆盖：权限新增 / 权限变更 / 权限关联；权限组新增 / 权限组变更 / 权限组关联。
 *              页面逻辑由 auth-common.js 的模块引擎统一承载，此处只做接口注册与模块配置。
 *
 * @author huazie
 * @version v1.0.0
 * @date 2026年9月26日
 */
define(function (require, exports, module) {

    // 授权管理公共模块
    var AuthCommon = require('../auth-common');

    // 权限列表
    ReqUrlMap.put("authPrivilegeList", "authPrivilege!list.flea");

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

    // 权限组列表
    ReqUrlMap.put("authPrivilegeGroupList", "authPrivilegeGroup!list.flea");

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

    /**
     * 增改类页面配置
     */
    var FORM_CONF = {
        "privilegeAdd": {
            formId: "privilege_add",
            listUrl: "authPrivilegeList",
            submitUrl: "authPrivilegeAdd",
            required: [["privilegeName", "权限名称"]],
            selects: [{"id": "groupId", "url": "authPrivilegeGroupList", "label": "权限组"}]
        },
        "privilegeModify": {
            formId: "privilege_change",
            listUrl: "authPrivilegeList",
            queryUrl: "authPrivilegeQuery",
            queryKey: "privilegeId",
            submitUrl: "authPrivilegeModify",
            required: [["privilegeName", "权限名称"]],
            selects: [{"id": "groupId", "url": "authPrivilegeGroupList", "label": "权限组"}]
        },
        "privilegeGroupAdd": {
            formId: "privilege_group_add",
            listUrl: "authPrivilegeGroupList",
            submitUrl: "authPrivilegeGroupAdd",
            required: [["privilegeGroupName", "权限组名称"]]
        },
        "privilegeGroupModify": {
            formId: "privilege_group_change",
            listUrl: "authPrivilegeGroupList",
            queryUrl: "authPrivilegeGroupQuery",
            queryKey: "privilegeGroupId",
            submitUrl: "authPrivilegeGroupModify",
            required: [["privilegeGroupName", "权限组名称"]]
        }
    };

    /**
     * 授权类页面配置
     */
    var AUTH_CONF = {
        "privilegeRel": {
            ownerListUrl: "authPrivilegeList",
            authUrl: "authPrivilegeAuth",
            submitUrl: "authPrivilegeAuthorize",
            ownerKey: "privilegeId",
            ownerLabel: "权限",
            relTypes: [
                ["PRIVILEGE_REL_MENU", "菜单"],
                ["PRIVILEGE_REL_OPERATION", "操作"],
                ["PRIVILEGE_REL_ELEMENT", "元素"],
                ["PRIVILEGE_REL_RESOURCE", "资源"]
            ]
        },
        "privilegeGroupRel": {
            ownerListUrl: "authPrivilegeGroupList",
            authUrl: "authPrivilegeGroupAuth",
            submitUrl: "authPrivilegeGroupAuthorize",
            ownerKey: "privilegeGroupId",
            ownerLabel: "权限组",
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
        AuthCommon.initModule(moduleType, {formConf: FORM_CONF, authConf: AUTH_CONF});
    };

});
