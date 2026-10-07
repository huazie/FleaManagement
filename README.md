# <img src="src/docs/fleamgmt.png" width="80" height="80"> FleaManagement

[![Maven Build](https://github.com/huazie/FleaManagement/actions/workflows/build.yml/badge.svg)](https://github.com/huazie/FleaManagement/actions/workflows/build.yml) [![license](https://img.shields.io/badge/license-MIT-orange)](https://github.com/huazie/FleaManagement/blob/main/LICENSE) [![GitHub Repo stars](https://img.shields.io/github/stars/huazie/FleaManagement?style=flat)](https://github.com/huazie/FleaManagement/stargazers)

跳蚤管家（FleaManagement），一款基于 [Flea Framework](https://github.com/huazie/flea-framework) 与 Ace 框架的后台管理系统，提供用户、角色、权限、功能等完整的授权管理能力。

[英文说明/English Documentation](README_EN.md)

## 功能特性

- **用户管理**：用户注册、变更、授权，支持用户组管理与登录日志（按月分表）
- **角色管理**：角色与角色组维护，角色授权支持菜单 / 操作 / 元素 / 资源多维度
- **权限管理**：权限与权限组维护，权限关联支持菜单 / 操作 / 元素 / 资源四维度
- **功能管理**：菜单、操作、元素、资源四类功能实体统一维护
- **通用页面引擎**：FormPage / WizardPage / GridPage / AuthPage 四类页面引擎，声明式配置即可获得列表 + 筛选 + 分页 + 标签页 + 穿梭框授权的完整交互
- **响应式适配**：窄屏（≤767px）自动隐藏次要列，并提供关键字快捷搜索

## 软件架构

| 模块 | 描述 |
|------|------|
| fleamgmt-config | 配置模块：JPA 持久化单元、Spring 等各类配置 |
| fleamgmt-business | 业务模块：用户、角色、权限、功能四大授权子模块的业务实现 |
| fleamgmt-struts2 | Struts2 交互模块 |
| fleamgmt-springmvc | SpringMVC 交互模块 |
| fleamgmt-web | 前端 Web 模块：基于 Ace 框架 + jqGrid 的管理页面 |

## 技术栈

- **后端**：[Flea Framework](https://github.com/huazie/flea-framework)（flea-auth / flea-db / flea-cache / flea-config）+ Spring + Spring MVC / Struts2
- **持久化**：JPA（EclipseLink），支持分库分表
- **缓存**：flea-cache，支持同时接入 MemCached、Redis
- **前端**：Ace Framework + Bootstrap + jQuery + jqGrid + FontAwesome
- **数据库**：MySQL 5.7+

## 快速开始

### 环境要求

- JDK 1.8
- Maven 3.9+
- Tomcat 7.x
- MySQL 5.7+

### 1. 初始化数据库

项目 `sql/` 目录提供原生可执行的初始化脚本（⚠️ 含 `DROP TABLE`，重复执行会清空数据）：

| 文件 | 内容 |
|------|------|
| `fleamgmt_init.sql` | **全量入口（推荐）**，一条命令完成建库 + 两库表结构 + 初始数据 |
| `01_create_databases.sql` | 建库：`fleaauth`（授权）+ `fleamgmtconfig`（框架配置） |
| `02_fleaauth_init.sql` | 授权库 31 表结构（取自 flea-framework）+ 种子数据 |
| `03_fleamgmtconfig_init.sql` | 框架配置库 7 表结构（取自 flea-framework）+ Jersey 客户端注册数据（10 条 FFS 操作） |

```bash
# 全量初始化（推荐）
mysql -uroot -p --default-character-set=utf8 < sql/fleamgmt_init.sql

# 或分步执行
mysql -uroot -p --default-character-set=utf8 < sql/01_create_databases.sql
mysql -uroot -p --default-character-set=utf8 < sql/02_fleaauth_init.sql
mysql -uroot -p --default-character-set=utf8 < sql/03_fleamgmtconfig_init.sql
```

初始化完成后即可登录：

| 账号 | 密码 | 说明 |
|------|------|------|
| `admin` | `admin123` | 默认管理员（超级管理员角色），**请首次登录后修改** |

### 2. 构建

```bash
git clone https://github.com/huazie/FleaManagement.git
cd FleaManagement
mvn -s your-settings.xml -DskipTests clean package
```

### 3. 部署运行

将 `fleamgmt-web/target/fleamgmt-web-1.0.0` 部署到 Tomcat 7，启动后访问：

```txt
http://localhost:8080/login.html
```

## 相关项目

- [Flea Framework](https://github.com/huazie/flea-framework) —— 小巧易用的 Java 应用基础开发框架

## 参与贡献

1. [huazie](https://github.com/huazie)

欢迎提交 Pull Request 或创建 Issue 来帮助改进本项目。

## 许可证

[MIT](LICENSE) © [huazie](https://github.com/huazie)
