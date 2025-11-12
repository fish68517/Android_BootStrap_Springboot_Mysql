### @TableId can't more than one in Class: "com.graduation.entity.CommentLikes".

啊哈！这**才是**真正的根本问题所在！

您之前遇到的所有 `Failed to parse mapping resource` 错误，很可能都是因为这个**Java 实体类**的错误导致 Spring Boot 启动失败，而缓存又让您误以为是 XML 的问题。

### 错误分析

**错误信息：** `@TableId can't more than one in Class: "com.graduation.entity.CommentLikes"`

**中文A：** 在 `com.graduation.entity.CommentLikes` 这个 Java 类中，**不能有超过一个 `@TableId` 注解**。

**原因：**
您在 `comment_likes` 表中使用了 `(user_id, comment_id)` 作为**复合主键**。因此，您很可能在 `CommentLikes.java` 实体类中，同时给 `userId` 和 `commentId` 这两个字段（property）都加上了 `@TableId` 注解，像这样：

```java
// 这是您（可能）的错误代码
public class CommentLikes {
  
    @TableId // 错误：第一个 @TableId
    private Integer userId;

    @TableId // 错误：第二个 @TableId，导致了报错
    private Integer commentId;

    private LocalDateTime likedAt;
}
```

**Mybatis-Plus (MP) 的规则是：** 一个实体类（Entity）只能有一个字段被指定为 `@TableId`。Mybatis-Plus 的 `BaseMapper`（例如 `selectById`）不支持开箱即用的复合主键。

---

### 解决方案

处理 Mybatis-Plus 复合主键的最简单、最实用的方法，就是**干脆不指定 `@TableId`**，因为您反正也无法使用 `selectById(id)` 这样的方法（因为 `id` 有两部分）。

请按以下步骤修改您的 Java 实体类：

1. 打开您的实体类文件：`com/graduation/entity/CommentLikes.java`。
2. **删除**所有的 `@TableId` 注解。
3. （可选）保留 `@TableField` 注解来确保字段和数据库列名正确映射（如果您的 MP 配置了驼峰转下划线，甚至可以不写）。

**修改后的 `CommentLikes.java` 示例：**

```java
package com.graduation.entity;

// (确保导入了你需要的包, e.g., @TableName)
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 评论点赞实体类
 */
@TableName("comment_likes") // 最好指定一下表名
public class CommentLikes implements Serializable {

    private static final long serialVersionUID = 1L;

    // 不再使用 @TableId
    // @TableId
    private Integer userId;

    // 也不再使用 @TableId
    // @TableId
    private Integer commentId;

    private LocalDateTime likedAt;

    // --- (下面是您自动生成的 getter 和 setter) ---
    // ...
}

```

### 为什么这样可行？

1. **解决了启动错误**：Mybatis-Plus 启动时扫描实体类，发现没有多个 `@TableId`，启动不再报错。
2. **不影响 XML**：您在 `CommentLikesMapper.xml` 中定义的 `<resultMap>` 仍然是有效的！

   ```xml
   <resultMap id="BaseResultMap" type="com.graduation.entity.CommentLikes">
       <id column="user_id" property="userId" />
       <id column="comment_id" property="commentId" />
       <result column="liked_at" property="likedAt" />
   </resultMap>
   ```

   这个 `resultMap` 是给 **MyBatis**（MP 的底层）使用的。它正确地告诉了 MyBatis，当执行您自定义的 SQL 查询时，如何将 `user_id` 和 `comment_id` 视为复合主键并映射回对象。

**总结：**
请去 `CommentLikes.java` 文件中，**删除所有 `@TableId` 注解**，然后重新启动项目。

# Spring Boot 和 Thymeleaf 如何协同处理静态资源的关键。


好的，这是一个非常核心且重要的问题，理解了它，你就掌握了 Spring Boot 和 Thymeleaf 如何协同处理静态资源的关键。

我们来详细分析这行代码：`<script th:src="@{/admin_theme/js/sb-admin-2.min.js}"></script>`

### **1. `sb-admin-2.min.js` 应该放置在哪个目录下？**

**答案：** 你应该将 `sb-admin-2.min.js` 文件放置在以下**物理路径**下：

`src/main/resources/static/admin_theme/js/sb-admin-2.min.js`

**正确的项目目录结构应该是这样的：**

```
src
└── main
    ├── java
    │   └── ... (你的Java代码)
    └── resources
        ├── static  <-- 静态资源根目录
        │   └── admin_theme
        │       ├── css
        │       │   └── sb-admin-2.min.css
        │       ├── js
        │       │   └── sb-admin-2.min.js  <-- 文件放在这里
        │       ├── vendor
        │       │   └── ...
        │       └── img
        │           └── undraw_profile.png
        │   
        ├── templates
        │   └── ... (你的HTML模板文件)
        │
        └── application.properties
```

