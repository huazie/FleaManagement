# <img src="src/docs/fleamgmt.png" width="80" height="80"> FleaManagement

[![license](https://img.shields.io/badge/license-MIT-orange)](https://github.com/huazie/FleaManagement/blob/main/LICENSE) [![GitHub Repo stars](https://img.shields.io/github/stars/huazie/FleaManagement?style=flat)](https://github.com/huazie/FleaManagement/stargazers)

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

Run the table creation scripts from the flea-framework project (⚠️ the scripts contain `DROP TABLE` statements and will wipe existing data):

```sql
-- fleaauth database (authorization module, 31 tables)
source flea-auth/fleaauth.sql
-- fleaconfig database (framework configuration, 7 tables)
source flea-core/fleaconfig.sql
```

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
