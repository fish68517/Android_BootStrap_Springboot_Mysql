非常好 👍
下面是为 **Cursor AI 编辑器** 精心编写的、可直接粘贴使用的 **Spring Boot 项目结构生成专用 Prompt 实例**。
它基于你提供的 `game_projct.sql` 数据库和 `dashboard.html` 管理员模板，能够指导 Cursor 自动生成完整后端与前端结构。

---

## 🧭 一、项目背景说明

系统名称：**游戏推荐与管理平台**
技术栈：

* **后端：** Spring Boot + Spring MVC + Spring Data JPA (或 MyBatis)
* **前端：** Thymeleaf + Bootstrap + jQuery
* **数据库：** MySQL 8.0
* **模板引擎：** Thymeleaf
* **界面模板来源：** dashboard.html（管理员后台）

---

## 🧠 二、Cursor 专用 Prompt 实例


### 🧾 **完整 Prompt**

```
你现在是一个资深全栈开发工程师，帮我基于 Spring Boot + MySQL + Thymeleaf + Bootstrap(sb-admin-2) 技术栈，
生成一个“游戏推荐与管理系统”的完整项目结构和主要代码。

---

🎯 【项目基础信息】
数据库设计文件：game_projct.sql  
管理员前端模板文件：dashboard.html  
目标是实现一个包含用户、游戏发布者、管理员三种角色的完整系统。

---

🧩 【功能模块要求】

1️⃣ 用户与账号管理：
- 用户注册（手机或邮箱 + 密码）
- 登录 / 退出
- 修改用户资料
- 找回密码 / 注销账号
- 管理员可查看和管理所有用户

2️⃣ 用户偏好收集：
- 用户登录后回答若干偏好问题（偏好表：preferences、user_preferences）
- 管理员可查看所有用户的偏好信息

3️⃣ 游戏发布与审核：
- 游戏发布者提交游戏信息（title, description, cover_image_url, etc.）
- 管理员审核游戏（通过 / 退回）
- 发布者可修改游戏后重新送审

4️⃣ 推荐系统：
- 管理员根据用户偏好推荐游戏（recommendations 表）
- 用户首页显示推荐列表

5️⃣ 评论系统：
- 用户可对游戏发表评论（comments 表）
- 用户与管理员都可删除自己的评论
- 管理员可审核评论（通过 / 拒绝）
- 用户可点赞 / 取消点赞评论（comment_likes 表）

6️⃣ 收藏系统：
- 用户收藏 / 取消收藏游戏（user_favorites 表）
- 可查看收藏列表

7️⃣ 撤回管理：
- 游戏发布者提交撤回游戏申请（withdrawal_requests 表）
- 管理员审核撤回申请（通过后删除相关信息）

8️⃣ 管理员后台（对应 dashboard.html）：
- 轮播 Banner 展示已发布游戏
- 搜索游戏
- 查看推荐列表
- 管理员登录后可审核游戏、评论、撤回请求
- 使用 Bootstrap + Thymeleaf 实现响应式布局

---

🏗️ 【生成目标】

请帮我生成一个标准的 Spring Boot 项目结构，要求如下：

📁 项目结构：
```

game-recommendation-system/
├─ src/
│  ├─ main/
│  │  ├─ java/com/example/gameproject/
│  │  │  ├─ controller/        # 控制层：UserController, AdminController, GameController, CommentController 等
│  │  │  ├─ service/           # 业务逻辑层：UserService, GameService, RecommendationService 等
│  │  │  ├─ repository/        # 数据访问层（使用 JPA 或 MyBatis）
│  │  │  ├─ model/             # 实体类，与 game_projct.sql 表结构一致
│  │  │  ├─ config/            # Spring Security 配置，CORS 设置等
│  │  │  └─ GameRecommendationApplication.java
│  │  ├─ resources/
│  │  │  ├─ templates/         # Thymeleaf 模板文件（dashboard.html, user_home.html 等）
│  │  │  ├─ static/            # 静态资源（CSS, JS, 图片）
│  │  │  ├─ application.yml    # 数据库连接配置
│  │  │  └─ schema.sql         # 初始化 SQL
│  

```

---

🧱 【生成要求】

1️⃣ 根据 game_projct.sql 自动生成对应的 JPA 实体类（含注释）：
   - User, Game, Comment, CommentLike, UserFavorite, Recommendation, Preference, UserPreference, WithdrawalRequest

2️⃣ 使用 Spring Data JPA Repository（或 MyBatis Mapper）访问数据库。

3️⃣ 每个主要模块生成相应的 Service 和 Controller：
   - UserController：注册、登录、修改资料、注销
   - GameController：发布、修改、查看游戏，提交审核
   - AdminController：审核游戏、审核评论、审核撤回、发布推荐
   - CommentController：发表评论、点赞、删除评论
   - FavoriteController：收藏/取消收藏/查看收藏
   - RecommendationController：显示推荐内容

4️⃣ 在 templates 目录中创建以下 Thymeleaf 页面：
   - login.html / register.html / user_home.html / game_detail.html
   - admin/dashboard.html（复用现有 dashboard.html）
   - admin/review_games.html / admin/review_comments.html / admin/review_withdrawals.html

5️⃣ 整合 dashboard.html 的 Thymeleaf 模板语法，与 Controller 传递的模型变量对接：
   - `${games}` 对应 GameController 提供的游戏数据
   - `${recommendations}` 对应 RecommendationController 提供的推荐列表

6️⃣ 前后端交互：
   - 后端返回 ModelAndView 或 @ResponseBody JSON
   - 前端使用 Thymeleaf 渲染页面数据
   - 搜索功能通过表单 `th:action` 提交至 `/admin/dashboard?query=xxx`

7️⃣ 加入 Spring Security（基础认证即可）：
   - 用户、发布者、管理员三种角色访问权限区分
   - 未登录用户跳转到登录页

8️⃣ 所有字段命名、数据类型、表名应与 game_projct.sql 完全一致。

---

🧰 【输出方式】

请先生成：
1️⃣ 完整的项目文件结构树；
2️⃣ `User` 与 `Game` 实体类示例；
3️⃣ `UserRepository` 与 `GameRepository`；
4️⃣ `UserController` 示例（注册与登录功能）；
5️⃣ 在 templates 中示例性生成 `login.html`、`register.html` 与简化的 `admin/dashboard.html`。

---

💡 生成后，我会再要求你继续生成其他模块（评论系统、收藏系统、推荐系统等）。
```

---