---

### **2. 原因是什么？**

这涉及到 Spring Boot 的**静态资源处理约定**。

1. **约定大于配置**：Spring Boot 默认会将 `src/main/resources/static/` 目录作为静态资源的根目录。当你的应用启动后，这个目录下的所有文件都可以通过网络直接访问。
2. **URL 与物理路径的映射**：Spring Boot 会将 URL 的路径直接映射到 `static` 目录下的物理路径。

   * 你代码中的 URL 是：`/admin_theme/js/sb-admin-2.min.js`
   * Spring Boot 会自动在 `static` 文件夹里寻找对应的路径：`static` + `/admin_theme/js/sb-admin-2.min.js`
   * 因此，它最终会定位到我们上面所说的那个物理位置。

你提供的 HTML 文件中所有静态资源的路径都遵循这个规则：

* `@{/admin_theme/vendor/fontawesome-free/css/all.min.css}` 对应 `static/admin_theme/vendor/fontawesome-free/css/all.min.css`
* `@{/img/undraw_profile.png}` 对应 `static/img/undraw_profile.png`

---

### **3. `@` 符号表示什么意思？**

在 Thymeleaf 中，`@{...}` 语法被称为 **"链接表达式" (Link Expressions)**，它专门用来处理 URL。

**它的核心作用是：生成一个相对于“应用上下文根 (Context Root)”的 URL。**

这听起来有点抽象，我们用一个例子来说明它为什么比直接写 `src="/..."` 更好。

* **简单情况**：你的应用直接在 `http://localhost:8080/` 下运行。

  * `th:src="@{/admin_theme/js/...}"` 会被渲染成 `<script src="/admin_theme/js/..."></script>`
  * `src="/admin_theme/js/..."` 也会得到 `<script src="/admin_theme/js/..."></script>`
  * 在这种情况下，两者效果完全一样。
* **复杂情况（未来部署时）**：假设未来你把这个项目部署到一台服务器上，并且它的访问路径被分配为 `http://yourserver.com/my-game-project/`。这里的 `/my-game-project` 就是“应用上下文根”。

  * `th:src="@{/admin_theme/js/...}"` **会自动**将上下文根加上，被正确地渲染成：`<script src="/my-game-project/admin_theme/js/..."></script>`。浏览器可以正确找到资源。
  * 而如果你写的是 `src="/admin_theme/js/..."`，它会被渲染成：`<script src="/admin_theme/js/..."></script>`。浏览器会去请求 `http://yourserver.com/admin_theme/js/...`，这个路径是错误的，导致资源加载失败 (404)。

**总结：**

`@{...}` 是一种更健壮、更专业的写法。它确保了无论你的项目将来部署在什么路径下，所有的静态资源链接都能自动适应并保持正确。**在 Thymeleaf 中，所有需要引用 CSS、JS、图片或页面跳转的地方，都应该使用 `@{...}` 语法。**

### **整个工作流程**

1. **你 (开发者)**：将 `sb-admin-2.min.js` 文件放入 `src/main/resources/static/admin_theme/js/` 目录下。
2. **浏览器**：请求你的管理员主页，例如 `http://localhost:8081/admin/dashboard`。
3. **服务器 (Thymeleaf)**：在返回 HTML 页面之前，处理 `th:src` 属性，看到 `@{...}` 语法，生成一个标准的 URL `/admin_theme/js/sb-admin-2.min.js`，并将其放入最终的 HTML 中。
4. **浏览器**：收到渲染好的 HTML，看到 `<script src="/admin_theme/js/sb-admin-2.min.js"></script>`，于是向服务器发起一个新的 GET 请求，地址是 `http://localhost:8081/admin_theme/js/sb-admin-2.min.js`。
5. **服务器 (Spring Boot)**：接收到这个请求，根据约定去 `static` 目录下找到了对应的文件，并将其内容返回给浏览器。JS 文件加载成功。


# Spring Boot 的一个核心设计理念：“约定优于配置”


问题找到了！**正是这个 `WebConfig` 文件导致的 404 错误。**

您遇到的问题非常典型，它清晰地揭示了 Spring Boot 的一个核心设计理念：“约定优于配置”。您的配置覆盖了 Spring Boot 的默认行为，并且文件放置的位置也不符合约定。

我们来详细分析一下，并给出正确的解决方案。

---

### **问题的根源：两个错误叠加**

#### **错误一：静态资源放错了位置**

您在问题描述中提到：**“我咋 templates/admin-theme 目录下有配置 js css 等文件”**

这是最关键的错误点。在 Spring Boot 项目中，`src/main/resources` 下的两个目录有严格的职责分工：

