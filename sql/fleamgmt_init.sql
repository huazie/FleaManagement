-- ============================================================
-- FleaManagement 全量初始化脚本(原生可执行)
--
-- 执行方式(任选其一):
--   mysql -uroot -p --default-character-set=utf8 < fleamgmt_init.sql
--   或在 mysql 客户端内: source fleamgmt_init.sql
--
-- 内容顺序:
--   1) 建库:fleaauth(授权)+ fleamgmtconfig(框架配置)
--   2) fleaauth 表结构(取自 flea-framework/fleaauth.sql)+ 种子数据
--   3) fleamgmtconfig 表结构(取自 flea-framework/fleaconfig.sql,
--      本工程 JPA 单元 fleaconfig 实际连接此库)
--      + Jersey 客户端注册(flea_jersey_res_client 10 条 FFS 操作,
--      resource/service 为提供方注册表,仅建表不灌数据)
--
-- 初始化后即可登录:账号 admin,密码 admin123(请首次登录后修改)
--
-- ⚠️ 警告:脚本含 DROP TABLE,重复执行会清空两个库的全部数据!
-- ⚠️ 字符集:务必以 utf8 连接执行(--default-character-set=utf8)
-- 适用:MySQL 5.7
-- ============================================================
SET NAMES utf8;
SET FOREIGN_KEY_CHECKS=0;

CREATE DATABASE IF NOT EXISTS `fleaauth` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE DATABASE IF NOT EXISTS `fleamgmtconfig` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

USE `fleaauth`;

-- 失效时间统一变量:只改下面这一行,全部种子行的 expiry_date 即刻生效
SET @EXPIRY_DATE = '2999-12-31 23:59:59';

SET FOREIGN_KEY_CHECKS=0;

-- ============================================================
-- 一、用户模块(账户、用户、用户属性、用户关联、用户组、组织、登录日志)
-- ============================================================

