-- ============================================================
-- FleaManagement 初始化 - 01 建库
-- 创建授权库 fleaauth 与框架配置库 fleamgmtconfig(已存在则跳过)
-- 说明:fleaconfig 持久化单元在本工程被 fleamgmtconfig-persistence.xml
--       覆盖,JPA 实际连接 fleamgmtconfig 库(表与框架 fleaconfig 相同)
-- 字符集:utf8(与框架建表脚本保持一致,MySQL 5.7)
-- ============================================================
SET NAMES utf8;

CREATE DATABASE IF NOT EXISTS `fleaauth` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE DATABASE IF NOT EXISTS `fleamgmtconfig` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
