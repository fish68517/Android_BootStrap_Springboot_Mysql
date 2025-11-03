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

-----

### 解决方案

处理 Mybatis-Plus 复合主键的最简单、最实用的方法，就是**干脆不指定 `@TableId`**，因为您反正也无法使用 `selectById(id)` 这样的方法（因为 `id` 有两部分）。

请按以下步骤修改您的 Java 实体类：

1.  打开您的实体类文件：`com/graduation/entity/CommentLikes.java`。
2.  **删除**所有的 `@TableId` 注解。
3.  （可选）保留 `@TableField` 注解来确保字段和数据库列名正确映射（如果您的 MP 配置了驼峰转下划线，甚至可以不写）。

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

1.  **解决了启动错误**：Mybatis-Plus 启动时扫描实体类，发现没有多个 `@TableId`，启动不再报错。
2.  **不影响 XML**：您在 `CommentLikesMapper.xml` 中定义的 `<resultMap>` 仍然是有效的！
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