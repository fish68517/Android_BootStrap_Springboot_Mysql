# Design Document

## Overview

游戏推荐与管理平台采用经典的三层架构设计，基于Spring Boot 3.2.6框架构建。系统使用MyBatis-Plus作为ORM框架，Thymeleaf作为模板引擎，Bootstrap (sb-admin-2)提供前端UI组件。

### Technology Stack

- **Backend Framework**: Spring Boot 3.2.6
- **ORM**: MyBatis-Plus 3.5.7 + Spring Data JPA
- **Template Engine**: Thymeleaf
- **Frontend**: Bootstrap (sb-admin-2), jQuery
- **Database**: MySQL 8.0
- **Security**: Spring Security (基础认证)
- **Build Tool**: Maven
- **Java Version**: 17

## Architecture

### Layered Architecture

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Controllers + Thymeleaf Templates)    │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│          Business Layer                 │
│            (Services)                   │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│        Data Access Layer                │
│    (Repositories/Mappers + Entities)    │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│          Database Layer                 │
│           (MySQL 8.0)                   │
└─────────────────────────────────────────┘
```

### Package Structure

```
com.partapp.gameproject/
├── controller/          # 控制器层
│   ├── UserController
│   ├── GameController
│   ├── CommentController
│   ├── FavoriteController
│   ├── RecommendationController
│   └── AdminController
├── service/            # 业务逻辑层
│   ├── UserService
│   ├── GameService
│   ├── CommentService
│   ├── FavoriteService
│   ├── RecommendationService
│   └── PreferenceService
├── mapper/             # MyBatis-Plus Mapper接口
│   ├── UserMapper
│   ├── GameMapper
│   ├── CommentMapper
│   ├── CommentLikeMapper
│   ├── UserFavoriteMapper
│   ├── RecommendationMapper
│   ├── PreferenceMapper
│   ├── UserPreferenceMapper
│   └── WithdrawalRequestMapper
├── entity/             # 实体类
│   ├── User
│   ├── Game
│   ├── Comment
│   ├── CommentLike
│   ├── UserFavorite
│   ├── Recommendation
│   ├── Preference
│   ├── UserPreference
│   └── WithdrawalRequest
├── dto/                # 数据传输对象
│   ├── UserRegisterDTO
│   ├── UserLoginDTO
│   ├── GameSubmitDTO
│   └── CommentDTO
├── config/             # 配置类
│   ├── SecurityConfig
│   └── WebMvcConfig
└── util/               # 工具类
    ├── PasswordUtil
    └── ResponseUtil
```

## Components and Interfaces

### 1. Entity Layer (实体层)

#### User Entity
```java
@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer userId;
    private String username;
    private String passwordHash;
    private String nickname;
    private String role; // user, publisher, admin
    private LocalDateTime createdAt;
}
```

#### Game Entity
```java
@Data
@TableName("games")
public class Game {
    @TableId(type = IdType.AUTO)
    private Integer gameId;
    private Integer publisherId;
    private String title;
    private String description;
    private String coverImageUrl;
    private String otherImageUrls; // 逗号分隔
    private String status; // pending, approved, rejected
    private LocalDateTime submittedAt;
    private Integer reviewedByAdminId;
    private LocalDateTime reviewedAt;
}
```

#### Comment Entity
```java
@Data
@TableName("comments")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Integer commentId;
    private Integer gameId;
    private Integer userId;
    private String content;
    private String status; // pending, approved, rejected
    private LocalDateTime createdAt;
    private Integer reviewedByAdminId;
}
```

#### Other Entities
- **CommentLike**: 评论点赞关联表 (复合主键: userId + commentId)
- **UserFavorite**: 用户收藏关联表 (复合主键: userId + gameId)
- **Recommendation**: 推荐记录
- **Preference**: 偏好定义
- **UserPreference**: 用户偏好关联表 (复合主键: userId + preferenceId)
- **WithdrawalRequest**: 撤回申请

### 2. Data Access Layer (数据访问层)

使用MyBatis-Plus的BaseMapper接口，提供基础CRUD操作：

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // MyBatis-Plus提供基础方法
    // 自定义查询方法
    User selectByUsername(String username);
}

@Mapper
public interface GameMapper extends BaseMapper<Game> {
    List<Game> selectByStatus(String status);
    List<Game> selectByPublisherId(Integer publisherId);
    List<Game> searchByTitle(String keyword);
}

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    List<Comment> selectByGameIdAndStatus(Integer gameId, String status);
    List<Comment> selectPendingComments();
}
```