-- ----------------------------
-- Table structure for `flea_account`
-- ----------------------------
DROP TABLE IF EXISTS `flea_account`;
CREATE TABLE `flea_account` (
  `account_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '账户编号',
  `user_id` int(11) NOT NULL COMMENT '用户编号',
  `account_code` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '账号',
  `account_pwd` varchar(255) NOT NULL COMMENT '密码',
  `account_state` tinyint(4) NOT NULL COMMENT '账户状态（0：删除，1：正常 ，2：禁用，3：待审核）',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `effective_date` datetime NOT NULL COMMENT '生效日期',
  `expiry_date` datetime NOT NULL COMMENT '失效日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `UNIQUE_CODE_PWD` (`account_code`,`account_pwd`) USING BTREE,
  KEY `INDEX_USER_ID` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_account_attr`
-- ----------------------------
DROP TABLE IF EXISTS `flea_account_attr`;
CREATE TABLE `flea_account_attr` (
  `attr_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '属性编号',
  `account_id` int(11) NOT NULL COMMENT '账户编号',
  `attr_code` varchar(50) NOT NULL COMMENT '属性码',
  `attr_value` varchar(1024) DEFAULT NULL COMMENT '属性值',
  `attr_desc` varchar(1024) DEFAULT NULL COMMENT '属性描述',
  `state` tinyint(4) NOT NULL COMMENT '属性状态(0: 删除 1: 正常）',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `effective_date` datetime DEFAULT NULL COMMENT '生效日期',
  `expiry_date` datetime DEFAULT NULL COMMENT '失效日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`attr_id`),
  KEY `INDEX_ACCOUNT_ID` (`account_id`) USING BTREE,
  KEY `INDEX_ATTR_CODE` (`attr_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_user`
-- ----------------------------
DROP TABLE IF EXISTS `flea_user`;
CREATE TABLE `flea_user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `user_name` varchar(30) NOT NULL COMMENT '昵称',
  `user_sex` tinyint(4) DEFAULT '1' COMMENT '性别（1：男 2：女 3：其他）',
  `user_birthday` date DEFAULT NULL COMMENT '生日',
  `user_address` varchar(50) DEFAULT NULL COMMENT '住址',
  `user_email` varchar(30) DEFAULT NULL COMMENT '邮箱',
  `user_phone` varchar(11) DEFAULT NULL COMMENT '手机',
  `group_id` int(11) NOT NULL DEFAULT '-1' COMMENT '用户组编号',
  `user_state` tinyint(4) NOT NULL COMMENT '用户状态（0：删除，1：正常 ，2：禁用，3：待审核）',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `effective_date` datetime NOT NULL COMMENT '生效日期',
  `expiry_date` datetime NOT NULL COMMENT '失效日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`user_id`),
  KEY `INDEX_USER_NAME` (`user_name`) USING BTREE,
  KEY `INDEX_USER_EMAIL` (`user_email`) USING BTREE,
  KEY `INDEX_USER_PHONE` (`user_phone`) USING BTREE,
  KEY `INDEX_USER_GROUP_ID` (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_user_rel`
-- ----------------------------
DROP TABLE IF EXISTS `flea_user_rel`;
CREATE TABLE `flea_user_rel` (
  `user_rel_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户关联编号',
  `user_id` int(11) NOT NULL COMMENT '用户编号',
  `rel_id` int(11) NOT NULL COMMENT '关联编号',
  `rel_type` varchar(50) NOT NULL COMMENT '关联类型',
  `rel_state` tinyint(4) NOT NULL COMMENT '关联状态(0: 删除 1: 正常)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  `rel_ext_a` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段A',
  `rel_ext_b` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段B',
  `rel_ext_c` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段C',
  `rel_ext_x` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段X',
  `rel_ext_y` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段Y',
  `rel_ext_z` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段Z',
  PRIMARY KEY (`user_rel_id`),
  KEY `INDEX_USER_ID` (`user_id`) USING BTREE,
  KEY `INDEX_REL_ID` (`rel_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_user_attr`
-- ----------------------------
DROP TABLE IF EXISTS `flea_user_attr`;
CREATE TABLE `flea_user_attr` (
  `attr_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '属性编号',
  `user_id` int(11) NOT NULL COMMENT '用户编号',
  `attr_code` varchar(50) NOT NULL COMMENT '属性码',
  `attr_value` varchar(1024) DEFAULT NULL COMMENT '属性值',
  `attr_desc` varchar(1024) DEFAULT NULL COMMENT '属性描述',
  `state` tinyint(4) NOT NULL COMMENT '属性状态(0: 删除 1: 正常)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `effective_date` datetime DEFAULT NULL COMMENT '生效日期',
  `expiry_date` datetime DEFAULT NULL COMMENT '失效日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`attr_id`),
  KEY `INDEX_USER_ID` (`user_id`) USING BTREE,
  KEY `INDEX_ATTR_CODE` (`attr_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_user_group`
-- ----------------------------
DROP TABLE IF EXISTS `flea_user_group`;
CREATE TABLE `flea_user_group` (
  `user_group_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户组编号',
  `user_group_name` varchar(50) NOT NULL COMMENT '用户组名',
  `user_group_desc` varchar(1024) DEFAULT NULL COMMENT '用户组描述',
  `user_group_state` tinyint(4) NOT NULL COMMENT '用户组状态(0: 删除 1: 正常)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`user_group_id`),
  KEY `INDEX_USER_GROUP_NAME` (`user_group_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_user_group_rel`
-- ----------------------------
DROP TABLE IF EXISTS `flea_user_group_rel`;
CREATE TABLE `flea_user_group_rel` (
  `user_group_rel_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户组关联编号',
  `user_group_id` int(11) NOT NULL COMMENT '用户组编号',
  `rel_id` int(11) NOT NULL COMMENT '关联编号',
  `rel_type` varchar(50) NOT NULL COMMENT '关联类型',
  `rel_state` tinyint(4) NOT NULL COMMENT '关联状态(0: 删除 1: 正常)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  `rel_ext_a` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段A',
  `rel_ext_b` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段B',
  `rel_ext_c` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段C',
  `rel_ext_x` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段X',
  `rel_ext_y` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段Y',
  `rel_ext_z` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段Z',
  PRIMARY KEY (`user_group_rel_id`),
  KEY `INDEX_USER_GROUP_ID` (`user_group_id`) USING BTREE,
  KEY `INDEX_REL_ID` (`rel_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_user_org_rel`
-- ----------------------------
DROP TABLE IF EXISTS `flea_user_org_rel`;
CREATE TABLE `flea_user_org_rel` (
  `user_org_rel_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户组织关联编号',
  `user_id`    int(11) NOT NULL COMMENT '用户编号',
  `org_id`     int(11) NOT NULL COMMENT '组织编号',
  `is_primary` tinyint(4) NOT NULL COMMENT '是否主组织(0:否 1:是)',
  `rel_state`  tinyint(4) NOT NULL COMMENT '关联状态(0:删除 1:正常)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date`  datetime DEFAULT NULL COMMENT '修改日期',
  `remarks`    varchar(1024) DEFAULT NULL COMMENT '备注信息',
  `rel_ext_a`  varchar(255) DEFAULT NULL COMMENT '关联扩展字段A',
  `rel_ext_b`  varchar(255) DEFAULT NULL COMMENT '关联扩展字段B',
  `rel_ext_c`  varchar(255) DEFAULT NULL COMMENT '关联扩展字段C',
  `rel_ext_x`  varchar(255) DEFAULT NULL COMMENT '关联扩展字段X',
  `rel_ext_y`  varchar(255) DEFAULT NULL COMMENT '关联扩展字段Y',
  `rel_ext_z`  varchar(255) DEFAULT NULL COMMENT '关联扩展字段Z',
  PRIMARY KEY (`user_org_rel_id`),
  UNIQUE KEY `UNIQUE_USER_ORG` (`user_id`,`org_id`) USING BTREE,
  KEY `INDEX_USER_ID` (`user_id`) USING BTREE,
  KEY `INDEX_ORG_ID` (`org_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_organization`
-- ----------------------------
DROP TABLE IF EXISTS `flea_organization`;
CREATE TABLE `flea_organization` (
  `org_id`       int(11)     NOT NULL AUTO_INCREMENT COMMENT '组织编号',
  `org_code`     varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '组织编码',
  `org_name`     varchar(100) NOT NULL COMMENT '组织名称',
  `org_desc`     varchar(255) DEFAULT NULL COMMENT '组织描述',
  `parent_id`    int(11)     NOT NULL COMMENT '父组织编号(-1 表示根)',
  `org_level`    int(11)     NOT NULL COMMENT '组织层级(根=1)',
  `org_type`     tinyint(4)  NOT NULL COMMENT '组织类型(1:公司 2:部门 3:小组)',
  `org_state`    tinyint(4)  NOT NULL COMMENT '组织状态(0:删除 1:正常)',
  `create_date`  datetime    NOT NULL COMMENT '创建日期',
  `done_date`    datetime    DEFAULT NULL COMMENT '修改日期',
  `remarks`      varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`org_id`),
  UNIQUE KEY `UNIQUE_ORG_CODE` (`org_code`) USING BTREE,
  KEY `INDEX_PARENT_ID` (`parent_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_real_name_info`
-- ----------------------------
DROP TABLE IF EXISTS `flea_real_name_info`;
CREATE TABLE `flea_real_name_info` (
  `real_name_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '实名编号',
  `cert_type` tinyint(4) NOT NULL COMMENT '证件类型（1：身份证）',
  `cert_code` varchar(30) NOT NULL COMMENT '证件号码',
  `cert_name` varchar(20) NOT NULL COMMENT '证件名称',
  `cert_address` varchar(80) DEFAULT NULL COMMENT '证件地址',
  `real_name_state` tinyint(4) NOT NULL COMMENT '实名信息状态(0: 删除 1: 正常)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `effective_date` datetime NOT NULL COMMENT '生效日期',
  `expiry_date` datetime NOT NULL COMMENT '失效日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`real_name_id`),
  KEY `INDEX_CERT_CODE` (`cert_code`) USING BTREE,
  KEY `INDEX_CERT_NAME` (`cert_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_login_log`
-- ----------------------------
DROP TABLE IF EXISTS `flea_login_log`;
CREATE TABLE `flea_login_log` (
  `login_log_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '登录日志编号',
  `account_id` int(11) NOT NULL COMMENT '账户编号',
  `system_account_id` int(11) NOT NULL COMMENT '系统账户编号',
  `login_ip4` varchar(15) NOT NULL COMMENT 'ip4地址',
  `login_ip6` varchar(40) DEFAULT NULL COMMENT 'ip6地址',
  `login_area` varchar(15) DEFAULT NULL COMMENT '登录地区',
  `login_state` tinyint(4) NOT NULL COMMENT '登录状态（1：登录中，2：已退出）',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  `logout_time` datetime DEFAULT NULL COMMENT '退出时间',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '描述信息',
  `ext1` varchar(1024) DEFAULT NULL COMMENT '扩展字段1',
  `ext2` varchar(1024) DEFAULT NULL COMMENT '扩展字段2',
  PRIMARY KEY (`login_log_id`),
  KEY `INDEX_ACCOUNT_ID` (`account_id`) USING BTREE,
  KEY `INDEX_SYS_ACCOUNT_ID` (`system_account_id`) USING BTREE,
  KEY `INDEX_LOGIN_AREA` (`login_area`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ============================================================
-- 二、角色模块(角色、角色关联、角色组)
-- ============================================================

-- ----------------------------
-- Table structure for `flea_role`
-- ----------------------------
DROP TABLE IF EXISTS `flea_role`;
CREATE TABLE `flea_role` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色编号',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_desc` varchar(1024) DEFAULT NULL COMMENT '角色描述',
  `group_id` int(11) NOT NULL DEFAULT '-1' COMMENT '角色组编号',
  `role_state` tinyint(4) NOT NULL COMMENT '角色状态(0: 删除 1: 正常)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`role_id`),
  KEY `INDEX_ROLE_GROUP_ID` (`group_id`) USING BTREE,
  KEY `INDEX_ROLE_NAME` (`role_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_role_rel`
-- ----------------------------
DROP TABLE IF EXISTS `flea_role_rel`;
CREATE TABLE `flea_role_rel` (
  `role_rel_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色关联编号',
  `role_id` int(11) NOT NULL COMMENT '角色编号',
  `rel_id` int(11) NOT NULL COMMENT '关联编号',
  `rel_type` varchar(50) NOT NULL COMMENT '关联类型',
  `rel_state` tinyint(4) NOT NULL COMMENT '关联状态(0: 删除 1: 正常)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  `rel_ext_a` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段A',
  `rel_ext_b` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段B',
  `rel_ext_c` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段C',
  `rel_ext_x` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段X',
  `rel_ext_y` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段Y',
  `rel_ext_z` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段Z',
  PRIMARY KEY (`role_rel_id`),
  KEY `INDEX_ROLE_ID` (`role_id`) USING BTREE,
  KEY `INDEX_REL_ID` (`rel_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_role_group`
-- ----------------------------
DROP TABLE IF EXISTS `flea_role_group`;
CREATE TABLE `flea_role_group` (
  `role_group_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色组编号',
  `role_group_name` varchar(50) NOT NULL COMMENT '角色组名称',
  `role_group_desc` varchar(1024) DEFAULT NULL COMMENT '角色组描述',
  `role_group_state` tinyint(4) NOT NULL COMMENT '角色组状态(0: 删除 1: 正常)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`role_group_id`),
  KEY `INDEX_ROLE_GROUP_NAME` (`role_group_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_role_group_rel`
-- ----------------------------
DROP TABLE IF EXISTS `flea_role_group_rel`;
CREATE TABLE `flea_role_group_rel` (
  `role_group_rel_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色组关联编号',
  `role_group_id` int(11) NOT NULL COMMENT '角色组编号',
  `rel_id` int(11) NOT NULL COMMENT '关联编号',
  `rel_type` varchar(50) NOT NULL COMMENT '关联类型',
  `rel_state` tinyint(4) NOT NULL COMMENT '关联状态(0: 删除 1: 正常)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  `rel_ext_a` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段A',
  `rel_ext_b` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段B',
  `rel_ext_c` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段C',
  `rel_ext_x` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段X',
  `rel_ext_y` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段Y',
  `rel_ext_z` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段Z',
  PRIMARY KEY (`role_group_rel_id`),
  KEY `INDEX_ROLE_GROUP_ID` (`role_group_id`) USING BTREE,
  KEY `INDEX_REL_ID` (`rel_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ============================================================
-- 三、权限模块(权限、权限关联、权限组)
-- ============================================================

-- ----------------------------
-- Table structure for `flea_privilege`
-- ----------------------------
DROP TABLE IF EXISTS `flea_privilege`;
CREATE TABLE `flea_privilege` (
  `privilege_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限编号',
  `privilege_name` varchar(50) NOT NULL COMMENT '权限名称',
  `privilege_desc` varchar(1024) DEFAULT NULL COMMENT '权限描述',
  `group_id` int(11) NOT NULL DEFAULT '-1' COMMENT '权限组编号',
  `privilege_state` tinyint(4) NOT NULL COMMENT '权限状态(0: 删除 1: 正常)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`privilege_id`),
  KEY `INDEX_GROUP_ID` (`group_id`) USING BTREE,
  KEY `INDEX_PRIVILEGE_NAME` (`privilege_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_privilege_rel`
-- ----------------------------
DROP TABLE IF EXISTS `flea_privilege_rel`;
CREATE TABLE `flea_privilege_rel` (
  `privilege_rel_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限关联编号',
  `privilege_id` int(11) NOT NULL COMMENT '权限编号',
  `rel_id` int(11) NOT NULL COMMENT '关联编号',
  `rel_type` varchar(50) NOT NULL COMMENT '关联类型',
  `rel_state` tinyint(4) NOT NULL COMMENT '关联状态(0: 删除 1: 正常)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  `rel_ext_a` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段A',
  `rel_ext_b` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段B',
  `rel_ext_c` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段C',
  `rel_ext_x` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段X',
  `rel_ext_y` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段Y',
  `rel_ext_z` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段Z',
  PRIMARY KEY (`privilege_rel_id`),
  KEY `INDEX_PRIVILEGE_ID` (`privilege_id`) USING BTREE,
  KEY `INDEX_REL_ID` (`rel_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_privilege_group`
-- ----------------------------
DROP TABLE IF EXISTS `flea_privilege_group`;
CREATE TABLE `flea_privilege_group` (
  `privilege_group_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限组编号',
  `privilege_group_name` varchar(50) NOT NULL COMMENT '权限组名称',
  `privilege_group_desc` varchar(1024) DEFAULT NULL COMMENT '权限组描述',
  `privilege_group_state` tinyint(4) NOT NULL COMMENT '权限组状态(0: 删除 1: 正常)',
  `is_main` tinyint(4) NOT NULL COMMENT '是否为主权限组（0：不是 1：是）',
  `function_type` varchar(25) DEFAULT NULL COMMENT '功能类型(菜单、操作、元素、资源)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`privilege_group_id`),
  KEY `INDEX_PRIVILEGE_GROUP_NAME` (`privilege_group_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_privilege_group_rel`
-- ----------------------------
DROP TABLE IF EXISTS `flea_privilege_group_rel`;
CREATE TABLE `flea_privilege_group_rel` (
  `privilege_group_rel_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限组关联编号',
  `privilege_group_id` int(11) NOT NULL COMMENT '权限组编号',
  `rel_id` int(11) NOT NULL COMMENT '关联编号',
  `rel_type` varchar(50) NOT NULL COMMENT '关联类型',
  `rel_state` tinyint(4) NOT NULL COMMENT '关联状态(0: 删除 1: 正常)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  `rel_ext_a` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段A',
  `rel_ext_b` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段B',
  `rel_ext_c` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段C',
  `rel_ext_x` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段X',
  `rel_ext_y` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段Y',
  `rel_ext_z` varchar(1024) DEFAULT NULL COMMENT '关联扩展字段Z',
  PRIMARY KEY (`privilege_group_rel_id`),
  KEY `INDEX_PRIVILEGE_GROUP_ID` (`privilege_group_id`) USING BTREE,
  KEY `INDEX_REL_ID` (`rel_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ============================================================
-- 四、功能模块(菜单、功能属性、操作、资源、元素)
-- ============================================================

-- ----------------------------
-- Table structure for `flea_menu`
-- ----------------------------
DROP TABLE IF EXISTS `flea_menu`;
CREATE TABLE `flea_menu` (
  `menu_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '菜单编号',
  `menu_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '菜单编码',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `menu_icon` varchar(30) NOT NULL COMMENT '菜单FontAwesome小图标',
  `menu_sort` tinyint(4) NOT NULL COMMENT '菜单展示顺序(同一个父菜单下)',
  `menu_view` varchar(255) DEFAULT NULL COMMENT '菜单对应页面（非叶子菜单的可以为空）',
  `menu_level` tinyint(4) NOT NULL COMMENT '菜单层级（1：一级菜单 2；二级菜单 3：三级菜单 4：四级菜单）',
  `menu_state` tinyint(4) NOT NULL COMMENT '菜单状态（0:下线，1: 在用 ）',
  `parent_id` int(11) NOT NULL COMMENT '父菜单编号',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `effective_date` datetime NOT NULL COMMENT '生效日期',
  `expiry_date` datetime NOT NULL COMMENT '失效日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '菜单描述',
  PRIMARY KEY (`menu_id`),
  UNIQUE KEY `UNIQUE_MENU_CODE` (`menu_code`) USING BTREE,
  KEY `INDEX_MENU_NAME` (`menu_name`) USING BTREE,
  KEY `INDEX_MENU_PARENT_ID` (`parent_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_function_attr`
-- ----------------------------
DROP TABLE IF EXISTS `flea_function_attr`;
CREATE TABLE `flea_function_attr` (
  `attr_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '属性编号',
  `function_id` int(11) NOT NULL COMMENT '功能编号',
  `function_type` varchar(25) NOT NULL COMMENT '功能类型(菜单、操作、元素、资源) ',
  `attr_code` varchar(50) NOT NULL COMMENT '属性码',
  `attr_value` varchar(1024) DEFAULT NULL COMMENT '属性值',
  `attr_desc` varchar(1024) DEFAULT NULL COMMENT '属性描述',
  `state` tinyint(4) NOT NULL COMMENT '属性状态(0: 删除 1: 正常）',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `effective_date` datetime DEFAULT NULL COMMENT '生效日期',
  `expiry_date` datetime DEFAULT NULL COMMENT '失效日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`attr_id`),
  KEY `INDEX_FUNCTION_ID` (`function_id`) USING BTREE,
  KEY `INDEX_ATTR_CODE` (`attr_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_function_attr_menu`
-- ----------------------------
DROP TABLE IF EXISTS `flea_function_attr_menu`;
CREATE TABLE `flea_function_attr_menu` (
  `attr_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '属性编号',
  `function_id` int(11) NOT NULL COMMENT '功能编号',
  `function_type` varchar(25) NOT NULL COMMENT '功能类型(菜单、操作、元素、资源) ',
  `attr_code` varchar(50) NOT NULL COMMENT '属性码',
  `attr_value` varchar(1024) DEFAULT NULL COMMENT '属性值',
  `attr_desc` varchar(1024) DEFAULT NULL COMMENT '属性描述',
  `state` tinyint(4) NOT NULL COMMENT '属性状态(0: 删除 1: 正常）',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `effective_date` datetime DEFAULT NULL COMMENT '生效日期',
  `expiry_date` datetime DEFAULT NULL COMMENT '失效日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`attr_id`),
  KEY `INDEX_FUNCTION_ID` (`function_id`) USING BTREE,
  KEY `INDEX_ATTR_CODE` (`attr_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_function_attr_operation`
-- ----------------------------
DROP TABLE IF EXISTS `flea_function_attr_operation`;
CREATE TABLE `flea_function_attr_operation` (
  `attr_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '属性编号',
  `function_id` int(11) NOT NULL COMMENT '功能编号',
  `function_type` varchar(25) NOT NULL COMMENT '功能类型(菜单、操作、元素、资源) ',
  `attr_code` varchar(50) NOT NULL COMMENT '属性码',
  `attr_value` varchar(1024) DEFAULT NULL COMMENT '属性值',
  `attr_desc` varchar(1024) DEFAULT NULL COMMENT '属性描述',
  `state` tinyint(4) NOT NULL COMMENT '属性状态(0: 删除 1: 正常）',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `effective_date` datetime DEFAULT NULL COMMENT '生效日期',
  `expiry_date` datetime DEFAULT NULL COMMENT '失效日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`attr_id`),
  KEY `INDEX_FUNCTION_ID` (`function_id`) USING BTREE,
  KEY `INDEX_ATTR_CODE` (`attr_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_function_attr_resource`
-- ----------------------------
DROP TABLE IF EXISTS `flea_function_attr_resource`;
CREATE TABLE `flea_function_attr_resource` (
  `attr_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '属性编号',
  `function_id` int(11) NOT NULL COMMENT '功能编号',
  `function_type` varchar(25) NOT NULL COMMENT '功能类型(菜单、操作、元素、资源) ',
  `attr_code` varchar(50) NOT NULL COMMENT '属性码',
  `attr_value` varchar(1024) DEFAULT NULL COMMENT '属性值',
  `attr_desc` varchar(1024) DEFAULT NULL COMMENT '属性描述',
  `state` tinyint(4) NOT NULL COMMENT '属性状态(0: 删除 1: 正常）',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `effective_date` datetime DEFAULT NULL COMMENT '生效日期',
  `expiry_date` datetime DEFAULT NULL COMMENT '失效日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`attr_id`),
  KEY `INDEX_FUNCTION_ID` (`function_id`) USING BTREE,
  KEY `INDEX_ATTR_CODE` (`attr_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_function_attr_element`
-- ----------------------------
DROP TABLE IF EXISTS `flea_function_attr_element`;
CREATE TABLE `flea_function_attr_element` (
  `attr_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '属性编号',
  `function_id` int(11) NOT NULL COMMENT '功能编号',
  `function_type` varchar(25) NOT NULL COMMENT '功能类型(菜单、操作、元素、资源) ',
  `attr_code` varchar(50) NOT NULL COMMENT '属性码',
  `attr_value` varchar(1024) DEFAULT NULL COMMENT '属性值',
  `attr_desc` varchar(1024) DEFAULT NULL COMMENT '属性描述',
  `state` tinyint(4) NOT NULL COMMENT '属性状态(0: 删除 1: 正常）',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `effective_date` datetime DEFAULT NULL COMMENT '生效日期',
  `expiry_date` datetime DEFAULT NULL COMMENT '失效日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`attr_id`),
  KEY `INDEX_FUNCTION_ID` (`function_id`) USING BTREE,
  KEY `INDEX_ATTR_CODE` (`attr_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_operation`
-- ----------------------------
DROP TABLE IF EXISTS `flea_operation`;
CREATE TABLE `flea_operation` (
  `operation_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '操作编号',
  `operation_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '操作编码',
  `operation_name` varchar(50) NOT NULL COMMENT '操作名称',
  `operation_desc` varchar(255) DEFAULT NULL COMMENT '操作描述',
  `operation_state` tinyint(4) NOT NULL COMMENT '操作状态(0: 删除 1: 正常)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `effective_date` datetime NOT NULL COMMENT '生效日期',
  `expiry_date` datetime NOT NULL COMMENT '失效日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`operation_id`),
  UNIQUE KEY `UNIQUE_OPERATION_CODE` (`operation_code`) USING BTREE,
  KEY `INDEX_OPERATION_NAME` (`operation_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_resource`
-- ----------------------------
DROP TABLE IF EXISTS `flea_resource`;
CREATE TABLE `flea_resource` (
  `resource_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '资源编号',
  `resource_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '资源编码',
  `resource_name` varchar(50) NOT NULL COMMENT '资源名称',
  `resource_desc` varchar(255) DEFAULT NULL COMMENT '资源描述',
  `resource_state` tinyint(4) NOT NULL COMMENT '资源状态(0: 删除 1: 正常)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `effective_date` datetime NOT NULL COMMENT '生效日期',
  `expiry_date` datetime NOT NULL COMMENT '失效日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`resource_id`),
  UNIQUE KEY `UNIQUE_RESOURCE_CODE` (`resource_code`) USING BTREE,
  KEY `INDEX_RESOURCE_NAME` (`resource_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_element`
-- ----------------------------
DROP TABLE IF EXISTS `flea_element`;
CREATE TABLE `flea_element` (
  `element_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '元素编号',
  `element_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '元素编码',
  `element_name` varchar(50) NOT NULL COMMENT '元素名称',
  `element_desc` varchar(255) DEFAULT NULL COMMENT '元素描述',
  `element_type` tinyint(4) NOT NULL COMMENT '元素类型',
  `element_content` varchar(2000) DEFAULT NULL COMMENT '元素内容',
  `element_state` tinyint(4) NOT NULL COMMENT '元素状态(0: 删除 1: 正常)',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `effective_date` datetime NOT NULL COMMENT '生效日期',
  `expiry_date` datetime NOT NULL COMMENT '失效日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`element_id`),
  UNIQUE KEY `UNIQUE_ELEMENT_CODE` (`element_code`) USING BTREE,
  KEY `INDEX_ELEMENT_NAME` (`element_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ============================================================
-- 五、公共模块(审计日志、ID 生成器水位)
-- ============================================================

-- ----------------------------
-- Table structure for `flea_auth_audit_log`
-- ----------------------------
DROP TABLE IF EXISTS `flea_auth_audit_log`;
CREATE TABLE `flea_auth_audit_log` (
  `audit_id`   int(11) NOT NULL AUTO_INCREMENT COMMENT '审计编号',
  `user_id`    int(11) DEFAULT NULL COMMENT '用户编号',
  `account_id` int(11) DEFAULT NULL COMMENT '账户编号',
  `op_type`    varchar(50) NOT NULL COMMENT '操作类型(LOGIN/AUTH/LOGOUT/CHG_PWD/GRANT...)',
  `op_target`  varchar(100) DEFAULT NULL COMMENT '操作对象',
  `op_desc`    varchar(512) DEFAULT NULL COMMENT '操作描述',
  `op_result`  tinyint(4) NOT NULL COMMENT '操作结果(0:失败 1:成功)',
  `ip_addr`    varchar(45) DEFAULT NULL COMMENT '客户端IP(支持IPv6)',
  `request_id` varchar(64) DEFAULT NULL COMMENT '请求追踪编号',
  `create_date` datetime NOT NULL COMMENT '创建日期',
  `remarks`    varchar(1024) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`audit_id`),
  KEY `INDEX_USER_ID` (`user_id`) USING BTREE,
  KEY `INDEX_CREATE_DATE` (`create_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `flea_id_generator`
-- ----------------------------
DROP TABLE IF EXISTS `flea_id_generator`;
CREATE TABLE `flea_id_generator` (
  `id_generator_key` varchar(50) NOT NULL COMMENT 'ID产生器的键【即主键生成策略的键值名称】',
  `id_generator_value` bigint(20) NOT NULL COMMENT 'ID产生器的值【即主键生成的值】',
  UNIQUE KEY `UNIQUE_KEY` (`id_generator_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ============================================================
-- 种子数据(按模块分组)
-- ============================================================

-- ============================================================
-- 一、用户模块(账户、用户、用户属性、用户关联、用户组、组织、登录日志)
-- ============================================================

INSERT INTO `flea_account` VALUES (1000,1000,'SYS_FLEA_FRAMEWORK','$2a$10$TvBdOUkyhjMRtcEa8r33vOTRPeSkV5VcyrRaZGsaXUQwayrm38I2K',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea Framework】');
INSERT INTO `flea_account` VALUES (1001,1001,'SYS_FLEA_MGMT','$2a$10$gtErfxlF0G.Qs4BFhUiBTuOy38Sh7DK/EtpBvFFS94zqlryKiTDBO',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea Management】');
INSERT INTO `flea_account` VALUES (1002,1002,'SYS_FLEA_FS','$2a$10$YwJ61RjMHU9M.feilEdhp.jxiGPR.X1OSpHdraWWra0vF2KkDc82W',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea File Server】');
INSERT INTO `flea_account` VALUES (10000,10000,'admin','$2a$10$5jmzH4xQG2GZ9RuNBGVEGOaF8xUNf5.1g/bRL.cbDbaE6K5NhJAAG',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'默认管理员账号,初始密码 admin123');

INSERT INTO `flea_account_attr` VALUES (1,1000,'ACCOUNT_TYPE','SYSTEM','系统账户',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea Framework】');
INSERT INTO `flea_account_attr` VALUES (2,1001,'ACCOUNT_TYPE','SYSTEM','系统账户',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea Management】');
INSERT INTO `flea_account_attr` VALUES (3,1002,'ACCOUNT_TYPE','SYSTEM','系统账户',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea File Server】');
INSERT INTO `flea_account_attr` VALUES (4,10000,'ACCOUNT_TYPE','OPERATOR','操作账户',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'默认管理员账号数据');

INSERT INTO `flea_user` VALUES (1000,'Flea框架',NULL,NULL,NULL,NULL,NULL,1000,1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea Framework】');
INSERT INTO `flea_user` VALUES (1001,'Flea管家',NULL,NULL,NULL,NULL,NULL,1000,1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea Management】');
INSERT INTO `flea_user` VALUES (1002,'Flea文件服务器',NULL,NULL,NULL,NULL,NULL,1000,1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea File Server】');
INSERT INTO `flea_user` VALUES (10000,'管理员',1,NULL,'','','',1001,1,NOW(),NULL,NOW(),@EXPIRY_DATE,'默认管理员账号数据');

INSERT INTO `flea_user_rel` VALUES (1,10000,1000,'USER_REL_ROLE',1,NOW(),NULL,'用户【管理员】绑定【超级管理员】角色',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_user_rel` VALUES (2,10000,1000,'USER_REL_ROLE_GROUP',1,NOW(),NULL,'用户【管理员】绑定【管理员】角色组',NULL,NULL,NULL,NULL,NULL,NULL);

INSERT INTO `flea_user_attr` VALUES (1,1000,'USER_TYPE','SYSTEM','系统用户',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea Framework】');
INSERT INTO `flea_user_attr` VALUES (2,1001,'USER_TYPE','SYSTEM','系统用户',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea Management】');
INSERT INTO `flea_user_attr` VALUES (3,1002,'USER_TYPE','SYSTEM','系统用户',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea File Server】');
INSERT INTO `flea_user_attr` VALUES (4,10000,'USER_TYPE','OPERATOR','操作用户',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'默认管理员账号数据');

INSERT INTO `flea_user_group` VALUES (1000,'系统用户','实际的系统用户归属的用户组',1,NOW(),NULL,'该用户组与实际的系统用户相关');
INSERT INTO `flea_user_group` VALUES (1001,'操作用户','实际的操作用户归属的用户组',1,NOW(),NULL,'该用户组与实际的操作用户相关');
INSERT INTO `flea_user_group` VALUES (1002,'FleaFS授权用户','实际接入FleaFS的用户归属的用户组',1,NOW(),NULL,'该用户组与实际接入FleaFS的用户相关');

INSERT INTO `flea_user_group_rel` VALUES (1,1000,1000,'USER_GROUP_REL_ROLE_GROUP',1,NOW(),NULL,'用户组【系统用户】绑定【管理员】角色组',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_user_group_rel` VALUES (2,1000,1000,'USER_GROUP_REL_USER',1,NOW(),NULL,'用户组【系统用户】关联用户【Flea框架】',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_user_group_rel` VALUES (3,1000,1001,'USER_GROUP_REL_USER',1,NOW(),NULL,'用户组【系统用户】关联用户【Flea管家】',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_user_group_rel` VALUES (4,1000,1002,'USER_GROUP_REL_USER',1,NOW(),NULL,'用户组【系统用户】关联用户【Flea文件服务器】',NULL,NULL,NULL,NULL,NULL,NULL);

-- ============================================================
-- 二、角色模块(角色、角色关联、角色组)
-- ============================================================

INSERT INTO `flea_role` VALUES (1000,'超级管理员','最高系统权限拥有者角色',1000,1,NOW(),NULL,'超级管理员拥有系统最高权限');
INSERT INTO `flea_role` VALUES (1001,'业务管理员','与业务处理相关的角色',-1,1,NOW(),NULL,'业务管理员用于与业务处理相关的权限');
INSERT INTO `flea_role` VALUES (1002,'授权管理员','与授权管理相关的角色',-1,1,NOW(),NULL,'授权管理员拥有与授权管理相关的权限，包含用户模块管理、角色模块管理、权限模块管理和功能模块管理');
INSERT INTO `flea_role` VALUES (1003,'用户模块管理员','与用户模块管理相关的角色',-1,1,NOW(),NULL,'用户模块管理员拥有与用户模块管理相关的权限，包含用户管理、用户组管理');
INSERT INTO `flea_role` VALUES (1004,'角色模块管理员','与角色模块管理相关的角色',-1,1,NOW(),NULL,'角色模块管理员拥有与角色模块管理相关的权限，包含角色管理、角色组管理');
INSERT INTO `flea_role` VALUES (1005,'权限模块管理员','与权限模块管理相关的角色',-1,1,NOW(),NULL,'权限模块管理员拥有与权限模块管理相关的权限，包含权限管理、权限组管理');
INSERT INTO `flea_role` VALUES (1006,'功能模块管理员','与功能模块管理相关的角色',-1,1,NOW(),NULL,'功能模块管理员拥有与功能模块管理相关的权限，包含菜单管理、操作管理、元素管理和资源管理');
INSERT INTO `flea_role` VALUES (1007,'游客','系统观光者的角色',-1,1,NOW(),NULL,'游客只拥有有限的系统浏览权限');
INSERT INTO `flea_role` VALUES (1008,'青铜主','一星等级的跳主角色',-1,1,NOW(),NULL,'青铜主拥有一星等级的权限，跳主角色中最低权限拥有者');
INSERT INTO `flea_role` VALUES (1009,'白银主','二星等级的跳主角色',-1,1,NOW(),NULL,'白银主拥有二星等级的权限');
INSERT INTO `flea_role` VALUES (1010,'黄金主','三星等级的跳主角色',-1,1,NOW(),NULL,'黄金主拥有三星等级的权限');
INSERT INTO `flea_role` VALUES (1011,'砖石主','四星等级的跳主角色',-1,1,NOW(),NULL,'砖石主拥有四星等级的权限');
INSERT INTO `flea_role` VALUES (1012,'皇冠主','五星等级的跳主角色',-1,1,NOW(),NULL,'皇冠主拥有五星等级的权限，跳主角色中最高权限拥有者');
INSERT INTO `flea_role` VALUES (1013,'青铜客','一星等级的跳客角色',-1,1,NOW(),NULL,'青铜客拥有一星等级的权限，跳客角色中最低权限拥有者');
INSERT INTO `flea_role` VALUES (1014,'白银客','二星等级的跳客角色',-1,1,NOW(),NULL,'白银客拥有二星等级的权限');
INSERT INTO `flea_role` VALUES (1015,'黄金客','三星等级的跳客角色',-1,1,NOW(),NULL,'黄金客拥有三星等级的权限');
INSERT INTO `flea_role` VALUES (1016,'砖石客','四星等级的跳客角色',-1,1,NOW(),NULL,'砖石客拥有四星等级的权限');
INSERT INTO `flea_role` VALUES (1017,'皇冠客','五星等级的跳客角色',-1,1,NOW(),NULL,'皇冠客拥有五星等级的权限，跳客角色中最高权限拥有者');
INSERT INTO `flea_role` VALUES (1018,'FleaFS接入员','用于接入Flea文件服务器的角色',-1,1,NOW(),NULL,'FleaFS接入员，拥有调用Flea文件服务器提供的资源的权限');

INSERT INTO `flea_role_rel` VALUES (1,1000,1000,'ROLE_REL_PRIVILEGE_GROUP',1,NOW(),NULL,'【超级管理员】角色绑定【菜单访问】权限组',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_role_rel` VALUES (2,1000,1000,'ROLE_REL_PRIVILEGE',1,NOW(),NULL,'【超级管理员】角色绑定【访问《控制台》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_role_rel` VALUES (3,1000,1001,'ROLE_REL_PRIVILEGE_GROUP',1,NOW(),NULL,'【超级管理员】角色绑定【FleaFS资源调用】权限组',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_role_rel` VALUES (4,1000,1002,'ROLE_REL_PRIVILEGE_GROUP',1,NOW(),NULL,'【超级管理员】角色绑定【《Ace管理》菜单访问】权限组',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_role_rel` VALUES (5,1000,1003,'ROLE_REL_PRIVILEGE_GROUP',1,NOW(),NULL,'【超级管理员】角色绑定【资源调用】权限组',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_role_rel` VALUES (6,1017,1004,'ROLE_REL_PRIVILEGE_GROUP',1,NOW(),NULL,'【皇冠客】角色绑定【元素展示】权限组',NULL,NULL,NULL,NULL,NULL,NULL);

INSERT INTO `flea_role_group` VALUES (1000,'管理员','各种管理员角色归属的角色组',1,NOW(),NULL,'管理员角色组包含各种管理员角色');
INSERT INTO `flea_role_group` VALUES (1001,'跳主','各星级跳主角色归属的角色组',1,NOW(),NULL,'【跳主】角色组包含各星级跳主角色');
INSERT INTO `flea_role_group` VALUES (1002,'跳客','各星级跳客角色归属的角色组',1,NOW(),NULL,'【跳客】角色组包含各星级跳客角色');

INSERT INTO `flea_role_group_rel` VALUES (1,1000,1000,'ROLE_GROUP_REL_ROLE',1,NOW(),NULL,'【管理员】角色组关联【超级管理员】角色',NULL,NULL,NULL,NULL,NULL,NULL);

-- ============================================================
-- 三、权限模块(权限、权限关联、权限组)
-- ============================================================

INSERT INTO `flea_privilege` VALUES (1000,'访问《控制台》菜单','拥有可以访问《控制台》菜单的权限',-1,1,NOW(),NULL,'【访问《控制台》菜单】权限对应【控制台】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1001,'访问《授权管理》菜单','拥有可以访问《授权管理》菜单的权限',1000,1,NOW(),NULL,'【访问《授权管理》菜单】权限对应【授权管理】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1002,'访问《用户模块管理》菜单','拥有可以访问《用户模块管理》菜单的权限',1000,1,NOW(),NULL,'【访问《用户模块管理》菜单】权限对应【用户模块管理】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1003,'访问《角色模块管理》菜单','拥有可以访问《角色模块管理》菜单的权限',1000,1,NOW(),NULL,'【访问《角色模块管理》菜单】权限对应【角色模块管理】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1004,'访问《权限模块管理》菜单','拥有可以访问《权限模块管理》菜单的权限',1000,1,NOW(),NULL,'【访问《权限模块管理》菜单】权限对应【权限模块管理】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1005,'访问《功能模块管理》菜单','拥有可以访问《功能模块管理》菜单的权限',1000,1,NOW(),NULL,'【访问《功能模块管理》菜单】权限对应【功能模块管理】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1006,'访问《用户管理》菜单','拥有可以访问《用户管理》菜单的权限',1000,1,NOW(),NULL,'【访问《用户管理》菜单】权限对应【用户管理】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1007,'访问《用户组管理》菜单','拥有可以访问《用户组管理》菜单的权限',1000,1,NOW(),NULL,'【访问《用户组管理》菜单】权限对应【用户组管理】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1008,'访问《角色管理》菜单','拥有可以访问《角色管理》菜单的权限',1000,1,NOW(),NULL,'【访问《角色管理》菜单】权限对应【角色管理】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1009,'访问《角色组管理》菜单','拥有可以访问《角色组管理》菜单的权限',1000,1,NOW(),NULL,'【访问《角色组管理》菜单】权限对应【角色组管理】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1010,'访问《权限管理》菜单','拥有可以访问《权限管理》菜单的权限',1000,1,NOW(),NULL,'【访问《权限管理》菜单】权限对应【权限管理】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1011,'访问《权限组管理》菜单','拥有可以访问《权限组管理》菜单的权限',1000,1,NOW(),NULL,'【访问《权限组管理》菜单】权限对应【权限组管理】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1012,'访问《菜单管理》菜单','拥有可以访问《菜单管理》菜单的权限',1000,1,NOW(),NULL,'【访问《菜单管理》菜单】权限对应【菜单管理】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1013,'访问《操作管理》菜单','拥有可以访问《操作管理》菜单的权限',1000,1,NOW(),NULL,'【访问《操作管理》菜单】权限对应【操作管理】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1014,'访问《元素管理》菜单','拥有可以访问《元素管理》菜单的权限',1000,1,NOW(),NULL,'【访问《元素管理》菜单】权限对应【元素管理】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1015,'访问《资源管理》菜单','拥有可以访问《资源管理》菜单的权限',1000,1,NOW(),NULL,'【访问《资源管理》菜单】权限对应【资源管理】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1016,'访问《用户注册》菜单','拥有可以访问《用户注册》菜单的权限',1000,1,NOW(),NULL,'【访问《用户注册》菜单】权限对应【用户注册】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1017,'访问《用户变更》菜单','拥有可以访问《用户变更》菜单的权限',1000,1,NOW(),NULL,'【访问《用户变更》菜单】权限对应【用户变更】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1018,'访问《用户授权》菜单','拥有可以访问《用户授权》菜单的权限',1000,1,NOW(),NULL,'【访问《用户授权》菜单】权限对应【用户授权】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1019,'访问《用户组新增》菜单','拥有可以访问《用户组新增》菜单的权限',1000,1,NOW(),NULL,'【访问《用户组新增》菜单】权限对应【用户组新增】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1020,'访问《用户组变更》菜单','拥有可以访问《用户组变更》菜单的权限',1000,1,NOW(),NULL,'【访问《用户组变更》菜单】权限对应【用户组变更】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1021,'访问《用户组授权》菜单','拥有可以访问《用户组授权》菜单的权限',1000,1,NOW(),NULL,'【访问《用户组授权》菜单】权限对应【用户组授权】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1022,'访问《角色新增》菜单','拥有可以访问《角色新增》菜单的权限',1000,1,NOW(),NULL,'【访问《角色新增》菜单】权限对应【角色新增】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1023,'访问《角色变更》菜单','拥有可以访问《角色变更》菜单的权限',1000,1,NOW(),NULL,'【访问《角色变更》菜单】权限对应【角色变更】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1024,'访问《角色授权》菜单','拥有可以访问《角色授权》菜单的权限',1000,1,NOW(),NULL,'【访问《角色授权》菜单】权限对应【角色授权】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1025,'访问《角色组新增》菜单','拥有可以访问《角色组新增》菜单的权限',1000,1,NOW(),NULL,'【访问《角色组新增》菜单】权限对应【角色组新增】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1026,'访问《角色组变更》菜单','拥有可以访问《角色组变更》菜单的权限',1000,1,NOW(),NULL,'【访问《角色组变更》菜单】权限对应【角色组变更】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1027,'访问《角色组关联》菜单','拥有可以访问《角色组关联》菜单的权限',1000,1,NOW(),NULL,'【访问《角色组关联》菜单】权限对应【角色组关联】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1028,'访问《权限新增》菜单','拥有可以访问《权限新增》菜单的权限',1000,1,NOW(),NULL,'【访问《权限新增》菜单】权限对应【权限新增】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1029,'访问《权限变更》菜单','拥有可以访问《权限变更》菜单的权限',1000,1,NOW(),NULL,'【访问《权限变更》菜单】权限对应【权限变更】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1030,'访问《权限关联》菜单','拥有可以访问《权限关联》菜单的权限',1000,1,NOW(),NULL,'【访问《权限关联》菜单】权限对应【权限关联】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1031,'访问《权限组新增》菜单','拥有可以访问《权限组新增》菜单的权限',1000,1,NOW(),NULL,'【访问《权限组新增》菜单】权限对应【权限组新增】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1032,'访问《权限组变更》菜单','拥有可以访问《权限组变更》菜单的权限',1000,1,NOW(),NULL,'【访问《权限组变更》菜单】权限对应【权限组变更】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1033,'访问《权限组关联》菜单','拥有可以访问《权限组关联》菜单的权限',1000,1,NOW(),NULL,'【访问《权限组关联》菜单】权限对应【权限组关联】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1034,'访问《菜单新增》菜单','拥有可以访问《菜单新增》菜单的权限',1000,1,NOW(),NULL,'【访问《菜单新增》菜单】权限对应【菜单新增】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1035,'访问《菜单变更》菜单','拥有可以访问《菜单变更》菜单的权限',1000,1,NOW(),NULL,'【访问《菜单变更》菜单】权限对应【菜单变更】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1036,'访问《操作新增》菜单','拥有可以访问《操作新增》菜单的权限',1000,1,NOW(),NULL,'【访问《操作新增》菜单】权限对应【操作新增】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1037,'访问《操作变更》菜单','拥有可以访问《操作变更》菜单的权限',1000,1,NOW(),NULL,'【访问《操作变更》菜单】权限对应【操作变更】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1038,'访问《元素新增》菜单','拥有可以访问《元素新增》菜单的权限',1000,1,NOW(),NULL,'【访问《元素新增》菜单】权限对应【元素新增】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1039,'访问《元素变更》菜单','拥有可以访问《元素变更》菜单的权限',1000,1,NOW(),NULL,'【访问《元素变更》菜单】权限对应【元素变更】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1040,'访问《资源新增》菜单','拥有可以访问《资源新增》菜单的权限',1000,1,NOW(),NULL,'【访问《资源新增》菜单】权限对应【资源新增】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1041,'访问《资源变更》菜单','拥有可以访问《资源变更》菜单的权限',1000,1,NOW(),NULL,'【访问《资源变更》菜单】权限对应【资源变更】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1042,'展示《待修改元素》元素','拥有可以展示《待修改元素》元素的权限',1004,1,NOW(),NULL,'【展示《待修改元素》元素】权限对应【待修改元素】元素，新增元素时自动生成');
INSERT INTO `flea_privilege` VALUES (1043,'展示《测试元素》元素','拥有可以展示《测试元素》元素的权限',1004,1,NOW(),NULL,'【展示《测试元素》元素】权限对应【测试元素】元素，新增元素时自动生成');
INSERT INTO `flea_privilege` VALUES (1044,'执行《文件上传》操作','拥有可以执行《文件上传》操作的权限',1005,1,NOW(),NULL,'【执行《文件上传》操作】权限对应【文件上传】操作，新增操作时自动生成');
INSERT INTO `flea_privilege` VALUES (1045,'执行《文件下载》操作','拥有可以执行《文件下载》操作的权限',1005,1,NOW(),NULL,'【执行《文件下载》操作】权限对应【文件下载】操作，新增操作时自动生成');
INSERT INTO `flea_privilege` VALUES (1046,'执行《文件更新》操作','拥有可以执行《文件更新》操作的权限',1005,1,NOW(),NULL,'【执行《文件更新》操作】权限对应【文件更新】操作，新增操作时自动生成');
INSERT INTO `flea_privilege` VALUES (1047,'执行《文件删除》操作','拥有可以执行《文件删除》操作的权限',1005,1,NOW(),NULL,'【执行《文件删除》操作】权限对应【文件删除】操作，新增操作时自动生成');
INSERT INTO `flea_privilege` VALUES (1048,'执行《文件搜索》操作','拥有可以执行《文件搜索》操作的权限',1005,1,NOW(),NULL,'【执行《文件搜索》操作】权限对应【文件搜索】操作，新增操作时自动生成');
INSERT INTO `flea_privilege` VALUES (1049,'执行《版本管理》操作','拥有可以执行《版本管理》操作的权限',1005,1,NOW(),NULL,'【执行《版本管理》操作】权限对应【版本管理】操作，新增操作时自动生成');
INSERT INTO `flea_privilege` VALUES (1050,'访问《Ace组件》菜单','拥有可以访问《Ace组件》菜单的权限',1002,1,NOW(),NULL,'【访问《Ace组件》菜单】权限对应【Ace组件】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1051,'访问《Ace控制台》菜单','拥有可以访问《Ace控制台》菜单的权限',1002,1,NOW(),NULL,'【访问《Ace控制台》菜单】权限对应【Ace控制台】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1052,'访问《常规组件》菜单','拥有可以访问《常规组件》菜单的权限',1002,1,NOW(),NULL,'【访问《常规组件》菜单】权限对应【常规组件】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1053,'访问《表格》菜单','拥有可以访问《表格》菜单的权限',1002,1,NOW(),NULL,'【访问《表格》菜单】权限对应【表格】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1054,'访问《表单》菜单','拥有可以访问《表单》菜单的权限',1002,1,NOW(),NULL,'【访问《表单》菜单】权限对应【表单】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1055,'访问《UI元素》菜单','拥有可以访问《UI元素》菜单的权限',1002,1,NOW(),NULL,'【访问《UI元素》菜单】权限对应【UI元素】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1056,'访问《综合示例》菜单','拥有可以访问《综合示例》菜单的权限',1002,1,NOW(),NULL,'【访问《综合示例》菜单】权限对应【综合示例】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1057,'访问《其他》菜单','拥有可以访问《其他》菜单的权限',1002,1,NOW(),NULL,'【访问《其他》菜单】权限对应【其他】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1058,'访问《画廊》菜单','拥有可以访问《画廊》菜单的权限',1002,1,NOW(),NULL,'【访问《画廊》菜单】权限对应【画廊】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1059,'访问《组件》菜单','拥有可以访问《组件》菜单的权限',1002,1,NOW(),NULL,'【访问《组件》菜单】权限对应【组件】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1060,'访问《排版》菜单','拥有可以访问《排版》菜单的权限',1002,1,NOW(),NULL,'【访问《排版》菜单】权限对应【排版】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1061,'访问《栅格》菜单','拥有可以访问《栅格》菜单的权限',1002,1,NOW(),NULL,'【访问《栅格》菜单】权限对应【栅格】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1062,'访问《日历》菜单','拥有可以访问《日历》菜单的权限',1002,1,NOW(),NULL,'【访问《日历》菜单】权限对应【日历】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1063,'访问《普通表格》菜单','拥有可以访问《普通表格》菜单的权限',1002,1,NOW(),NULL,'【访问《普通表格》菜单】权限对应【普通表格】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1064,'访问《数据表格》菜单','拥有可以访问《数据表格》菜单的权限',1002,1,NOW(),NULL,'【访问《数据表格》菜单】权限对应【数据表格】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1065,'访问《表单元素》菜单','拥有可以访问《表单元素》菜单的权限',1002,1,NOW(),NULL,'【访问《表单元素》菜单】权限对应【表单元素】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1066,'访问《富文本》菜单','拥有可以访问《富文本》菜单的权限',1002,1,NOW(),NULL,'【访问《富文本》菜单】权限对应【富文本】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1067,'访问《表单向导》菜单','拥有可以访问《表单向导》菜单的权限',1002,1,NOW(),NULL,'【访问《表单向导》菜单】权限对应【表单向导】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1068,'访问《文件上传》菜单','拥有可以访问《文件上传》菜单的权限',1002,1,NOW(),NULL,'【访问《文件上传》菜单】权限对应【文件上传】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1069,'访问《按钮》菜单','拥有可以访问《按钮》菜单的权限',1002,1,NOW(),NULL,'【访问《按钮》菜单】权限对应【按钮】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1070,'访问《页面元素》菜单','拥有可以访问《页面元素》菜单的权限',1002,1,NOW(),NULL,'【访问《页面元素》菜单】权限对应【页面元素】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1071,'访问《JQuery UI》菜单','拥有可以访问《JQuery UI》菜单的权限',1002,1,NOW(),NULL,'【访问《JQuery UI》菜单】权限对应【JQuery UI】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1072,'访问《树形视图》菜单','拥有可以访问《树形视图》菜单的权限',1002,1,NOW(),NULL,'【访问《树形视图》菜单】权限对应【树形视图】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1073,'访问《嵌套列表》菜单','拥有可以访问《嵌套列表》菜单的权限',1002,1,NOW(),NULL,'【访问《嵌套列表》菜单】权限对应【嵌套列表】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1074,'访问《时间轴》菜单','拥有可以访问《时间轴》菜单的权限',1002,1,NOW(),NULL,'【访问《时间轴》菜单】权限对应【时间轴】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1075,'访问《个人资料》菜单','拥有可以访问《个人资料》菜单的权限',1002,1,NOW(),NULL,'【访问《个人资料》菜单】权限对应【个人资料】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1076,'访问《价格表》菜单','拥有可以访问《价格表》菜单的权限',1002,1,NOW(),NULL,'【访问《价格表》菜单】权限对应【价格表】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1077,'访问《发票》菜单','拥有可以访问《发票》菜单的权限',1002,1,NOW(),NULL,'【访问《发票》菜单】权限对应【发票】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1078,'访问《收件箱》菜单','拥有可以访问《收件箱》菜单的权限',1002,1,NOW(),NULL,'【访问《收件箱》菜单】权限对应【收件箱】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1079,'访问《常见问题》菜单','拥有可以访问《常见问题》菜单的权限',1002,1,NOW(),NULL,'【访问《常见问题》菜单】权限对应【常见问题】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1080,'访问《404错误页》菜单','拥有可以访问《404错误页》菜单的权限',1002,1,NOW(),NULL,'【访问《404错误页》菜单】权限对应【404错误页】菜单，新增菜单时自动生成');
INSERT INTO `flea_privilege` VALUES (1081,'访问《500错误页》菜单','拥有可以访问《500错误页》菜单的权限',1002,1,NOW(),NULL,'【访问《500错误页》菜单】权限对应【500错误页】菜单，新增菜单时自动生成');

INSERT INTO `flea_privilege_rel` VALUES (1,1000,1000,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【控制台】菜单绑定【访问《控制台》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (2,1001,1001,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【授权管理】菜单绑定【访问《授权管理》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (3,1002,1002,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【用户模块管理】菜单绑定【访问《用户模块管理》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (4,1003,1003,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【角色模块管理】菜单绑定【访问《角色模块管理》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (5,1004,1004,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【权限模块管理】菜单绑定【访问《权限模块管理》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (6,1005,1005,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【功能模块管理】菜单绑定【访问《功能模块管理》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (7,1006,1006,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【用户管理】菜单绑定【访问《用户管理》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (8,1007,1007,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【用户组管理】菜单绑定【访问《用户组管理》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (9,1008,1008,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【角色管理】菜单绑定【访问《角色管理》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (10,1009,1009,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【角色组管理】菜单绑定【访问《角色组管理》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (11,1010,1010,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【权限管理】菜单绑定【访问《权限管理》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (12,1011,1011,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【权限组管理】菜单绑定【访问《权限组管理》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (13,1012,1012,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【菜单管理】菜单绑定【访问《菜单管理》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (14,1013,1013,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【操作管理】菜单绑定【访问《操作管理》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (15,1014,1014,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【元素管理】菜单绑定【访问《元素管理》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (16,1015,1015,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【资源管理】菜单绑定【访问《资源管理》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (17,1016,1016,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【用户注册】菜单绑定【访问《用户注册》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (18,1017,1017,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【用户变更】菜单绑定【访问《用户变更》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (19,1018,1018,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【用户授权】菜单绑定【访问《用户授权》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (20,1019,1019,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【用户组新增】菜单绑定【访问《用户组新增》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (21,1020,1020,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【用户组变更】菜单绑定【访问《用户组变更》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (22,1021,1021,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【用户组授权】菜单绑定【访问《用户组授权》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (23,1022,1022,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【角色新增】菜单绑定【访问《角色新增》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (24,1023,1023,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【角色变更】菜单绑定【访问《角色变更》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (25,1024,1024,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【角色授权】菜单绑定【访问《角色授权》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (26,1025,1025,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【角色组新增】菜单绑定【访问《角色组新增》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (27,1026,1026,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【角色组变更】菜单绑定【访问《角色组变更》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (28,1027,1027,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【角色组关联】菜单绑定【访问《角色组关联》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (29,1028,1028,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【权限新增】菜单绑定【访问《权限新增》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (30,1029,1029,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【权限变更】菜单绑定【访问《权限变更》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (31,1030,1030,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【权限关联】菜单绑定【访问《权限关联》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (32,1031,1031,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【权限组新增】菜单绑定【访问《权限组新增》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (33,1032,1032,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【权限组变更】菜单绑定【访问《权限组变更》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (34,1033,1033,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【权限组关联】菜单绑定【访问《权限组关联》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (35,1034,1034,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【菜单新增】菜单绑定【访问《菜单新增》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (36,1035,1035,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【菜单变更】菜单绑定【访问《菜单变更》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (37,1036,1036,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【操作新增】菜单绑定【访问《操作新增》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (38,1037,1037,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【操作变更】菜单绑定【访问《操作变更》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (39,1038,1038,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【元素新增】菜单绑定【访问《元素新增》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (40,1039,1039,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【元素变更】菜单绑定【访问《元素变更》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (41,1040,1040,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【资源新增】菜单绑定【访问《资源新增》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (42,1041,1041,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【资源变更】菜单绑定【访问《资源变更》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (43,1042,1001,'PRIVILEGE_REL_ELEMENT',1,NOW(),NULL,'【待修改元素】元素绑定【展示《待修改元素》元素】权限，新增元素时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (44,1043,1002,'PRIVILEGE_REL_ELEMENT',1,NOW(),NULL,'【测试元素】元素绑定【展示《测试元素》元素】权限，新增元素时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (45,1044,1001,'PRIVILEGE_REL_OPERATION',1,NOW(),NULL,'【文件上传】操作绑定【执行《文件上传》操作】权限，新增操作时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (46,1045,1002,'PRIVILEGE_REL_OPERATION',1,NOW(),NULL,'【文件下载】操作绑定【执行《文件下载》操作】权限，新增操作时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (47,1046,1003,'PRIVILEGE_REL_OPERATION',1,NOW(),NULL,'【文件更新】操作绑定【执行《文件更新》操作】权限，新增操作时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (48,1047,1004,'PRIVILEGE_REL_OPERATION',1,NOW(),NULL,'【文件删除】操作绑定【执行《文件删除》操作】权限，新增操作时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (49,1048,1005,'PRIVILEGE_REL_OPERATION',1,NOW(),NULL,'【文件搜索】操作绑定【执行《文件搜索》操作】权限，新增操作时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (50,1049,1006,'PRIVILEGE_REL_OPERATION',1,NOW(),NULL,'【版本管理】操作绑定【执行《版本管理》操作】权限，新增操作时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (51,1050,1042,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【Ace组件】菜单绑定【访问《Ace组件》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (52,1051,1043,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【Ace控制台】菜单绑定【访问《Ace控制台》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (53,1052,1044,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【常规组件】菜单绑定【访问《常规组件》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (54,1053,1045,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【表格】菜单绑定【访问《表格》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (55,1054,1046,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【表单】菜单绑定【访问《表单》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (56,1055,1047,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【UI元素】菜单绑定【访问《UI元素》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (57,1056,1048,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【综合示例】菜单绑定【访问《综合示例》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (58,1057,1049,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【其他】菜单绑定【访问《其他》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (59,1058,1050,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【画廊】菜单绑定【访问《画廊》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (60,1059,1051,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【组件】菜单绑定【访问《组件》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (61,1060,1052,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【排版】菜单绑定【访问《排版》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (62,1061,1053,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【栅格】菜单绑定【访问《栅格》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (63,1062,1054,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【日历】菜单绑定【访问《日历》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (64,1063,1055,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【普通表格】菜单绑定【访问《普通表格》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (65,1064,1056,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【数据表格】菜单绑定【访问《数据表格》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (66,1065,1057,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【表单元素】菜单绑定【访问《表单元素》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (67,1066,1058,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【富文本】菜单绑定【访问《富文本》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (68,1067,1059,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【表单向导】菜单绑定【访问《表单向导》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (69,1068,1060,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【文件上传】菜单绑定【访问《文件上传》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (70,1069,1061,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【按钮】菜单绑定【访问《按钮》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (71,1070,1062,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【页面元素】菜单绑定【访问《页面元素》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (72,1071,1063,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【JQuery UI】菜单绑定【访问《JQuery UI》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (73,1072,1064,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【树形视图】菜单绑定【访问《树形视图》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (74,1073,1065,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【嵌套列表】菜单绑定【访问《嵌套列表》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (75,1074,1066,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【时间轴】菜单绑定【访问《时间轴》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (76,1075,1067,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【个人资料】菜单绑定【访问《个人资料》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (77,1076,1068,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【价格表】菜单绑定【访问《价格表》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (78,1077,1069,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【发票】菜单绑定【访问《发票》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (79,1078,1070,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【收件箱】菜单绑定【访问《收件箱》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (80,1079,1071,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【常见问题】菜单绑定【访问《常见问题》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (81,1080,1072,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【404错误页】菜单绑定【访问《404错误页》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_rel` VALUES (82,1081,1073,'PRIVILEGE_REL_MENU',1,NOW(),NULL,'【500错误页】菜单绑定【访问《500错误页》菜单】权限，新增菜单时自动生成',NULL,NULL,NULL,NULL,NULL,NULL);

INSERT INTO `flea_privilege_group` VALUES (1000,'菜单访问','与【菜单访问】相关的权限归属的权限组',1,1,'MENU',NOW(),NULL,'该权限组包含了【菜单访问】相关的权限');
INSERT INTO `flea_privilege_group` VALUES (1001,'FleaFS资源调用','与【FleaFS资源调用】相关的权限归属的权限组',1,0,'RESOURCE',NOW(),NULL,'该权限组包含了【FleaFS资源调用】相关的权限，FleaFS后续新增资源调用权限也需要关联该权限组');
INSERT INTO `flea_privilege_group` VALUES (1002,'《Ace管理》菜单访问','与【《Ace管理》菜单访问】相关的权限归属的权限组',1,0,'MENU',NOW(),NULL,'该权限组包含了【《Ace管理》菜单访问】相关的权限');
INSERT INTO `flea_privilege_group` VALUES (1003,'资源调用','与【资源调用】相关的权限归属的权限组',1,1,'RESOURCE',NOW(),NULL,'该权限组包含了【资源调用】相关的权限');
INSERT INTO `flea_privilege_group` VALUES (1004,'元素展示','与【元素展示】相关的权限归属的权限组',1,1,'ELEMENT',NOW(),NULL,'该权限组包含了【元素展示】相关的权限');
INSERT INTO `flea_privilege_group` VALUES (1005,'操作执行','与【操作执行】相关的权限归属的权限组',1,1,'OPERATION',NOW(),NULL,'该权限组包含了【操作执行】相关的权限');

INSERT INTO `flea_privilege_group_rel` VALUES (1,1000,1000,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《控制台》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (2,1000,1001,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《授权管理》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (3,1000,1002,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《用户模块管理》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (4,1000,1003,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《角色模块管理》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (5,1000,1004,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《权限模块管理》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (6,1000,1005,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《功能模块管理》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (7,1000,1006,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《用户管理》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (8,1000,1007,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《用户组管理》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (9,1000,1008,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《角色管理》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (10,1000,1009,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《角色组管理》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (11,1000,1010,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《权限管理》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (12,1000,1011,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《权限组管理》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (13,1000,1012,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《菜单管理》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (14,1000,1013,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《操作管理》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (15,1000,1014,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《元素管理》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (16,1000,1015,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《资源管理》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (17,1000,1016,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《用户注册》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (18,1000,1017,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《用户变更》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (19,1000,1018,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《用户授权》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (20,1000,1019,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《用户组新增》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (21,1000,1020,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《用户组变更》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (22,1000,1021,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《用户组授权》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (23,1000,1022,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《角色新增》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (24,1000,1023,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《角色变更》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (25,1000,1024,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《角色授权》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (26,1000,1025,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《角色组新增》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (27,1000,1026,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《角色组变更》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (28,1000,1027,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《角色组关联》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (29,1000,1028,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《权限新增》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (30,1000,1029,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《权限变更》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (31,1000,1030,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《权限关联》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (32,1000,1031,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《权限组新增》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (33,1000,1032,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《权限组变更》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (34,1000,1033,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《权限组关联》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (35,1000,1034,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《菜单新增》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (36,1000,1035,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《菜单变更》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (37,1000,1036,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《操作新增》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (38,1000,1037,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《操作变更》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (39,1000,1038,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《元素新增》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (40,1000,1039,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《元素变更》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (41,1000,1040,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《资源新增》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (42,1000,1041,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【菜单访问】权限组关联【访问《资源变更》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (43,1004,1042,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【元素展示】权限组关联【展示《待修改元素》元素】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (44,1004,1043,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【元素展示】权限组关联【展示《测试元素》元素】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (45,1005,1044,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【操作执行】权限组关联【执行《文件上传》操作】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (46,1005,1045,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【操作执行】权限组关联【执行《文件下载》操作】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (47,1005,1046,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【操作执行】权限组关联【执行《文件更新》操作】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (48,1005,1047,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【操作执行】权限组关联【执行《文件删除》操作】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (49,1005,1048,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【操作执行】权限组关联【执行《文件搜索》操作】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (50,1005,1049,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【操作执行】权限组关联【执行《版本管理》操作】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (51,1002,1050,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《Ace组件》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (52,1002,1051,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《Ace控制台》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (53,1002,1052,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《常规组件》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (54,1002,1053,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《表格》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (55,1002,1054,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《表单》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (56,1002,1055,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《UI元素》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (57,1002,1056,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《综合示例》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (58,1002,1057,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《其他》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (59,1002,1058,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《画廊》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (60,1002,1059,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《组件》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (61,1002,1060,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《排版》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (62,1002,1061,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《栅格》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (63,1002,1062,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《日历》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (64,1002,1063,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《普通表格》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (65,1002,1064,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《数据表格》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (66,1002,1065,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《表单元素》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (67,1002,1066,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《富文本》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (68,1002,1067,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《表单向导》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (69,1002,1068,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《文件上传》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (70,1002,1069,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《按钮》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (71,1002,1070,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《页面元素》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (72,1002,1071,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《JQuery UI》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (73,1002,1072,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《树形视图》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (74,1002,1073,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《嵌套列表》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (75,1002,1074,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《时间轴》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (76,1002,1075,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《个人资料》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (77,1002,1076,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《价格表》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (78,1002,1077,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《发票》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (79,1002,1078,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《收件箱》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (80,1002,1079,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《常见问题》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (81,1002,1080,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《404错误页》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO `flea_privilege_group_rel` VALUES (82,1002,1081,'PRIVILEGE_GROUP_REL_PRIVILEGE',1,NOW(),NULL,'【《Ace管理》菜单访问】权限组关联【访问《500错误页》菜单】权限',NULL,NULL,NULL,NULL,NULL,NULL);

-- ============================================================
-- 四、功能模块(菜单、功能属性、操作、资源、元素)
-- ============================================================

INSERT INTO `flea_menu` VALUES (1000,'console','控制台','dashboard',1,'mgmt/console.html',1,1,-1,NOW(),NULL,NOW(),@EXPIRY_DATE,'控制台，展示收藏夹，快捷菜单入口');
INSERT INTO `flea_menu` VALUES (1001,'auth_mgmt','授权管理','font',2,NULL,1,1,-1,NOW(),NULL,NOW(),@EXPIRY_DATE,'授权管理，包含用户模块管理、角色模块管理、权限模块管理和功能模块管理');
INSERT INTO `flea_menu` VALUES (1002,'user_module_mgmt','用户模块管理','user-circle',1,NULL,2,1,1001,NOW(),NULL,NOW(),@EXPIRY_DATE,'用户模块管理，包含用户管理、用户组管理');
INSERT INTO `flea_menu` VALUES (1003,'role_module_mgmt','角色模块管理','user-secret',2,NULL,2,1,1001,NOW(),NULL,NOW(),@EXPIRY_DATE,'角色模块管理，包含角色管理、角色组管理');
INSERT INTO `flea_menu` VALUES (1004,'privilege_module_mgmt','权限模块管理','lock',3,NULL,2,1,1001,NOW(),NULL,NOW(),@EXPIRY_DATE,'权限模块管理，包含权限管理、权限组管理');
INSERT INTO `flea_menu` VALUES (1005,'function_module_mgmt','功能模块管理','gears',4,NULL,2,1,1001,NOW(),NULL,NOW(),@EXPIRY_DATE,'功能模块管理，包含菜单管理、操作管理、元素管理和资源管理');
INSERT INTO `flea_menu` VALUES (1006,'user_mgmt','用户管理','user',1,NULL,3,1,1002,NOW(),NULL,NOW(),@EXPIRY_DATE,'用户管理，包含用户注册、用户变更、用户授权');
INSERT INTO `flea_menu` VALUES (1007,'user_group_mgmt','用户组管理','users',2,NULL,3,1,1002,NOW(),NULL,NOW(),@EXPIRY_DATE,'用户组管理，包含用户组新增、用户组变更、用户组授权');
INSERT INTO `flea_menu` VALUES (1008,'role_mgmt','角色管理','user',1,NULL,3,1,1003,NOW(),NULL,NOW(),@EXPIRY_DATE,'角色管理，包含角色新增、角色变更、角色授权');
INSERT INTO `flea_menu` VALUES (1009,'role_group_mgmt','角色组管理','users',2,NULL,3,1,1003,NOW(),NULL,NOW(),@EXPIRY_DATE,'角色组管理，包含角色组新增、角色组变更、角色组关联');
INSERT INTO `flea_menu` VALUES (1010,'privilege_mgmt','权限管理','tag',1,NULL,3,1,1004,NOW(),NULL,NOW(),@EXPIRY_DATE,'权限管理，包含权限新增、权限变更、权限关联');
INSERT INTO `flea_menu` VALUES (1011,'privilege_group_mgmt','权限组管理','tags',2,NULL,3,1,1004,NOW(),NULL,NOW(),@EXPIRY_DATE,'权限组管理，包含权限组新增、权限组变更、权限组关联');
INSERT INTO `flea_menu` VALUES (1012,'menu_mgmt','菜单管理','list-alt',1,NULL,3,1,1005,NOW(),NULL,NOW(),@EXPIRY_DATE,'菜单管理，包含菜单新增、菜单变更');
INSERT INTO `flea_menu` VALUES (1013,'operation_mgmt','操作管理','wrench',2,NULL,3,1,1005,NOW(),NULL,NOW(),@EXPIRY_DATE,'操作管理，包含操作新增、操作变更');
INSERT INTO `flea_menu` VALUES (1014,'element_mgmt','元素管理','code',3,NULL,3,1,1005,NOW(),NULL,NOW(),@EXPIRY_DATE,'元素管理，包含元素新增、元素变更');
INSERT INTO `flea_menu` VALUES (1015,'resource_mgmt','资源管理','link',4,NULL,3,1,1005,NOW(),NULL,NOW(),@EXPIRY_DATE,'资源管理，包含资源新增、资源变更');
INSERT INTO `flea_menu` VALUES (1016,'user_register','用户注册','user-plus',1,'auth/user-module/user/userRegister.html',4,1,1006,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于注册新用户，包含系统用户和操作用户');
INSERT INTO `flea_menu` VALUES (1017,'user_modify','用户变更','edit',2,'auth/user-module/user/userModify.html',4,1,1006,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于修改用户信息');
INSERT INTO `flea_menu` VALUES (1018,'user_auth','用户授权','arrows',3,'auth/user-module/user/userAuth.html',4,1,1006,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于新增或修改用户关联角色（角色组）数据');
INSERT INTO `flea_menu` VALUES (1019,'user_group_add','用户组新增','plus-circle',1,'auth/user-module/user-group/userGroupAdd.html',4,1,1007,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于新增用户组数据');
INSERT INTO `flea_menu` VALUES (1020,'user_group_modify','用户组变更','edit',2,'auth/user-module/user-group/userGroupModify.html',4,1,1007,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于修改用户组数据');
INSERT INTO `flea_menu` VALUES (1021,'user_group_auth','用户组授权','arrows',3,'auth/user-module/user-group/userGroupAuth.html',4,1,1007,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于新增或修改用户组关联角色（角色组）数据');
INSERT INTO `flea_menu` VALUES (1022,'role_add','角色新增','plus-circle',1,'auth/role-module/role/roleAdd.html',4,1,1008,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于新增角色数据');
INSERT INTO `flea_menu` VALUES (1023,'role_modify','角色变更','edit',2,'auth/role-module/role/roleModify.html',4,1,1008,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于修改角色数据');
INSERT INTO `flea_menu` VALUES (1024,'role_auth','角色授权','arrows',3,'auth/role-module/role/roleAuth.html',4,1,1008,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于新增或修改角色关联权限（权限组）数据');
INSERT INTO `flea_menu` VALUES (1025,'role_group_add','角色组新增','plus-circle',1,'auth/role-module/role-group/roleGroupAdd.html',4,1,1009,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于新增角色组数据');
INSERT INTO `flea_menu` VALUES (1026,'role_group_modify','角色组变更','edit',2,'auth/role-module/role-group/roleGroupModify.html',4,1,1009,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于修改角色组数据');
INSERT INTO `flea_menu` VALUES (1027,'role_group_rel','角色组关联','exchange',3,'auth/role-module/role-group/roleGroupRel.html',4,1,1009,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于新增或修改角色组关联角色数据');
INSERT INTO `flea_menu` VALUES (1028,'privilege_add','权限新增','plus-circle',1,'auth/privilege-module/privilege/privilegeAdd.html',4,1,1010,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于新增权限数据');
INSERT INTO `flea_menu` VALUES (1029,'privilege_modify','权限变更','edit',2,'auth/privilege-module/privilege/privilegeModify.html',4,1,1010,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于修改权限数据');
INSERT INTO `flea_menu` VALUES (1030,'privilege_rel','权限关联','exchange',3,'auth/privilege-module/privilege/privilegeRel.html',4,1,1010,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于新增或修改权限关联功能数据');
INSERT INTO `flea_menu` VALUES (1031,'privilege_group_add','权限组新增','plus-circle',1,'auth/privilege-module/privilege-group/privilegeGroupAdd.html',4,1,1011,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于新增权限组数据');
INSERT INTO `flea_menu` VALUES (1032,'privilege_group_modify','权限组变更','edit',2,'auth/privilege-module/privilege-group/privilegeGroupModify.html',4,1,1011,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于修改权限组数据');
INSERT INTO `flea_menu` VALUES (1033,'privilege_group_rel','权限组关联','exchange',3,'auth/privilege-module/privilege-group/privilegeGroupRel.html',4,1,1011,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于新增或修改权限组关联权限数据');
INSERT INTO `flea_menu` VALUES (1034,'menu_add','菜单新增','plus-circle',1,'auth/function-module/menu/menuAdd.html',4,1,1012,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于新增菜单相关数据，包含菜单数据、菜单扩展属性、访问菜单权限、访问菜单权限关联和菜单访问权限组关联');
INSERT INTO `flea_menu` VALUES (1035,'menu_modify','菜单变更','edit',2,'auth/function-module/menu/menuModify.html',4,1,1012,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于修改菜单数据、菜单扩展属性');
INSERT INTO `flea_menu` VALUES (1036,'operation_add','操作新增','plus-circle',1,'auth/function-module/operation/operationAdd.html',4,1,1013,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于新增操作相关数据，包含操作数据、操作扩展属性、执行操作权限、执行操作权限关联和操作执行权限组关联');
INSERT INTO `flea_menu` VALUES (1037,'operation_modify','操作变更','edit',2,'auth/function-module/operation/operationModify.html',4,1,1013,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于修改操作数据、操作扩展属性');
INSERT INTO `flea_menu` VALUES (1038,'element_add','元素新增','plus-circle',1,'auth/function-module/element/elementAdd.html',4,1,1014,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于新增元素相关数据，包含元素数据、元素扩展属性、展示元素权限、展示元素权限关联和元素展示权限组关联');
INSERT INTO `flea_menu` VALUES (1039,'element_modify','元素变更','edit',2,'auth/function-module/element/elementModify.html',4,1,1014,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于修改元素数据、元素扩展属性');
INSERT INTO `flea_menu` VALUES (1040,'resource_add','资源新增','plus-circle',1,'auth/function-module/resource/resourceAdd.html',4,1,1015,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于新增资源相关数据，包含资源数据、资源扩展属性、调用资源权限、调用资源权限关联、资源调用权限组关联');
INSERT INTO `flea_menu` VALUES (1041,'resource_modify','资源变更','edit',2,'auth/function-module/resource/resourceModify.html',4,1,1015,NOW(),NULL,NOW(),@EXPIRY_DATE,'该菜单用于修改资源数据、资源扩展属性');
INSERT INTO `flea_menu` VALUES (1042,'ace_mgmt','Ace组件','th-large',3,NULL,1,1,-1,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace Admin 模板组件展示菜单');
INSERT INTO `flea_menu` VALUES (1043,'ace_console','Ace控制台','dashboard',1,'ace/aceConsole.html',2,1,1042,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 控制台页面');
INSERT INTO `flea_menu` VALUES (1044,'ace_general','常规组件','th',2,NULL,2,1,1042,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 常规组件：画廊/组件/排版/栅格/日历');
INSERT INTO `flea_menu` VALUES (1045,'ace_table','表格','table',3,NULL,2,1,1042,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 表格组件');
INSERT INTO `flea_menu` VALUES (1046,'ace_form','表单','wpforms',4,NULL,2,1,1042,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 表单组件');
INSERT INTO `flea_menu` VALUES (1047,'ace_ui','UI元素','cubes',5,NULL,2,1,1042,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace UI 元素组件');
INSERT INTO `flea_menu` VALUES (1048,'ace_more','综合示例','files-o',6,NULL,2,1,1042,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 综合示例页面');
INSERT INTO `flea_menu` VALUES (1049,'ace_other','其他','ellipsis-h',7,NULL,2,1,1042,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 其他页面');
INSERT INTO `flea_menu` VALUES (1050,'ace_gallery','画廊','picture-o',1,'ace/general/gallery.html',3,1,1044,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 画廊');
INSERT INTO `flea_menu` VALUES (1051,'ace_widgets','组件','th-large',2,'ace/general/widgets.html',3,1,1044,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 组件展示');
INSERT INTO `flea_menu` VALUES (1052,'ace_typography','排版','font',3,'ace/general/typography.html',3,1,1044,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 排版示例');
INSERT INTO `flea_menu` VALUES (1053,'ace_grid','栅格','th',4,'ace/general/grid.html',3,1,1044,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 栅格系统');
INSERT INTO `flea_menu` VALUES (1054,'ace_calendar','日历','calendar',5,'ace/general/calendar.html',3,1,1044,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 日历组件');
INSERT INTO `flea_menu` VALUES (1055,'ace_tables','普通表格','table',1,'ace/table/tables.html',3,1,1045,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 普通表格');
INSERT INTO `flea_menu` VALUES (1056,'ace_jqgrid','数据表格','th',2,'ace/table/jqgrid.html',3,1,1045,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace jqGrid 数据表格');
INSERT INTO `flea_menu` VALUES (1057,'ace_form_elements','表单元素','list-alt',1,'ace/form/form-elements.html',3,1,1046,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 表单元素');
INSERT INTO `flea_menu` VALUES (1058,'ace_wysiwyg','富文本','align-justify',2,'ace/form/wysiwyg.html',3,1,1046,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 富文本编辑器');
INSERT INTO `flea_menu` VALUES (1059,'ace_form_wizard','表单向导','magic',3,'ace/form/form-wizard.html',3,1,1046,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 表单向导');
INSERT INTO `flea_menu` VALUES (1060,'ace_dropzone','文件上传','upload',4,'ace/form/dropzone.html',3,1,1046,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace Dropzone 文件上传');
INSERT INTO `flea_menu` VALUES (1061,'ace_buttons','按钮','square',1,'ace/ui/buttons.html',3,1,1047,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 按钮');
INSERT INTO `flea_menu` VALUES (1062,'ace_elements','页面元素','cubes',2,'ace/ui/elements.html',3,1,1047,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 页面元素');
INSERT INTO `flea_menu` VALUES (1063,'ace_jquery_ui','JQuery UI','clone',3,'ace/ui/jquery-ui.html',3,1,1047,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace JQuery UI');
INSERT INTO `flea_menu` VALUES (1064,'ace_treeview','树形视图','sitemap',4,'ace/ui/treeview.html',3,1,1047,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 树形视图');
INSERT INTO `flea_menu` VALUES (1065,'ace_nestable','嵌套列表','bars',5,'ace/ui/nestable-list.html',3,1,1047,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 嵌套列表');
INSERT INTO `flea_menu` VALUES (1066,'ace_timeline','时间轴','clock-o',1,'ace/more/timeline.html',3,1,1048,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 时间轴');
INSERT INTO `flea_menu` VALUES (1067,'ace_profile','个人资料','user',2,'ace/more/profile.html',3,1,1048,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 个人资料');
INSERT INTO `flea_menu` VALUES (1068,'ace_pricing','价格表','money',3,'ace/more/pricing.html',3,1,1048,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 价格表');
INSERT INTO `flea_menu` VALUES (1069,'ace_invoice','发票','file-text',4,'ace/more/invoice.html',3,1,1048,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 发票');
INSERT INTO `flea_menu` VALUES (1070,'ace_inbox','收件箱','inbox',5,'ace/more/inbox.html',3,1,1048,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 收件箱');
INSERT INTO `flea_menu` VALUES (1071,'ace_faq','常见问题','question',1,'ace/other/faq.html',3,1,1049,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 常见问题');
INSERT INTO `flea_menu` VALUES (1072,'ace_error_404','404错误页','exclamation-triangle',2,'ace/other/error-404.html',3,1,1049,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 404 错误页');
INSERT INTO `flea_menu` VALUES (1073,'ace_error_500','500错误页','exclamation-circle',3,'ace/other/error-500.html',3,1,1049,NOW(),NULL,NOW(),@EXPIRY_DATE,'Ace 500 错误页');

INSERT INTO `flea_function_attr_menu` VALUES (1,1000,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (2,1001,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (3,1002,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (4,1003,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (5,1004,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (6,1005,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (7,1006,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (8,1007,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (9,1008,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (10,1009,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (11,1010,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (12,1011,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (13,1012,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (14,1013,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (15,1014,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (16,1015,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (17,1016,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (18,1017,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (19,1018,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (20,1019,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (21,1020,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (22,1021,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (23,1022,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (24,1023,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (25,1024,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (26,1025,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (27,1026,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (28,1027,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (29,1028,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (30,1029,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (31,1030,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (32,1031,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (33,1032,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (34,1033,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (35,1034,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (36,1035,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (37,1036,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (38,1037,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (39,1038,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (40,1039,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (41,1040,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (42,1041,'MENU','SYSTEM_IN_USE','1001','归属系统【Flea管家】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea管家】正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (43,1042,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (44,1043,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (45,1044,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (46,1045,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (47,1046,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (48,1047,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (49,1048,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (50,1049,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (51,1050,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (52,1051,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (53,1052,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (54,1053,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (55,1054,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (56,1055,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (57,1056,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (58,1057,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (59,1058,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (60,1059,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (61,1060,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (62,1061,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (63,1062,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (64,1063,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (65,1064,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (66,1065,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (67,1066,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (68,1067,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (69,1068,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (70,1069,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (71,1070,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (72,1071,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (73,1072,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');
INSERT INTO `flea_function_attr_menu` VALUES (74,1073,'MENU','SYSTEM_IN_USE','1001','所属系统《Flea管家》',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'《Flea管家》正在使用中');

INSERT INTO `flea_function_attr_operation` VALUES (1,1000,'OPERATION','OPERATION_ATTR','OPERATION_ATTR','OPERATION_ATTR',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'OPERATION_ATTR');
INSERT INTO `flea_function_attr_operation` VALUES (2,1001,'OPERATION','SYSTEM_IN_USE','1002','归属系统【Flea文件服务器】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea文件服务器】正在使用中');
INSERT INTO `flea_function_attr_operation` VALUES (3,1002,'OPERATION','SYSTEM_IN_USE','1002','归属系统【Flea文件服务器】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea文件服务器】正在使用中');
INSERT INTO `flea_function_attr_operation` VALUES (4,1003,'OPERATION','SYSTEM_IN_USE','1002','归属系统【Flea文件服务器】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea文件服务器】正在使用中');
INSERT INTO `flea_function_attr_operation` VALUES (5,1004,'OPERATION','SYSTEM_IN_USE','1002','归属系统【Flea文件服务器】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea文件服务器】正在使用中');
INSERT INTO `flea_function_attr_operation` VALUES (6,1005,'OPERATION','SYSTEM_IN_USE','1002','归属系统【Flea文件服务器】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea文件服务器】正在使用中');
INSERT INTO `flea_function_attr_operation` VALUES (7,1006,'OPERATION','SYSTEM_IN_USE','1002','归属系统【Flea文件服务器】',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'【Flea文件服务器】正在使用中');

INSERT INTO `flea_operation` VALUES (1000,'operation','操作1','操作1',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'操作保存测试');
INSERT INTO `flea_operation` VALUES (1001,'UPLOAD','文件上传','该操作用于定义文件上传功能',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'该操作用于定义文件上传功能');
INSERT INTO `flea_operation` VALUES (1002,'DOWNLOAD','文件下载','该操作用于定义文件下载功能',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'该操作用于定义文件下载功能');
INSERT INTO `flea_operation` VALUES (1003,'UPDATE','文件更新','该操作用于定义文件更新功能',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'该操作用于定义文件更新功能');
INSERT INTO `flea_operation` VALUES (1004,'DELETE','文件删除','该操作用于定义文件删除功能',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'该操作用于定义文件删除功能');
INSERT INTO `flea_operation` VALUES (1005,'SEARCH','文件搜索','该操作用于定义文件搜索功能',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'该操作用于定义文件搜索功能');
INSERT INTO `flea_operation` VALUES (1006,'VERSION','版本管理','该操作用于定义文件版本管理功能',1,NOW(),NULL,NOW(),@EXPIRY_DATE,'该操作用于定义文件版本管理功能');

-- ============================================================
-- 五、公共模块(ID 生成器水位,存最后分配值,下一个 ID = 水位 + 1)
-- ============================================================

INSERT INTO `flea_id_generator` VALUES ('pk_flea_account',10000);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_account_attr',4);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_auth_audit_log',0);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_element',999);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_function_attr_element',0);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_function_attr_menu',74);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_function_attr_operation',7);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_function_attr_resource',0);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_menu',1073);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_operation',1006);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_organization',0);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_privilege',1081);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_privilege_group',1005);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_privilege_group_rel',82);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_privilege_rel',82);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_real_name_info',0);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_resource',999);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_role',1018);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_role_group',1002);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_role_group_rel',1);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_role_rel',6);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_user',10000);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_user_attr',4);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_user_group',1002);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_user_group_rel',4);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_user_org_rel',0);
INSERT INTO `flea_id_generator` VALUES ('pk_flea_user_rel',2);

-- ============================================================
-- 登录日志按年月分表(动态生成,共 12 个月)
-- 以 SQL 执行时刻所在年月为起点,连续生成分表 flea_login_log_YYYYMM
-- (结构与主表 flea_login_log 相同),并同步插入对应 ID 生成器水位键
-- pk_flea_login_log_YYYYMM(=0;分表规则:flea-auth-table-split.xml,
--   按 create_date 路由,后缀 yyyyMM;框架不自动建表,故预建 1 年)
-- ============================================================
DROP PROCEDURE IF EXISTS create_login_log_tables;
DELIMITER $$
CREATE PROCEDURE create_login_log_tables()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE ym CHAR(6);
    WHILE i < 12 DO
        SET ym = DATE_FORMAT(DATE_ADD(NOW(), INTERVAL i MONTH), '%Y%m');
        SET @ddl = CONCAT('CREATE TABLE IF NOT EXISTS `flea_login_log_', ym, '` LIKE `flea_login_log`');
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SET @dml = CONCAT('INSERT INTO `flea_id_generator` VALUES (''pk_flea_login_log_', ym, ''', 0)');
        PREPARE stmt FROM @dml;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SET i = i + 1;
    END WHILE;
END
$$
DELIMITER ;
CALL create_login_log_tables();
DROP PROCEDURE IF EXISTS create_login_log_tables;

USE `fleamgmtconfig`;


SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `flea_config_data`
-- ----------------------------
DROP TABLE IF EXISTS `flea_config_data`;
CREATE TABLE `flea_config_data` (
  `config_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '配置编号',
  `config_type` varchar(50) NOT NULL COMMENT '配置类型',
  `config_code` varchar(50) NOT NULL COMMENT '配置编码',
  `config_name` varchar(60) NOT NULL COMMENT '配置名称',
  `config_desc` varchar(200) DEFAULT NULL COMMENT '配置描述',
  `config_state` tinyint(2) NOT NULL COMMENT '配置状态',
  `data1` varchar(2048) DEFAULT NULL COMMENT '数据1',
  `data2` varchar(2048) DEFAULT NULL COMMENT '数据2',
  `data3` varchar(2048) DEFAULT NULL COMMENT '数据3',
  `data4` varchar(2048) DEFAULT NULL COMMENT '数据4',
  `data5` varchar(2048) DEFAULT NULL COMMENT '数据5',
  `data6` varchar(2048) DEFAULT NULL COMMENT '数据6',
  `data7` varchar(2048) DEFAULT NULL COMMENT '数据7',
  `data8` varchar(2048) DEFAULT NULL COMMENT '数据8',
  `data9` varchar(2048) DEFAULT NULL COMMENT '数据9',
  `data10` varchar(2048) DEFAULT NULL COMMENT '数据10',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of flea_config_data
-- ----------------------------

-- ----------------------------
-- Table structure for `flea_jersey_i18n_error_mapping`
-- ----------------------------
DROP TABLE IF EXISTS `flea_jersey_i18n_error_mapping`;
CREATE TABLE `flea_jersey_i18n_error_mapping` (
  `mapping_id` int(10) NOT NULL AUTO_INCREMENT COMMENT '国际码和错误码映射编号',
  `resource_code` varchar(50) NOT NULL COMMENT '资源编码',
  `service_code` varchar(50) NOT NULL COMMENT '服务编码',
  `i18n_code` varchar(50) NOT NULL COMMENT '国际码',
  `error_code` varchar(50) NOT NULL COMMENT '错误码',
  `return_mess` varchar(2000) DEFAULT NULL COMMENT '响应公共报文返回信息',
  `state` tinyint(1) NOT NULL COMMENT '状态(0：删除 1：正常 ）',
  `create_date` datetime DEFAULT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改时间',
  `remarks` varchar(2000) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`mapping_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of flea_jersey_i18n_error_mapping
-- ----------------------------

-- ----------------------------
-- Table structure for `flea_jersey_resource`
-- ----------------------------
DROP TABLE IF EXISTS `flea_jersey_resource`;
CREATE TABLE `flea_jersey_resource` (
  `resource_code` varchar(30) NOT NULL COMMENT '资源编码',
  `resource_name` varchar(300) NOT NULL COMMENT '资源名称',
  `resource_packages` varchar(2000) NOT NULL COMMENT '资源包名(如果存在多个，以逗号分隔)',
  `state` tinyint(1) NOT NULL COMMENT '状态(0：删除 1：正常 ）',
  `create_date` date DEFAULT NULL COMMENT '创建日期',
  `done_date` date DEFAULT NULL COMMENT '修改时间',
  `remarks` varchar(2000) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`resource_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of flea_jersey_resource
-- ----------------------------

-- ----------------------------
-- Table structure for `flea_jersey_res_client`
-- ----------------------------
DROP TABLE IF EXISTS `flea_jersey_res_client`;
CREATE TABLE `flea_jersey_res_client` (
  `client_code` varchar(25) NOT NULL COMMENT '客户端编码',
  `resource_url` varchar(150) NOT NULL COMMENT '资源地址',
  `resource_code` varchar(30) NOT NULL COMMENT '资源编码',
  `service_code` varchar(30) NOT NULL COMMENT '服务编码',
  `request_mode` varchar(10) NOT NULL COMMENT '请求方式',
  `media_type` varchar(20) NOT NULL COMMENT '媒体类型',
  `client_input` varchar(150) NOT NULL COMMENT '客户端业务入参',
  `client_output` varchar(150) DEFAULT NULL COMMENT '客户端业务出参',
  `state` tinyint(1) NOT NULL COMMENT '状态(0：删除 1：正常 ）',
  `create_date` date DEFAULT NULL COMMENT '创建日期',
  `done_date` date DEFAULT NULL COMMENT '修改日期',
  `remarks` varchar(2000) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`client_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of flea_jersey_res_client
-- ----------------------------

-- ----------------------------
-- Table structure for `flea_jersey_res_service`
-- ----------------------------
DROP TABLE IF EXISTS `flea_jersey_res_service`;
CREATE TABLE `flea_jersey_res_service` (
  `service_code` varchar(30) NOT NULL COMMENT '服务编码',
  `resource_code` varchar(30) NOT NULL COMMENT '资源编码',
  `service_name` varchar(300) NOT NULL COMMENT '服务名称',
  `service_interfaces` varchar(150) NOT NULL COMMENT '服务接口类',
  `service_method` varchar(30) NOT NULL COMMENT '服务方法',
  `service_input` varchar(150) NOT NULL COMMENT '服务入参',
  `service_output` varchar(150) NOT NULL COMMENT '服务出参',
  `state` tinyint(1) NOT NULL COMMENT '状态(0：删除 1：正常 ）',
  `create_date` datetime DEFAULT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改时间',
  `remarks` varchar(2000) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`service_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of flea_jersey_res_service
-- ----------------------------

-- ----------------------------
-- Table structure for `flea_jersey_res_service_log`
-- ----------------------------
DROP TABLE IF EXISTS `flea_jersey_res_service_log`;
CREATE TABLE `flea_jersey_res_service_log` (
  `log_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '资源服务日志编号',
  `resource_code` varchar(30) NOT NULL COMMENT '资源编码',
  `service_code` varchar(30) NOT NULL COMMENT '服务编码',
  `input` text COMMENT '请求入参',
  `output` text COMMENT '响应出参',
  `result_code` varchar(50) DEFAULT NULL COMMENT '操作结果码',
  `result_mess` varchar(2048) DEFAULT NULL COMMENT '操作结果信息',
  `account_id` int(11) NOT NULL COMMENT '操作账户编号',
  `system_account_id` int(11) NOT NULL COMMENT '系统账户编号',
  `create_date` datetime DEFAULT NULL COMMENT '创建日期',
  `done_date` datetime DEFAULT NULL COMMENT '修改日期',
  `remarks` varchar(1024) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of flea_jersey_res_service_log
-- ----------------------------

-- ----------------------------
-- Table structure for `flea_menu_favorites`
-- ----------------------------
DROP TABLE IF EXISTS `flea_menu_favorites`;
CREATE TABLE `flea_menu_favorites` (
  `favorites_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '菜单收藏夹编号',
  `account_id` int(11) NOT NULL COMMENT '账户编号',
  `menu_code` varchar(50) NOT NULL COMMENT '菜单编码',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `menu_icon` varchar(30) NOT NULL COMMENT '菜单FontAwesome小图标',
  `favorites_state` tinyint(4) NOT NULL COMMENT '菜单收藏夹状态（1: 正常  0: 删除）',
  `create_date` datetime NOT NULL COMMENT '创建时间',
  `done_date` datetime DEFAULT NULL COMMENT '修改时间',
  `effective_date` datetime NOT NULL COMMENT '生效时间',
  `expiry_date` datetime NOT NULL COMMENT '失效时间',
  `remarks` varchar(255) DEFAULT NULL COMMENT '备注',
  `ext1` varchar(255) DEFAULT NULL COMMENT '扩展字段1',
  `ext2` varchar(255) DEFAULT NULL COMMENT '扩展字段2',
  `ext3` varchar(255) DEFAULT NULL COMMENT '扩展字段3',
  PRIMARY KEY (`favorites_id`),
  KEY `fk_favorite_account_id` (`account_id`) USING BTREE,
  KEY `fk_favorite_menu_code` (`menu_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of flea_menu_favorites
-- ----------------------------
-- ============================================================
-- 种子数据(仅 flea_jersey_res_client,共 10 条 FFS 操作)
-- ============================================================

INSERT INTO `flea_jersey_res_client` VALUES ('FLEA_CLIENT_UPLOAD_AUTH', 'http://localhost:8080/fleafs', 'upload', 'FLEA_SERVICE_UPLOAD_AUTH', 'post', 'application/xml', 'com.huazie.ffs.pojo.upload.input.InputUploadAuthInfo', 'com.huazie.ffs.pojo.upload.output.OutputUploadAuthInfo', 1,NOW(),NULL, '上传鉴权服务');
INSERT INTO `flea_jersey_res_client` VALUES ('FLEA_CLIENT_FILE_UPLOAD', 'http://localhost:8080/fleafs', 'upload', 'FLEA_SERVICE_FILE_UPLOAD', 'fpost', 'multipart/form-data', 'com.huazie.ffs.pojo.upload.input.InputFileUploadInfo', 'com.huazie.ffs.pojo.upload.output.OutputFileUploadInfo', 1,NOW(),NULL, '文件上传服务');
INSERT INTO `flea_jersey_res_client` VALUES ('FLEA_CLIENT_DOWNLOAD_AUTH', 'http://localhost:8080/fleafs', 'download', 'FLEA_SERVICE_DOWNLOAD_AUTH', 'post', 'application/xml', 'com.huazie.ffs.pojo.download.input.InputDownloadAuthInfo', 'com.huazie.ffs.pojo.download.output.OutputDownloadAuthInfo', 1,NOW(),NULL, '下载鉴权服务');
INSERT INTO `flea_jersey_res_client` VALUES ('FLEA_CLIENT_FILE_DOWNLOAD', 'http://localhost:8080/fleafs', 'download', 'FLEA_SERVICE_FILE_DOWNLOAD', 'fget', 'multipart/form-data', 'com.huazie.ffs.pojo.download.input.InputFileDownloadInfo', 'com.huazie.ffs.pojo.download.output.OutputFileDownloadInfo', 1,NOW(),NULL, '文件下载服务');
INSERT INTO `flea_jersey_res_client` VALUES ('FLEA_CLIENT_UPDATE_AUTH', 'http://localhost:8080/fleafs', 'update', 'FLEA_SERVICE_UPDATE_AUTH', 'post', 'application/xml', 'com.huazie.ffs.pojo.update.input.InputUpdateAuthInfo', 'com.huazie.ffs.pojo.update.output.OutputUpdateAuthInfo', 1,NOW(),NULL, '更新鉴权服务');
INSERT INTO `flea_jersey_res_client` VALUES ('FLEA_CLIENT_FILE_UPDATE', 'http://localhost:8080/fleafs', 'update', 'FLEA_SERVICE_FILE_UPDATE', 'fpost', 'multipart/form-data', 'com.huazie.ffs.pojo.update.input.InputFileUpdateInfo', 'com.huazie.ffs.pojo.update.output.OutputFileUpdateInfo', 1,NOW(),NULL, '文件更新服务');
INSERT INTO `flea_jersey_res_client` VALUES ('FLEA_CLIENT_DELETE_AUTH', 'http://localhost:8080/fleafs', 'delete', 'FLEA_SERVICE_DELETE_AUTH', 'post', 'application/xml', 'com.huazie.ffs.pojo.delete.input.InputDeleteAuthInfo', 'com.huazie.ffs.pojo.delete.output.OutputDeleteAuthInfo', 1,NOW(),NULL, '删除鉴权服务');
INSERT INTO `flea_jersey_res_client` VALUES ('FLEA_CLIENT_FILE_DELETE', 'http://localhost:8080/fleafs', 'delete', 'FLEA_SERVICE_FILE_DELETE', 'post', 'application/xml', 'com.huazie.ffs.pojo.delete.input.InputFileDeleteInfo', 'com.huazie.ffs.pojo.delete.output.OutputFileDeleteInfo', 1,NOW(),NULL, '文件逻辑删除服务');
INSERT INTO `flea_jersey_res_client` VALUES ('FLEA_CLIENT_FILE_VERSION', 'http://localhost:8080/fleafs', 'version', 'FLEA_SERVICE_FILE_VERSION', 'post', 'application/xml', 'com.huazie.ffs.pojo.version.input.InputFileVersionInfo', 'com.huazie.ffs.pojo.version.output.OutputFileVersionInfo', 1,NOW(),NULL, '文件版本查询服务');
INSERT INTO `flea_jersey_res_client` VALUES ('FLEA_CLIENT_FILE_SEARCH', 'http://localhost:8080/fleafs', 'search', 'FLEA_SERVICE_FILE_SEARCH', 'post', 'application/xml', 'com.huazie.ffs.pojo.search.input.InputFileSearchInfo', 'com.huazie.ffs.pojo.search.output.OutputFileSearchInfo', 1,NOW(),NULL, '文件搜索服务');

SET FOREIGN_KEY_CHECKS=1;
