-- ============================================================
-- FleaManagement 初始化 - 03 框架配置库 fleamgmtconfig
-- 表结构:取自 flea-framework/flea-core/fleaconfig.sql(结构 1:1 保留)
-- 库名说明:fleaconfig 持久化单元在本工程被 fleamgmtconfig-persistence.xml
--           覆盖,JPA 实际连接 fleamgmtconfig 库(表与框架 fleaconfig 相同)
-- 角色说明:fleamgmt 仅作为 Jersey 客户端(调用方)
--     · flea_jersey_resource / flea_jersey_res_service 为服务提供方注册表,
--       本工程不使用,表结构保留、数据为空
--     · flea_jersey_res_client 注册全部 FFS 客户端调用(10 条:
--       上传/下载/更新/删除 各含 鉴权+文件 服务,另有 版本查询、文件搜索),
--       数据与 FleaFS 服务提供端(fleafsconfig)严格对齐
-- 时间统一:create_date=NOW()(执行时刻)、done_date=NULL
-- ⚠️ 含 DROP TABLE,重复执行会清空重建 fleamgmtconfig 全部数据
-- ============================================================
USE `fleamgmtconfig`;

-- 失效时间统一变量:只改下面这一行,全部种子行的 expiry_date 即刻生效
SET @EXPIRY_DATE = '2999-12-31 23:59:59';

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