### 3. Service Layer (业务逻辑层)

#### UserService
```java
@Service
public class UserService {
    // 用户注册
    User register(UserRegisterDTO dto);
    
    // 用户登录
    User login(String username, String password);
    
    // 更新用户信息
    void updateProfile(Integer userId, User user);
    
    // 删除用户
    void deleteUser(Integer userId);
    
    // 管理员查看所有用户
    List<User> getAllUsers();
}
```

#### GameService
```java
@Service
public class GameService {
    // 发布游戏
    Game publishGame(GameSubmitDTO dto, Integer publisherId);
    
    // 修改游戏信息
    void updateGame(Integer gameId, GameSubmitDTO dto);
    
    // 查看游戏列表（按状态）
    List<Game> getGamesByStatus(String status);
    
    // 搜索游戏
    List<Game> searchGames(String keyword);
    
    // 获取游戏详情
    Game getGameDetail(Integer gameId);
    
    // 管理员审核游戏
    void reviewGame(Integer gameId, String status, Integer adminId);
}
```

#### CommentService
```java
@Service
public class CommentService {
    // 发表评论
    Comment postComment(Integer gameId, Integer userId, String content);
    
    // 删除评论
    void deleteComment(Integer commentId, Integer userId);
    
    // 管理员审核评论
    void reviewComment(Integer commentId, String status, Integer adminId);
    
    // 获取游戏的已审核评论
    List<Comment> getApprovedComments(Integer gameId);
    
    // 点赞评论
    void likeComment(Integer commentId, Integer userId);
    
    // 取消点赞
    void unlikeComment(Integer commentId, Integer userId);
    
    // 获取评论点赞数
    int getLikeCount(Integer commentId);
}
```

#### FavoriteService
```java
@Service
public class FavoriteService {
    // 收藏游戏
    void favoriteGame(Integer userId, Integer gameId);
    
    // 取消收藏
    void unfavoriteGame(Integer userId, Integer gameId);
    
    // 获取用户收藏列表
    List<Game> getUserFavorites(Integer userId);
    
    // 检查是否已收藏
    boolean isFavorited(Integer userId, Integer gameId);
}
```

#### RecommendationService
```java
@Service
public class RecommendationService {
    // 管理员创建推荐
    Recommendation createRecommendation(Integer adminId, Integer userId, 
                                       Integer gameId, String reason);
    
    // 获取用户的推荐列表
    List<Recommendation> getUserRecommendations(Integer userId);
    
    // 基于用户偏好自动推荐（可选功能）
    List<Game> getRecommendedGames(Integer userId);
}
```

#### PreferenceService
```java
@Service
public class PreferenceService {
    // 获取所有偏好选项
    List<Preference> getAllPreferences();
    
    // 设置用户偏好
    void setUserPreferences(Integer userId, List<Integer> preferenceIds);
    
    // 获取用户偏好
    List<Preference> getUserPreferences(Integer userId);
}
```

### 4. Controller Layer (控制器层)

#### UserController
```java
@Controller
@RequestMapping("/user")
public class UserController {
    @GetMapping("/register")
    String showRegisterPage();
    
    @PostMapping("/register")
    String register(UserRegisterDTO dto, Model model);
    
    @GetMapping("/login")
    String showLoginPage();
    
    @PostMapping("/login")
    String login(UserLoginDTO dto, HttpSession session, Model model);
    
    @GetMapping("/logout")
    String logout(HttpSession session);
    
    @GetMapping("/profile")
    String showProfile(HttpSession session, Model model);
    
    @PostMapping("/profile/update")
    String updateProfile(User user, HttpSession session);
    
    @PostMapping("/delete")
    String deleteAccount(HttpSession session);
}
```