* `src/main/resources/templates/`：**模板目录**。这个目录是给 Thymeleaf、Freemarker 等模板引擎使用的。**此目录下的所有文件（如HTML）默认是不允许通过 URL 直接公开访问的**，它们必须由 Controller 转发才能被渲染。
* `src/main/resources/static/`：**静态资源目录**。这个目录才是用来存放 CSS、JavaScript、图片等静态文件的。**此目录下的所有文件默认都可以通过 URL 直接公开访问**。

您将 `admin_theme` 文件夹（包含CSS, JS）放进了 `templates` 目录，这违反了 Spring Boot 的基本约定，导致 Spring Boot 默认情况下根本找不到它们。

#### **错误二：用自定义配置去“修正”第一个错误**

您意识到了资源无法访问，于是尝试在 `WebConfig` 中通过 `addResourceHandlers` 方法来手动建立映射，试图“告诉”Spring Boot 去 `templates` 目录下找这些文件。

```java
// 您的配置
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/admin_theme/**")
            .addResourceLocations("classpath:/templates/static/admin_theme/");
}
```

这个配置本身做了两件事：

1. **它确实建立了一个映射**：让 URL `http://.../admin_theme/...` 指向了物理路径 `classpath:/templates/static/admin_theme/`。
2. **（副作用）它覆盖了 Spring Boot 的默认配置**：一旦您自己实现了 `addResourceHandlers` 方法，Spring Boot 就会认为：“好的，开发者想要完全自己控制静态资源，那么我就不再使用默认的 `static` 目录了。”

这就导致即使您把其他文件（比如之前的 `image` 文件夹）放在了正确的 `static` 目录下，它们也可能因为默认配置被覆盖而无法访问。

**这是一种用不规范的方式去解决一个本不该存在的问题，导致了更大的混乱。**

---

### **如何修复 (The Spring Boot Way)**

我们应该回归到 Spring Boot 的标准约定上来，这会让一切变得简单、清晰。

#### **第一步：移动文件夹**

将您 `src/main/resources/templates/` 目录下的 `admin_theme` 文件夹（或者您说的 `templates/static/admin_theme`），**整个移动到 `src/main/resources/static/` 目录下**。

**操作前，您的目录结构可能是这样的（错误的）：**

```
src/main/resources/
├── templates
│   ├── static
│   │   └── admin_theme  <-- 在这里
│   │       ├── css
│   │       └── js
│   └── your_page.html
└── static
    └── image
```

**操作后，正确的目录结构应该是这样的：**

```
src/main/resources/
├── templates
│   └── your_page.html
└── static
    ├── admin_theme      <-- 移动到这里
    │   ├── css
    │   └── js
    └── image
```

#### **第二步：删除或注释掉自定义配置**

现在文件已经回到了正确的位置，Spring Boot 的自动配置就能找到它们了。因此，您之前为了“修正”错误而添加的 `addResourceHandlers` 方法就**不再需要，反而有害**。

请打开您的 `WebConfig.java` 文件，**删除或注释掉整个 `addResourceHandlers` 方法**。

```java
package com.graduation.config;

// ... imports

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${file.upload-dir:src/main/resources/static/image}")
    private String uploadDir;

    /**
     *  !!!!  删除或注释掉下面这个方法  !!!!
     *  因为它是不必要的，并且会覆盖Spring Boot的默认静态资源处理
     */
    /*
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/admin_theme/**")
                .addResourceLocations("classpath:/templates/static/admin_theme/");
    }
    */
  
    /**
     * 配置跨域支持 (这个保留，它是您需要的功能)
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // ... 您的跨域配置 ...
    }
}
```

#### **第三步：重启并测试**

完成以上两步后，**重新启动您的 Spring Boot 应用**。

现在，当浏览器请求 `http://localhost:8081/admin_theme/js/sb-admin-2.min.js` 时：

1. 您的 `WebConfig` 不再干预静态资源处理。
2. Spring Boot 的默认配置会生效，它会自动去 `src/main/resources/static/` 目录下寻找。
3. 它会在 `static` 目录下找到 `admin_theme/js/sb-admin-2.min.js` 文件。
4. 资源加载成功，404 错误消失。

### **总结**

* **永远将静态文件（CSS, JS, 图片等）放在 `src/main/resources/static/` 目录下。**
* **永远将需要模板引擎渲染的 HTML 文件放在 `src/main/resources/templates/` 目录下。**
* **除非有非常特殊的理由，否则不要自己实现 `addResourceHandlers` 方法**，相信并使用 Spring Boot 的自动配置。

遵循这个约定，您的项目会更加清晰，也不容易出现这类难以排查的 404 错误。
