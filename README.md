# 花店管理系统 (Flower Shop System)

这是一个基于 Web 前端实战开发的复合型花店管理系统项目，包含了完整的用户端、后台管理端以及后端服务。

---

##  项目架构与模块说明

项目采用了多模块架构，各目录下对应的职责如下：
* **flower_shop_user**：用户端（前台商城，供顾客浏览花卉、下单、查看订单）。
* **flower_shop_admin**：后台管理端（供管理员/店长管理商品、分类、处理订单、查看报表）。
* **flower_shop_back**：后端服务（提供 API 接口，处理业务逻辑与数据库交互）。
* **flower_shop.sql**：项目配套的数据库结构及初始化数据脚本。

---

## 技术栈

* **前端**：Vue 3 (Composition API) / TypeScript / Pinia / Vue Router / Element Plus
* **后端**：Java Servlet / Tomcat / MySQL / CORS 跨域过滤器 / JWT (Token) 安全鉴权
* **工具**：Git、Visual Studio Code、IntelliJ IDEA

---

## 本地开发环境搭建

请确保你的本地环境已安装 [Node.js](https://nodejs.org/) 和 [MySQL](https://www.mysql.com/)。

### 1. 数据库初始化 (MySQL 终端操作)

请确保你的本地已安装并配置好 MySQL 环境变量，然后在终端依次执行以下命令：

```bash
# A. 登录 MySQL (回车后输入你的数据库密码)
mysql -u root -p

# B. 在 MySQL 终端中创建数据库并退出
CREATE DATABASE IF NOT EXISTS flower_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;

# C. 在项目根目录下，直接用终端将 SQL 脚本导入刚创建的数据库
mysql -u root -p flower_shop < flower_shop.sql
```

### 2. 后端服务启动 (flower_shop_back)
由于后端基于 Java Servlet & Tomcat 构建，请使用 IntelliJ IDEA 运行：

1、用 IntelliJ IDEA 打开 flower_shop_back 文件夹。

2、修改数据库配置：打开项目中的 druid.properties 配置文件，将 username 和 password 修改为你自己电脑上的本地 MySQL 用户名和密码。

3、配置 Tomcat：在 IDEA 中添加 Tomcat Server 配置，并部署项目的 Artifacts。

4、运行服务：点击绿色启动按钮运行 Tomcat（默认通常运行在 http://localhost:8080）。

### 3. 用户端启动 (flower_shop_user)
打开终端，切入用户端目录并启动：

```bash
cd flower_shop_user
npm install   # 安装前端依赖
npm run dev   # 启动前台项目
```

### 4. 管理端启动 (flower_shop_admin)
打开终端，切入管理端目录并启动：

```bash
cd ../flower_shop_admin
npm install   # 安装后台依赖
npm run dev   # 启动后台管理系统
```