#### GameController
```java
@Controller
@RequestMapping("/game")
public class GameController {
    @GetMapping("/list")
    String listGames(@RequestParam(required = false) String query, Model model);
    
    @GetMapping("/detail/{id}")
    String gameDetail(@PathVariable Integer id, Model model);
    
    @GetMapping("/publish")
    String showPublishPage(HttpSession session);
    
    @PostMapping("/publish")
    String publishGame(GameSubmitDTO dto, HttpSession session);
    
    @GetMapping("/edit/{id}")
    String showEditPage(@PathVariable Integer id, Model model);
    
    @PostMapping("/edit/{id}")
    String updateGame(@PathVariable Integer id, GameSubmitDTO dto);
    
    @GetMapping("/my-games")
    String myGames(HttpSession session, Model model);
}
```

#### AdminController
```java
@Controller
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/dashboard")
    String dashboard(Model model);
    
    @GetMapping("/games/pending")
    String pendingGames(Model model);
    
    @PostMapping("/games/review")
    String reviewGame(@RequestParam Integer gameId, 
                     @RequestParam String status,
                     HttpSession session);
    
    @GetMapping("/comments/pending")
    String pendingComments(Model model);
    
    @PostMapping("/comments/review")
    String reviewComment(@RequestParam Integer commentId,
                        @RequestParam String status,
                        HttpSession session);
    
    @GetMapping("/withdrawals/pending")
    String pendingWithdrawals(Model model);
    
    @PostMapping("/withdrawals/review")
    String reviewWithdrawal(@RequestParam Integer requestId,
                           @RequestParam String status,
                           HttpSession session);
    
    @GetMapping("/users")
    String manageUsers(Model model);
    
    @GetMapping("/recommendations")
    String manageRecommendations(Model model);
    
    @PostMapping("/recommendations/create")
    String createRecommendation(@RequestParam Integer userId,
                               @RequestParam Integer gameId,
                               @RequestParam String reason,
                               HttpSession session);
}
```

#### CommentController
```java
@Controller
@RequestMapping("/comment")
public class CommentController {
    @PostMapping("/post")
    String postComment(@RequestParam Integer gameId,
                      @RequestParam String content,
                      HttpSession session);
    
    @PostMapping("/delete/{id}")
    String deleteComment(@PathVariable Integer id, HttpSession session);
    
    @PostMapping("/like/{id}")
    @ResponseBody
    Map<String, Object> likeComment(@PathVariable Integer id, HttpSession session);
    
    @PostMapping("/unlike/{id}")
    @ResponseBody
    Map<String, Object> unlikeComment(@PathVariable Integer id, HttpSession session);
}
```

#### FavoriteController
```java
@Controller
@RequestMapping("/favorite")
public class FavoriteController {
    @PostMapping("/add")
    @ResponseBody
    Map<String, Object> addFavorite(@RequestParam Integer gameId, HttpSession session);
    
    @PostMapping("/remove")
    @ResponseBody
    Map<String, Object> removeFavorite(@RequestParam Integer gameId, HttpSession session);
    
    @GetMapping("/list")
    String listFavorites(HttpSession session, Model model);
}
```

## Data Models

### Database Schema

数据库设计完全遵循 `game_projct.sql` 文件中的表结构：

- **users**: 用户表，包含三种角色
- **games**: 游戏信息表，包含审核状态
- **comments**: 评论表，包含审核状态
- **comment_likes**: 评论点赞关联表
- **user_favorites**: 用户收藏关联表
- **recommendations**: 推荐记录表
- **preferences**: 偏好定义表
- **user_preferences**: 用户偏好关联表
- **withdrawal_requests**: 撤回申请表

### DTO Objects

```java
// 用户注册DTO
public class UserRegisterDTO {
    private String username;
    private String password;
    private String nickname;
}

// 用户登录DTO
public class UserLoginDTO {
    private String username;
    private String password;
}

// 游戏提交DTO
public class GameSubmitDTO {
    private String title;
    private String description;
    private String coverImageUrl;
    private String otherImageUrls;
}

// 评论DTO
public class CommentDTO {
    private Integer gameId;
    private String content;
}
```

