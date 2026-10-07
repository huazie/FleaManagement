# <img src="src/docs/fleamgmt.png" width="80" height="80"> FleaManagement

[![Maven Build](https://github.com/huazie/FleaManagement/actions/workflows/build.yml/badge.svg)](https://github.com/huazie/FleaManagement/actions/workflows/build.yml) [![license](https://img.shields.io/badge/license-MIT-orange)](https://github.com/huazie/FleaManagement/blob/main/LICENSE) [![GitHub Repo stars](https://img.shields.io/github/stars/huazie/FleaManagement?style=flat)](https://github.com/huazie/FleaManagement/stargazers)

FleaManagement ("Flea Housekeeper") is an admin management system built on [Flea Framework](https://github.com/huazie/flea-framework) and the Ace framework, providing complete authorization management for users, roles, privileges and functions.

[中文说明/Chinese Documentation](README.md)

## Features

- **User Management**: user registration, modification and authorization, with user group management and login logs (partitioned by month)
- **Role Management**: role and role group maintenance; role authorization covers multiple dimensions including menus / operations / elements / resources
- **Privilege Management**: privilege and privilege group maintenance; privilege relations cover four dimensions: menus / operations / elements / resources
- **Function Management**: unified maintenance of four function entity types — menus, operations, elements and resources
- **Common Page Engine**: four page engines (FormPage / WizardPage / GridPage / AuthPage); declarative configuration gives you a complete interaction with list + filtering + pagination + tabs + transfer-list authorization
- **Responsive Design**: secondary columns are hidden automatically on narrow screens (≤767px), with quick keyword search

## Architecture

| Module | Description |
|--------|-------------|
| fleamgmt-config | Configuration module: JPA persistence units, Spring and other configurations |
| fleamgmt-business | Business module: business implementation of the four authorization sub-modules (user / role / privilege / function) |
| fleamgmt-struts2 | Struts2 interaction module |
| fleamgmt-springmvc | SpringMVC interaction module |
| fleamgmt-web | Front-end web module: admin pages based on Ace framework + jqGrid |

## Tech Stack

- **Back-end**: [Flea Framework](https://github.com/huazie/flea-framework) (flea-auth / flea-db / flea-cache / flea-config) + Spring + Spring MVC / Struts2
- **Persistence**: JPA (EclipseLink), with sharding support
- **Cache**: flea-cache, supports MemCached and Redis simultaneously
- **Front-end**: Ace Framework + Bootstrap + jQuery + jqGrid + FontAwesome
- **Database**: MySQL 5.7+

## Getting Started

### Requirements

- JDK 1.8
- Maven 3.9+
- Tomcat 7.x
- MySQL 5.7+

### 1. Initialize the Database

The `sql/` directory provides ready-to-run initialization scripts (⚠️ they contain `DROP TABLE` statements and will wipe existing data on re-run):

| File | Content |
|------|---------|
| `fleamgmt_init.sql` | **All-in-one entry (recommended)**: creates databases + both schemas + seed data in one command |
| `01_create_databases.sql` | Creates `fleaauth` (authorization) + `fleamgmtconfig` (framework configuration) |
| `02_fleaauth_init.sql` | Authorization schema, 31 tables (from flea-framework) + seed data |
| `03_fleamgmtconfig_init.sql` | Framework configuration schema, 7 tables (from flea-framework) + Jersey client registrations (10 FFS operations) |

```bash
# Full initialization (recommended)
mysql -uroot -p --default-character-set=utf8 < sql/fleamgmt_init.sql

# Or step by step
mysql -uroot -p --default-character-set=utf8 < sql/01_create_databases.sql
mysql -uroot -p --default-character-set=utf8 < sql/02_fleaauth_init.sql
mysql -uroot -p --default-character-set=utf8 < sql/03_fleamgmtconfig_init.sql
```

After initialization, you can log in with:

| Account | Password | Notes |
|---------|----------|-------|
| `admin` | `admin123` | Default administrator (super admin role) — **change it after first login** |

### 2. Build

```bash
git clone https://github.com/huazie/FleaManagement.git
cd FleaManagement
mvn -s your-settings.xml -DskipTests clean package
```

### 3. Deploy and Run

Deploy `fleamgmt-web/target/fleamgmt-web-1.0.0` to Tomcat 7, then visit:

```txt
http://localhost:8080/login.html
```

## Related Projects

- [Flea Framework](https://github.com/huazie/flea-framework) — a compact and easy-to-use Java application development framework

## Contributors

1. [huazie](https://github.com/huazie)

Pull Requests and Issues are welcome to help improve this project.

## License

[MIT](LICENSE) © [huazie](https://github.com/huazie)