## Error Handling

### Exception Hierarchy

```java
// 基础业务异常
public class BusinessException extends RuntimeException {
    private String code;
    private String message;
}

// 具体异常类型
public class UserNotFoundException extends BusinessException {}
public class UnauthorizedException extends BusinessException {}
public class DuplicateUsernameException extends BusinessException {}
public class GameNotFoundException extends BusinessException {}
public class InvalidStatusException extends BusinessException {}
```

### Global Exception Handler

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error";
    }
    
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("error", "系统错误，请稍后重试");
        return "error";
    }
}
```

## Security Design

### Spring Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/user/register", "/user/login", "/game/list", "/game/detail/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/game/publish", "/game/edit/**").hasAnyRole("PUBLISHER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/user/login")
                .defaultSuccessUrl("/")
            )
            .logout(logout -> logout
                .logoutUrl("/user/logout")
                .logoutSuccessUrl("/")
            );
        return http.build();
    }
}
```

### Session-Based Authentication

使用HttpSession存储登录用户信息：
- 登录成功后，将User对象存入session
- 每个请求从session获取当前用户
- 退出登录时清除session

## Frontend Design

### Thymeleaf Templates

#### Template Structure
```
templates/
├── layout/
│   ├── header.html          # 公共头部
│   └── footer.html          # 公共底部
├── user/
│   ├── register.html        # 注册页面
│   ├── login.html           # 登录页面
│   ├── profile.html         # 个人资料
│   └── preferences.html     # 偏好设置
├── game/
│   ├── list.html            # 游戏列表
│   ├── detail.html          # 游戏详情
│   ├── publish.html         # 发布游戏
│   └── my-games.html        # 我的游戏
├── admin/
│   ├── dashboard.html       # 管理员仪表板（基于提供的dashboard.html）
│   ├── review-games.html    # 审核游戏
│   ├── review-comments.html # 审核评论
│   ├── review-withdrawals.html # 审核撤回
│   ├── users.html           # 用户管理
│   └── recommendations.html # 推荐管理
├── favorite/
│   └── list.html            # 收藏列表
└── index.html               # 首页
```

#### Dashboard Integration

将提供的 `dashboard.html` 集成为管理员后台：
- 使用Thymeleaf语法替换静态数据
- 轮播Banner展示已发布游戏：`th:each="game : ${games}"`
- 搜索功能：`th:action="@{/admin/dashboard}" method="get"`
- 推荐列表：`th:each="rec : ${recommendations}"`

### Static Resources

```
static/
├── css/
│   ├── sb-admin-2.min.css   # Bootstrap主题
│   └── custom.css           # 自定义样式
├── js/
│   ├── jquery.min.js
│   ├── bootstrap.bundle.min.js
│   └── custom.js            # 自定义JavaScript
└── img/
    └── (游戏图片上传目录)
```

## Testing Strategy

### Unit Testing
- 使用JUnit 5和Mockito
- 测试Service层业务逻辑
- Mock Repository层依赖

### Integration Testing
- 测试Controller层端到端流程
- 使用@SpringBootTest
- 测试数据库交互

### Test Coverage Focus
- 用户注册和登录流程
- 游戏发布和审核流程
- 评论发布和审核流程
- 收藏和点赞功能
- 权限控制验证

## Configuration

### application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/game_projct?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
  
  thymeleaf:
    cache: false
    prefix: classpath:/templates/
    suffix: .html

mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.partapp.gameproject.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

server:
  port: 8080
  servlet:
    session:
      timeout: 30m
```

## Implementation Notes

1. **密码加密**: 使用BCrypt对密码进行加密存储
2. **图片上传**: 图片URL存储在数据库，实际文件存储在static/img目录
3. **级联删除**: 数据库外键配置ON DELETE CASCADE，删除用户/游戏时自动删除关联数据
4. **审核流程**: 所有需要审核的内容（游戏、评论、撤回）默认状态为pending
5. **角色权限**: 使用Spring Security的角色控制访问权限
6. **Session管理**: 使用HttpSession存储登录状态，超时时间30分钟
